package com.formation.controller;

import com.formation.dto.SalleCreateDTO;
import com.formation.dto.SalleResponseDTO;
import com.formation.dto.ConflitDTO;
import com.formation.service.SalleService;
import com.formation.service.SeanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/salles")
@RequiredArgsConstructor
@Slf4j
public class SalleController {

    private final SalleService salleService;
    private final SeanceService seanceService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SalleResponseDTO> createSalle(@Valid @RequestBody SalleCreateDTO dto) {
        log.info("POST /api/salles");
        SalleResponseDTO response = salleService.createSalle(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SalleResponseDTO> updateSalle(@PathVariable Long id, @Valid @RequestBody SalleCreateDTO dto) {
        log.info("PUT /api/salles/{}", id);
        SalleResponseDTO response = salleService.updateSalle(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteSalle(@PathVariable Long id) {
        log.info("DELETE /api/salles/{}", id);
        salleService.deleteSalle(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Salle supprimée");
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','FORMATEUR')")
    public ResponseEntity<Page<SalleResponseDTO>> getAllSalles(Pageable pageable) {
        log.info("GET /api/salles");
        Page<SalleResponseDTO> response = salleService.getAllSalles(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','FORMATEUR')")
    public ResponseEntity<SalleResponseDTO> getSalle(@PathVariable Long id) {
        log.info("GET /api/salles/{}", id);
        SalleResponseDTO response = salleService.getSalle(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/disponibles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SalleResponseDTO>> getSallesDisponibles() {
        log.info("GET /api/salles/disponibles");
        List<SalleResponseDTO> response = salleService.getSallesDisponibles();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/disponibilite")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> checkDisponibilite(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin,
            @RequestParam(required = false) Long seanceId) {
        log.info("GET /api/salles/{}/disponibilite", id);

        boolean disponible = salleService.checkDisponibiliteSalle(id, debut, fin, seanceId);
        List<ConflitDTO> conflits = seanceService.checkConflitsHoraires(id, null, debut, fin, seanceId);

        Map<String, Object> response = new HashMap<>();
        response.put("disponible", disponible);
        response.put("conflits", conflits);

        return ResponseEntity.ok(response);
    }
}
