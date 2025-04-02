package com.srsoft.modorder25;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.srsoft.modaccessk01.entity")
@EnableJpaRepositories(basePackages = {"com.srsoft.modaccessk01.repository"})
public class Modorder25Application {

	public static void main(String[] args) {
		SpringApplication.run(Modorder25Application.class, args);
	}

}
