package com.bignumber.web.exception;

import com.bignumber.web.dto.ProblemDetailResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static org.junit.jupiter.api.Assertions.*;

class ApiExceptionHandlerTest {

    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test
    @DisplayName("handleValidation: Nhánh field includeSteps -> INVALID_INCLUDE_STEPS")
    void testValidationIncludeStepsField() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "target");
        bindingResult.addError(new FieldError("target", "includeSteps", "includeSteps must be boolean"));

        MethodParameter parameter = new MethodParameter(this.getClass().getDeclaredMethods()[0], -1);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<ProblemDetailResponse> response = handler.handleValidation(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INVALID_INCLUDE_STEPS", response.getBody().code());
    }

    @Test
    @DisplayName("handleValidation: Nhánh field không xác định -> INVALID_REQUEST")
    void testValidationUnknownField() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "target");
        bindingResult.addError(new FieldError("target", "otherField", "Other error"));

        MethodParameter parameter = new MethodParameter(this.getClass().getDeclaredMethods()[0], -1);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<ProblemDetailResponse> response = handler.handleValidation(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INVALID_REQUEST", response.getBody().code());
    }

    @Test
    @DisplayName("handleValidation: Nhánh defaultMessage == null -> Dùng fallback message")
    void testValidationNullDefaultMessage() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "target");
        bindingResult.addError(new FieldError("target", "num1", null, false, null, null, null));

        MethodParameter parameter = new MethodParameter(this.getClass().getDeclaredMethods()[0], -1);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<ProblemDetailResponse> response = handler.handleValidation(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid request parameter", response.getBody().detail());
        assertEquals("INVALID_NUM1", response.getBody().code());
    }

    @Test
    @DisplayName("handleValidation: Nhánh num1 vượt độ dài -> NUM1_TOO_LONG")
    void testValidationNum1TooLong() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "target");
        bindingResult.addError(new FieldError("target", "num1", "num1 exceeds the maximum length"));

        MethodParameter parameter = new MethodParameter(this.getClass().getDeclaredMethods()[0], -1);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<ProblemDetailResponse> response = handler.handleValidation(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("NUM1_TOO_LONG", response.getBody().code());
    }

    @Test
    @DisplayName("handleValidation: Nhánh num2 vượt độ dài -> NUM2_TOO_LONG")
    void testValidationNum2TooLong() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "target");
        bindingResult.addError(new FieldError("target", "num2", "num2 exceeds the maximum length"));

        MethodParameter parameter = new MethodParameter(this.getClass().getDeclaredMethods()[0], -1);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<ProblemDetailResponse> response = handler.handleValidation(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("NUM2_TOO_LONG", response.getBody().code());
    }

    @Test
    @DisplayName("handleValidation: Nhánh num2 hợp lệ về độ dài nhưng sai định dạng -> INVALID_NUM2")
    void testValidationNum2Invalid() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "target");
        bindingResult.addError(new FieldError("target", "num2", "num2 must contain digits only"));

        MethodParameter parameter = new MethodParameter(this.getClass().getDeclaredMethods()[0], -1);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<ProblemDetailResponse> response = handler.handleValidation(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("INVALID_NUM2", response.getBody().code());
    }

    @Test
    @DisplayName("handleIllegalArgument: Bắt IllegalArgumentException -> INVALID_REQUEST")
    void testHandleIllegalArgument() {
        IllegalArgumentException ex = new IllegalArgumentException("Tham số bị sai");
        ResponseEntity<ProblemDetailResponse> response = handler.handleIllegalArgument(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Tham số bị sai", response.getBody().detail());
        assertEquals("INVALID_REQUEST", response.getBody().code());
    }

    @Test
    @DisplayName("handleInternalError: Bắt lỗi Exception bất ngờ -> HTTP 500 INTERNAL_ERROR")
    void testHandleInternalError() {
        RuntimeException ex = new RuntimeException("Lỗi hệ thống bất ngờ");
        ResponseEntity<ProblemDetailResponse> response = handler.handleInternalError(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INTERNAL_ERROR", response.getBody().code());
        assertEquals("An unexpected error occurred", response.getBody().detail());
    }

    @Test
    @DisplayName("handleMalformedJson: Bắt HttpMessageNotReadableException -> HTTP 400 MALFORMED_REQUEST")
    void testHandleMalformedJson() {
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("JSON không hợp lệ");
        ResponseEntity<ProblemDetailResponse> response = handler.handleMalformedJson(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("MALFORMED_REQUEST", response.getBody().code());
    }

    @Test
    @DisplayName("handleMethodNotSupported: Bắt HttpRequestMethodNotSupportedException -> HTTP 405 METHOD_NOT_ALLOWED")
    void testHandleMethodNotSupported() {
        HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException("DELETE");
        ResponseEntity<ProblemDetailResponse> response = handler.handleMethodNotSupported(ex);

        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
        assertEquals("METHOD_NOT_ALLOWED", response.getBody().code());
    }

    @Test
    @DisplayName("handleNoResourceFound: Bắt NoResourceFoundException -> HTTP 404 RESOURCE_NOT_FOUND")
    void testHandleNoResourceFound() {
        NoResourceFoundException ex = new NoResourceFoundException(org.springframework.http.HttpMethod.GET, "/unknown");
        ResponseEntity<ProblemDetailResponse> response = handler.handleNoResourceFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("RESOURCE_NOT_FOUND", response.getBody().code());
    }
}
