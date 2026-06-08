package com.formation.service;

import com.formation.dto.NoteCreateDTO;
import com.formation.dto.NoteBulkDTO;
import com.formation.dto.NoteResponseDTO;
import com.formation.entity.*;
import com.formation.exception.*;
import com.formation.repository.InscriptionRepository;
import com.formation.repository.NoteRepository;
import com.formation.repository.SeanceRepository;
import com.formation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NoteService {

    private final NoteRepository noteRepository;
    private final SeanceRepository seanceRepository;
    private final UserRepository userRepository;
    private final InscriptionRepository inscriptionRepository;

    public NoteResponseDTO enregistrerNote(NoteCreateDTO dto, Long formateurId) {
        log.info("Recording note for seance: {}, apprenant: {}", dto.getSeanceId(), dto.getApprenantId());

        // Validate note value
        if (dto.getValeur() < 0 || dto.getValeur() > 20) {
            throw new NoteInvalideException("La note doit être comprise entre 0 et 20");
        }

        // Verify seance exists
        Seance seance = seanceRepository.findById(dto.getSeanceId())
                .orElseThrow(() -> new SeanceNotFoundException("Séance non trouvée"));

        // Verify formateur is responsible for this seance
        if (seance.getFormateur() == null || !seance.getFormateur().getId().equals(formateurId)) {
            throw new FormateurNonResponsableException("Vous n'êtes pas le formateur responsable de cette séance");
        }

        // Verify apprenant exists
        User apprenant = userRepository.findById(dto.getApprenantId())
                .orElseThrow(() -> new UnauthorizedActionException("Apprenant non trouvé"));

        // Verify apprenant is enrolled in the formation
        Inscription inscription = inscriptionRepository.findByApprenantIdAndFormationId(dto.getApprenantId(), seance.getFormation().getId())
                .orElseThrow(() -> new ApprenantNonInscritException("L'apprenant n'est pas inscrit à cette formation"));

        if (inscription.getStatut() != InscriptionStatut.ACCEPTEE) {
            throw new ApprenantNonInscritException("L'inscription de l'apprenant n'est pas acceptée");
        }

        User enregistrePar = userRepository.findById(formateurId).orElse(null);

        // Check if note already exists for this type
        Note note = noteRepository.findBySeanceIdAndApprenantIdAndTypeEvaluation(
                dto.getSeanceId(), dto.getApprenantId(), dto.getTypeEvaluation())
                .orElse(null);

        if (note == null) {
            note = Note.builder()
                    .seance(seance)
                    .apprenant(apprenant)
                    .valeur(dto.getValeur())
                    .coefficient(dto.getCoefficient() != null ? dto.getCoefficient() : 1.0)
                    .typeEvaluation(dto.getTypeEvaluation())
                    .commentaire(dto.getCommentaire())
                    .enregistrePar(enregistrePar)
                    .build();
        } else {
            note.setValeur(dto.getValeur());
            note.setCoefficient(dto.getCoefficient() != null ? dto.getCoefficient() : 1.0);
            note.setCommentaire(dto.getCommentaire());
            note.setEnregistrePar(enregistrePar);
        }

        Note saved = noteRepository.save(note);
        log.info("Note recorded with id: {}", saved.getId());
        return convertToDTO(saved);
    }

    public List<NoteResponseDTO> enregistrerNotesBulk(NoteBulkDTO dto, Long formateurId) {
        log.info("Recording bulk notes for seance: {}", dto.getSeanceId());

        List<NoteResponseDTO> results = dto.getNotes().stream()
                .map(item -> {
                    NoteCreateDTO createDTO = NoteCreateDTO.builder()
                            .seanceId(dto.getSeanceId())
                            .apprenantId(item.getApprenantId())
                            .valeur(item.getValeur())
                            .coefficient(item.getCoefficient())
                            .typeEvaluation(item.getTypeEvaluation())
                            .commentaire(item.getCommentaire())
                            .build();
                    return enregistrerNote(createDTO, formateurId);
                })
                .collect(Collectors.toList());

        log.info("Bulk note recording completed with {} entries", results.size());
        return results;
    }

    public List<NoteResponseDTO> getNotesParSeance(Long seanceId) {
        log.debug("Getting notes for seance: {}", seanceId);
        return noteRepository.findBySeanceId(seanceId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<NoteResponseDTO> getNotesParApprenant(Long apprenantId) {
        log.debug("Getting notes for apprenant: {}", apprenantId);
        return noteRepository.findByApprenantId(apprenantId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<NoteResponseDTO> getNotesParApprenantEtFormation(Long apprenantId, Long formationId) {
        log.debug("Getting notes for apprenant: {} in formation: {}", apprenantId, formationId);
        return noteRepository.findByApprenantIdAndSeanceFormationId(apprenantId, formationId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public double calculerMoyenne(Long apprenantId, Long formationId) {
        log.debug("Calculating average for apprenant: {} in formation: {}", apprenantId, formationId);

        var moyenneOpt = noteRepository.calculerMoyennePonderee(apprenantId, formationId);
        return moyenneOpt.orElse(0.0);
    }

    private NoteResponseDTO convertToDTO(Note note) {
        NoteResponseDTO.UserDTO apprenantDTO = NoteResponseDTO.UserDTO.builder()
                .id(note.getApprenant().getId())
                .nom(note.getApprenant().getNom())
                .prenom(note.getApprenant().getPrenom())
                .email(note.getApprenant().getEmail())
                .build();

        NoteResponseDTO.UserDTO enregistreParDTO = null;
        if (note.getEnregistrePar() != null) {
            enregistreParDTO = NoteResponseDTO.UserDTO.builder()
                    .id(note.getEnregistrePar().getId())
                    .nom(note.getEnregistrePar().getNom())
                    .prenom(note.getEnregistrePar().getPrenom())
                    .build();
        }

        return NoteResponseDTO.builder()
                .id(note.getId())
                .seanceId(note.getSeance().getId())
                .seanceTitre(note.getSeance().getTitre())
                .apprenant(apprenantDTO)
                .valeur(note.getValeur())
                .coefficient(note.getCoefficient())
                .typeEvaluation(note.getTypeEvaluation())
                .commentaire(note.getCommentaire())
                .dateCreation(note.getDateCreation())
                .enregistrePar(enregistreParDTO)
                .build();
    }
}
