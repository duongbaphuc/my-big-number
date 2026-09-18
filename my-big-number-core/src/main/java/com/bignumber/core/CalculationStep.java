package com.bignumber.core;

/**
 * Đại diện cho một bước tính toán cộng theo cột dọc của học sinh tiểu học.
 */
public class CalculationStep {
    private final int stepNumber;
    private final String description;
    private final String intermediateResult;
    private final int carry;

    public CalculationStep(int stepNumber, String description, String intermediateResult, int carry) {
        this.stepNumber = stepNumber;
        this.description = description;
        this.intermediateResult = intermediateResult;
        this.carry = carry;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public String getDescription() {
        return description;
    }

    public String getIntermediateResult() {
        return intermediateResult;
    }

    public int getCarry() {
        return carry;
    }
}