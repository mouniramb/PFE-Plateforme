package com.formation.controller;

import com.formation.entity.Rapport;
import com.formation.service.RapportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/rapports")
@CrossOrigin(origins = "http://localhost:4200")
public class RapportController {

    @Autowired
    private RapportService rapportService;

    @GetMapping
    public ResponseEntity<List<Rapport>> getAllRapports() {
        return ResponseEntity.ok(rapportService.getAllRapports());
    }

    @PostMapping("/generate")
    public ResponseEntity<Rapport> generateRapport(
            @RequestParam String type,
            @RequestParam(required = false) String dateDebut,
            @RequestParam(required = false) String dateFin) {
        return ResponseEntity.ok(rapportService.generateRapport(type, dateDebut, dateFin));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRapport(@PathVariable Long id) {
        rapportService.deleteRapport(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadRapport(@PathVariable Long id) {
        String content = "Rapport PDF - " + id;
        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=rapport-" + id + ".pdf")
            .body(content.getBytes());
    }
}
