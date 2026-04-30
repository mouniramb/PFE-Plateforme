package com.formation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class InscriptionRejectionDTO {

    @NotBlank(message = "Le motif de rejet est obligatoire")
    private String motif;
}
