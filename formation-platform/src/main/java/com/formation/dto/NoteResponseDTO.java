package com.formation.dto;

import com.formation.entity.TypeEvaluation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteResponseDTO {
    private Long id;
    private Long seanceId;
    private String seanceTitre;
    
    private UserDTO apprenant;
    private Double valeur;
    private Double coefficient;
    private TypeEvaluation typeEvaluation;
    private String commentaire;
    private LocalDateTime dateCreation;
    private UserDTO enregistrePar;

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
