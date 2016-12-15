package edu.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
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

        List<Game> gamesList = GameRepo.findAll();
        //Map<long, Game> gameObject = Map<long id, List gamesList>;

        List<Object> games = new ArrayList<Object>();

       /* for (Game g : gamesList) {

            games.add(xxx);  //xxx map of id Map<long id, Game game>

        }*/

        //crear variable lista de objetos
        games = gamesList //list of Game objects w/ id & creationDate
                .stream()  // convert to stream
                .map(g -> g.getId())   //maps the requested method to a List long
                .collect(Collectors.toList());  // closes the stream returning a List of Objects

        /**
         * Streams are declared in the Game and Player Classes
         * **/


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
