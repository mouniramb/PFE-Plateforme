package com.formation.controller;

import com.formation.dto.PresenceCreateDTO;
import com.formation.dto.PresenceBulkDTO;
import com.formation.dto.PresenceResponseDTO;
import com.formation.entity.User;
import com.formation.service.PresenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/presences")
@RequiredArgsConstructor
@Slf4j
public class PresenceController {

    private final PresenceService presenceService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','FORMATEUR')")
    public ResponseEntity<PresenceResponseDTO> registerPresence(
            @Valid @RequestBody PresenceCreateDTO dto,
            Authentication authentication) {
        log.info("POST /api/presences");
        // Extract userId from authentication (from JWT token in a real scenario)
        Long formateurId = extractUserIdFromAuth(authentication);
        PresenceResponseDTO response = presenceService.enregistrerPresence(dto, formateurId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasAnyRole('ADMIN','FORMATEUR')")
    public ResponseEntity<List<PresenceResponseDTO>> registerPresencesBulk(
            @Valid @RequestBody PresenceBulkDTO dto,
            Authentication authentication) {
        log.info("POST /api/presences/bulk");
        Long formateurId = extractUserIdFromAuth(authentication);
        List<PresenceResponseDTO> response = presenceService.enregistrerPresencesBulk(dto, formateurId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/seance/{seanceId}")
    @PreAuthorize("hasAnyRole('ADMIN','FORMATEUR')")
    public ResponseEntity<List<PresenceResponseDTO>> getPresencesBySeance(@PathVariable Long seanceId) {
        log.info("GET /api/presences/seance/{}", seanceId);
        List<PresenceResponseDTO> response = presenceService.getPresencesParSeance(seanceId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/apprenant/{apprenantId}")
    @PreAuthorize("hasAnyRole('ADMIN','APPRENANT','FORMATEUR')")
    public ResponseEntity<List<PresenceResponseDTO>> getPresencesByApprenant(@PathVariable Long apprenantId) {
        log.info("GET /api/presences/apprenant/{}", apprenantId);
        List<PresenceResponseDTO> response = presenceService.getPresencesParApprenant(apprenantId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/apprenant/{apprenantId}/formation/{formationId}")
    @PreAuthorize("hasAnyRole('ADMIN','FORMATEUR','APPRENANT')")
    public ResponseEntity<List<PresenceResponseDTO>> getPresencesByApprenantAndFormation(
            @PathVariable Long apprenantId,
            @PathVariable Long formationId) {
        log.info("GET /api/presences/apprenant/{}/formation/{}", apprenantId, formationId);
        List<PresenceResponseDTO> response = presenceService.getPresencesParApprenantEtFormation(apprenantId, formationId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/apprenant/{apprenantId}/formation/{formationId}/taux")
    @PreAuthorize("hasAnyRole('ADMIN','FORMATEUR','APPRENANT')")
    public ResponseEntity<Map<String, Object>> getPresenceRate(
            @PathVariable Long apprenantId,
            @PathVariable Long formationId) {
        log.info("GET /api/presences/apprenant/{}/formation/{}/taux", apprenantId, formationId);
        double tauxPresence = presenceService.calculerTauxPresence(apprenantId, formationId);
        
        List<PresenceResponseDTO> presences = presenceService.getPresencesParApprenantEtFormation(apprenantId, formationId);
        long seancesAssistees = presences.stream()
                .filter(p -> p.getStatut().name().equals("PRESENT") || p.getStatut().name().equals("RETARD"))
                .count();

        Map<String, Object> response = new HashMap<>();
        response.put("tauxPresence", tauxPresence);
        response.put("seancesAssistees", seancesAssistees);
        response.put("totalSeances", presences.stream().map(PresenceResponseDTO::getSeanceId).distinct().count());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/mes-presences")
    @PreAuthorize("hasRole('APPRENANT')")
    public ResponseEntity<List<PresenceResponseDTO>> getMesPresences(Authentication authentication) {
        log.info("GET /api/presences/mes-presences");
        Long apprenantId = extractUserIdFromAuth(authentication);
        List<PresenceResponseDTO> response = presenceService.getPresencesParApprenant(apprenantId);
        return ResponseEntity.ok(response);
    }

    private Long extractUserIdFromAuth(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalArgumentException("Utilisateur non authentifié");
        }
        User user = (User) authentication.getPrincipal();
        return user.getId();
    }
}
