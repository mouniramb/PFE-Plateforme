package com.formation.controller;

import com.formation.dto.PaiementCreateDTO;
import com.formation.dto.PaiementResponseDTO;
import com.formation.dto.PaiementValidationDTO;
import com.formation.entity.User;
import com.formation.service.PaiementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/paiements")
@RequiredArgsConstructor
public class PaiementController {

    private final PaiementService paiementService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('FORMATEUR')")
    public ResponseEntity<PaiementResponseDTO> enregistrerPaiement(
            @Valid @RequestBody PaiementCreateDTO dto,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(paiementService.enregistrerPaiement(dto, currentUser.getId()));
    }

    @PutMapping("/{id}/valider")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaiementResponseDTO> validerPaiement(
            @PathVariable Long id,
            @Valid @RequestBody PaiementValidationDTO dto,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(paiementService.validerPaiement(id, dto, currentUser.getId()));
    }

    @PutMapping("/{id}/rejeter")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaiementResponseDTO> rejeterPaiement(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(paiementService.rejeterPaiement(id, body.get("raison")));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePaiement(@PathVariable Long id) {
        paiementService.deletePaiement(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/en-attente")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<PaiementResponseDTO>> getPaiementsEnAttente(Pageable pageable) {
        return ResponseEntity.ok(paiementService.getPaiementsEnAttente(pageable));
    }

    @GetMapping("/apprenant/{apprenantId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FORMATEUR') or hasRole('APPRENANT')")
    public ResponseEntity<List<PaiementResponseDTO>> getPaiementsApprenant(
            @PathVariable Long apprenantId,
            @AuthenticationPrincipal User currentUser) {
        if (currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_APPRENANT"))
                && !currentUser.getId().equals(apprenantId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(paiementService.getPaiementsApprenant(apprenantId));
    }

    @GetMapping("/apprenant/{apprenantId}/formation/{formationId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FORMATEUR') or hasRole('APPRENANT')")
    public ResponseEntity<List<PaiementResponseDTO>> getPaiementsApprenantFormation(
            @PathVariable Long apprenantId,
            @PathVariable Long formationId,
            @AuthenticationPrincipal User currentUser) {
        if (currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_APPRENANT"))
                && !currentUser.getId().equals(apprenantId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(paiementService.getPaiementsApprenantFormation(apprenantId, formationId));
    }

    @GetMapping("/mes-paiements")
    @PreAuthorize("hasRole('APPRENANT')")
    public ResponseEntity<List<PaiementResponseDTO>> getMesPaiements(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(paiementService.getPaiementsApprenant(currentUser.getId()));
    }

    @GetMapping("/formateur/{formateurId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FORMATEUR')")
    public ResponseEntity<List<PaiementResponseDTO>> getPaiementsFormateur(
            @PathVariable Long formateurId,
            @AuthenticationPrincipal User currentUser) {
        // Un formateur ne peut voir que ses propres données
        if (currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_FORMATEUR"))
                && !currentUser.getId().equals(formateurId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(paiementService.getPaiementsFormateur(formateurId));
    }
}
