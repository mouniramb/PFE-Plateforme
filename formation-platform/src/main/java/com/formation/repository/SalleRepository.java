package com.formation.repository;

import com.formation.entity.Salle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalleRepository extends JpaRepository<Salle, Long> {
    List<Salle> findByDisponible(boolean disponible);
    List<Salle> findByCapaciteGreaterThanEqual(int capaciteMin);
}
