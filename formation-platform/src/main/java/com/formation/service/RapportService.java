package com.formation.service;

import com.formation.entity.Rapport;
import com.formation.repository.RapportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RapportService {

    @Autowired
    private RapportRepository rapportRepository;

    public List<Rapport> getAllRapports() {
        return rapportRepository.findAll();
    }

    public Rapport getRapportById(Long id) {
        return rapportRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Rapport non trouvé avec l'ID: " + id));
    }

    public Rapport generateRapport(String type, String dateDebut, String dateFin) {
        Rapport rapport = new Rapport();
        rapport.setNom("Rapport " + type + " " + LocalDateTime.now().toLocalDate());
        rapport.setType(type);
        rapport.setDateGeneration(LocalDateTime.now());
        rapport.setTaille(String.format("%.1f MB", Math.random() * 5 + 1));
        rapport.setContenu("Rapport généré pour: " + type);
        return rapportRepository.save(rapport);
    }

    public void deleteRapport(Long id) {
        rapportRepository.deleteById(id);
    }
}
