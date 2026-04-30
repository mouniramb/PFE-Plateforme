package com.formation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InscriptionAcceptanceDTO {

    @NotNull(message = "L'identifiant d'inscription est obligatoire")
    private Long inscriptionId;
}
