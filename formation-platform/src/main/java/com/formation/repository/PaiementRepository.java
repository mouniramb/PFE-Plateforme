package com.formation.repository;

import com.formation.entity.Paiement;
import com.formation.entity.PaiementStatut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PaiementRepository extends JpaRepository<Paiement, Long> {

    List<Paiement> findByApprenantId(Long apprenantId);

    List<Paiement> findByApprenantIdAndFormationId(Long apprenantId, Long formationId);

    List<Paiement> findByFormationId(Long formationId);

    Page<Paiement> findByStatut(PaiementStatut statut, Pageable pageable);

    List<Paiement> findByFormationIdIn(List<Long> formationIds);

    @Query("SELECT COUNT(p) FROM Paiement p WHERE p.statut = :statut")
    long countByStatut(@Param("statut") PaiementStatut statut);
}
