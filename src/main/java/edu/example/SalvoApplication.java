package edu.example;

/**
 * SpringBoot Library link for using CommandLineRunner
 */


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/**
 * Springboot Library link for using BEANS
 * */
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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
                                      SalvoRepository salvos, GameScoreRepository gameScores) {
        return (args) -> {

            Date date = new Date();

            /** Create and Save Initial Players */

            Player player1 = new Player("Jack", "jack@daniels.com", "iamjack");
            Player player2 = new Player("Jane", "jane@daniels.com", "itsmejane");
            Player player3 = new Player("Jimi Hendrix", "jimi@hendrix.org", "littlewing");
            Player player4 = new Player("Tony Iommi", "tonyIommi@blacksabath.net", "paranoid");
            Player player5 = new Player("Rafa Nadal", "rafa@nadal.es", "soytenista");
            Player player6 = new Player("Roger Federer", "roger@federer.ch", "ichbinderbeste");


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
            Game game4 = new Game(Date.from(date.toInstant().plusSeconds(10800))); games.save(game4);
            Game game5 = new Game(Date.from(date.toInstant().plusSeconds(10800))); games.save(game5);
            Game game6 = new Game(Date.from(date.toInstant().plusSeconds(10800))); games.save(game6);

            /** Create 5 more dates with 30min difference each (1800s) */

            Date date2 = Date.from(date.toInstant().plusSeconds(1800));
            Date date3 = Date.from(date.toInstant().plusSeconds(3600));
            Date date4 = Date.from(date.toInstant().plusSeconds(5400));
            Date date5 = Date.from(date.toInstant().plusSeconds(7200));
            Date date6 = Date.from(date.toInstant().plusSeconds(9000));
            Date date7 = Date.from(date.toInstant().plusSeconds(9900));
            Date date8 = Date.from(date.toInstant().plusSeconds(10800));
            Date date9 = Date.from(date.toInstant().plusSeconds(11700));
            Date date10 = Date.from(date.toInstant().plusSeconds(12600));
            Date date11 = Date.from(date.toInstant().plusSeconds(13500));
            Date date12 = Date.from(date.toInstant().plusSeconds(12600));

            /**
             *  Create GamePlayers and assign them to variables
             * */
            GamePlayer gp1 = new GamePlayer(date, player1, game1);
            GamePlayer gp2 = new GamePlayer(date2, player2, game1);
            GamePlayer gp3 = new GamePlayer(date3, player3, game2);
            GamePlayer gp4 = new GamePlayer(date4, player4, game2);
            GamePlayer gp5 = new GamePlayer(date5, player5, game3);
            GamePlayer gp6 = new GamePlayer(date6, player6, game3);
            GamePlayer gp7 = new GamePlayer(date7, player1, game4);
            GamePlayer gp8 = new GamePlayer(date8, player3, game4);
            GamePlayer gp9 = new GamePlayer(date9, player2, game5);
            GamePlayer gp10 = new GamePlayer(date10, player5, game5);
            GamePlayer gp11 = new GamePlayer(date11, player4, game6);
            GamePlayer gp12 = new GamePlayer(date12, player6, game6);


            gamePlayers.save(gp1);
            gamePlayers.save(gp2);
            gamePlayers.save(gp3);
            gamePlayers.save(gp4);
            gamePlayers.save(gp5);
            gamePlayers.save(gp6);
            /** Important Note: this Games will not have any salvos or ships, they're just for the leaderboard */
            gamePlayers.save(gp7);gamePlayers.save(gp8); gamePlayers.save(gp9); gamePlayers.save(gp10);
            gamePlayers.save(gp11); gamePlayers.save(gp12);


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


            /** Create some fake GameScore Instances */

            GameScore gs1_1 = new GameScore(game1, player1, 0);  gameScores.save(gs1_1);
            GameScore gs1_2 = new GameScore(game1, player2, 1);  gameScores.save(gs1_2);
            GameScore gs2_1 = new GameScore(game2, player3, 0.5);gameScores.save(gs2_1);
            GameScore gs2_2 = new GameScore(game2, player4, 0.5);gameScores.save(gs2_2);
            GameScore gs3_1 = new GameScore(game3, player5, 0);  gameScores.save(gs3_1);
            GameScore gs3_2 = new GameScore(game3, player6, 1);  gameScores.save(gs3_2);
            GameScore gs4_1 = new GameScore(game4, player1, 0.5);gameScores.save(gs4_1);
            GameScore gs4_2 = new GameScore(game4, player3, 0.5);gameScores.save(gs4_2);
            GameScore gs5_1 = new GameScore(game5, player2, 1);  gameScores.save(gs5_1);
            GameScore gs5_2 = new GameScore(game5, player5, 0);  gameScores.save(gs5_2);
            GameScore gs6_1 = new GameScore(game6, player4, 0.5);gameScores.save(gs6_1);
            GameScore gs6_2 = new GameScore(game6, player6, 0.5);gameScores.save(gs6_2);




        };




    }


}

@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {
    @Autowired
    PlayerRepository playerRepo;

    @Bean
    UserDetailsService UserDetailsService(){

        return new UserDetailsService() {


            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                List<Player> players = playerRepo.findByUsername(username);
                if (!players.isEmpty()) {
                    Player player = players.get(0); //get the first (and ideally the only) player of the list
                    return new User(player.getUsername(), player.getPassword(),
                            AuthorityUtils.createAuthorityList("USER", "PLAYER"+player.getId()));
                } else {
                    throw new UsernameNotFoundException("Unknown user: " + username);
                }
            }
        };

    }

}

@EnableWebSecurity
@Configuration
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    PlayerRepository playerRepo;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/login.html", "/js/**",
                        "/api/players", "/api/games", "/api/scores",
                        "/games.css", "/games.html", "/api/tests", "/api/games/**").permitAll()
                .antMatchers("/**").hasAuthority("USER")
                .and().formLogin();

        http.formLogin()
                .loginPage("/app/login");
                /*.anyRequest().fullyAuthenticated().
                and().httpBasic();*/

        http.logout().logoutUrl("/app/logout");

        // turn off checking for CSRF tokens
        http.csrf().disable();

        // if user is not authenticated, just send an authentication failure response
        http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // if login is successful, just clear the flags asking for authentication
        http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

        // if login fails, just send an authentication failure response
        http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // if logout is successful, just send a success response
        http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
    }

    /** Takes a request as an argument. Gets the session from the request. If it exists, its Auth attributes get removed*/
    private void clearAuthenticationAttributes(HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
    }
}