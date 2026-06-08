package com.formation.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "paiements")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"apprenant", "formation", "seance", "enregistrePar", "validePar"})
@ToString(exclude = {"apprenant", "formation", "seance", "enregistrePar", "validePar"})
public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apprenant_id", nullable = false)
    private User apprenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formation_id", nullable = false)
    private Formation formation;

    @Column(name = "montant", nullable = false, precision = 10, scale = 2)
    private BigDecimal montant;

    @Builder.Default
    @Column(name = "remise", precision = 10, scale = 2)
    private BigDecimal remise = BigDecimal.ZERO;

    @Column(name = "montant_net", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantNet;

    @Column(name = "date_paiement", nullable = false)
    private LocalDate datePaiement;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode_paiement", nullable = false, length = 20)
    private ModePaiement modePaiement;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 20)
    private PaiementStatut statut = PaiementStatut.EN_ATTENTE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seance_id")
    private Seance seance;

    @Column(name = "tranche_number")
    private Integer trancheNumber;

    @Column(name = "mois_annuaire")
    private Integer moisAnnuaire;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "commentaire", columnDefinition = "TEXT")
    private String commentaire;

    @Column(name = "date_enregistrement")
    private LocalDateTime dateEnregistrement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enregistre_par_id")
    private User enregistrePar;

    @Column(name = "date_validation")
    private LocalDateTime dateValidation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "valide_par_id")
    private User validePar;

    @PrePersist
    protected void onCreate() {
        this.dateEnregistrement = LocalDateTime.now();
        if (this.remise == null) this.remise = BigDecimal.ZERO;
        if (this.montantNet == null) {
            this.montantNet = this.montant.subtract(this.remise);
        }
    }
}
