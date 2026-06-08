package com.formation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"seance_id", "apprenant_id", "type_evaluation"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Note {

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

    @NotNull(message = "La valeur de la note est obligatoire")
    @Min(value = 0, message = "La note doit être >= 0")
    @Max(value = 20, message = "La note doit être <= 20")
    @Column(nullable = false, columnDefinition = "NUMERIC(4,2)")
    private Double valeur;

    @Builder.Default
    @Column(columnDefinition = "NUMERIC(3,1)")
    private Double coefficient = 1.0;

    @NotNull(message = "Le type d'évaluation est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "type_evaluation", nullable = false, length = 20)
    private TypeEvaluation typeEvaluation;

    @Column(columnDefinition = "TEXT")
    private String commentaire;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enregistre_par_id")
    private User enregistrePar;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
    }

    public boolean isValide() {
        return valeur != null && valeur >= 0 && valeur <= 20;
    }
}
