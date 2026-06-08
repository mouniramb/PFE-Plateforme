package com.formation.repository;

import com.formation.entity.Note;
import com.formation.entity.TypeEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findBySeanceId(Long seanceId);
    
    List<Note> findByApprenantId(Long apprenantId);
    
    List<Note> findBySeanceFormationId(Long formationId);
    
    List<Note> findByApprenantIdAndSeanceFormationId(Long apprenantId, Long formationId);
    
    Optional<Note> findBySeanceIdAndApprenantIdAndTypeEvaluation(Long seanceId, Long apprenantId, TypeEvaluation typeEvaluation);
    
    boolean existsBySeanceIdAndApprenantIdAndTypeEvaluation(Long seanceId, Long apprenantId, TypeEvaluation typeEvaluation);

    @Query("SELECT AVG(n.valeur * n.coefficient) / AVG(n.coefficient) " +
           "FROM Note n WHERE n.apprenant.id = :apprenantId " +
           "AND n.seance.formation.id = :formationId")
    Optional<Double> calculerMoyennePonderee(@Param("apprenantId") Long apprenantId,
                                              @Param("formationId") Long formationId);
}
