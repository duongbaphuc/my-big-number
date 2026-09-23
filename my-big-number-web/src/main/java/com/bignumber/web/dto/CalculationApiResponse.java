package com.bignumber.web.dto;

import java.util.List;

public record CalculationApiResponse(
    String sum,
    List<CalculationStepDto> steps
) {}
