package com.cheikh.commun.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    // --- Gestion des exceptions personnalisées ---
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionSchema> handleEntityNotFound(EntityNotFoundException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionSchema> handleBadRequest(BadRequestException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<ExceptionSchema> handleInternalServerError(InternalServerErrorException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request.getRequestURI());
    }

    // --- Gestion des erreurs de validation ---
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionSchema> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getAllErrors()
                .stream()
                .map(error -> (error instanceof FieldError fe)
                        ? fe.getField() + ": " + fe.getDefaultMessage()
                        : error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return buildResponse(HttpStatus.BAD_REQUEST, message, request.getRequestURI());
    }

    // --- Gestion des erreurs JSON mal formé ---
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionSchema> handleMalformedJson(HttpMessageNotReadableException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Invalid JSON input: " + ex.getMostSpecificCause().getMessage(), request.getRequestURI());
    }

    // --- Erreur de type de paramètre ---
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ExceptionSchema> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String message = "Invalid value for parameter '" + ex.getName() + "'. Expected type: " + ex.getRequiredType().getSimpleName();
        return buildResponse(HttpStatus.BAD_REQUEST, message, request.getRequestURI());
    }

    // --- Gestion des routes inexistantes ---
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ExceptionSchema> handleNotFound(NoHandlerFoundException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, "Endpoint not found: " + request.getRequestURI(), request.getRequestURI());
    }

    // --- Méthode HTTP non supportée ---
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ExceptionSchema> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.METHOD_NOT_ALLOWED, "Method not allowed: " + ex.getMethod(), request.getRequestURI());
    }

    // --- Catch-all fallback ---
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionSchema> handleAllExceptions(Exception ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error: " + ex.getMessage(), request.getRequestURI());
    }

    // --- Méthode utilitaire ---
    private ResponseEntity<ExceptionSchema> buildResponse(HttpStatus status, String message, String path) {
        ExceptionSchema error = new ExceptionSchema(status.value(), message, path);
        return new ResponseEntity<>(error, status);
    }
}
