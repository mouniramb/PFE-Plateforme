package com.formation.repository;

import com.formation.entity.Inscription;
import com.formation.entity.InscriptionStatut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InscriptionRepository extends JpaRepository<Inscription, Long> {

    Optional<Inscription> findByFormationIdAndApprenantId(Long formationId, Long apprenantId);

    boolean existsByFormationIdAndApprenantId(Long formationId, Long apprenantId);

    boolean existsByFormationId(Long formationId);

    Page<Inscription> findByFormationId(Long formationId, Pageable pageable);

    Page<Inscription> findByApprenantId(Long apprenantId, Pageable pageable);

    Page<Inscription> findByStatut(InscriptionStatut statut, Pageable pageable);

    Page<Inscription> findByFormationIdAndStatut(Long formationId, InscriptionStatut statut, Pageable pageable);

    List<Inscription> findByFormationIdAndStatut(Long formationId, InscriptionStatut statut);

    Page<Inscription> findByFormationFormateursIdAndStatut(Long formateurId, InscriptionStatut statut, Pageable pageable);

    List<Inscription> findByApprenantIdAndStatut(Long apprenantId, InscriptionStatut statut);

    Optional<Inscription> findByApprenantIdAndFormationId(Long apprenantId, Long formationId);
}
