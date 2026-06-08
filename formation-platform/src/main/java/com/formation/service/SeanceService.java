package com.formation.service;

import com.formation.dto.*;
import com.formation.entity.*;
import com.formation.exception.*;
import com.formation.repository.FormationRepository;
import com.formation.repository.SalleRepository;
import com.formation.repository.SeanceRepository;
import com.formation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SeanceService {

    private final SeanceRepository seanceRepository;
    private final FormationRepository formationRepository;
    private final UserRepository userRepository;
    private final SalleRepository salleRepository;
    private final SalleService salleService;
    private final EmailService emailService;

    public SeanceResponseDTO createSeance(SeanceCreateDTO dto) {
        log.info("Creating new seance: {}", dto.getTitre());

        // Validate dates
        if (dto.getDateHeureFin().isBefore(dto.getDateHeureDebut())) {
            throw new InvalidSeanceDatesException("L'heure de fin doit être après l'heure de début");
        }

        // Verify formation exists and is in appropriate status
        Formation formation = formationRepository.findById(dto.getFormationId())
                .orElseThrow(() -> new FormationNotFoundException("Formation non trouvée"));

        // Verify formateur exists and has FORMATEUR role
        User formateur = null;
        if (dto.getFormateurId() != null) {
            formateur = userRepository.findById(dto.getFormateurId())
                    .orElseThrow(() -> new UnauthorizedActionException("Formateur non trouvé"));
            if (formateur.getRole() != Role.FORMATEUR) {
                throw new UnauthorizedActionException("L'utilisateur n'est pas formateur");
            }
        }

        // Check for conflicts if salle is provided
        if (dto.getSalleId() != null) {
            if (!salleService.checkDisponibiliteSalle(dto.getSalleId(), dto.getDateHeureDebut(), dto.getDateHeureFin(), null)) {
                throw new ConflitHoraireException("La salle est déjà réservée à cette heure");
            }
        }

        // Check formateur conflicts
        if (formateur != null) {
            List<Seance> conflitsFormateur = seanceRepository.findConflitsFormateur(formateur.getId(), dto.getDateHeureDebut(), dto.getDateHeureFin());
            if (!conflitsFormateur.isEmpty()) {
                throw new ConflitHoraireException("Le formateur a déjà une séance à cette heure");
            }
        }

        Salle salle = null;
        if (dto.getSalleId() != null) {
            salle = salleRepository.findById(dto.getSalleId())
                    .orElseThrow(() -> new SalleNotFoundException("Salle non trouvée"));
        }

        Seance seance = Seance.builder()
                .titre(dto.getTitre())
                .description(dto.getDescription())
                .dateHeureDebut(dto.getDateHeureDebut())
                .dateHeureFin(dto.getDateHeureFin())
                .statut(SeanceStatut.PLANIFIEE)
                .formation(formation)
                .salle(salle)
                .formateur(formateur)
                .build();

        Seance saved = seanceRepository.save(seance);
        
        // Send email notification to formateur
        if (formateur != null) {
            emailService.sendSeanceAssignmentEmail(formateur, saved);
        }

        log.info("Seance created with id: {}", saved.getId());
        return convertToDTO(saved);
    }

    public SeanceResponseDTO updateSeance(Long id, SeanceUpdateDTO dto) {
        log.info("Updating seance with id: {}", id);

        Seance seance = seanceRepository.findById(id)
                .orElseThrow(() -> new SeanceNotFoundException("Séance non trouvée"));

        if (dto.getDateHeureFin().isBefore(dto.getDateHeureDebut())) {
            throw new InvalidSeanceDatesException("L'heure de fin doit être après l'heure de début");
        }

        // Check for conflicts (excluding current seance)
        if (dto.getSalleId() != null && !dto.getSalleId().equals(seance.getSalle() != null ? seance.getSalle().getId() : null)) {
            if (!salleService.checkDisponibiliteSalle(dto.getSalleId(), dto.getDateHeureDebut(), dto.getDateHeureFin(), id)) {
                throw new ConflitHoraireException("La salle est déjà réservée à cette heure");
            }
        }

        if (dto.getFormateurId() != null && !dto.getFormateurId().equals(seance.getFormateur() != null ? seance.getFormateur().getId() : null)) {
            List<Seance> conflits = seanceRepository.findConflitsFormateur(dto.getFormateurId(), dto.getDateHeureDebut(), dto.getDateHeureFin());
            conflits = conflits.stream().filter(s -> !s.getId().equals(id)).collect(Collectors.toList());
            if (!conflits.isEmpty()) {
                throw new ConflitHoraireException("Le formateur a déjà une séance à cette heure");
            }
        }

        seance.setTitre(dto.getTitre());
        seance.setDescription(dto.getDescription());
        seance.setDateHeureDebut(dto.getDateHeureDebut());
        seance.setDateHeureFin(dto.getDateHeureFin());
        
        if (dto.getStatut() != null) {
            seance.setStatut(dto.getStatut());
        }

        Seance updated = seanceRepository.save(seance);
        
        if (seance.getFormateur() != null) {
            emailService.sendSeanceModificationEmail(seance.getFormateur(), updated);
        }

        log.info("Seance updated with id: {}", id);
        return convertToDTO(updated);
    }

    public void deleteSeance(Long id) {
        log.info("Deleting seance with id: {}", id);

        Seance seance = seanceRepository.findById(id)
                .orElseThrow(() -> new SeanceNotFoundException("Séance non trouvée"));

        if (seance.getStatut() != SeanceStatut.PLANIFIEE) {
            throw new CannotDeleteSeanceException("Impossible de supprimer une séance en cours ou terminée");
        }

        seanceRepository.delete(seance);
        log.info("Seance deleted with id: {}", id);
    }

    public SeanceResponseDTO getSeance(Long id) {
        log.debug("Getting seance with id: {}", id);
        Seance seance = seanceRepository.findById(id)
                .orElseThrow(() -> new SeanceNotFoundException("Séance non trouvée"));
        return convertToDTO(seance);
    }

    public Page<SeanceResponseDTO> getSeancesByFormation(Long formationId, Pageable pageable) {
        log.debug("Getting seances for formation: {}", formationId);
        return seanceRepository.findByFormationId(formationId, pageable)
                .map(this::convertToDTO);
    }

    public Page<SeanceResponseDTO> getSeancesByFormateur(Long formateurId, Pageable pageable) {
        log.debug("Getting seances for formateur: {}", formateurId);
        return seanceRepository.findByFormateurId(formateurId, pageable)
                .map(this::convertToDTO);
    }

    public SeanceResponseDTO updateStatutSeance(Long id, SeanceStatut statut) {
        log.info("Updating seance {} status to: {}", id, statut);

        Seance seance = seanceRepository.findById(id)
                .orElseThrow(() -> new SeanceNotFoundException("Séance non trouvée"));

        seance.setStatut(statut);
        Seance updated = seanceRepository.save(seance);
        return convertToDTO(updated);
    }

    public List<ConflitDTO> checkConflitsHoraires(Long salleId, Long formateurId, LocalDateTime debut, LocalDateTime fin, Long seanceIdExclure) {
        log.debug("Checking conflicts for salle: {}, formateur: {}, from: {} to: {}", salleId, formateurId, debut, fin);

        List<ConflitDTO> conflits = new ArrayList<>();

        if (salleId != null) {
            List<Seance> conflitsSalle = seanceRepository.findConflitsSalle(salleId, debut, fin);
            if (seanceIdExclure != null) {
                conflitsSalle = conflitsSalle.stream().filter(s -> !s.getId().equals(seanceIdExclure)).collect(Collectors.toList());
            }
            for (Seance seance : conflitsSalle) {
                String message = String.format("La salle %s est déjà réservée le %s de %s à %s pour la séance %s (Formation: %s)",
                        seance.getSalle().getNom(),
                        seance.getDateHeureDebut().toLocalDate(),
                        seance.getDateHeureDebut().toLocalTime(),
                        seance.getDateHeureFin().toLocalTime(),
                        seance.getTitre(),
                        seance.getFormation().getTitre());
                conflits.add(ConflitDTO.builder()
                        .seanceExistante(convertToDTO(seance))
                        .messageConflit(message)
                        .build());
            }
        }

        if (formateurId != null) {
            List<Seance> conflitsFormateur = seanceRepository.findConflitsFormateur(formateurId, debut, fin);
            if (seanceIdExclure != null) {
                conflitsFormateur = conflitsFormateur.stream().filter(s -> !s.getId().equals(seanceIdExclure)).collect(Collectors.toList());
            }
            for (Seance seance : conflitsFormateur) {
                String message = String.format("Le formateur %s %s a déjà une séance le %s de %s à %s",
                        seance.getFormateur().getNom(),
                        seance.getFormateur().getPrenom(),
                        seance.getDateHeureDebut().toLocalDate(),
                        seance.getDateHeureDebut().toLocalTime(),
                        seance.getDateHeureFin().toLocalTime());
                conflits.add(ConflitDTO.builder()
                        .seanceExistante(convertToDTO(seance))
                        .messageConflit(message)
                        .build());
            }
        }

        return conflits;
    }

    public PlanningDTO getPlanningFormateur(Long formateurId, LocalDate debut, LocalDate fin) {
        log.debug("Getting planning for formateur: {} from: {} to: {}", formateurId, debut, fin);

        LocalDateTime startDateTime = debut.atStartOfDay();
        LocalDateTime endDateTime = fin.atTime(LocalTime.MAX);

        List<Seance> seances = seanceRepository.findPlanningFormateur(formateurId, startDateTime, endDateTime);
        List<SeanceResponseDTO> seancesDTOs = seances.stream().map(this::convertToDTO).collect(Collectors.toList());

        return PlanningDTO.builder()
                .dateDebut(debut)
                .dateFin(fin)
                .seances(seancesDTOs)
                .build();
    }

    public PlanningDTO getPlanningApprenant(Long apprenantId, LocalDate debut, LocalDate fin) {
        log.debug("Getting planning for apprenant: {} from: {} to: {}", apprenantId, debut, fin);

        LocalDateTime startDateTime = debut.atStartOfDay();
        LocalDateTime endDateTime = fin.atTime(LocalTime.MAX);

        List<Seance> seances = seanceRepository.findPlanningApprenant(apprenantId, startDateTime, endDateTime);
        List<SeanceResponseDTO> seancesDTOs = seances.stream().map(this::convertToDTO).collect(Collectors.toList());

        return PlanningDTO.builder()
                .dateDebut(debut)
                .dateFin(fin)
                .seances(seancesDTOs)
                .build();
    }

    public PlanningDTO getPlanningAdmin(LocalDate debut, LocalDate fin) {
        log.debug("Getting admin planning from: {} to: {}", debut, fin);

        LocalDateTime startDateTime = debut.atStartOfDay();
        LocalDateTime endDateTime = fin.atTime(LocalTime.MAX);

        List<Seance> seances = seanceRepository.findPlanningAdmin(startDateTime, endDateTime);
        List<SeanceResponseDTO> seancesDTOs = seances.stream().map(this::convertToDTO).collect(Collectors.toList());

        return PlanningDTO.builder()
                .dateDebut(debut)
                .dateFin(fin)
                .seances(seancesDTOs)
                .build();
    }

    private SeanceResponseDTO convertToDTO(Seance seance) {
        SeanceResponseDTO.FormationDTO formationDTO = null;
        if (seance.getFormation() != null) {
            formationDTO = SeanceResponseDTO.FormationDTO.builder()
                    .id(seance.getFormation().getId())
                    .titre(seance.getFormation().getTitre())
                    .build();
        }

        SeanceResponseDTO.SalleDTO salleDTO = null;
        if (seance.getSalle() != null) {
            salleDTO = SeanceResponseDTO.SalleDTO.builder()
                    .id(seance.getSalle().getId())
                    .nom(seance.getSalle().getNom())
                    .localisation(seance.getSalle().getLocalisation())
                    .build();
        }

        SeanceResponseDTO.UserDTO formateurDTO = null;
        if (seance.getFormateur() != null) {
            formateurDTO = SeanceResponseDTO.UserDTO.builder()
                    .id(seance.getFormateur().getId())
                    .nom(seance.getFormateur().getNom())
                    .prenom(seance.getFormateur().getPrenom())
                    .email(seance.getFormateur().getEmail())
                    .build();
        }

        return SeanceResponseDTO.builder()
                .id(seance.getId())
                .titre(seance.getTitre())
                .description(seance.getDescription())
                .dateHeureDebut(seance.getDateHeureDebut())
                .dateHeureFin(seance.getDateHeureFin())
                .statut(seance.getStatut())
                .dureeMinutes(seance.getDureeMinutes())
                .formation(formationDTO)
                .salle(salleDTO)
                .formateur(formateurDTO)
                .dateCreation(seance.getDateCreation())
                .dateModification(seance.getDateModification())
                .build();
    }
}
