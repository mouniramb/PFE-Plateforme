package com.formation.controller;

import com.formation.dto.FormationConfigDTO;
import com.formation.dto.FormationCreateDTO;
import com.formation.dto.FormationResponseDTO;
import com.formation.dto.FormationUpdateDTO;
import com.formation.entity.Formation;
import com.formation.entity.FormationStatut;
import com.formation.entity.User;
import com.formation.service.FormationService;
import com.formation.service.PaiementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/formations")
@RequiredArgsConstructor
public class FormationController {

    private final FormationService formationService;
    private final PaiementService paiementService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FormationResponseDTO> createFormation(@Valid @RequestBody FormationCreateDTO dto) {
        Formation created = formationService.createFormation(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(FormationResponseDTO.fromEntity(created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FormationResponseDTO> updateFormation(
            @PathVariable Long id,
            @Valid @RequestBody FormationUpdateDTO dto
    ) {
        Formation updated = formationService.updateFormation(id, dto);
        return ResponseEntity.ok(FormationResponseDTO.fromEntity(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteFormation(@PathVariable Long id) {
        formationService.deleteFormation(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<FormationResponseDTO>> getAllFormations(
            @RequestParam(required = false) FormationStatut statut,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<Formation> page = statut == null
                ? formationService.getAllFormations(pageable)
                : formationService.getFormationsByStatut(statut, pageable);

        return ResponseEntity.ok(page.map(FormationResponseDTO::fromEntity));
    }

    @GetMapping("/catalogue")
    public ResponseEntity<Page<FormationResponseDTO>> getCatalogueFormations(
            @RequestParam(required = false) FormationStatut statut,
            @RequestParam(required = false) LocalDate dateDebutMin,
            @RequestParam(required = false) LocalDate dateFinMax,
            @RequestParam(required = false) BigDecimal prixMin,
            @RequestParam(required = false) BigDecimal prixMax,
            @RequestParam(required = false) Integer capaciteMin,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<Formation> page = formationService.getCatalogueFormations(
                statut,
                dateDebutMin,
                dateFinMax,
                prixMin,
                prixMax,
                capaciteMin,
                pageable
        );

        return ResponseEntity.ok(page.map(FormationResponseDTO::fromEntity));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FormationResponseDTO> getFormation(@PathVariable Long id) {
        return ResponseEntity.ok(FormationResponseDTO.fromEntity(formationService.getFormation(id)));
    }

    @GetMapping("/formateur/{formateurId}")
    @PreAuthorize("hasAnyRole('FORMATEUR', 'ADMIN')")
    public ResponseEntity<Page<FormationResponseDTO>> getFormationsByFormateur(
            @PathVariable Long formateurId,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(
                formationService.getFormationsByFormateur(formateurId, pageable)
                        .map(FormationResponseDTO::fromEntity)
        );
    }

    @GetMapping("/search")
    public ResponseEntity<Page<FormationResponseDTO>> searchFormations(
            @RequestParam String keyword,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(
                formationService.searchFormations(keyword, pageable)
                        .map(FormationResponseDTO::fromEntity)
        );
    }

    @GetMapping("/{id}/config")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FORMATEUR') or hasRole('APPRENANT')")
    public ResponseEntity<FormationConfigDTO> getFormationConfig(@PathVariable Long id) {
        return ResponseEntity.ok(paiementService.getFormationConfig(id));
    }

    @PutMapping("/{id}/config")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FormationConfigDTO> saveFormationConfig(
            @PathVariable Long id,
            @RequestBody FormationConfigDTO dto) {
        return ResponseEntity.ok(paiementService.saveFormationConfig(id, dto));
    }
}
