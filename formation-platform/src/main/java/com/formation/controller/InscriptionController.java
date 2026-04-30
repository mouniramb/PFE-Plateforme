package com.formation.controller;

import com.formation.dto.InscriptionDTO;
import com.formation.dto.InscriptionRejectionDTO;
import com.formation.dto.InscriptionResponseDTO;
import com.formation.entity.Inscription;
import com.formation.entity.Role;
import com.formation.entity.User;
import com.formation.exception.UnauthorizedActionException;
import com.formation.service.FormationService;
import com.formation.service.InscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inscriptions")
@RequiredArgsConstructor
public class InscriptionController {

    private final InscriptionService inscriptionService;
    private final FormationService formationService;

    @PostMapping("/s-inscrire")
    @PreAuthorize("hasRole('APPRENANT')")
    public ResponseEntity<InscriptionResponseDTO> inscrireApprenant(
            @Valid @RequestBody InscriptionDTO dto,
            Authentication authentication
    ) {
        User apprenant = (User) authentication.getPrincipal();
        Inscription inscription = inscriptionService.inscrireApprenant(dto.getFormationId(), apprenant.getId());
        return ResponseEntity.ok(InscriptionResponseDTO.fromEntity(inscription));
    }

    @PutMapping("/{id}/accepter")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InscriptionResponseDTO> accepterInscription(@PathVariable Long id) {
        return ResponseEntity.ok(InscriptionResponseDTO.fromEntity(inscriptionService.accepterInscription(id)));
    }

    @PutMapping("/{id}/rejeter")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InscriptionResponseDTO> rejeterInscription(
            @PathVariable Long id,
            @Valid @RequestBody InscriptionRejectionDTO dto
    ) {
        return ResponseEntity.ok(InscriptionResponseDTO.fromEntity(inscriptionService.rejeterInscription(id, dto.getMotif())));
    }

    @GetMapping("/en-attente")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<InscriptionResponseDTO>> getInscriptionsEnAttente(
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(
                inscriptionService.getInscriptionsEnAttente(pageable)
                        .map(InscriptionResponseDTO::fromEntity)
        );
    }

    @GetMapping("/formation/{formationId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR')")
    public ResponseEntity<Page<InscriptionResponseDTO>> getInscriptionsParFormation(
            @PathVariable Long formationId,
            @PageableDefault(size = 10) Pageable pageable,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        if (user.getRole() == Role.FORMATEUR
                && !formationService.isFormateurAssignedToFormation(user.getId(), formationId)) {
            throw new UnauthorizedActionException("Action non autorisée");
        }

        return ResponseEntity.ok(
                inscriptionService.getInscriptionsAccepteesParFormation(formationId, pageable)
                        .map(InscriptionResponseDTO::fromEntity)
        );
    }

    @GetMapping("/apprenant/{apprenantId}")
    @PreAuthorize("hasAnyRole('APPRENANT', 'ADMIN')")
    public ResponseEntity<Page<InscriptionResponseDTO>> getInscriptionsApprenant(
            @PathVariable Long apprenantId,
            @PageableDefault(size = 10) Pageable pageable,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        if (user.getRole() == Role.APPRENANT && !user.getId().equals(apprenantId)) {
            throw new UnauthorizedActionException("Action non autorisée");
        }

        return ResponseEntity.ok(
                inscriptionService.getInscriptionsApprenant(apprenantId, pageable)
                        .map(InscriptionResponseDTO::fromEntity)
        );
    }

    @DeleteMapping("/{id}/annuler")
    @PreAuthorize("hasRole('APPRENANT')")
    public ResponseEntity<Void> annulerInscription(@PathVariable Long id, Authentication authentication) {
        User apprenant = (User) authentication.getPrincipal();
        inscriptionService.annulerInscription(id, apprenant.getId());
        return ResponseEntity.noContent().build();
    }
}
