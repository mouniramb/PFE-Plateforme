package com.formation.controller;

import com.formation.dto.PaiementFormateurCreateDTO;
import com.formation.dto.PaiementFormateurResponseDTO;
import com.formation.entity.User;
import com.formation.service.PaiementFormateurService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/paiements-formateurs")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class PaiementFormateurController {

    private final PaiementFormateurService service;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('FORMATEUR')")
    public ResponseEntity<PaiementFormateurResponseDTO> enregistrer(
            @Valid @RequestBody PaiementFormateurCreateDTO dto,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(service.enregistrer(dto, currentUser.getId()));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PaiementFormateurResponseDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/formateur/{formateurId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FORMATEUR')")
    public ResponseEntity<List<PaiementFormateurResponseDTO>> getByFormateur(
            @PathVariable Long formateurId,
            @AuthenticationPrincipal User currentUser) {
        if (currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_FORMATEUR"))
                && !currentUser.getId().equals(formateurId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(service.getByFormateur(formateurId));
    }

    @GetMapping("/mes-revenus")
    @PreAuthorize("hasRole('FORMATEUR')")
    public ResponseEntity<List<PaiementFormateurResponseDTO>> getMesRevenus(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(service.getByFormateur(currentUser.getId()));
    }
}
