package com.bignumber.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CalculationApiRequest {

    @NotBlank(message = "num1 must not be blank")
    @Pattern(regexp = "^[0-9]+$", message = "num1 must contain digits only")
    @Size(max = 100000, message = "num1 exceeds the maximum length")
    private String num1;

    @NotBlank(message = "num2 must not be blank")
    @Pattern(regexp = "^[0-9]+$", message = "num2 must contain digits only")
    @Size(max = 100000, message = "num2 exceeds the maximum length")
    private String num2;

    private Boolean includeSteps = false;

    public CalculationApiRequest() {}

    public CalculationApiRequest(String num1, String num2, Boolean includeSteps) {
        this.num1 = num1;
        this.num2 = num2;
        this.includeSteps = includeSteps != null ? includeSteps : false;
    }

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

    public Boolean getIncludeSteps() {
        return includeSteps;
    }

    public void setIncludeSteps(Boolean includeSteps) {
        this.includeSteps = includeSteps;
    }
}
