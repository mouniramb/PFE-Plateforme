package com.formation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "salles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Salle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom de la salle est obligatoire")
    @Column(nullable = false, length = 100)
    private String nom;

    @Min(value = 1, message = "La capacité doit être > 0")
    @Column(nullable = false)
    private Integer capacite;

    @Column(length = 255)
    private String localisation;

    @Column(columnDefinition = "TEXT")
    private String equipements;

    @Builder.Default
    @Column(nullable = false)
    private Boolean disponible = true;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @OneToMany(mappedBy = "salle", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @Builder.Default
    private List<Seance> seances = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
    }

    public boolean isDisponible(LocalDateTime debut, LocalDateTime fin) {
        if (seances == null) {
            return true;
        }
        return seances.stream().noneMatch(seance ->
                seance.getStatut() != SeanceStatut.ANNULEE &&
                seance.getDateHeureDebut().isBefore(fin) &&
                seance.getDateHeureFin().isAfter(debut)
        );
    }
}
