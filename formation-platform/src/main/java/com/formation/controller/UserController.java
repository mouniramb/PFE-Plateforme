package com.formation.controller;

import com.formation.dto.CreateFormateurRequest;
import com.formation.dto.UserResponse;
import com.formation.entity.User;
import com.formation.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ── Profil utilisateur connecté ────────────────────────────────────────

    /**
     * GET /api/users/me
     * Retourne le profil de l'utilisateur connecté
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(UserResponse.fromUser(user));
    }

    // ── CRUD Formateurs (ADMIN uniquement) ─────────────────────────────────

    /**
     * GET /api/users/formateurs
     * Lister tous les formateurs
     */
    @GetMapping("/formateurs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllFormateurs() {
        return ResponseEntity.ok(userService.getAllFormateurs());
    }

    /**
     * GET /api/users/formateurs/{id}
     * Récupérer un formateur par id
     */
    @GetMapping("/formateurs/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getFormateurById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getFormateurResponseById(id));
    }

    /**
     * POST /api/users/formateurs
     * Créer un formateur (génère mdp + envoie email)
     */
    @PostMapping("/formateurs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createFormateur(@Valid @RequestBody CreateFormateurRequest request) {
        try {
            UserResponse created = userService.createFormateur(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * PUT /api/users/formateurs/{id}
     * Modifier un formateur
     */
    @PutMapping("/formateurs/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateFormateur(
            @PathVariable Long id,
            @Valid @RequestBody CreateFormateurRequest request) {
        try {
            UserResponse updated = userService.updateFormateur(id, request);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            // Erreur métier : données invalides (ex: email déjà utilisé)
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", e.getMessage()));
        } catch (RuntimeException e) {
            // Erreur générale : ex: formateur non trouvé
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * DELETE /api/users/formateurs/{id}
     * Supprimer un formateur
     */
    @DeleteMapping("/formateurs/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteFormateur(@PathVariable Long id) {
        try {
            userService.deleteFormateur(id);
            return ResponseEntity.ok(Map.of("message", "Formateur supprimé avec succès."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }
}