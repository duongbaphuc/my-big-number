package com.bignumber.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class CalculationRequest {

    @NotBlank(message = "Số thứ nhất không được để trống.")
    @Pattern(regexp = "^[0-9]+$", message = "Số thứ nhất chỉ được phép chứa các chữ số (0-9).")
    private String num1 = "";

    @NotBlank(message = "Số thứ hai không được để trống.")
    @Pattern(regexp = "^[0-9]+$", message = "Số thứ hai chỉ được phép chứa các chữ số (0-9).")
    private String num2 = "";

    public String getNum1() {
        return num1;
    }

    public void setNum1(String num1) {
        this.num1 = num1;
    }

    public String getNum2() {
        return num2;
    }

    public void setNum2(String num2) {
        this.num2 = num2;
    }
}