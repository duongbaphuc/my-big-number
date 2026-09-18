package com.bignumber.web;

import com.bignumber.core.MyBigNumber;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BigNumberWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(BigNumberWebApplication.class, args);
    }

    /**
     * Khởi tạo lớp MyBigNumber từ sub-module core thành một Bean cho Spring quản lý.
     */
    @Bean
    public MyBigNumber myBigNumber() {
        return new MyBigNumber();
    }
}