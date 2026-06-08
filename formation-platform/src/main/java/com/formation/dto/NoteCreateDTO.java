package com.formation.dto;

import com.formation.entity.TypeEvaluation;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteCreateDTO {
    @NotNull(message = "La séance est obligatoire")
    private Long seanceId;

    @NotNull(message = "L'apprenant est obligatoire")
    private Long apprenantId;

    @NotNull(message = "La valeur est obligatoire")
    @Min(value = 0, message = "La note doit être >= 0")
    @Max(value = 20, message = "La note doit être <= 20")
    private Double valeur;

    @Min(value = 0, message = "Le coefficient doit être >= 0")
    @Builder.Default
    private Double coefficient = 1.0;

    @NotNull(message = "Le type d'évaluation est obligatoire")
    private TypeEvaluation typeEvaluation;

    private String commentaire;
}
