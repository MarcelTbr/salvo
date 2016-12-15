package edu.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Created by marceltauber on 14/12/16.
 */

@RequestMapping("/api")
@RestController
public class SalvoController {


    //@Autowired
    //private GamePlayerRepository GamePlayerRepo;

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
        List<Object> games = new ArrayList<Object>();

        /**
         * next we loop through that list of [Game Instances]
         * creating a new [HashMap] with Key: String(a meaningful name)
         * Value: Object (different each time)
         * and we fill it with the Game.id, Game.creationDate
         * and also the Players Instance we get by calling the method
         * Game.getPlayersList()
         * */
       for (Game g : gamesList) {

           HashMap<String, Object> newGame = new HashMap<String, Object>();
           newGame.put("id", g.getId());  //xxx map of id Map<long id, Game game>
           newGame.put("date", g.getCreationDate());
           newGame.put("game_players", g.getPlayersList().stream()
                   .map(gp_in_g -> gp_in_g.getPlayerInfo(gp_in_g))
                   .collect(Collectors.toList()));

           games.add(newGame);
        }



        //crear variable lista de objetos
       /** games = gamesList //list of Game objects w/ id & creationDate
                .stream()  // convert to stream
                .map(g -> g.getId())   //maps the requested method to a List long
                .collect(Collectors.toList());  // closes the stream returning a List of Objects




        return games;
        */

        return games;
                /*.stream()
                .map(b -> b.getClass()) *//* games -> makeGamesDTO(games)*//*
                .collect(Collectors.toList());*/

        /*
       public Set<BillingType> getBillingTypes(List<Billing> billings) {
        return
        billings.stream()
                .filter(b -> b.getType() == Billing.GROCERY)
                .sorted((b1, b2) -> b2.getValue() - b1.getValue())
                .collect(toList());*/
        /* Before:  public List<GamePlayer> getAll() { return repo.findAll();} */


    }


/*
    @RequestMapping("/gameplayers")
    public List<GamePlayer> getGamePlayers(List<GamePlayer> gameplayers) {

        //return GamePlayerRepo.findAll();
    }*/

    }
