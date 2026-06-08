package com.formation.controller;

import com.formation.dto.StatistiquesApprenantDTO;
import com.formation.entity.User;
import com.formation.service.SuiviPedagogiqueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suivi")
@RequiredArgsConstructor
@Slf4j
public class SuiviPedagogiqueController {

    private final SuiviPedagogiqueService suiviPedagogiqueService;

    @GetMapping("/apprenant/{apprenantId}/formation/{formationId}")
    @PreAuthorize("hasAnyRole('ADMIN','FORMATEUR','APPRENANT')")
    public ResponseEntity<StatistiquesApprenantDTO> getStatistiquesApprenant(
            @PathVariable Long apprenantId,
            @PathVariable Long formationId) {
        log.info("GET /api/suivi/apprenant/{}/formation/{}", apprenantId, formationId);
        StatistiquesApprenantDTO response = suiviPedagogiqueService.getStatistiquesApprenant(apprenantId, formationId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/formation/{formationId}")
    @PreAuthorize("hasAnyRole('ADMIN','FORMATEUR')")
    public ResponseEntity<List<StatistiquesApprenantDTO>> getStatistiquesFormation(
            @PathVariable Long formationId) {
        log.info("GET /api/suivi/formation/{}", formationId);
        List<StatistiquesApprenantDTO> response = suiviPedagogiqueService.getStatistiquesFormation(formationId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/mon-suivi")
    @PreAuthorize("hasRole('APPRENANT')")
    public ResponseEntity<List<StatistiquesApprenantDTO>> getMonSuivi(Authentication authentication) {
        log.info("GET /api/suivi/mon-suivi");
        Long apprenantId = extractUserIdFromAuth(authentication);
        List<StatistiquesApprenantDTO> response = suiviPedagogiqueService.getProgressionApprenant(apprenantId);
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
