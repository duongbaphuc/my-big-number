package com.bignumber.core;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Lớp cài đặt thuật toán cộng hai số lớn dưới dạng chuỗi
 * mô phỏng cách tính toán của học sinh tiểu học.
 */
public class MyBigNumber {

    private static final Logger LOGGER = Logger.getLogger(MyBigNumber.class.getName());

    /**
     * Phương thức phục vụ Task 1: Trả về chuỗi kết quả phép cộng.
     * Tối ưu luồng tính toán trực tiếp (Fast Path) đạt độ phức tạp O(N) thời gian
     * và O(1) bộ nhớ phụ (chỉ cấp phát duy nhất 1 mảng char kết quả).
     *
     * @param stn1 Chuỗi số thứ nhất
     * @param stn2 Chuỗi số thứ hai
     * @return Chuỗi kết quả phép cộng
     * @throws IllegalArgumentException nếu chuỗi là null, rỗng hoặc chứa ký tự không hợp lệ
     */
    public String sum(String stn1, String stn2) {
        validateInputs(stn1, stn2);

        int len1 = stn1.length();
        int len2 = stn2.length();
        int maxLen = Math.max(len1, len2);

        char[] resultChars = new char[maxLen + 1];
        int pos = resultChars.length - 1;
        int carry = 0;

        int i;
        int index1;
        int index2;
        int digit1;
        int digit2;
        char c1;
        char c2;
        int total;

        for (i = 0; i < maxLen; i++) {
            index1 = len1 - 1 - i;
            index2 = len2 - 1 - i;

            digit1 = 0;
            if (index1 >= 0) {
                c1 = stn1.charAt(index1);
                if (c1 < '0' || c1 > '9') {
                    LOGGER.severe("LỖI: Chuỗi 1 chứa ký tự không hợp lệ '" + c1 + "' tại vị trí " + index1 + ".");
                    throw new IllegalArgumentException(String.format("Chuỗi 1 chứa ký tự không hợp lệ '%c' tại vị trí %d.", c1, index1));
                }
                digit1 = c1 - '0';
            }

            digit2 = 0;
            if (index2 >= 0) {
                c2 = stn2.charAt(index2);
                if (c2 < '0' || c2 > '9') {
                    LOGGER.severe("LỖI: Chuỗi 2 chứa ký tự không hợp lệ '" + c2 + "' tại vị trí " + index2 + ".");
                    throw new IllegalArgumentException(String.format("Chuỗi 2 chứa ký tự không hợp lệ '%c' tại vị trí %d.", c2, index2));
                }
                digit2 = c2 - '0';
            }

            total = digit1 + digit2 + carry;
            resultChars[pos--] = (char) ('0' + (total % 10));
            carry = total / 10;
        }

        if (carry > 0) {
            resultChars[pos--] = (char) ('0' + carry);
        }

        return new String(resultChars, pos + 1, resultChars.length - 1 - pos);
    }

