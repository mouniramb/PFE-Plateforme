package com.formation.dto;

import com.formation.entity.SeanceStatut;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeanceResponseDTO {
    private Long id;
    private String titre;
    private String description;
    private LocalDateTime dateHeureDebut;
    private LocalDateTime dateHeureFin;
    private SeanceStatut statut;
    private Long dureeMinutes;
    
    private FormationDTO formation;
    private SalleDTO salle;
    private UserDTO formateur;
    
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FormationDTO {
        private Long id;
        private String titre;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SalleDTO {
        private Long id;
        private String nom;
        private String localisation;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserDTO {
        private Long id;
        private String nom;
        private String prenom;
        private String email;
    }
}
