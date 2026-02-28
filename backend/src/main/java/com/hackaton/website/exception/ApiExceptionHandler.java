package com.hackaton.website.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(OutOfNyStateException.class)
    public ResponseEntity<ApiError> outOfNy(OutOfNyStateException e, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, e.getMessage(), req);
    }

    @ExceptionHandler(TaxRateNotFoundException.class)
    public ResponseEntity<ApiError> taxNotFound(TaxRateNotFoundException e, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, e.getMessage(), req);
    }

    @ExceptionHandler({GeocodingException.class, CsvImportException.class})
    public ResponseEntity<ApiError> serviceErrors(RuntimeException e, HttpServletRequest req) {
        return build(HttpStatus.BAD_GATEWAY, e.getMessage(), req);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> badRequest(IllegalArgumentException e, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, e.getMessage(), req);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> fallback(Exception e, HttpServletRequest req) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), req);
    }

    private ResponseEntity<ApiError> build(HttpStatus st, String msg, HttpServletRequest req) {
        ApiError body = new ApiError(
                Instant.now(),
                st.value(),
                st.getReasonPhrase(),
                msg,
                req.getRequestURI()
        );
        return ResponseEntity.status(st).body(body);
    }
}