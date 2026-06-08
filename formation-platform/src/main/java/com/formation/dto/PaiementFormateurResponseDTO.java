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
public class PaiementFormateurResponseDTO {

    private Long id;
    private Long formateurId;
    private String formateurNom;
    private String formateurPrenom;
    private Long formationId;
    private String formationTitre;
    private BigDecimal montant;
    private BigDecimal montantParTranche;
    private ModePaiement modePaiement;
    private Integer nombreTranches;
    private Integer numeroTranche;
    private LocalDate datePaiement;
    private PaiementStatut statut;
    private String notes;
    private LocalDateTime dateCreation;
}
