package com.formation.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(FormationNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleFormationNotFound(FormationNotFoundException ex) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(InscriptionNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleInscriptionNotFound(InscriptionNotFoundException ex) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // Sprint 3 - Salles
    @ExceptionHandler(SalleNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleSalleNotFound(SalleNotFoundException ex) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(CannotDeleteSalleException.class)
    public ResponseEntity<Map<String, Object>> handleCannotDeleteSalle(CannotDeleteSalleException ex) {
        return buildError(HttpStatus.CONFLICT, ex.getMessage());
    }

    // Sprint 3 - Seances
    @ExceptionHandler(SeanceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleSeanceNotFound(SeanceNotFoundException ex) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(CannotDeleteSeanceException.class)
    public ResponseEntity<Map<String, Object>> handleCannotDeleteSeance(CannotDeleteSeanceException ex) {
        return buildError(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(InvalidSeanceDatesException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidSeanceDates(InvalidSeanceDatesException ex) {
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(ConflitHoraireException.class)
    public ResponseEntity<Map<String, Object>> handleConflitHoraire(ConflitHoraireException ex) {
        return buildError(HttpStatus.CONFLICT, ex.getMessage());
    }

    // Sprint 3 - Presences
    @ExceptionHandler(PresenceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlePresenceNotFound(PresenceNotFoundException ex) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // Sprint 3 - Notes
    @ExceptionHandler(NoteNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoteNotFound(NoteNotFoundException ex) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(NoteInvalideException.class)
    public ResponseEntity<Map<String, Object>> handleNoteInvalide(NoteInvalideException ex) {
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // Sprint 3 - Permissions
    @ExceptionHandler(FormateurNonResponsableException.class)
    public ResponseEntity<Map<String, Object>> handleFormateurNonResponsable(FormateurNonResponsableException ex) {
        return buildError(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(ApprenantNonInscritException.class)
    public ResponseEntity<Map<String, Object>> handleApprenantNonInscrit(ApprenantNonInscritException ex) {
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler({
            InscriptionAlreadyExistsException.class,
            InvalidFormationDatesException.class,
            InvalidFormationDataException.class,
            NoAvailablePlacesException.class,
            CannotDeleteFormationException.class
    })
    public ResponseEntity<Map<String, Object>> handleBusinessConflict(RuntimeException ex) {
        return buildError(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedActionException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorizedAction(UnauthorizedActionException ex) {
        return buildError(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    // Sprint 4 - Gestion financière
    @ExceptionHandler(PaiementNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlePaiementNotFound(PaiementNotFoundException ex) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(MontantInvalidException.class)
    public ResponseEntity<Map<String, Object>> handleMontantInvalid(MontantInvalidException ex) {
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalState(IllegalStateException ex) {
        return buildError(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(err -> err.getField() + " : " + err.getDefaultMessage())
                .orElse("Requête invalide");
        return buildError(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraint(ConstraintViolationException ex) {
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        return buildError(HttpStatus.FORBIDDEN, "Action non autorisée");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        log.error("Erreur inattendue", ex);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur interne du serveur");
    }

    private ResponseEntity<Map<String, Object>> buildError(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}
