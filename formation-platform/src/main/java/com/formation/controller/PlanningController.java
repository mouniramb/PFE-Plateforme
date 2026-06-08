package com.formation.controller;

import com.formation.dto.PlanningDTO;
import com.formation.entity.Role;
import com.formation.entity.User;
import com.formation.service.SeanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Contrôleur REST pour la gestion de la planification (Planning)
 */
@RestController
@RequestMapping("/api/planning")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
public class PlanningController {

    private final SeanceService seanceService;

    /**
     * GET - Récupérer la planification pour tous les administrateurs
     */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PlanningDTO> getPlanningAdmin(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        log.info("GET /api/planning/admin - debut: {}, fin: {}", debut, fin);
        PlanningDTO response = seanceService.getPlanningAdmin(debut, fin);
        return ResponseEntity.ok(response);
    }

    /**
     * GET - Récupérer la planification pour un formateur spécifique
     */
    @GetMapping("/formateur/{formateurId}")
    @PreAuthorize("hasAnyRole('ADMIN','FORMATEUR')")
    public ResponseEntity<PlanningDTO> getPlanningFormateur(
            @PathVariable Long formateurId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        log.info("GET /api/planning/formateur/{} - debut: {}, fin: {}", formateurId, debut, fin);
        PlanningDTO response = seanceService.getPlanningFormateur(formateurId, debut, fin);
        return ResponseEntity.ok(response);
    }

    /**
     * GET - Récupérer la planification pour un apprenant spécifique
     */
    @GetMapping("/apprenant/{apprenantId}")
    @PreAuthorize("hasAnyRole('ADMIN','APPRENANT')")
    public ResponseEntity<PlanningDTO> getPlanningApprenant(
            @PathVariable Long apprenantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        log.info("GET /api/planning/apprenant/{} - debut: {}, fin: {}", apprenantId, debut, fin);
        PlanningDTO response = seanceService.getPlanningApprenant(apprenantId, debut, fin);
        return ResponseEntity.ok(response);
    }

    /**
     * GET - Récupérer mon planning personnel (Formateur ou Apprenant)
     */
    @GetMapping("/mon-planning")
    @PreAuthorize("hasAnyRole('FORMATEUR','APPRENANT')")
    public ResponseEntity<PlanningDTO> getMonPlanning(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin,
            Authentication authentication) {
        log.info("GET /api/planning/mon-planning - debut: {}, fin: {}", debut, fin);
        User user = (User) authentication.getPrincipal();
        PlanningDTO response = user.getRole() == Role.FORMATEUR
                ? seanceService.getPlanningFormateur(user.getId(), debut, fin)
                : seanceService.getPlanningApprenant(user.getId(), debut, fin);
        return ResponseEntity.ok(response);
    }
}
