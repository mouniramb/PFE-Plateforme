package com.formation.service;

import com.formation.entity.Tarif;
import com.formation.repository.TarifRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TarifService {

    @Autowired
    private TarifRepository tarifRepository;

    public List<Tarif> getAllTarifs() {
        return tarifRepository.findAll();
    }

    public Tarif getTarifById(Long id) {
        return tarifRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Tarif non trouvé avec l'ID: " + id));
    }

    public List<Tarif> getTarifsByFormationId(Long formationId) {
        return tarifRepository.findByFormationId(formationId);
    }

    public Tarif saveTarif(Tarif tarif) {
        return tarifRepository.save(tarif);
    }

    public Tarif updateTarif(Long id, Tarif tarif) {
        Tarif existing = getTarifById(id);
        existing.setFormationId(tarif.getFormationId());
        existing.setPrixFormation(tarif.getPrixFormation());
        existing.setPrixParSeance(tarif.getPrixParSeance());
        existing.setMontantsTranches(tarif.getMontantsTranches());
        return tarifRepository.save(existing);
    }

    public void deleteTarif(Long id) {
        tarifRepository.deleteById(id);
    }
}
