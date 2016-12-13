package edu.example;

/**
 * SpringBoot Library link for using CommandLineRunner
 * */

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/**
 * Springboot Library link for using BEANS
 * */
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}



	@Bean
	public CommandLineRunner initData(PlayerRepository repository) {
		return (args) -> {
			// save a couple of newly registered players
			repository.save(new Player("Jack", "jack@daniels.com"));
			repository.save(new Player("Jane", "jane@daniels.com"));

		};
	}






}
