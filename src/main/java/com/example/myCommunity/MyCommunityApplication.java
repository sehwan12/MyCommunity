package com.example.myCommunity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MyCommunityApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyCommunityApplication.class, args);
	}

}
