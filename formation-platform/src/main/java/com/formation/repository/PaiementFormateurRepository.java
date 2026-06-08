package com.formation.repository;

import com.formation.entity.PaiementFormateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PaiementFormateurRepository extends JpaRepository<PaiementFormateur, Long> {

    List<PaiementFormateur> findByFormateurId(Long formateurId);

    List<PaiementFormateur> findByFormationId(Long formationId);

    List<PaiementFormateur> findByFormateurIdAndFormationId(Long formateurId, Long formationId);
}
