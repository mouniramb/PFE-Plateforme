package com.formation.repository;

import com.formation.entity.Seance;
import com.formation.entity.SeanceStatut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SeanceRepository extends JpaRepository<Seance, Long> {
    Page<Seance> findByFormationId(Long formationId, Pageable pageable);
    
    Page<Seance> findByFormateurId(Long formateurId, Pageable pageable);
    
    Page<Seance> findBySalleId(Long salleId, Pageable pageable);
    
    Page<Seance> findByStatut(SeanceStatut statut, Pageable pageable);

    @Query("SELECT s FROM Seance s WHERE s.formateur.id = :formateurId " +
           "AND s.dateHeureDebut < :fin AND s.dateHeureFin > :debut " +
           "ORDER BY s.dateHeureDebut")
    List<Seance> findPlanningFormateur(@Param("formateurId") Long formateurId,
                                       @Param("debut") LocalDateTime debut,
                                       @Param("fin") LocalDateTime fin);

    @Query("SELECT DISTINCT s FROM Seance s " +
           "JOIN s.formation f JOIN f.inscriptions i " +
           "WHERE i.apprenant.id = :apprenantId " +
           "AND i.statut = 'ACCEPTEE' " +
           "AND s.dateHeureDebut < :fin AND s.dateHeureFin > :debut " +
           "ORDER BY s.dateHeureDebut")
    List<Seance> findPlanningApprenant(@Param("apprenantId") Long apprenantId,
                                       @Param("debut") LocalDateTime debut,
                                       @Param("fin") LocalDateTime fin);

    @Query("SELECT s FROM Seance s WHERE s.dateHeureDebut < :fin " +
           "AND s.dateHeureFin > :debut ORDER BY s.dateHeureDebut")
    List<Seance> findPlanningAdmin(@Param("debut") LocalDateTime debut,
                                   @Param("fin") LocalDateTime fin);

    @Query("SELECT s FROM Seance s WHERE s.salle.id = :salleId " +
           "AND s.statut != 'ANNULEE' " +
           "AND s.dateHeureDebut < :fin AND s.dateHeureFin > :debut")
    List<Seance> findConflitsSalle(@Param("salleId") Long salleId,
                                   @Param("debut") LocalDateTime debut,
                                   @Param("fin") LocalDateTime fin);

    @Query("SELECT s FROM Seance s WHERE s.formateur.id = :formateurId " +
           "AND s.statut != 'ANNULEE' " +
           "AND s.dateHeureDebut < :fin AND s.dateHeureFin > :debut")
    List<Seance> findConflitsFormateur(@Param("formateurId") Long formateurId,
                                       @Param("debut") LocalDateTime debut,
                                       @Param("fin") LocalDateTime fin);
}
