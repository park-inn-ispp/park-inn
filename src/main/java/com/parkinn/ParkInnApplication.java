package com.parkinn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class ParkInnApplication {
	
    @Bean
    public RestTemplate getresttemplate() {
        return new RestTemplate();
    }
	
	
	public static void main(String[] args) {
		SpringApplication.run(ParkInnApplication.class, args);
	}

		
	}

