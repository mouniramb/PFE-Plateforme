package com.formation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanningDTO {
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private List<SeanceResponseDTO> seances;
}
