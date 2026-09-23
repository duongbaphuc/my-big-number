package com.bignumber.web.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DtoTest {

    @Test
    @DisplayName("Kiểm thử toàn bộ getters, setters và constructors của CalculationRequest")
    void testCalculationRequest() {
        CalculationRequest request = new CalculationRequest();
        assertEquals("", request.getNum1());
        assertEquals("", request.getNum2());

        request.setNum1("12345");
        request.setNum2("67890");

        assertEquals("12345", request.getNum1());
        assertEquals("67890", request.getNum2());
    }

    @Test
    @DisplayName("Kiểm thử toàn bộ constructors, getters, setters của CalculationApiRequest")
    void testCalculationApiRequest() {
        CalculationApiRequest reqDefault = new CalculationApiRequest();
        assertNull(reqDefault.getNum1());
        assertNull(reqDefault.getNum2());
        assertFalse(reqDefault.getIncludeSteps());

        reqDefault.setNum1("100");
        reqDefault.setNum2("200");
        reqDefault.setIncludeSteps(true);

        assertEquals("100", reqDefault.getNum1());
        assertEquals("200", reqDefault.getNum2());
        assertTrue(reqDefault.getIncludeSteps());

        // Parameterized constructor with null includeSteps -> false
        CalculationApiRequest reqNullSteps = new CalculationApiRequest("11", "22", null);
        assertEquals("11", reqNullSteps.getNum1());
        assertEquals("22", reqNullSteps.getNum2());
        assertFalse(reqNullSteps.getIncludeSteps());

        // Parameterized constructor with true includeSteps
        CalculationApiRequest reqTrueSteps = new CalculationApiRequest("33", "44", true);
        assertEquals("33", reqTrueSteps.getNum1());
        assertEquals("44", reqTrueSteps.getNum2());
        assertTrue(reqTrueSteps.getIncludeSteps());
    }

    @Test
    @DisplayName("Kiểm thử các record DTOs: CalculationApiResponse, CalculationStepDto, ProblemDetailResponse")
    void testRecordDtos() {
        CalculationStepDto step = new CalculationStepDto(1, "Step 1", "3", 0);
        assertEquals(1, step.stepNumber());
        assertEquals("Step 1", step.description());
        assertEquals("3", step.intermediateResult());
        assertEquals(0, step.carry());

        CalculationApiResponse response = new CalculationApiResponse("123", List.of(step));
        assertEquals("123", response.sum());
        assertEquals(1, response.steps().size());
        assertEquals(step, response.steps().get(0));

        ProblemDetailResponse problem = new ProblemDetailResponse(
                "https://example.com/prob",
                "Bad Request",
                400,
                "Invalid field",
                "INVALID_FIELD"
        );
        assertEquals("https://example.com/prob", problem.type());
        assertEquals("Bad Request", problem.title());
        assertEquals(400, problem.status());
        assertEquals("Invalid field", problem.detail());
        assertEquals("INVALID_FIELD", problem.code());
    }
}
