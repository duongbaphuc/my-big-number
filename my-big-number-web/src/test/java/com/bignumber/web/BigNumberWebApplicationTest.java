package com.bignumber.web;

import com.bignumber.core.MyBigNumber;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

class BigNumberWebApplicationTest {

    @Test
    @DisplayName("Kiểm thử khởi tạo context và bean myBigNumber")
    void testBeanCreationAndConstructor() {
        BigNumberWebApplication app = new BigNumberWebApplication();
        assertNotNull(app);

        MyBigNumber bean = app.myBigNumber();
        assertNotNull(bean);
        assertEquals("3", bean.sum("1", "2"));
    }

    @Test
    @DisplayName("Kiểm thử phương thức main khởi chạy Spring Boot")
    void testMainMethod() {
        System.setProperty("spring.main.web-application-type", "none");
        try {
            BigNumberWebApplication.main(new String[]{});
        } finally {
            System.clearProperty("spring.main.web-application-type");
        }
    }
}
