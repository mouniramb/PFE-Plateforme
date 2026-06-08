package com.formation.dto;

import com.formation.entity.PresenceStatut;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PresenceResponseDTO {
    private Long id;
    private Long seanceId;
    private String seanceTitre;
    
    private UserDTO apprenant;
    private PresenceStatut statut;
    private String commentaire;
    private LocalDateTime dateEnregistrement;
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
