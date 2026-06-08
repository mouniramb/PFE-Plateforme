package com.formation.service;

import com.formation.dto.PresenceCreateDTO;
import com.formation.dto.PresenceBulkDTO;
import com.formation.dto.PresenceResponseDTO;
import com.formation.entity.*;
import com.formation.exception.*;
import com.formation.repository.InscriptionRepository;
import com.formation.repository.PresenceRepository;
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
public class PresenceService {

    private final PresenceRepository presenceRepository;
    private final SeanceRepository seanceRepository;
    private final UserRepository userRepository;
    private final InscriptionRepository inscriptionRepository;

    public PresenceResponseDTO enregistrerPresence(PresenceCreateDTO dto, Long formateurId) {
        log.info("Registering presence for seance: {}, apprenant: {}", dto.getSeanceId(), dto.getApprenantId());

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

        // Check if presence already exists
        User enregistrePar = userRepository.findById(formateurId).orElse(null);

        Presence presence = presenceRepository.findBySeanceIdAndApprenantId(dto.getSeanceId(), dto.getApprenantId())
                .orElse(null);

        if (presence == null) {
            presence = Presence.builder()
                    .seance(seance)
                    .apprenant(apprenant)
                    .statut(dto.getStatut())
                    .commentaire(dto.getCommentaire())
                    .enregistrePar(enregistrePar)
                    .build();
        } else {
            presence.setStatut(dto.getStatut());
            presence.setCommentaire(dto.getCommentaire());
            presence.setEnregistrePar(enregistrePar);
        }

        Presence saved = presenceRepository.save(presence);
        log.info("Presence registered with id: {}", saved.getId());
        return convertToDTO(saved);
    }

    public List<PresenceResponseDTO> enregistrerPresencesBulk(PresenceBulkDTO dto, Long formateurId) {
        log.info("Registering bulk presences for seance: {}", dto.getSeanceId());

        List<PresenceResponseDTO> results = dto.getPresences().stream()
                .map(item -> {
                    PresenceCreateDTO createDTO = PresenceCreateDTO.builder()
                            .seanceId(dto.getSeanceId())
                            .apprenantId(item.getApprenantId())
                            .statut(item.getStatut())
                            .commentaire(item.getCommentaire())
                            .build();
                    return enregistrerPresence(createDTO, formateurId);
                })
                .collect(Collectors.toList());

        log.info("Bulk presence registration completed with {} entries", results.size());
        return results;
    }

    public List<PresenceResponseDTO> getPresencesParSeance(Long seanceId) {
        log.debug("Getting presences for seance: {}", seanceId);
        return presenceRepository.findBySeanceId(seanceId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PresenceResponseDTO> getPresencesParApprenant(Long apprenantId) {
        log.debug("Getting presences for apprenant: {}", apprenantId);
        return presenceRepository.findByApprenantId(apprenantId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PresenceResponseDTO> getPresencesParApprenantEtFormation(Long apprenantId, Long formationId) {
        log.debug("Getting presences for apprenant: {} in formation: {}", apprenantId, formationId);
        return presenceRepository.findByApprenantId(apprenantId)
                .stream()
                .filter(p -> p.getSeance().getFormation().getId().equals(formationId))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public double calculerTauxPresence(Long apprenantId, Long formationId) {
        log.debug("Calculating presence rate for apprenant: {} in formation: {}", apprenantId, formationId);

        long seancesAssistees = presenceRepository.countPresencesEffectives(apprenantId, formationId);
        long totalSeances = presenceRepository.countSeancesTerminees(formationId);

        if (totalSeances == 0) {
            return 0.0;
        }

        return (double) seancesAssistees / totalSeances * 100;
    }

    private PresenceResponseDTO convertToDTO(Presence presence) {
        PresenceResponseDTO.UserDTO apprenantDTO = PresenceResponseDTO.UserDTO.builder()
                .id(presence.getApprenant().getId())
                .nom(presence.getApprenant().getNom())
                .prenom(presence.getApprenant().getPrenom())
                .email(presence.getApprenant().getEmail())
                .build();

        PresenceResponseDTO.UserDTO enregistreParDTO = null;
        if (presence.getEnregistrePar() != null) {
            enregistreParDTO = PresenceResponseDTO.UserDTO.builder()
                    .id(presence.getEnregistrePar().getId())
                    .nom(presence.getEnregistrePar().getNom())
                    .prenom(presence.getEnregistrePar().getPrenom())
                    .build();
        }

        return PresenceResponseDTO.builder()
                .id(presence.getId())
                .seanceId(presence.getSeance().getId())
                .seanceTitre(presence.getSeance().getTitre())
                .apprenant(apprenantDTO)
                .statut(presence.getStatut())
                .commentaire(presence.getCommentaire())
                .dateEnregistrement(presence.getDateEnregistrement())
                .enregistrePar(enregistreParDTO)
                .build();
    }
}
