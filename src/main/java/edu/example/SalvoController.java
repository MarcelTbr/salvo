package edu.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by marceltauber on 14/12/16.
 */

@RequestMapping("/api")
@RestController
public class SalvoController {


    //Calendar now = new GregorianCalendar(TimeZone.getTimeZone("ES"));

    @Autowired
    private GameRepository GameRepo;



    /**
     * it has to return an object (we want the ids)
     * because it can have many properties and methods
     * */
    @RequestMapping("/games")
    public List<Object> getGames() {

        /**
         * first we get a list of Game instances through the GameRepository
         * */
        List<Game> gamesList = GameRepo.findAll();


        /**
         * Then we create an empty List of Objects to store the games we will get later.
         * */
        LinkedList<Object> games = new LinkedList<Object>();

        /**
         * next we loop through that list of [Game Instances]
         * creating a new [HashMap] with Key: String(a meaningful name)
         * Value: Object (different each time)
         * and we fill it with the Game.id, Game.creationDate
         * and also the Players Instance we get by calling the method
         * Game.getPlayersList()
         * */
       for (Game g : gamesList) {

        Map<String, Object> newGame = new LinkedHashMap<String, Object>();
           newGame.put("game_id", g.getId());
           newGame.put("game_date", g.getCreationDate());
           newGame.put("game_players", g.getPlayersList().stream() // convert to stream
                   .map(gp_in_g -> gp_in_g.getPlayerInfo(gp_in_g)) // Game/gamePlayers.PlayerInfo(Game/GamePlayers)
                   .collect(Collectors.toList())); //close the stream

           games.add(newGame);
       }


        return games;


    }

    }
