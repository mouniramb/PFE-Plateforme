package com.formation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "statistiques")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Statistique {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_calcul")
    private LocalDateTime dateCalcul = LocalDateTime.now();

    @Column(name = "nb_formations")
    private Integer nbFormations = 0;

    @Column(name = "nb_apprenants")
    private Integer nbApprenants = 0;

    @Column(name = "nb_instructeurs")
    private Integer nbInstructeurs = 0;

    @Column(name = "revenu_total", precision = 15, scale = 2)
    private BigDecimal revenuTotal = BigDecimal.ZERO;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation = LocalDateTime.now();
}
