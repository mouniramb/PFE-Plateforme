package com.formation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InscriptionDTO {

    @NotNull(message = "L'identifiant de la formation est obligatoire")
    private Long formationId;
}
