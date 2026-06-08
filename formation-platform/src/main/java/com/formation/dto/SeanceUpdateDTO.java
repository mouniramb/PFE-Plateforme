package com.formation.dto;

import com.formation.entity.SeanceStatut;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeanceUpdateDTO {
    @NotBlank(message = "Le titre est obligatoire")
    private String titre;

    private String description;

    @NotNull(message = "L'heure de début est obligatoire")
    private LocalDateTime dateHeureDebut;

    @NotNull(message = "L'heure de fin est obligatoire")
    private LocalDateTime dateHeureFin;

    @NotNull(message = "La formation est obligatoire")
    private Long formationId;

    private Long salleId;

    private Long formateurId;

    private SeanceStatut statut;
}
