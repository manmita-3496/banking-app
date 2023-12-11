package com.example.bank;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
		title = "Bank App",
         description = "Backend API for a bank",
		version = "v1.0",
		contact = @Contact(
				name = "Manmita Patnaik",
				email = "pman4579@gmail.com",
				url = "https://www.linkedin.com/in/manmita-patnaik-104077243"
				),
		license=@License(
				name = "The Free Association ",
				url = "https://www.linkedin.com/in/manmita-patnaik-104077243"
		)
	 ),
	externalDocs = @ExternalDocumentation(
		description = "The Free Company",
			url = "https://www.linkedin.com/in/manmita-patnaik-104077243"
	)
)
public class BankApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankApplication.class, args);
	}

}
