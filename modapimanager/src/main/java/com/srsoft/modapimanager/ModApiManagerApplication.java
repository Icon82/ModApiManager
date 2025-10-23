package com.srsoft.modapimanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.srsoft.modapimanager.entity")
@EnableJpaRepositories(basePackages = {"com.srsoft.modapimanager.repository"})
public class ModApiManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ModApiManagerApplication.class, args);
		
	}

}
