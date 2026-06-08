package com.formation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "rapports")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rapport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String type;

    @Column(name = "date_generation")
    private LocalDateTime dateGeneration = LocalDateTime.now();

    @Column
    private String taille;

    @Column(columnDefinition = "TEXT")
    private String contenu;

    @Column(name = "chemin_fichier")
    private String cheminFichier;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation = LocalDateTime.now();
}
