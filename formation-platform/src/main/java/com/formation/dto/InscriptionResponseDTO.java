package com.formation.dto;

import com.formation.entity.Inscription;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class InscriptionResponseDTO {
    private Long id;
    private Long formationId;
    private String formationTitre;
    private Long apprenantId;
    private String apprenantNom;
    private String apprenantPrenom;
    private String apprenantEmail;
    private LocalDateTime dateInscription;
    private String statut;
    private LocalDateTime dateAcceptation;
    private String motifRejet;
    private LocalDateTime dateRejet;

    public static InscriptionResponseDTO fromEntity(Inscription inscription) {
        return InscriptionResponseDTO.builder()
                .id(inscription.getId())
                .formationId(inscription.getFormation().getId())
                .formationTitre(inscription.getFormation().getTitre())
                .apprenantId(inscription.getApprenant().getId())
                .apprenantNom(inscription.getApprenant().getNom())
                .apprenantPrenom(inscription.getApprenant().getPrenom())
                .apprenantEmail(inscription.getApprenant().getEmail())
                .dateInscription(inscription.getDateInscription())
                .statut(inscription.getStatut().name())
                .dateAcceptation(inscription.getDateAcceptation())
                .motifRejet(inscription.getMotifRejet())
                .dateRejet(inscription.getDateRejet())
                .build();
    }
}
