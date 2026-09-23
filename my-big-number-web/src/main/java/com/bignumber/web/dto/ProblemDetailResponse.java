package com.bignumber.web.dto;

public record ProblemDetailResponse(
    String type,
    String title,
    int status,
    String detail,
    String code
) {}
