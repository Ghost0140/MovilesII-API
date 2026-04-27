package com.cibertec.SkillsFest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SkillsFestApplication {

	public static void main(String[] args) {
		SpringApplication.run(SkillsFestApplication.class, args);
	}


}
