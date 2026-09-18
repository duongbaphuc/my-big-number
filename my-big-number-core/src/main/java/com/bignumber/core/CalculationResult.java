package com.bignumber.core;

import java.util.List;

/**
 * Record chứa kết quả cuối cùng cùng toàn bộ các bước tính toán chi tiết.
 */
public record CalculationResult(String sum, List<CalculationStep> steps) {}