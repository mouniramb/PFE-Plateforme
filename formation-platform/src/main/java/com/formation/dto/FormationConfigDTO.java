package com.formation.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormationConfigDTO {

    private Long formationId;
    private String formationTitre;
    private BigDecimal prixFormation;

    private boolean permetFormationComplete;

    private boolean permetParSeance;
    private BigDecimal prixParSeance;

    private boolean permetParTranche;
    private Integer nombreTranches;
    private String montantsTranches;

    private boolean permetParAnnuaire;
    private BigDecimal montantMensuel;
    private Integer nombreMois;
}
