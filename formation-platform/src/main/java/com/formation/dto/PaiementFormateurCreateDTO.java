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
public class PaiementFormateurCreateDTO {

    @NotNull(message = "L'identifiant du formateur est obligatoire")
    @Min(value = 1)
    private Long formateurId;

    @NotNull(message = "L'identifiant de la formation est obligatoire")
    @Min(value = 1)
    private Long formationId;

    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.01")
    private BigDecimal montant;

    @NotNull(message = "Le mode de paiement est obligatoire")
    private ModePaiement modePaiement;

    private Integer nombreTranches;

    private Integer numeroTranche;

    @NotNull(message = "La date de paiement est obligatoire")
    private LocalDate datePaiement;

    private String notes;
}
