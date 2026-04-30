package com.formation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "inscriptions",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_formation_apprenant", columnNames = {"formation_id", "apprenant_id"})
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "formation_id", nullable = false)
    private Formation formation;

    @ManyToOne(optional = false)
    @JoinColumn(name = "apprenant_id", nullable = false)
    private User apprenant;

    @Column(name = "date_inscription", nullable = false, updatable = false)
    private LocalDateTime dateInscription;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private InscriptionStatut statut = InscriptionStatut.EN_ATTENTE;

    @Column(name = "date_acceptation")
    private LocalDateTime dateAcceptation;

    @Column(name = "motif_rejet", columnDefinition = "TEXT")
    private String motifRejet;

    @Column(name = "date_rejet")
    private LocalDateTime dateRejet;

    @PrePersist
    protected void onCreate() {
        this.dateInscription = LocalDateTime.now();
    }
}
