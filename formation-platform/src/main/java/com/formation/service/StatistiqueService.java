package com.formation.service;

import com.formation.entity.Statistique;
import com.formation.repository.StatistiqueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class StatistiqueService {

    @Autowired
    private StatistiqueRepository statistiqueRepository;

    public Statistique getStatistiques() {
        return statistiqueRepository.findAll().stream()
            .findFirst()
            .orElseGet(() -> {
                Statistique stat = new Statistique();
                stat.setNbFormations(5);
                stat.setNbApprenants(25);
                stat.setNbInstructeurs(4);
                stat.setRevenuTotal(BigDecimal.valueOf(18000));
                return statistiqueRepository.save(stat);
            });
    }
}
