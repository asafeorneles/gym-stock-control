package com.asafeorneles.gymstock;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Swagger GymStock - Stock & Sales Control", version = "1", description = "API for stock and sales control of a gym store."))
public class GymStockApplication {

	public static void main(String[] args) {
		SpringApplication.run(GymStockApplication.class, args);
	}

}
