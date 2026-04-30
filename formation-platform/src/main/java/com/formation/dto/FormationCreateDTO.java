package com.formation.dto;

import com.formation.entity.FormationStatut;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
public class FormationCreateDTO {

    @NotBlank(message = "Le titre est obligatoire")
    private String titre;

    @NotBlank(message = "La description est obligatoire")
    private String description;

    @NotNull(message = "La durée est obligatoire")
    @Min(value = 1, message = "La durée doit être > 0")
    private Integer duree;

    @NotNull(message = "La date de début est obligatoire")
    private LocalDate dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    private LocalDate dateFin;

    @NotNull(message = "La capacité maximale est obligatoire")
    @Min(value = 1, message = "La capacité doit être > 0")
    private Integer capaciteMax;

    @NotNull(message = "Le prix est obligatoire")
    @DecimalMin(value = "0.0", inclusive = true, message = "Le prix doit être >= 0")
    private BigDecimal prix;

    @NotNull(message = "Le statut est obligatoire")
    private FormationStatut statut;

    private Set<Long> formateurIds;  // ✅ OPTIONNEL - peut être null ou vide
}
