package com.formation.dto;

import com.formation.entity.ModePaiement;
import com.formation.entity.PaiementStatut;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaiementResponseDTO {

    private Long id;
    private Long apprenantId;
    private String apprenantNom;
    private String apprenantPrenom;
    private Long formationId;
    private String formationTitre;
    private ModePaiement modePaiement;
    private BigDecimal montant;
    private BigDecimal remise;
    private BigDecimal montantNet;
    private LocalDate datePaiement;
    private PaiementStatut statut;
    private Integer trancheNumber;
    private Long seanceId;
    private String seanceTitre;
    private Integer moisAnnuaire;
    private String notes;
    private String commentaire;
    private LocalDateTime dateEnregistrement;
    private UserInfo enregistrePar;
    private LocalDateTime dateValidation;
    private UserInfo validePar;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String nom;
        private String prenom;
    }
}
