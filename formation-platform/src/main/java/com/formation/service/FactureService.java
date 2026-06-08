package com.formation.service;

import com.formation.entity.Facture;
import com.formation.repository.FactureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FactureService {

    @Autowired
    private FactureRepository factureRepository;

    public List<Facture> getAllFactures() {
        return factureRepository.findAll();
    }

    public Facture getFactureById(Long id) {
        return factureRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Facture non trouvée avec l'ID: " + id));
    }

    public List<Facture> filterByStatut(String statut) {
        return factureRepository.findByStatut(statut);
    }

    public List<Facture> getFacturesByApprenantId(Long apprenantId) {
        return factureRepository.findByApprenantId(apprenantId);
    }

    public Facture saveFacture(Facture facture) {
        return factureRepository.save(facture);
    }

    public Facture updateFacture(Long id, Facture facture) {
        Facture existing = getFactureById(id);
        existing.setNumero(facture.getNumero());
        existing.setApprenantId(facture.getApprenantId());
        existing.setFormationId(facture.getFormationId());
        existing.setMontant(facture.getMontant());
        existing.setDateFacture(facture.getDateFacture());
        existing.setStatut(facture.getStatut());
        return factureRepository.save(existing);
    }

    public void deleteFacture(Long id) {
        factureRepository.deleteById(id);
    }
}
