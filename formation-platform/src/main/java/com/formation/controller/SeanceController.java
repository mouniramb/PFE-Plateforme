package com.formation.controller;

import com.formation.dto.*;
import com.formation.entity.SeanceStatut;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/seances")
@RequiredArgsConstructor
@Slf4j
public class SeanceController {

    private final SeanceService seanceService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createSeance(@Valid @RequestBody SeanceCreateDTO dto, @RequestParam(defaultValue = "false") boolean forcer) {
        log.info("POST /api/seances");
        try {
            SeanceResponseDTO response = seanceService.createSeance(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            if (!forcer && e.getMessage().contains("Conflit")) {
                // Return conflict details
                List<ConflitDTO> conflits = seanceService.checkConflitsHoraires(dto.getSalleId(), dto.getFormateurId(), dto.getDateHeureDebut(), dto.getDateHeureFin(), null);
                return ResponseEntity.status(409).body(conflits);
            }
            throw e;
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SeanceResponseDTO> updateSeance(@PathVariable Long id, @Valid @RequestBody SeanceUpdateDTO dto) {
        log.info("PUT /api/seances/{}", id);
        SeanceResponseDTO response = seanceService.updateSeance(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteSeance(@PathVariable Long id) {
        log.info("DELETE /api/seances/{}", id);
        seanceService.deleteSeance(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Séance supprimée");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','FORMATEUR','APPRENANT')")
    public ResponseEntity<SeanceResponseDTO> getSeance(@PathVariable Long id) {
        log.info("GET /api/seances/{}", id);
        SeanceResponseDTO response = seanceService.getSeance(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/formation/{formationId}")
    @PreAuthorize("hasAnyRole('ADMIN','FORMATEUR')")
    public ResponseEntity<Page<SeanceResponseDTO>> getSeancesByFormation(@PathVariable Long formationId, Pageable pageable) {
        log.info("GET /api/seances/formation/{}", formationId);
        Page<SeanceResponseDTO> response = seanceService.getSeancesByFormation(formationId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/formateur/{formateurId}")
    @PreAuthorize("hasAnyRole('ADMIN','FORMATEUR')")
    public ResponseEntity<Page<SeanceResponseDTO>> getSeancesByFormateur(@PathVariable Long formateurId, Pageable pageable) {
        log.info("GET /api/seances/formateur/{}", formateurId);
        Page<SeanceResponseDTO> response = seanceService.getSeancesByFormateur(formateurId, pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/statut")
    @PreAuthorize("hasAnyRole('ADMIN','FORMATEUR')")
    public ResponseEntity<SeanceResponseDTO> updateStatut(@PathVariable Long id, @RequestBody Map<String, String> body) {
        log.info("PUT /api/seances/{}/statut", id);
        SeanceStatut statut = SeanceStatut.valueOf(body.get("statut"));
        SeanceResponseDTO response = seanceService.updateStatutSeance(id, statut);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/conflits")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ConflitDTO>> checkConflits(@RequestBody Map<String, Object> body) {
        log.info("POST /api/seances/conflits");
        Long salleId = body.get("salleId") != null ? Long.parseLong(body.get("salleId").toString()) : null;
        Long formateurId = body.get("formateurId") != null ? Long.parseLong(body.get("formateurId").toString()) : null;
        LocalDateTime debut = LocalDateTime.parse(body.get("dateHeureDebut").toString());
        LocalDateTime fin = LocalDateTime.parse(body.get("dateHeureFin").toString());
        Long seanceIdExclure = body.get("seanceIdExclure") != null ? Long.parseLong(body.get("seanceIdExclure").toString()) : null;

        List<ConflitDTO> conflits = seanceService.checkConflitsHoraires(salleId, formateurId, debut, fin, seanceIdExclure);
        return ResponseEntity.ok(conflits);
    }

    @GetMapping("/planning/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PlanningDTO> getPlanningAdmin(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        log.info("GET /api/planning/admin");
        PlanningDTO response = seanceService.getPlanningAdmin(debut, fin);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/planning/formateur/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','FORMATEUR')")
    public ResponseEntity<PlanningDTO> getPlanningFormateur(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        log.info("GET /api/planning/formateur/{}", id);
        PlanningDTO response = seanceService.getPlanningFormateur(id, debut, fin);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/planning/apprenant/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','APPRENANT')")
    public ResponseEntity<PlanningDTO> getPlanningApprenant(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        log.info("GET /api/planning/apprenant/{}", id);
        PlanningDTO response = seanceService.getPlanningApprenant(id, debut, fin);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/planning/mon-planning")
    @PreAuthorize("hasAnyRole('FORMATEUR','APPRENANT')")
    public ResponseEntity<PlanningDTO> getMonPlanning(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        log.info("GET /api/planning/mon-planning");
        // In a real implementation, extract userId from JWT token
        // For now, returning empty planning - this would need to be implemented based on your security setup
        return ResponseEntity.ok(PlanningDTO.builder().dateDebut(debut).dateFin(fin).build());
    }
}
