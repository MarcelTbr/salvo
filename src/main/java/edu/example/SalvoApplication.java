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

import java.util.Calendar;
import java.util.Date;


@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}


	public Date date = new Date();


	@Bean
	public CommandLineRunner initData(PlayerRepository players, GameRepository games) {
		return (args) -> {
			// save a couple of newly registered players
			players.save(new Player("Jack", "jack@daniels.com"));
			players.save(new Player("Jane", "jane@daniels.com"));
			games.save(new Game(new Date()));
			games.save(new Game( Date.from( date.toInstant().plusSeconds(3600) ) ) );
			games.save(new Game( Date.from( date.toInstant().plusSeconds(7200) ) ) );

		};
	}






}
