package com.formation.repository;

import com.formation.entity.Presence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PresenceRepository extends JpaRepository<Presence, Long> {
    List<Presence> findBySeanceId(Long seanceId);
    
    List<Presence> findByApprenantId(Long apprenantId);
    
    Optional<Presence> findBySeanceIdAndApprenantId(Long seanceId, Long apprenantId);
    
    boolean existsBySeanceIdAndApprenantId(Long seanceId, Long apprenantId);

    @Query("SELECT COUNT(p) FROM Presence p " +
           "WHERE p.apprenant.id = :apprenantId " +
           "AND p.seance.formation.id = :formationId " +
           "AND p.statut IN ('PRESENT', 'RETARD')")
    long countPresencesEffectives(@Param("apprenantId") Long apprenantId,
                                  @Param("formationId") Long formationId);

    @Query("SELECT COUNT(s) FROM Seance s " +
           "WHERE s.formation.id = :formationId " +
           "AND s.statut = 'TERMINEE'")
    long countSeancesTerminees(@Param("formationId") Long formationId);
}
