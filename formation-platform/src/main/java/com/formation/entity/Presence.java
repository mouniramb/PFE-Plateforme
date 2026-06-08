package com.formation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "presences", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"seance_id", "apprenant_id"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Presence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La séance est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seance_id", nullable = false)
    private Seance seance;

    @NotNull(message = "L'apprenant est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apprenant_id", nullable = false)
    private User apprenant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private PresenceStatut statut = PresenceStatut.ABSENT;

    @Column(columnDefinition = "TEXT")
    private String commentaire;

    @Column(name = "date_enregistrement", nullable = false, updatable = false)
    private LocalDateTime dateEnregistrement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enregistre_par_id")
    private User enregistrePar;

    @PrePersist
    protected void onCreate() {
        dateEnregistrement = LocalDateTime.now();
    }
}
