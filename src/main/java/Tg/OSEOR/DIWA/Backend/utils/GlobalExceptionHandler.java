package Tg.OSEOR.DIWA.Backend.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

/**
 * Gestionnaire global des exceptions HTTP.
 *
 * Ordre de résolution (du plus spécifique au plus général) :
 *  1. ResponseStatusException  → status + message du service
 *  2. MethodArgumentNotValidException → 400 validation
 *  3. AccessDeniedException    → 401 (anonyme) ou 403 (authentifié sans droit)
 *  4. RuntimeException         → 500 (fallback)
 *  5. Exception                → 500 (fallback ultime)
 *
 * Correctifs apportés :
 *  - Avant cette version, RuntimeException attrapait aussi ResponseStatusException
 *    et AccessDeniedException → tout retournait 500 quelle que soit l'exception.
 *  - Ajout de handlers spécifiques pour ces deux types.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ─── 1. Validation @Valid ─────────────────────────────────────────────

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Object>> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request) {

        String message = ex.getBindingResult().getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .findFirst()
                .orElse("Erreur de validation");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new BaseResponse<>(400, message, null));
    }

    // ─── 2. ResponseStatusException (404, 400, 409, etc.) ────────────────
    //
    // DOIT être déclaré AVANT RuntimeException, car ResponseStatusException
    // étend NestedRuntimeException qui étend RuntimeException.
    // Sans ce handler, tous les 404/400/409/... levés dans les services
    // seraient interceptés par handleRuntimeException et retourneraient 500.

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<BaseResponse<Object>> handleResponseStatusException(
            ResponseStatusException ex, WebRequest request) {

        int statusCode = ex.getStatusCode().value();
        String reason = ex.getReason() != null ? ex.getReason() : ex.getMessage();
        return ResponseEntity.status(ex.getStatusCode())
                .body(new BaseResponse<>(statusCode, reason, null));
    }

    // ─── 3. AccessDeniedException (sécurité méthode @PreAuthorize) ───────
    //
    // Depuis Spring Security 6.3, @PreAuthorize lève AuthorizationDeniedException
    // qui étend AccessDeniedException.
    //
    // DOIT être déclaré AVANT RuntimeException pour ne pas retourner 500.
    //
    // Différenciation anonyme / authentifié :
    //  - Anonyme (AnonymousAuthenticationToken ou null) → 401 UNAUTHORIZED
    //  - Authentifié mais rôle insuffisant              → 403 FORBIDDEN

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<BaseResponse<Object>> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAnonymous = (auth == null || auth instanceof AnonymousAuthenticationToken);

        if (isAnonymous) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new BaseResponse<>(401, "Non authentifié : " + ex.getMessage(), null));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new BaseResponse<>(403, "Accès refusé : " + ex.getMessage(), null));
    }

    // ─── 4. RuntimeException générique ───────────────────────────────────
    //
    // Fallback pour les RuntimeException non gérées ci-dessus.
    // AccessDeniedException et ResponseStatusException ont déjà leur handler.

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<BaseResponse<Object>> handleRuntimeException(
            RuntimeException ex, WebRequest request) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new BaseResponse<>(500, ex.getMessage(), null));
    }

    // ─── 5. Exception générique (fallback ultime) ─────────────────────────

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Object>> handleGlobalException(
            Exception ex, WebRequest request) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new BaseResponse<>(500, "Erreur serveur interne : " + ex.getMessage(), null));
    }
}
