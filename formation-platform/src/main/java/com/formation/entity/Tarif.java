package com.formation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tarifs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tarif {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "formation_id", nullable = false)
    private Long formationId;

    @Column(name = "prix_formation", nullable = false, precision = 10, scale = 2)
    private BigDecimal prixFormation;

    @Column(name = "prix_par_seance", nullable = false, precision = 10, scale = 2)
    private BigDecimal prixParSeance;

    @Column(name = "montants_tranches")
    private String montantsTranches;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation = LocalDateTime.now();

    @Column(name = "date_modification")
    private LocalDateTime dateModification = LocalDateTime.now();
}
