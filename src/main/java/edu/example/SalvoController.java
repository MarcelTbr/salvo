package edu.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * Created by marceltauber on 14/12/16.
 */

@RequestMapping("/api")
@RestController
public class SalvoController {


    @Autowired
    private GamePlayerRepository GamePlayerRepo;

    @Autowired
    private GameRepository GameRepo;


    @RequestMapping("/games")
    public List<Game> getGames(List<Game> games) {


        /**
         * Streams are declared in the Game and Player Classes
         * **/
        return GameRepo.findAll();
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



    @RequestMapping("/gameplayers")
    public List<GamePlayer> getGamePlayers(List<GamePlayer> gameplayers) {

        return GamePlayerRepo.findAll();
    }

    }
