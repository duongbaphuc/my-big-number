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
     *
     * @param stn1 Chuỗi số thứ nhất
     * @param stn2 Chuỗi số thứ hai
     * @return Chuỗi kết quả phép cộng
     * @throws IllegalArgumentException nếu chuỗi là null, rỗng hoặc chứa ký tự không hợp lệ
     */
    public String sum(String stn1, String stn2) {
        return sumWithProgress(stn1, stn2).sum();
    }

    /**
     * Phương thức phục vụ Task 2: Trả về kết quả và danh sách chi tiết các bước tính toán.
     *
     * @param stn1 Chuỗi số thứ nhất
     * @param stn2 Chuỗi số thứ hai
     * @return Đối tượng CalculationResult chứa kết quả và danh sách các bước
     * @throws IllegalArgumentException nếu chuỗi là null, rỗng hoặc chứa ký tự không hợp lệ
     */
    public CalculationResult sumWithProgress(String stn1, String stn2) {
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

        List<CalculationStep> steps = new ArrayList<>();
        StringBuilder result = new StringBuilder();
        int len1 = stn1.length();
        int len2 = stn2.length();
        int maxLen = Math.max(len1, len2);

        int carry = 0;
        int step = 1;

        LOGGER.log(Level.INFO, "Bắt đầu phép tính cộng: {0} + {1}", new Object[]{stn1, stn2});

        for (int i = 0; i < maxLen; i++) {
            int index1 = len1 - 1 - i;
            int index2 = len2 - 1 - i;

            int digit1 = 0;
            if (index1 >= 0) {
                char c1 = stn1.charAt(index1);
                if (c1 < '0' || c1 > '9') {
                    String errorMsg = String.format("Chuỗi 1 chứa ký tự không hợp lệ '%c' tại vị trí %d.", c1, index1);
                    LOGGER.severe("LỖI: " + errorMsg);
                    throw new IllegalArgumentException(errorMsg);
                }
                digit1 = c1 - '0';
            }

            int digit2 = 0;
            if (index2 >= 0) {
                char c2 = stn2.charAt(index2);
                if (c2 < '0' || c2 > '9') {
                    String errorMsg = String.format("Chuỗi 2 chứa ký tự không hợp lệ '%c' tại vị trí %d.", c2, index2);
                    LOGGER.severe("LỖI: " + errorMsg);
                    throw new IllegalArgumentException(errorMsg);
                }
                digit2 = c2 - '0';
            }

            int sumDigits = digit1 + digit2;
            int total = sumDigits + carry;
            int currentDigit = total % 10;
            int nextCarry = total / 10;

            StringBuilder desc = new StringBuilder();
            desc.append(String.format("Lấy %d cộng với %d được %d.", digit1, digit2, sumDigits));
            if (carry > 0) {
                desc.append(String.format(" Cộng tiếp với nhớ %d được %d.", carry, total));
            }
            result.insert(0, currentDigit);
            desc.append(String.format(" Lưu %d vào kết quả tạm \"%s\".", currentDigit, result.toString()));
            if (nextCarry > 0) {
                desc.append(String.format(" Ghi nhớ %d.", nextCarry));
            } else {
                desc.append(" Ghi nhớ 0.");
            }

            LOGGER.info(String.format("Bước %d: %s", step, desc.toString()));

            steps.add(new CalculationStep(step, desc.toString(), result.toString(), nextCarry));

            carry = nextCarry;
            step++;
        }

        if (carry > 0) {
            result.insert(0, carry);
            String desc = String.format("Hạ số nhớ %d vào đầu kết quả thu được: \"%s\".", carry, result.toString());
            LOGGER.info(String.format("Bước cuối: %s", desc));
            steps.add(new CalculationStep(step, desc, result.toString(), 0));
        }

        LOGGER.log(Level.INFO, "Kết quả cuối cùng: {0}", result.toString());
        return new CalculationResult(result.toString(), steps);
    }
}