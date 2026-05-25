package com.example.proyecto_dbp.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException e, HttpServletRequest r) {
        return build(HttpStatus.CONFLICT, "Usuario ya existe", e.getMessage(), r);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException e, HttpServletRequest r) {
        return build(HttpStatus.NOT_FOUND, "Recurso no encontrado", e.getMessage(), r);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException e, HttpServletRequest r) {
        return build(HttpStatus.UNAUTHORIZED, "No autorizado", e.getMessage(), r);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(ForbiddenException e, HttpServletRequest r) {
        return build(HttpStatus.FORBIDDEN, "Acceso denegado", e.getMessage(), r);
    }


    @ExceptionHandler(ProductNotAvailableException.class)
    public ResponseEntity<ErrorResponse> handleProductNotAvailableException(ProductNotAvailableException e, HttpServletRequest r) {
        return build(HttpStatus.CONFLICT, "Product no disponible", e.getMessage(), r);
    }

    @ExceptionHandler(TransactionInvalidException.class)
    public ResponseEntity<ErrorResponse> handleTransactionInvalidException(TransactionInvalidException e, HttpServletRequest r) {
        return build(HttpStatus.BAD_REQUEST, "TransacciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n invÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡lida", e.getMessage(), r);
    }

    @ExceptionHandler(MessageNotAllowedException.class)
    public ResponseEntity<ErrorResponse> handleMessageNotAllowedException(MessageNotAllowedException e, HttpServletRequest r) {
        return build(HttpStatus.FORBIDDEN, "Message no permitido", e.getMessage(), r);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException e, HttpServletRequest r) {
        return build(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas", e.getMessage(), r);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e, HttpServletRequest r) {
        String detail = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .reduce("", (a, b) -> a + " | " + b);
        return build(HttpStatus.BAD_REQUEST, "Error de validaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n", detail, r);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException e, HttpServletRequest r) {
        return build(HttpStatus.BAD_REQUEST, "Message no legible", "El cuerpo de la solicitud es invÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡lido o estÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡ malformado", r);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception e, HttpServletRequest r) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno", e.getMessage(), r);
    }
    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOperationException(InvalidOperationException e, HttpServletRequest r) {
        return build(HttpStatus.BAD_REQUEST, "OperaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n invÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡lida", e.getMessage(), r);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String error, String message, HttpServletRequest r) {
        return ResponseEntity.status(status)
                .body(new ErrorResponse(status.value(), error, message, r.getRequestURI()));
    }
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResourceException(DuplicateResourceException e, HttpServletRequest r) {
        return build(HttpStatus.CONFLICT, "Recurso duplicado", e.getMessage(), r);
    }
}