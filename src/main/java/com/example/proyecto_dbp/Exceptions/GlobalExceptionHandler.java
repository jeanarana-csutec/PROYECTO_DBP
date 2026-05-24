package com.example.proyecto_dbp.Exceptions;

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

    @ExceptionHandler(UserAlreadyExists.class)
    public ResponseEntity<ErrorResponseDTO> handleUserAlreadyExists(UserAlreadyExists e, HttpServletRequest r) {
        return build(HttpStatus.CONFLICT, "Usuario ya existe", e.getMessage(), r);
    }

    @ExceptionHandler(ResourceNotFound.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceNotFound(ResourceNotFound e, HttpServletRequest r) {
        return build(HttpStatus.NOT_FOUND, "Recurso no encontrado", e.getMessage(), r);
    }

    @ExceptionHandler(Unauthorized.class)
    public ResponseEntity<ErrorResponseDTO> handleUnauthorized(Unauthorized e, HttpServletRequest r) {
        return build(HttpStatus.UNAUTHORIZED, "No autorizado", e.getMessage(), r);
    }

    @ExceptionHandler(Forbidden.class)
    public ResponseEntity<ErrorResponseDTO> handleForbidden(Forbidden e, HttpServletRequest r) {
        return build(HttpStatus.FORBIDDEN, "Acceso denegado", e.getMessage(), r);
    }


    @ExceptionHandler(ProductoNoDisponible.class)
    public ResponseEntity<ErrorResponseDTO> handleProductoNoDisponible(ProductoNoDisponible e, HttpServletRequest r) {
        return build(HttpStatus.CONFLICT, "Producto no disponible", e.getMessage(), r);
    }

    @ExceptionHandler(TransaccionInvalida.class)
    public ResponseEntity<ErrorResponseDTO> handleTransaccionInvalida(TransaccionInvalida e, HttpServletRequest r) {
        return build(HttpStatus.BAD_REQUEST, "Transacción inválida", e.getMessage(), r);
    }

    @ExceptionHandler(MensajeNoPermitido.class)
    public ResponseEntity<ErrorResponseDTO> handleMensajeNoPermitido(MensajeNoPermitido e, HttpServletRequest r) {
        return build(HttpStatus.FORBIDDEN, "Mensaje no permitido", e.getMessage(), r);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDTO> handleBadCredentials(BadCredentialsException e, HttpServletRequest r) {
        return build(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas", e.getMessage(), r);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidation(MethodArgumentNotValidException e, HttpServletRequest r) {
        String detail = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .reduce("", (a, b) -> a + " | " + b);
        return build(HttpStatus.BAD_REQUEST, "Error de validación", detail, r);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleHttpMessageNotReadable(HttpMessageNotReadableException e, HttpServletRequest r) {
        return build(HttpStatus.BAD_REQUEST, "Mensaje no legible", "El cuerpo de la solicitud es inválido o está malformado", r);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneral(Exception e, HttpServletRequest r) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno", e.getMessage(), r);
    }
    @ExceptionHandler(InvalidOperation.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidOperation(InvalidOperation e, HttpServletRequest r) {
        return build(HttpStatus.BAD_REQUEST, "Operación inválida", e.getMessage(), r);
    }

    private ResponseEntity<ErrorResponseDTO> build(HttpStatus status, String error, String message, HttpServletRequest r) {
        return ResponseEntity.status(status)
                .body(new ErrorResponseDTO(status.value(), error, message, r.getRequestURI()));
    }
    @ExceptionHandler(DuplicateResource.class)
    public ResponseEntity<ErrorResponseDTO> handleDuplicateResource(DuplicateResource e, HttpServletRequest r) {
        return build(HttpStatus.CONFLICT, "Recurso duplicado", e.getMessage(), r);
    }
}