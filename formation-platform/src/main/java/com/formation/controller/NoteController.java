package com.formation.controller;

import com.formation.dto.NoteCreateDTO;
import com.formation.dto.NoteBulkDTO;
import com.formation.dto.NoteResponseDTO;
import com.formation.entity.User;
import com.formation.service.NoteService;
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
@RequestMapping("/api/notes")
@RequiredArgsConstructor
@Slf4j
public class NoteController {

    private final NoteService noteService;

    @PostMapping
    @PreAuthorize("hasRole('FORMATEUR')")
    public ResponseEntity<NoteResponseDTO> recordNote(
            @Valid @RequestBody NoteCreateDTO dto,
            Authentication authentication) {
        log.info("POST /api/notes");
        Long formateurId = extractUserIdFromAuth(authentication);
        NoteResponseDTO response = noteService.enregistrerNote(dto, formateurId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasRole('FORMATEUR')")
    public ResponseEntity<List<NoteResponseDTO>> recordNotesBulk(
            @Valid @RequestBody NoteBulkDTO dto,
            Authentication authentication) {
        log.info("POST /api/notes/bulk");
        Long formateurId = extractUserIdFromAuth(authentication);
        List<NoteResponseDTO> response = noteService.enregistrerNotesBulk(dto, formateurId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/seance/{seanceId}")
    @PreAuthorize("hasAnyRole('ADMIN','FORMATEUR')")
    public ResponseEntity<List<NoteResponseDTO>> getNotesBySeance(@PathVariable Long seanceId) {
        log.info("GET /api/notes/seance/{}", seanceId);
        List<NoteResponseDTO> response = noteService.getNotesParSeance(seanceId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/apprenant/{apprenantId}")
    @PreAuthorize("hasAnyRole('ADMIN','APPRENANT')")
    public ResponseEntity<List<NoteResponseDTO>> getNotesByApprenant(@PathVariable Long apprenantId) {
        log.info("GET /api/notes/apprenant/{}", apprenantId);
        List<NoteResponseDTO> response = noteService.getNotesParApprenant(apprenantId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/apprenant/{apprenantId}/formation/{formationId}")
    @PreAuthorize("hasAnyRole('ADMIN','FORMATEUR','APPRENANT')")
    public ResponseEntity<List<NoteResponseDTO>> getNotesByApprenantAndFormation(
            @PathVariable Long apprenantId,
            @PathVariable Long formationId) {
        log.info("GET /api/notes/apprenant/{}/formation/{}", apprenantId, formationId);
        List<NoteResponseDTO> response = noteService.getNotesParApprenantEtFormation(apprenantId, formationId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/apprenant/{apprenantId}/formation/{formationId}/moyenne")
    @PreAuthorize("hasAnyRole('ADMIN','FORMATEUR','APPRENANT')")
    public ResponseEntity<Map<String, Object>> getAverage(
            @PathVariable Long apprenantId,
            @PathVariable Long formationId) {
        log.info("GET /api/notes/apprenant/{}/formation/{}/moyenne", apprenantId, formationId);
        double moyenne = noteService.calculerMoyenne(apprenantId, formationId);
        
        List<NoteResponseDTO> notes = noteService.getNotesParApprenantEtFormation(apprenantId, formationId);

        Map<String, Object> response = new HashMap<>();
        response.put("moyenne", moyenne);
        response.put("totalNotes", notes.size());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/mes-notes")
    @PreAuthorize("hasRole('APPRENANT')")
    public ResponseEntity<List<NoteResponseDTO>> getMesNotes(Authentication authentication) {
        log.info("GET /api/notes/mes-notes");
        Long apprenantId = extractUserIdFromAuth(authentication);
        List<NoteResponseDTO> response = noteService.getNotesParApprenant(apprenantId);
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
