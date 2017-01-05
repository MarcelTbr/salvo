package edu.example;

/**
 * SpringBoot Library link for using CommandLineRunner
 */


import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/**
 * Springboot Library link for using BEANS
 * */
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


@SpringBootApplication
public class SalvoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SalvoApplication.class, args);
    }


    @Bean
    public CommandLineRunner initData(PlayerRepository players, GameRepository games, ShipRepository ships, GamePlayerRepository gamePlayers,
                                      SalvoRepository salvos) {
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
            Game game2 = new Game(Date.from(date.toInstant().plusSeconds(3600)));
            Game game3 = new Game(Date.from(date.toInstant().plusSeconds(7200)));

            games.save(game1);
            games.save(game2);
            games.save(game3);

            /** Create 5 more dates with 30min difference each (1800s) */

            Date date2 = Date.from(date.toInstant().plusSeconds(1800));
            Date date3 = Date.from(date.toInstant().plusSeconds(3600));
            Date date4 = Date.from(date.toInstant().plusSeconds(5400));
            Date date5 = Date.from(date.toInstant().plusSeconds(7200));
            Date date6 = Date.from(date.toInstant().plusSeconds(9000));


            /**
             *  Create GamePlayers and assign them to variables
             * */
            GamePlayer gp1 = new GamePlayer(date, player1, game1);
            GamePlayer gp2 = new GamePlayer(date2, player2, game1);
            GamePlayer gp3 = new GamePlayer(date3, player3, game2);
            GamePlayer gp4 = new GamePlayer(date4, player4, game2);
            GamePlayer gp5 = new GamePlayer(date5, player5, game3);
            GamePlayer gp6 = new GamePlayer(date6, player6, game3);


            gamePlayers.save(gp1);
            gamePlayers.save(gp2);
            gamePlayers.save(gp3);
            gamePlayers.save(gp4);
            gamePlayers.save(gp5);
            gamePlayers.save(gp6);


            /**
             * Create & Save some ships for Game1 --> GamePlayer1/Player1
             * **/

            /** First we store the locations of a ship in a list*/

            List<String> ship1_1_loc1 = new ArrayList<>();

            ship1_1_loc1.add("A1");
            ship1_1_loc1.add("A2");
            ship1_1_loc1.add("A3");

            /** later we pass it as an argument to make a new Ship instance */

            Ship ship1_1 = new Ship(gp1, ship1_1_loc1, "Destroyer");

            /**
             * Create more locations to later create a more Ship instances.
             * */

            List<String> ship1_2_loc2 = new ArrayList<>();
            ship1_2_loc2.add("C4"); ship1_2_loc2.add("D4"); ship1_2_loc2.add("E4");

            List<String> ship1_3_loc3 = new ArrayList<>();
            ship1_3_loc3.add("E5"); ship1_3_loc3.add("E6");

            /** We create all the other Ship instances
             *  with a common GamePlayer instance as an argument */

            Ship ship1_2 = new Ship(gp1, ship1_2_loc2, "Submarine");
            Ship ship1_3 = new Ship(gp1, ship1_3_loc3, "Patrol Boat");

            /** This adds an instance of a Ship to it's GamePlayer instance */
            gp1.addShip(ship1_1);
            gp1.addShip(ship1_2);
            gp1.addShip(ship1_3);

            ships.save(ship1_1);
            ships.save(ship1_2);
            ships.save(ship1_3);

            /** Ships for other game players */
            List<String> ship2_1_loc1 = new ArrayList<>();
            ship2_1_loc1.add("C7"); ship2_1_loc1.add("D7"); ship2_1_loc1.add("E7");
            List<String> ship2_2_loc2 = new ArrayList<>();
            ship2_2_loc2.add("B5"); ship2_2_loc2.add("C5"); ship2_2_loc2.add("D5");
            List<String> ship2_3_loc3 = new ArrayList<>();
            ship2_3_loc3.add("I5"); ship2_3_loc3.add("I6");

            Ship ship2_1 = new Ship(gp2, ship2_1_loc1, "Destroyer");
            Ship ship2_2 = new Ship(gp2, ship2_2_loc2, "Submarine");
            Ship ship2_3 = new Ship(gp2, ship2_3_loc3, "Patrol Boat");

            gp2.addShip(ship2_1); gp2.addShip(ship2_2); gp2.addShip(ship2_3);
            ships.save(ship2_1); ships.save(ship2_2); ships.save(ship2_3);

            /** Create and Save some Salvos for a couple of turns*/
            List<String> salvo1_1_locs = new LinkedList<String>() {};
            salvo1_1_locs.add("B5"); salvo1_1_locs.add("I5");
            Salvo salvo1_1 = new Salvo(gp1, 1, salvo1_1_locs);

            List<String> salvo1_2_locs = new LinkedList<String>() {};
            salvo1_2_locs.add("C7"); salvo1_2_locs.add("I6");
            Salvo salvo1_2 = new Salvo(gp1, 2, salvo1_2_locs);

            List<String> salvo1_3_locs = new LinkedList<>();
            salvo1_3_locs.add("E2"); salvo1_3_locs.add("G8");
            Salvo salvo1_3 = new Salvo(gp1, 3, salvo1_3_locs);

            /** Linking each salvo to its gamePlayer and vice-versa*/
            gp1.addSalvo(salvo1_1);
            gp1.addSalvo(salvo1_2);
            gp1.addSalvo(salvo1_3);

            /** saving salvos to their repository */
            salvos.save(salvo1_1);
            salvos.save(salvo1_2);
            salvos.save(salvo1_3);

            /** Create and Save Salvos for gp2 */
            List<String> salvo2_1_locs = new LinkedList<String>() {};
            salvo2_1_locs.add("A2"); salvo2_1_locs.add("A3");
            Salvo salvo2_1 = new Salvo(gp2, 1, salvo2_1_locs);
            gp2.addSalvo(salvo2_1); salvos.save(salvo2_1);
            List<String> salvo2_2_locs = new LinkedList<String>() {};
            salvo2_2_locs.add("D4"); salvo2_2_locs.add("E6");
            Salvo salvo2_2 = new Salvo(gp2, 2, salvo2_2_locs);
            gp2.addSalvo(salvo2_2); salvos.save(salvo2_2);
            List<String> salvo2_3_locs = new LinkedList<>();
            salvo2_3_locs.add("G1"); salvo2_3_locs.add("A9");
            Salvo salvo2_3 = new Salvo(gp2, 3, salvo2_3_locs);
            gp2.addSalvo(salvo2_3); salvos.save(salvo2_3);

        };
    }


}
