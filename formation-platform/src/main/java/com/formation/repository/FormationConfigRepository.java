package com.formation.repository;

import com.formation.entity.FormationConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface FormationConfigRepository extends JpaRepository<FormationConfig, Long> {
    Optional<FormationConfig> findByFormationId(Long formationId);
}
