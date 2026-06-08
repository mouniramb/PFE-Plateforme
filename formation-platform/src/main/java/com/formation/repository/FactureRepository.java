package com.formation.repository;

import com.formation.entity.Facture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FactureRepository extends JpaRepository<Facture, Long> {
    List<Facture> findByStatut(String statut);
    List<Facture> findByApprenantId(Long apprenantId);
    List<Facture> findByFormationId(Long formationId);
}
