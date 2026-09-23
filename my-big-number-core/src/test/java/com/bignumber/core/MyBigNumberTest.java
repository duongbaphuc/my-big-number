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

    @ParameterizedTest(name = "Ca kiểm thử cơ bản {index}: {0} + {1} = {2}")
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

    @ParameterizedTest(name = "Độ lệch độ dài vừa phải {index}: {0} ({3} chữ số) + {1} ({4} chữ số) = {2}")
    @CsvSource({
            // 1 chữ số + 2 chữ số (và ngược lại)
            "5, 25, 30, 1, 2",
            "25, 5, 30, 2, 1",
            // 1 chữ số + 4 chữ số (và ngược lại)
            "7, 1234, 1241, 1, 4",
            "1234, 7, 1241, 4, 1",
            // 1 chữ số + 4 chữ số nhớ liên tiếp dây chuyền
            "1, 9999, 10000, 1, 4",
            "9999, 1, 10000, 4, 1",
            // 2 chữ số + 5 chữ số
            "55, 10055, 10110, 2, 5",
            "10055, 55, 10110, 5, 2",
            // 3 chữ số + 6 chữ số
            "999, 100001, 101000, 3, 6",
            "100001, 999, 101000, 6, 3",
            // 4 chữ số + 8 chữ số
            "1234, 87654321, 87655555, 4, 8",
            "87654321, 1234, 87655555, 8, 4"
    })
    @DisplayName("Kiểm thử phép cộng các số có độ lệch độ dài vừa phải (1-8 chữ số)")
    void testModerateLengthDifferences(String num1, String num2, String expected, int len1, int len2) {
        assertEquals(expected, myBigNumber.sum(num1, num2));
    }

    @ParameterizedTest(name = "Độ lệch độ dài cực lớn {index}: {0} ({3} chữ số) + {1} ({4} chữ số) = {2}")
    @CsvSource({
            // 1 chữ số + 20 chữ số (nhớ dồn toàn bộ sang hàng cao nhất)
            "9, 99999999999999999999, 100000000000000000008, 1, 20",
            "99999999999999999999, 9, 100000000000000000008, 20, 1",
            // 1 chữ số + 50 chữ số 9
            "1, 99999999999999999999999999999999999999999999999999, 100000000000000000000000000000000000000000000000000, 1, 50",
            // 5 chữ số + 30 chữ số
            "12345, 100000000000000000000000000000, 100000000000000000000000012345, 5, 30",
            // 10 chữ số + 40 chữ số (nhớ dồn chuỗi sang hàng cao nhất)
            "1111111111, 9999999999999999999999999999998888888889, 10000000000000000000000000000000000000000, 10, 40"
    })
    @DisplayName("Kiểm thử phép cộng các số có độ lệch độ dài cực lớn (chênh lệch từ 20 đến 50 chữ số)")
    void testExtremeLengthDifferences(String num1, String num2, String expected, int len1, int len2) {
        assertEquals(expected, myBigNumber.sum(num1, num2));
    }

    @ParameterizedTest(name = "Cùng độ dài các cấp {index}: {0} ({3} chữ số) + {1} ({3} chữ số) = {2}")
    @CsvSource({
            "7, 8, 15, 1",
            "45, 55, 100, 2",
            "12345, 87655, 100000, 5",
            "1111111111, 2222222222, 3333333333, 10",
            "555555555555555, 444444444444445, 1000000000000000, 15",
            "500000000000000000000000000000, 500000000000000000000000000000, 1000000000000000000000000000000, 30"
    })
    @DisplayName("Kiểm thử các cấp độ dài khác nhau khi 2 số có cùng số lượng chữ số (1, 2, 5, 10, 15, 30 chữ số)")
    void testSameLengthVariousSizes(String num1, String num2, String expected, int length) {
        assertEquals(expected, myBigNumber.sum(num1, num2));
    }

    @Test
    @DisplayName("Kiểm thử độ lệch độ dài lớn tự động sinh: 100 chữ số cộng với 5 chữ số")
    void testDynamicLargeLengthDifference() {
        // Chuỗi 1: 100 chữ số '1'
        String num100 = "1".repeat(100);
        // Chuỗi 2: 5 chữ số "99999"
        String num5 = "99999";

        // 11111 + 99999 = 111110 -> 95 chữ số '1' đầu tiên, 1 chữ số '2', sau đó "1110"
        String expected = "1".repeat(94) + "211110";
        assertEquals(expected, myBigNumber.sum(num100, num5));
        assertEquals(expected, myBigNumber.sum(num5, num100)); // Kiểm tra tính giao hoán
    }

    @Test
    @DisplayName("Kiểm thử sumWithProgress khi 2 số có độ dài chênh lệch (5 chữ số + 1 chữ số)")
    void testSumWithProgressWithLengthDifference() {
        // "10000" (5 chữ số) + "5" (1 chữ số) -> maxLen = 5 -> đúng 5 bước tính
        CalculationResult result = myBigNumber.sumWithProgress("10000", "5");
        assertEquals("10005", result.sum());
        assertEquals(5, result.steps().size());
        assertEquals("5", result.steps().get(0).getIntermediateResult());
        assertEquals("10005", result.steps().get(4).getIntermediateResult());
    }

    @ParameterizedTest(name = "Ca ném ngoại lệ {index}: num1='{0}', num2='{1}'")
    @CsvSource({
            "12a4, 897",
            "1234, 8b7",
            "abc, 123",
            "123, xyz",
            "12.3, 456",
            "456, 12.3",
            "-123, 456",
            "456, -123",
            "123, 45 6",
            "45 6, 123",
            "@#$, 123",
            "123, @#$"
    })
    @DisplayName("Ném IllegalArgumentException khi chuỗi chứa chữ cái hoặc ký tự không hợp lệ")
    void testInvalidInputsWithLettersAndSymbols(String num1, String num2) {
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, () -> myBigNumber.sum(num1, num2));
        assertNotNull(ex1.getMessage());

        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, () -> myBigNumber.sumWithProgress(num1, num2));
        assertNotNull(ex2.getMessage());
    }

    @Test
    @DisplayName("Ném ngoại lệ IllegalArgumentException khi tham số là null hoặc rỗng")
    void testNullAndEmptyInputs() {
        assertThrows(IllegalArgumentException.class, () -> myBigNumber.sum(null, "123"));
        assertThrows(IllegalArgumentException.class, () -> myBigNumber.sum("123", null));
        assertThrows(IllegalArgumentException.class, () -> myBigNumber.sum("", "123"));
        assertThrows(IllegalArgumentException.class, () -> myBigNumber.sum("123", ""));

        assertThrows(IllegalArgumentException.class, () -> myBigNumber.sumWithProgress(null, "123"));
        assertThrows(IllegalArgumentException.class, () -> myBigNumber.sumWithProgress("123", null));
        assertThrows(IllegalArgumentException.class, () -> myBigNumber.sumWithProgress("", "123"));
        assertThrows(IllegalArgumentException.class, () -> myBigNumber.sumWithProgress("123", ""));
    }

    @Test
    @DisplayName("Kiểm tra sumWithProgress trả về đầy đủ các bước tính toán theo đề bài")
    void testSumWithProgress() {
        CalculationResult result = myBigNumber.sumWithProgress("1234", "897");
        assertEquals("2131", result.sum());
        assertEquals(4, result.steps().size());
    }

    @Test
    @DisplayName("Kiểm thử sumWithProgress khi có số nhớ cuối cùng và kiểm tra toàn bộ getter CalculationStep")
    void testSumWithProgressWithFinalCarryAndGetters() {
        CalculationResult result = myBigNumber.sumWithProgress("999", "1");
        assertEquals("1000", result.sum());
        assertEquals(4, result.steps().size());

        // Kiểm tra bước đầu tiên
        CalculationStep firstStep = result.steps().get(0);
        assertEquals(1, firstStep.getStepNumber());
        assertNotNull(firstStep.getDescription());
        assertEquals("0", firstStep.getIntermediateResult());
        assertEquals(1, firstStep.getCarry());

        // Kiểm tra bước cuối cùng (hạ số nhớ)
        CalculationStep lastStep = result.steps().get(3);
        assertEquals(4, lastStep.getStepNumber());
        assertNotNull(lastStep.getDescription());
        assertEquals("1000", lastStep.getIntermediateResult());
        assertEquals(0, lastStep.getCarry());
    }

    @Test
    @DisplayName("Kiểm thử khi tắt log INFO (Logger level = WARNING) để bao phủ toàn bộ nhánh rẽ isLoggable")
    void testWhenLoggingInfoIsDisabled() {
        java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MyBigNumber.class.getName());
        java.util.logging.Level originalLevel = logger.getLevel();
        try {
            logger.setLevel(java.util.logging.Level.WARNING);
            assertEquals("2131", myBigNumber.sumWithProgress("1234", "897").sum());
            assertEquals("1000", myBigNumber.sumWithProgress("999", "1").sum());
        } finally {
            logger.setLevel(originalLevel);
        }
    }

    @Test
    @DisplayName("Kiểm thử các phương thức sinh tự động của Record CalculationResult")
    void testCalculationResultRecordMethods() {
        CalculationResult res1 = myBigNumber.sumWithProgress("1234", "897");
        CalculationResult res2 = myBigNumber.sumWithProgress("1234", "897");
        CalculationResult res3 = myBigNumber.sumWithProgress("999", "1");

        assertNotNull(res1.toString());
        assertEquals(res1.hashCode(), res2.hashCode());
        assertEquals(res1, res2);
        org.junit.jupiter.api.Assertions.assertNotEquals(res1, res3);
    }
}