package com.formation.dto;

import com.formation.entity.ModePaiement;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaiementCreateDTO {

    @NotNull(message = "L'identifiant de l'apprenant est obligatoire")
    @Min(value = 1, message = "L'identifiant doit être supérieur à 0")
    private Long apprenantId;

    @NotNull(message = "L'identifiant de la formation est obligatoire")
    private Long formationId;

    @NotNull(message = "Le mode de paiement est obligatoire")
    private ModePaiement modePaiement;

    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à 0")
    private BigDecimal montant;

    private BigDecimal remise;

    @Min(value = 1, message = "Le numéro de tranche doit être entre 1 et 9")
    @Max(value = 9, message = "Le numéro de tranche doit être entre 1 et 9")
    private Integer trancheNumber;

    private Long seanceId;

    @Min(value = 1, message = "Le mois doit être entre 1 et 12")
    @Max(value = 12, message = "Le mois doit être entre 1 et 12")
    private Integer moisAnnuaire;

    @NotNull(message = "La date de paiement est obligatoire")
    private LocalDate datePaiement;

    private String notes;
}
