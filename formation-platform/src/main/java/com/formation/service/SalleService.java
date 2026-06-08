package com.formation.service;

import com.formation.dto.SalleCreateDTO;
import com.formation.dto.SalleResponseDTO;
import com.formation.entity.Salle;
import com.formation.exception.SalleNotFoundException;
import com.formation.exception.CannotDeleteSalleException;
import com.formation.repository.SalleRepository;
import com.formation.repository.SeanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SalleService {

    private final SalleRepository salleRepository;
    private final SeanceRepository seanceRepository;

    public SalleResponseDTO createSalle(SalleCreateDTO dto) {
        log.info("Creating new salle: {}", dto.getNom());
        
        if (dto.getCapacite() <= 0) {
            throw new IllegalArgumentException("La capacité doit être > 0");
        }

        Salle salle = Salle.builder()
                .nom(dto.getNom())
                .capacite(dto.getCapacite())
                .localisation(dto.getLocalisation())
                .equipements(dto.getEquipements())
                .disponible(dto.getDisponible() != null ? dto.getDisponible() : true)
                .build();

        Salle saved = salleRepository.save(salle);
        log.info("Salle created with id: {}", saved.getId());
        return convertToDTO(saved);
    }

    public SalleResponseDTO updateSalle(Long id, SalleCreateDTO dto) {
        log.info("Updating salle with id: {}", id);
        
        Salle salle = salleRepository.findById(id)
                .orElseThrow(() -> new SalleNotFoundException("Salle non trouvée"));

        if (dto.getCapacite() != null && dto.getCapacite() <= 0) {
            throw new IllegalArgumentException("La capacité doit être > 0");
        }

        if (dto.getNom() != null) salle.setNom(dto.getNom());
        if (dto.getCapacite() != null) salle.setCapacite(dto.getCapacite());
        if (dto.getLocalisation() != null) salle.setLocalisation(dto.getLocalisation());
        if (dto.getEquipements() != null) salle.setEquipements(dto.getEquipements());
        if (dto.getDisponible() != null) salle.setDisponible(dto.getDisponible());

        Salle updated = salleRepository.save(salle);
        log.info("Salle updated with id: {}", id);
        return convertToDTO(updated);
    }

    public void deleteSalle(Long id) {
        log.info("Deleting salle with id: {}", id);
        
        Salle salle = salleRepository.findById(id)
                .orElseThrow(() -> new SalleNotFoundException("Salle non trouvée"));

        // Check if salle has planned or ongoing sessions
        boolean hasActiveSessions = salle.getSeances().stream()
                .anyMatch(seance -> seance.getStatut().name().equals("PLANIFIEE") 
                        || seance.getStatut().name().equals("EN_COURS"));

        if (hasActiveSessions) {
            throw new CannotDeleteSalleException("Impossible de supprimer une salle avec des séances planifiées ou en cours");
        }

        salleRepository.delete(salle);
        log.info("Salle deleted with id: {}", id);
    }

    public SalleResponseDTO getSalle(Long id) {
        log.debug("Getting salle with id: {}", id);
        Salle salle = salleRepository.findById(id)
                .orElseThrow(() -> new SalleNotFoundException("Salle non trouvée"));
        return convertToDTO(salle);
    }

    public Page<SalleResponseDTO> getAllSalles(Pageable pageable) {
        log.debug("Getting all salles with pagination");
        return salleRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    public List<SalleResponseDTO> getSallesDisponibles() {
        log.debug("Getting available salles");
        return salleRepository.findByDisponible(true)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public boolean checkDisponibiliteSalle(Long salleId, LocalDateTime debut, LocalDateTime fin, Long seanceIdExclure) {
        log.debug("Checking salle availability for id: {} from {} to {}", salleId, debut, fin);
        
        Salle salle = salleRepository.findById(salleId)
                .orElseThrow(() -> new SalleNotFoundException("Salle non trouvée"));

        // Verify salle exists (thrown above if not)
        if (salle == null) {
            throw new SalleNotFoundException("Salle non trouvée");
        }

        List<com.formation.entity.Seance> conflits = seanceRepository.findConflitsSalle(salleId, debut, fin);
        
        // Filter out the session to exclude if provided
        if (seanceIdExclure != null) {
            conflits = conflits.stream()
                    .filter(s -> !s.getId().equals(seanceIdExclure))
                    .collect(Collectors.toList());
        }

        return conflits.isEmpty();
    }

    private SalleResponseDTO convertToDTO(Salle salle) {
        return SalleResponseDTO.builder()
                .id(salle.getId())
                .nom(salle.getNom())
                .capacite(salle.getCapacite())
                .localisation(salle.getLocalisation())
                .equipements(salle.getEquipements())
                .disponible(salle.getDisponible())
                .dateCreation(salle.getDateCreation())
                .build();
    }
}