    /**
     * Phương thức phục vụ Task 2: Trả về kết quả và danh sách chi tiết các bước tính toán.
     * Sử dụng mảng char[] điền từ phải sang trái O(1) thay vì StringBuilder.insert(0) O(N^2),
     * pre-allocate dung lượng cho List, tái sử dụng StringBuilder mô tả và kiểm tra isLoggable.
     *
     * @param stn1 Chuỗi số thứ nhất
     * @param stn2 Chuỗi số thứ hai
     * @return Đối tượng CalculationResult chứa kết quả và danh sách các bước
     * @throws IllegalArgumentException nếu chuỗi là null, rỗng hoặc chứa ký tự không hợp lệ
     */
    public CalculationResult sumWithProgress(String stn1, String stn2) {
        validateInputs(stn1, stn2);

        int len1 = stn1.length();
        int len2 = stn2.length();
        int maxLen = Math.max(len1, len2);

        // Pre-allocate kích thước danh sách bước tính để tránh resizing mảng
        List<CalculationStep> steps = new ArrayList<>(maxLen + 1);

        // Buffer chứa kết quả cộng từ phải sang trái để đạt O(1) thay vì insert(0) tốn O(N^2)
        char[] buffer = new char[maxLen + 1];
        int pos = buffer.length - 1;

        int carry = 0;
        int step = 1;

        int i;
        int index1;
        int index2;
        int digit1;
        int digit2;
        char c1;
        char c2;
        int sumDigits;
        int total;
        int currentDigit;
        int nextCarry;
        StringBuilder desc = new StringBuilder(128);

        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "Bắt đầu phép tính cộng: {0} + {1}", new Object[]{stn1, stn2});
        }

        for (i = 0; i < maxLen; i++) {
            index1 = len1 - 1 - i;
            index2 = len2 - 1 - i;

            digit1 = 0;
            if (index1 >= 0) {
                c1 = stn1.charAt(index1);
                if (c1 < '0' || c1 > '9') {
                    LOGGER.severe("LỖI: Chuỗi 1 chứa ký tự không hợp lệ '" + c1 + "' tại vị trí " + index1 + ".");
                    throw new IllegalArgumentException(String.format("Chuỗi 1 chứa ký tự không hợp lệ '%c' tại vị trí %d.", c1, index1));
                }
                digit1 = c1 - '0';
            }

            digit2 = 0;
            if (index2 >= 0) {
                c2 = stn2.charAt(index2);
                if (c2 < '0' || c2 > '9') {
                    LOGGER.severe("LỖI: Chuỗi 2 chứa ký tự không hợp lệ '" + c2 + "' tại vị trí " + index2 + ".");
                    throw new IllegalArgumentException(String.format("Chuỗi 2 chứa ký tự không hợp lệ '%c' tại vị trí %d.", c2, index2));
                }
                digit2 = c2 - '0';
            }

            sumDigits = digit1 + digit2;
            total = sumDigits + carry;
            currentDigit = total % 10;
            nextCarry = total / 10;

            // Điền trực tiếp vào buffer O(1)
            buffer[pos--] = (char) ('0' + currentDigit);
            String intermediateResult = new String(buffer, pos + 1, buffer.length - 1 - pos);

            desc.setLength(0);
            desc.append("Lấy ").append(digit1).append(" cộng với ").append(digit2).append(" được ").append(sumDigits).append(".");
            if (carry > 0) {
                desc.append(" Cộng tiếp với nhớ ").append(carry).append(" được ").append(total).append(".");
            }
            desc.append(" Lưu ").append(currentDigit).append(" vào kết quả tạm \"").append(intermediateResult).append("\".");
            if (nextCarry > 0) {
                desc.append(" Ghi nhớ ").append(nextCarry).append(".");
            } else {
                desc.append(" Ghi nhớ 0.");
            }

            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.info("Bước " + step + ": " + desc);
            }

            steps.add(new CalculationStep(step, desc.toString(), intermediateResult, nextCarry));

            carry = nextCarry;
            step++;
        }

        if (carry > 0) {
            buffer[pos--] = (char) ('0' + carry);
            String finalResult = new String(buffer, pos + 1, buffer.length - 1 - pos);

            desc.setLength(0);
            desc.append("Hạ số nhớ ").append(carry).append(" vào đầu kết quả thu được: \"").append(finalResult).append("\".");
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.info("Bước cuối: " + desc);
            }
            steps.add(new CalculationStep(step, desc.toString(), finalResult, 0));
        }

        String finalSum = new String(buffer, pos + 1, buffer.length - 1 - pos);
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "Kết quả cuối cùng: {0}", finalSum);
        }

        return new CalculationResult(finalSum, steps);
    }

    /**
     * Phương thức kiểm tra tham số đầu vào chung.
     */
    private void validateInputs(String stn1, String stn2) {
        if (stn1 == null || stn2 == null) {
            String errorMsg = "Tham số truyền vào không được là null.";
            LOGGER.severe("LỖI: " + errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }
        if (stn1.isEmpty() || stn2.isEmpty()) {
            String errorMsg = "Chuỗi số không được để trống.";
            LOGGER.severe("LỖI: " + errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }
    }
}