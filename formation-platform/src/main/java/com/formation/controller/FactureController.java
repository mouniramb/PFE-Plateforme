package com.formation.controller;

import com.formation.entity.Facture;
import com.formation.service.FactureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/factures")
@CrossOrigin(origins = "http://localhost:4200")
public class FactureController {

    @Autowired
    private FactureService factureService;

    @GetMapping
    public ResponseEntity<List<Facture>> getFactures(@RequestParam(required = false) String statut) {
        if (statut != null && !statut.isEmpty()) {
            return ResponseEntity.ok(factureService.filterByStatut(statut));
        }
        return ResponseEntity.ok(factureService.getAllFactures());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Facture> getFactureById(@PathVariable Long id) {
        return ResponseEntity.ok(factureService.getFactureById(id));
    }

    @PostMapping
    public ResponseEntity<Facture> createFacture(@RequestBody Facture facture) {
        return ResponseEntity.ok(factureService.saveFacture(facture));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Facture> updateFacture(@PathVariable Long id, @RequestBody Facture facture) {
        return ResponseEntity.ok(factureService.updateFacture(id, facture));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFacture(@PathVariable Long id) {
        factureService.deleteFacture(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadFacturePDF(@PathVariable Long id) {
        String content = "Facture PDF - " + id;
        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=facture-" + id + ".pdf")
            .body(content.getBytes());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Facture>> getFacturesByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(factureService.getFacturesByApprenantId(userId));
    }
}
