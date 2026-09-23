package com.bignumber.web.controller;

import com.bignumber.web.BigNumberWebApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = BigNumberWebApplication.class)
@AutoConfigureMockMvc
class BigNumberRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("API-001: Phép cộng hai số hợp lệ không cần bước tính (Fast Path)")
    void testValidCalculationFastPath() throws Exception {
        String json = """
            {
              "num1": "1234",
              "num2": "897",
              "includeSteps": false
            }
            """;

        mockMvc.perform(post("/api/calculations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.sum").value("2131"))
                .andExpect(jsonPath("$.steps").isArray())
                .andExpect(jsonPath("$.steps", hasSize(0)));
    }

    @Test
    @DisplayName("API-006: Phép cộng có yêu cầu chi tiết từng bước tính toán")
    void testValidCalculationWithSteps() throws Exception {
        String json = """
            {
              "num1": "1234",
              "num2": "897",
              "includeSteps": true
            }
            """;

        mockMvc.perform(post("/api/calculations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.sum").value("2131"))
                .andExpect(jsonPath("$.steps", hasSize(4)))
                .andExpect(jsonPath("$.steps[0].stepNumber").value(1))
                .andExpect(jsonPath("$.steps[0].description").isNotEmpty())
                .andExpect(jsonPath("$.steps[0].intermediateResult").value("1"))
                .andExpect(jsonPath("$.steps[0].carry").value(1));
    }

    @Test
    @DisplayName("API-009: Trường num1 bị rỗng -> HTTP 400 và INVALID_NUM1")
    void testBlankNum1() throws Exception {
        String json = """
            {
              "num1": "",
              "num2": "897",
              "includeSteps": false
            }
            """;

        mockMvc.perform(post("/api/calculations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("INVALID_NUM1"))
                .andExpect(jsonPath("$.detail").value("num1 must not be blank"));
    }

    @Test
    @DisplayName("API-011: Trường num1 chứa ký tự không phải số -> HTTP 400 và INVALID_NUM1")
    void testInvalidDigitNum1() throws Exception {
        String json = """
            {
              "num1": "12a4",
              "num2": "897"
            }
            """;

        mockMvc.perform(post("/api/calculations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("INVALID_NUM1"))
                .andExpect(jsonPath("$.detail").value("num1 must contain digits only"));
    }

    @Test
    @DisplayName("API-014: JSON sai cú pháp hoặc rác -> HTTP 400 và MALFORMED_REQUEST")
    void testMalformedJson() throws Exception {
        String json = "not-a-valid-json";

        mockMvc.perform(post("/api/calculations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("MALFORMED_REQUEST"));
    }

    @Test
    @DisplayName("API-015: Sử dụng method GET không hỗ trợ -> HTTP 405 và METHOD_NOT_ALLOWED")
    void testUnsupportedMethod() throws Exception {
        mockMvc.perform(get("/api/calculations"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status").value(405))
                .andExpect(jsonPath("$.code").value("METHOD_NOT_ALLOWED"));
    }

    @Test
    @DisplayName("API-002: Hai số đều bằng 0 -> HTTP 200 và sum=0")
    void testZeroValues() throws Exception {
        String json = """
            {
              "num1": "0",
              "num2": "0",
              "includeSteps": false
            }
            """;

        mockMvc.perform(post("/api/calculations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sum").value("0"))
                .andExpect(jsonPath("$.steps", hasSize(0)));
    }

    @Test
    @DisplayName("API-003: Phép tính có nhớ qua nhiều hàng -> HTTP 200 và sum=1000")
    void testCarryAcrossDigits() throws Exception {
        String json = """
            {
              "num1": "999",
              "num2": "1",
              "includeSteps": false
            }
            """;

        mockMvc.perform(post("/api/calculations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sum").value("1000"))
                .andExpect(jsonPath("$.steps", hasSize(0)));
    }

    @Test
    @DisplayName("API-004: Hai số lệch độ dài nhiều -> HTTP 200 và sum=1000000")
    void testUnequalLengths() throws Exception {
        String json = """
            {
              "num1": "1",
              "num2": "999999",
              "includeSteps": false
            }
            """;

        mockMvc.perform(post("/api/calculations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sum").value("1000000"));
    }

    @Test
    @DisplayName("API-007: Bỏ qua trường includeSteps -> Mặc định là false")
    void testOmitIncludeSteps() throws Exception {
        String json = """
            {
              "num1": "1",
              "num2": "2"
            }
            """;

        mockMvc.perform(post("/api/calculations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sum").value("3"))
                .andExpect(jsonPath("$.steps", hasSize(0)));
    }

    @Test
    @DisplayName("API-008: Số có số 0 ở đầu -> Chuẩn hóa kết quả")
    void testLeadingZeros() throws Exception {
        String json = """
            {
              "num1": "000123",
              "num2": "001",
              "includeSteps": false
            }
            """;

        mockMvc.perform(post("/api/calculations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sum").value("000124"));
    }

    @Test
    @DisplayName("API-010: Thiếu trường num1 -> HTTP 400 và INVALID_NUM1")
    void testMissingNum1() throws Exception {
        String json = """
            {
              "num2": "1"
            }
            """;

        mockMvc.perform(post("/api/calculations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_NUM1"));
    }

    @Test
    @DisplayName("API-NUM2-BLANK: Trường num2 bị rỗng -> HTTP 400 và INVALID_NUM2")
    void testBlankNum2() throws Exception {
        String json = """
            {
              "num1": "123",
              "num2": ""
            }
            """;

        mockMvc.perform(post("/api/calculations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_NUM2"))
                .andExpect(jsonPath("$.detail").value("num2 must not be blank"));
    }

    @Test
    @DisplayName("API-NUM2-INVALID: Trường num2 chứa ký tự chữ -> HTTP 400 và INVALID_NUM2")
    void testInvalidDigitNum2() throws Exception {
        String json = """
            {
              "num1": "123",
              "num2": "89x"
            }
            """;

        mockMvc.perform(post("/api/calculations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_NUM2"))
                .andExpect(jsonPath("$.detail").value("num2 must contain digits only"));
    }

    @Test
    @DisplayName("API-012: Chứa field lạ ngoài schema -> HTTP 400 và MALFORMED_REQUEST")
    void testUnknownFieldRejected() throws Exception {
        String json = """
            {
              "num1": "1",
              "num2": "2",
              "extra": "unexpected"
            }
            """;

        mockMvc.perform(post("/api/calculations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("MALFORMED_REQUEST"));
    }

    @Test
    @DisplayName("API-FAVICON: Request /favicon.ico -> HTTP 200 OK không gây NoResourceFoundException")
    void testFaviconEndpoint() throws Exception {
        mockMvc.perform(get("/favicon.ico"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("API-404: Resource không tồn tại -> HTTP 404 và RESOURCE_NOT_FOUND")
    void testResourceNotFound() throws Exception {
        mockMvc.perform(get("/non-existent-endpoint-xyz"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));
    }
}
