package com.bignumber.web.exception;

import com.bignumber.web.dto.ProblemDetailResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.logging.Level;
import java.util.logging.Logger;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger LOGGER = Logger.getLogger(ApiExceptionHandler.class.getName());
    private static final String PROBLEM_BASE_URL = "https://my-big-number.example/problems/invalid-request";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetailResponse> handleValidation(MethodArgumentNotValidException ex) {
        FieldError error = ex.getBindingResult().getFieldErrors().stream()
                .filter(fe -> fe.getDefaultMessage() != null && fe.getDefaultMessage().contains("must not be blank"))
                .findFirst()
                .orElse(ex.getBindingResult().getFieldErrors().get(0));
        String field = error.getField();
        String defaultMessage = error.getDefaultMessage();

        boolean isTooLong = defaultMessage != null && defaultMessage.contains("maximum length");
        String code;
        if ("num1".equals(field)) {
            code = isTooLong ? "NUM1_TOO_LONG" : "INVALID_NUM1";
        } else if ("num2".equals(field)) {
            code = isTooLong ? "NUM2_TOO_LONG" : "INVALID_NUM2";
        } else if ("includeSteps".equals(field)) {
            code = "INVALID_INCLUDE_STEPS";
        } else {
            code = "INVALID_REQUEST";
        }

        ProblemDetailResponse problem = new ProblemDetailResponse(
                PROBLEM_BASE_URL,
                "Invalid request",
                HttpStatus.BAD_REQUEST.value(),
                defaultMessage != null ? defaultMessage : "Invalid request parameter",
                code
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetailResponse> handleMalformedJson(HttpMessageNotReadableException ex) {
        ProblemDetailResponse problem = new ProblemDetailResponse(
                PROBLEM_BASE_URL,
                "Invalid request",
                HttpStatus.BAD_REQUEST.value(),
                "Request body is invalid",
                "MALFORMED_REQUEST"
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ProblemDetailResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        ProblemDetailResponse problem = new ProblemDetailResponse(
                PROBLEM_BASE_URL,
                "Method Not Allowed",
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                "HTTP method is not supported",
                "METHOD_NOT_ALLOWED"
        );
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(problem);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetailResponse> handleIllegalArgument(IllegalArgumentException ex) {
        ProblemDetailResponse problem = new ProblemDetailResponse(
                PROBLEM_BASE_URL,
                "Invalid request",
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                "INVALID_REQUEST"
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ProblemDetailResponse> handleNoResourceFound(NoResourceFoundException ex) {
        ProblemDetailResponse problem = new ProblemDetailResponse(
                "https://my-big-number.example/problems/not-found",
                "Not Found",
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                "RESOURCE_NOT_FOUND"
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetailResponse> handleInternalError(Exception ex) {
        LOGGER.log(Level.SEVERE, "Unexpected error occurred during calculation", ex);
        ProblemDetailResponse problem = new ProblemDetailResponse(
                "https://my-big-number.example/problems/internal-error",
                "Internal Server Error",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred",
                "INTERNAL_ERROR"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
    }
}
