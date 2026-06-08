package com.formation.controller;

import com.formation.entity.Tarif;
import com.formation.service.TarifService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tarifs")
@CrossOrigin(origins = "http://localhost:4200")
public class TarifController {

    @Autowired
    private TarifService tarifService;

    @GetMapping
    public ResponseEntity<List<Tarif>> getAllTarifs() {
        return ResponseEntity.ok(tarifService.getAllTarifs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tarif> getTarifById(@PathVariable Long id) {
        return ResponseEntity.ok(tarifService.getTarifById(id));
    }

    @PostMapping
    public ResponseEntity<Tarif> createTarif(@RequestBody Tarif tarif) {
        return ResponseEntity.ok(tarifService.saveTarif(tarif));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tarif> updateTarif(@PathVariable Long id, @RequestBody Tarif tarif) {
        return ResponseEntity.ok(tarifService.updateTarif(id, tarif));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTarif(@PathVariable Long id) {
        tarifService.deleteTarif(id);
        return ResponseEntity.noContent().build();
    }
}
