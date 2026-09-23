package com.bignumber.web.controller;

import com.bignumber.core.CalculationResult;
import com.bignumber.core.MyBigNumber;
import com.bignumber.web.dto.CalculationRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class BigNumberController {

    private final MyBigNumber myBigNumber;

    public BigNumberController(MyBigNumber myBigNumber) {
        this.myBigNumber = myBigNumber;
    }

    @GetMapping("favicon.ico")
    @ResponseBody
    public void favicon() {
        // Trả về 200 OK rỗng để trình duyệt không gọi thiếu file và không gây NoResourceFoundException
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("calculationRequest", new CalculationRequest());
        return "index";
    }

    @PostMapping("/calculate")
    public String calculate(@Valid @ModelAttribute("calculationRequest") CalculationRequest request,
                            BindingResult bindingResult,
                            Model model) {
        if (bindingResult.hasErrors()) {
            return "index";
        }

        try {
            CalculationResult calculationResult = myBigNumber.sumWithProgress(request.getNum1(), request.getNum2());
            model.addAttribute("result", calculationResult.sum());
            model.addAttribute("steps", calculationResult.steps());
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
        }

        return "index";
    }
}