package com.formation.dto;

import com.formation.entity.PaiementStatut;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaiementValidationDTO {

    @NotNull(message = "Le statut est obligatoire")
    private PaiementStatut statut;

    private String commentaire;
}
