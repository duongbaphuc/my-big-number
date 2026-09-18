package com.bignumber.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MyBigNumberTest {

    private MyBigNumber myBigNumber;

    @BeforeEach
    void setUp() {
        myBigNumber = new MyBigNumber();
    }

    @Test
    @DisplayName("Cộng theo ví dụ mẫu trong đề bài: 1234 + 897 = 2131")
    void testExampleCase() {
        String result = myBigNumber.sum("1234", "897");
        assertEquals("2131", result);
    }

    @ParameterizedTest(name = "Ca kiểm thử hợp lệ {index}: {0} + {1} = {2}")
    @CsvSource({
            "0, 0, 0",
            "9, 1, 10",
            "999, 1, 1000",
            "123, 456, 579",
            "99999999999999999999, 1, 100000000000000000000",
            "897, 1234, 2131"
    })
    void testVariousValidCases(String num1, String num2, String expected) {
        assertEquals(expected, myBigNumber.sum(num1, num2));
    }

    @ParameterizedTest(name = "Ca ném ngoại lệ {index}: num1='{0}', num2='{1}'")
    @CsvSource({
            "12a4, 897",
            "1234, 8b7",
            "abc, 123",
            "123, xyz",
            "12.3, 456",
            "-123, 456",
            "123, 45 6",
            "@#$, 123"
    })
    @DisplayName("Ném IllegalArgumentException khi chuỗi chứa chữ cái hoặc ký tự không hợp lệ")
    void testInvalidInputsWithLettersAndSymbols(String num1, String num2) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            myBigNumber.sum(num1, num2);
        });
        assertNotNull(exception.getMessage());
    }

    @Test
    @DisplayName("Kiểm tra sumWithProgress trả về đầy đủ các bước tính toán")
    void testSumWithProgress() {
        CalculationResult result = myBigNumber.sumWithProgress("1234", "897");
        assertEquals("2131", result.sum());
        assertEquals(4, result.steps().size());
    }
}