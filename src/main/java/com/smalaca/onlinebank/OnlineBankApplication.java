package com.smalaca.onlinebank;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
        info = @Info(
                title = "Bank Online API",
                version = "1.0.0",
                description = "Core Banking Operations Documentation"))
@SpringBootApplication
public class OnlineBankApplication {
    public static void main(String[] args) {
        SpringApplication.run(OnlineBankApplication.class, args);
    }
}
