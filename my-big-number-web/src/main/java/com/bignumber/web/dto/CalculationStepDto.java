package com.bignumber.web.dto;

public record CalculationStepDto(
    int stepNumber,
    String description,
    String intermediateResult,
    int carry
) {}
