package com.formation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "seances")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Seance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    @Column(nullable = false, length = 255)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "L'heure de début est obligatoire")
    @Column(name = "date_heure_debut", nullable = false)
    private LocalDateTime dateHeureDebut;

    @NotNull(message = "L'heure de fin est obligatoire")
    @Column(name = "date_heure_fin", nullable = false)
    private LocalDateTime dateHeureFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private SeanceStatut statut = SeanceStatut.PLANIFIEE;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formation_id", nullable = false)
    private Formation formation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salle_id")
    private Salle salle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formateur_id")
    private User formateur;

    @OneToMany(mappedBy = "seance", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @Builder.Default
    private List<Presence> presences = new ArrayList<>();

    @OneToMany(mappedBy = "seance", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @Builder.Default
    private List<Note> notes = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dateModification = LocalDateTime.now();
    }

    public long getDureeMinutes() {
        return ChronoUnit.MINUTES.between(dateHeureDebut, dateHeureFin);
    }

    public boolean isEnConflit(LocalDateTime debut, LocalDateTime fin) {
        return dateHeureDebut.isBefore(fin) && dateHeureFin.isAfter(debut);
    }
}
