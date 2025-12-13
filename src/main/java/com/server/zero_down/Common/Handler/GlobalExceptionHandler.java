package com.server.zero_down.Common.Handler;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    // ------------------ 1. Handle User Not Found ------------------
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponseHandler> handleUserNotFound(UsernameNotFoundException ex) {
        ErrorResponseHandler error = new ErrorResponseHandler(
                HttpStatus.NOT_FOUND.name(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // ------------------ 2. Handle Bad Credentials (login failures) ------------------
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseHandler> handleBadCredentials(BadCredentialsException ex) {
        ErrorResponseHandler error = new ErrorResponseHandler(
                HttpStatus.UNAUTHORIZED.name(),
                "Invalid username or password",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    // ------------------ 3. JWT Signature / Tampering ------------------
    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ErrorResponseHandler> handleInvalidJwt(SignatureException ex) {
        ErrorResponseHandler error = new ErrorResponseHandler(
                HttpStatus.UNAUTHORIZED.name(),
                "Invalid or tampered JWT token",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    // ------------------ 4. JWT Expired ------------------
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponseHandler> handleExpiredJwt(ExpiredJwtException ex) {
        ErrorResponseHandler error = new ErrorResponseHandler(
                HttpStatus.UNAUTHORIZED.name(),
                "JWT token expired",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    // ------------------ 5. Validation Errors ------------------
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseHandler> handleValidationErrors(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();

        ErrorResponseHandler error = new ErrorResponseHandler(
                HttpStatus.BAD_REQUEST.name(),
                message,
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // ------------------ 6. Database Integrity Errors ------------------
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseHandler> handleDataIntegrityError(DataIntegrityViolationException ex) {
        ErrorResponseHandler error = new ErrorResponseHandler(
                HttpStatus.BAD_REQUEST.name(),
                "Database constraint violation: " + ex.getMostSpecificCause().getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // ------------------ 7. IllegalArgumentException ------------------
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseHandler> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorResponseHandler error = new ErrorResponseHandler(
                HttpStatus.BAD_REQUEST.name(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // ------------------ 8. Catch-All Handler ------------------
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseHandler> handleGeneralError(Exception ex) {
        ErrorResponseHandler error = new ErrorResponseHandler(
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                "Something went wrong: " + ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
