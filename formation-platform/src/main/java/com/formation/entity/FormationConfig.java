package com.formation.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "formation_configs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"formation"})
@ToString(exclude = {"formation"})
public class FormationConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formation_id", nullable = false, unique = true)
    private Formation formation;

    @Builder.Default
    @Column(name = "permet_formation_complete", nullable = false)
    private Boolean permetFormationComplete = true;

    @Builder.Default
    @Column(name = "permet_par_seance", nullable = false)
    private Boolean permetParSeance = false;

    @Column(name = "prix_par_seance", precision = 10, scale = 2)
    private BigDecimal prixParSeance;

    @Builder.Default
    @Column(name = "permet_par_tranche", nullable = false)
    private Boolean permetParTranche = false;

    @Column(name = "nombre_tranches")
    private Integer nombreTranches;

    @Column(name = "montants_tranches", columnDefinition = "TEXT")
    private String montantsTranches;

    @Builder.Default
    @Column(name = "permet_par_annuaire", nullable = false)
    private Boolean permetParAnnuaire = false;

    @Column(name = "montant_mensuel", precision = 10, scale = 2)
    private BigDecimal montantMensuel;

    @Column(name = "nombre_mois")
    private Integer nombreMois;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @PrePersist
    protected void onCreate() {
        this.dateCreation = LocalDateTime.now();
        this.dateModification = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.dateModification = LocalDateTime.now();
    }
}
