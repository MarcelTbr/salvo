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

import java.util.Date;


@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}


	@Bean
	public CommandLineRunner initData(PlayerRepository players, GameRepository games, ShipRepository ships, GamePlayerRepository gamePlayers) {
		return (args) -> {

			 Date date = new Date();

			 /** Create and Save Initial Players */

			 Player player1 = new Player("Jack", "jack@daniels.com");
			 Player player2 = new Player("Jane", "jane@daniels.com");
			 Player player3 = new Player("Jimi Hendrix", "jimi@hendrix.org");
			 Player player4 = new Player("Tony Iommi", "tonyIommi@blacksabath.net");
			 Player player5 = new Player("Rafa Nadal", "rafa@nadal.es");
			 Player player6 = new Player("Roger Federer", "roger@federer.ch");


			players.save(player1); // 1
			players.save(player2); // 2
			players.save(player3); // etc.
			players.save(player4);
			players.save(player5);
			players.save(player6);


			/**
			 * Create three games, with one hour difference each
			 * */

			 Game game1 = new Game(new Date());
			 Game game2 = new Game( Date.from( date.toInstant().plusSeconds(3600) ) );
			 Game game3 = new Game( Date.from( date.toInstant().plusSeconds(7200) ) );

			games.save(game1);
			games.save(game2);
			games.save(game3);

			/** Create 5 more dates with 30min difference each (1800s) */

			 Date date2 = Date.from( date.toInstant().plusSeconds(1800));
			 Date date3 = Date.from( date.toInstant().plusSeconds(3600));
			 Date date4 = Date.from( date.toInstant().plusSeconds(5400));
			 Date date5 = Date.from( date.toInstant().plusSeconds(7200));
			 Date date6 = Date.from( date.toInstant().plusSeconds(9000));


			/**
			 *  Create GamePlayers and assign them to variables
			 * */
			 GamePlayer gp1 = new GamePlayer(date, player1, game1);
			 GamePlayer gp2 = new GamePlayer(date2, player2, game1);
			 GamePlayer gp3 = new GamePlayer(date3, player3, game2);
			 GamePlayer gp4 = new GamePlayer(date4, player4, game2);
			 GamePlayer gp5 = new GamePlayer(date5, player5, game3);
			 GamePlayer gp6 = new GamePlayer(date6, player6, game3);

			/**
			 * Saving initial games with some of the created players
			 * */
			gamePlayers.save( gp1 );
			gamePlayers.save( gp2 );
			gamePlayers.save( gp3 );
			gamePlayers.save( gp4 );
			gamePlayers.save( gp5 );
			gamePlayers.save( gp6 );


			/**
			 * Create & Save some ships for Game1 --> GamePlayer1/Player1
			 * **/

			 Ship ship1_1 = new Ship(gp1, "A1-A2-A3");
				ships.save(ship1_1);
			 Ship ship1_2 = new Ship(gp1, "C4, D4, E4");
				ships.save(ship1_2);
			 Ship ship1_3 = new Ship(gp1, "E5, E6");
				ships.save(ship1_3);
			 Ship ship1_4 = new Ship(gp1, "F9");
				ships.save(ship1_4);


			/**
			 * Create new ShipLocations for each ship
			 * */

			 ShipLocation ship1_1_loc = new ShipLocation(ship1_1);







			gp1.addShip(ship1_1);

			//gp1.addShip(ship1_1,  "A1", "A2", "A3", "A4");  //add as an ArrayList

			//gp1.addShip( ship1_1, "A1-A2-A3");

		};
	}






}
