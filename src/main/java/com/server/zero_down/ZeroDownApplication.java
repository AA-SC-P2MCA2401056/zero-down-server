package com.server.zero_down;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ZeroDownApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZeroDownApplication.class, args);
	}

}
