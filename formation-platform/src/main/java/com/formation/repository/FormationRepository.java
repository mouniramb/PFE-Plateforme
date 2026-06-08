package com.formation.repository;

import com.formation.entity.Formation;
import com.formation.entity.FormationStatut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface FormationRepository extends JpaRepository<Formation, Long> {

    Page<Formation> findByStatut(FormationStatut statut, Pageable pageable);

    boolean existsByTitre(String titre);

    @Query("SELECT DISTINCT f FROM Formation f JOIN f.formateurs formateur WHERE formateur.id = :formateurId")
    Page<Formation> findByFormateurId(@Param("formateurId") Long formateurId, Pageable pageable);

    @Query("""
            SELECT f FROM Formation f
            WHERE LOWER(f.titre) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(f.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
            """)
    Page<Formation> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
            SELECT DISTINCT f FROM Formation f
            WHERE f.statut IN :statuts
              AND (:dateDebutMin IS NULL OR f.dateDebut >= :dateDebutMin)
              AND (:dateFinMax IS NULL OR f.dateFin <= :dateFinMax)
              AND (:prixMin IS NULL OR f.prix >= :prixMin)
              AND (:prixMax IS NULL OR f.prix <= :prixMax)
              AND (:capaciteMin IS NULL OR (f.capaciteMax - f.capaciteActuelle) >= :capaciteMin)
            """)
    Page<Formation> findCatalogue(
            @Param("statuts") List<FormationStatut> statuts,
            @Param("dateDebutMin") LocalDate dateDebutMin,
            @Param("dateFinMax") LocalDate dateFinMax,
            @Param("prixMin") BigDecimal prixMin,
            @Param("prixMax") BigDecimal prixMax,
            @Param("capaciteMin") Integer capaciteMin,
            Pageable pageable
    );
}
