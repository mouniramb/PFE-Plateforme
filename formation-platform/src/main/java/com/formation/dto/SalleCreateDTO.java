package com.formation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalleCreateDTO {
    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @Min(value = 1, message = "La capacité doit être > 0")
    private Integer capacite;

    private String localisation;

    private String equipements;

    @Builder.Default
    private Boolean disponible = true;
}
