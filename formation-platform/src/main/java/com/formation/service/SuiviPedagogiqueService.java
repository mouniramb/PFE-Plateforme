package com.formation.service;

import com.formation.dto.NoteResponseDTO;
import com.formation.dto.PresenceResponseDTO;
import com.formation.dto.StatistiquesApprenantDTO;
import com.formation.entity.Formation;
import com.formation.entity.Inscription;
import com.formation.entity.InscriptionStatut;
import com.formation.entity.User;
import com.formation.repository.FormationRepository;
import com.formation.repository.InscriptionRepository;
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
@Transactional(readOnly = true)
public class SuiviPedagogiqueService {

    private final UserRepository userRepository;
    private final FormationRepository formationRepository;
    private final InscriptionRepository inscriptionRepository;
    private final PresenceService presenceService;
    private final NoteService noteService;

    public StatistiquesApprenantDTO getStatistiquesApprenant(Long apprenantId, Long formationId) {
        log.debug("Getting statistics for apprenant: {} in formation: {}", apprenantId, formationId);

        User apprenant = userRepository.findById(apprenantId)
                .orElseThrow(() -> new RuntimeException("Apprenant non trouvé"));

        Formation formation = formationRepository.findById(formationId)
                .orElseThrow(() -> new RuntimeException("Formation non trouvée"));

        // Verify apprenant is enrolled in the formation
        Inscription inscription = inscriptionRepository.findByApprenantIdAndFormationId(apprenantId, formationId)
                .orElseThrow(() -> new RuntimeException("L'apprenant n'est pas inscrit à cette formation"));

        if (inscription.getStatut() != InscriptionStatut.ACCEPTEE) {
            throw new RuntimeException("L'apprenant n'a pas une inscription acceptée à cette formation");
        }

        // Get presence data
        List<PresenceResponseDTO> presences = presenceService.getPresencesParApprenantEtFormation(apprenantId, formationId);
        double tauxPresence = presenceService.calculerTauxPresence(apprenantId, formationId);

        // Get notes data
        List<NoteResponseDTO> notes = noteService.getNotesParApprenantEtFormation(apprenantId, formationId);
        double moyenneGenerale = noteService.calculerMoyenne(apprenantId, formationId);

        // Calculate total seances
        long totalSeances = presenceService.getPresencesParApprenantEtFormation(apprenantId, formationId)
                .stream()
                .map(PresenceResponseDTO::getSeanceId)
                .distinct()
                .count();

        // Calculate attended seances (PRESENT or RETARD)
        long seancesAssistees = presences.stream()
                .filter(p -> p.getStatut().name().equals("PRESENT") || p.getStatut().name().equals("RETARD"))
                .count();

        return StatistiquesApprenantDTO.builder()
                .apprenantId(apprenant.getId())
                .nom(apprenant.getNom())
                .prenom(apprenant.getPrenom())
                .email(apprenant.getEmail())
                .formationId(formation.getId())
                .formationTitre(formation.getTitre())
                .totalSeances((int) totalSeances)
                .seancesAssistees((int) seancesAssistees)
                .tauxPresence(tauxPresence)
                .moyenneGenerale(moyenneGenerale)
                .detailNotes(notes)
                .detailPresences(presences)
                .build();
    }

    public List<StatistiquesApprenantDTO> getStatistiquesFormation(Long formationId) {
        log.debug("Getting statistics for formation: {}", formationId);

        formationRepository.findById(formationId)
                .orElseThrow(() -> new RuntimeException("Formation non trouvée"));

        // Get all accepted enrollments
        List<Inscription> inscriptions = inscriptionRepository.findByFormationIdAndStatut(formationId, InscriptionStatut.ACCEPTEE);

        return inscriptions.stream()
                .map(inscription -> getStatistiquesApprenant(inscription.getApprenant().getId(), formationId))
                .collect(Collectors.toList());
    }

    public List<StatistiquesApprenantDTO> getProgressionApprenant(Long apprenantId) {
        log.debug("Getting progression for apprenant: {}", apprenantId);

        userRepository.findById(apprenantId)
                .orElseThrow(() -> new RuntimeException("Apprenant non trouvé"));

        // Get all accepted enrollments for this apprenant
        List<Inscription> inscriptions = inscriptionRepository.findByApprenantIdAndStatut(apprenantId, InscriptionStatut.ACCEPTEE);

        return inscriptions.stream()
                .map(inscription -> getStatistiquesApprenant(apprenantId, inscription.getFormation().getId()))
                .collect(Collectors.toList());
    }
}
