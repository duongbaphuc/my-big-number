package com.bignumber.web.controller;

import com.bignumber.core.MyBigNumber;
import com.bignumber.web.BigNumberWebApplication;
import com.bignumber.web.dto.CalculationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = BigNumberWebApplication.class)
@AutoConfigureMockMvc
class BigNumberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MyBigNumber myBigNumber;

    @Test
    @DisplayName("GET / -> Trả về trang chủ index và model calculationRequest")
    void testIndexPage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("calculationRequest"));
    }

    @Test
    @DisplayName("GET /favicon.ico -> Trả về HTTP 200 OK rỗng")
    void testFavicon() throws Exception {
        mockMvc.perform(get("/favicon.ico"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /calculate thành công -> Trả về trang index cùng kết quả và các bước")
    void testCalculateSuccess() throws Exception {
        mockMvc.perform(post("/calculate")
                        .param("num1", "123")
                        .param("num2", "456"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("result", "579"))
                .andExpect(model().attributeExists("steps"));
    }

    @Test
    @DisplayName("POST /calculate có lỗi validation -> Trả về trang index và giữ bindingResult có lỗi")
    void testCalculateValidationErrors() throws Exception {
        mockMvc.perform(post("/calculate")
                        .param("num1", "")
                        .param("num2", "abc"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().hasErrors());
    }

    @Test
    @DisplayName("Unit test trực tiếp phương thức calculate khi core ném IllegalArgumentException")
    void testCalculateIllegalArgumentExceptionHandling() {
        // Tạo controller với mock hoặc lambda ném ngoại lệ
        MyBigNumber mockCore = new MyBigNumber() {
            @Override
            public com.bignumber.core.CalculationResult sumWithProgress(String stn1, String stn2) {
                throw new IllegalArgumentException("Lỗi định dạng giả lập");
            }
        };

        BigNumberController controller = new BigNumberController(mockCore);
        CalculationRequest request = new CalculationRequest();
        request.setNum1("123");
        request.setNum2("456");

        BindingResult bindingResult = new BeanPropertyBindingResult(request, "calculationRequest");
        Model model = new ConcurrentModel();

        String viewName = controller.calculate(request, bindingResult, model);

        assertEquals("index", viewName);
        assertTrue(model.containsAttribute("errorMessage"));
        assertEquals("Lỗi định dạng giả lập", model.asMap().get("errorMessage"));
    }
}
