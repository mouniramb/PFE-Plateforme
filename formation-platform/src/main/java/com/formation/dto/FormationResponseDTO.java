package com.formation.dto;

import com.formation.entity.Formation;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
public class FormationResponseDTO {
    private Long id;
    private String titre;
    private String description;
    private Integer duree;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Integer capaciteMax;
    private Integer capaciteActuelle;
    private Integer placesRestantes;
    private BigDecimal prix;
    private String statut;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    private Set<UserResponse> formateurs;

    public static FormationResponseDTO fromEntity(Formation formation) {
        return FormationResponseDTO.builder()
                .id(formation.getId())
                .titre(formation.getTitre())
                .description(formation.getDescription())
                .duree(formation.getDuree())
                .dateDebut(formation.getDateDebut())
                .dateFin(formation.getDateFin())
                .capaciteMax(formation.getCapaciteMax())
                .capaciteActuelle(formation.getCapaciteActuelle())
                .placesRestantes(formation.getPlacesRestantes())
                .prix(formation.getPrix())
                .statut(formation.getStatut().name())
                .dateCreation(formation.getDateCreation())
                .dateModification(formation.getDateModification())
                .formateurs(formation.getFormateurs().stream()
                        .map(UserResponse::fromUser)
                        .collect(Collectors.toSet()))
                .build();
    }
}
