package com.formation.controller;

import com.formation.entity.Statistique;
import com.formation.service.StatistiqueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/statistiques")
@CrossOrigin(origins = "http://localhost:4200")
public class StatistiqueController {

    @Autowired
    private StatistiqueService statistiqueService;

    @GetMapping
    public ResponseEntity<Statistique> getStatistiques() {
        return ResponseEntity.ok(statistiqueService.getStatistiques());
    }

    @GetMapping("/formations")
    public ResponseEntity<?> getStatistiquesFormations() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Statistiques formations");
        response.put("totalFormations", 5);
        response.put("formations", new Object[] {
            new Object[] { "JavaScript", 3 },
            new Object[] { "Angular", 4 },
            new Object[] { "Spring Boot", 2 },
            new Object[] { "React", 3 },
            new Object[] { "Python", 2 }
        });
        return ResponseEntity.ok(response);
    }

    @GetMapping("/apprenants")
    public ResponseEntity<?> getStatistiquesApprenants() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Statistiques apprenants");
        response.put("totalApprenants", 25);
        response.put("parFormation", new Object[] {
            new Object[] { "JavaScript", 5 },
            new Object[] { "Angular", 6 },
            new Object[] { "Spring Boot", 4 },
            new Object[] { "React", 5 },
            new Object[] { "Python", 5 }
        });
        return ResponseEntity.ok(response);
    }
}
