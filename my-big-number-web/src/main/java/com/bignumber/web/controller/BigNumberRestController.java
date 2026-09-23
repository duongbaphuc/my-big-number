package com.bignumber.web.controller;

import com.bignumber.core.CalculationResult;
import com.bignumber.core.MyBigNumber;
import com.bignumber.web.dto.CalculationApiRequest;
import com.bignumber.web.dto.CalculationApiResponse;
import com.bignumber.web.dto.CalculationStepDto;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/calculations")
public class BigNumberRestController {

    private final MyBigNumber myBigNumber;

    public BigNumberRestController(MyBigNumber myBigNumber) {
        this.myBigNumber = myBigNumber;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CalculationApiResponse> calculate(
            @Valid @RequestBody CalculationApiRequest request) {

        boolean includeSteps = Boolean.TRUE.equals(request.getIncludeSteps());

        if (!includeSteps) {
            String sum = myBigNumber.sum(request.getNum1(), request.getNum2());
            CalculationApiResponse response = new CalculationApiResponse(sum, Collections.emptyList());
            return ResponseEntity.ok(response);
        }

        CalculationResult result = myBigNumber.sumWithProgress(request.getNum1(), request.getNum2());

        List<CalculationStepDto> steps = result.steps().stream()
                .map(step -> new CalculationStepDto(
                        step.getStepNumber(),
                        step.getDescription(),
                        step.getIntermediateResult(),
                        step.getCarry()))
                .toList();

        CalculationApiResponse response = new CalculationApiResponse(result.sum(), steps);
        return ResponseEntity.ok(response);
    }
}
