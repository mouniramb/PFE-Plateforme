package com.formation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalleResponseDTO {
    private Long id;
    private String nom;
    private Integer capacite;
    private String localisation;
    private String equipements;
    private Boolean disponible;
    private LocalDateTime dateCreation;
}
