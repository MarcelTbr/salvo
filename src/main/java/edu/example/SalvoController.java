package edu.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
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


    //Calendar now = new GregorianCalendar(TimeZone.getTimeZone("ES")); //TODO: Refactor Date (format at endpoint)

    @Autowired
    private GameRepository gameRepo;

    @Autowired
    private GamePlayerRepository gamePlayerRepo;



    /**
     * it has to return an object (we want the ids)
     * because it can have many properties and methods
     * */
    @RequestMapping("/games")
    public List<Object> getGames() {

        /**
         * first we get a list of Game instances through the GameRepository
         * */
        List<Game> gamesList = gameRepo.findAll();


        /**
         * Then we create an empty List of Objects to store the games we will get later.
         * */
        LinkedList<Object> gamesDTO = new LinkedList<Object>(); //DTO

        /**
         * next we loop through that list of [Game Instances]
         * creating a new [HashMap] with Key: String(a meaningful name)
         * Value: Object (different each time)
         * and we fill it with the Game.id, Game.creationDate
         * and also the Players Instance we get by calling the method
         * Game.getGamePlayers()
         * */
       for (Game g : gamesList) {

        Map<String, Object> newGame = new LinkedHashMap<String, Object>();
           newGame.put("game_id", g.getId());
           newGame.put("game_date", g.getCreationDate());
           newGame.put("game_players", g.getGamePlayers().stream() // convert to stream
                   .map(gp_in_g -> gp_in_g.getPlayerInfo(gp_in_g)) // Game/gamePlayers.PlayerInfo(Game/GamePlayers)
                   .collect(Collectors.toList())); //close the stream

           gamesDTO.add(newGame);
       }


        return gamesDTO;


    }

    /** Here we're mapping the endpoint for
     *  each GamePlayer instance
     *  with a PathVariable called gamePlayerId
     *  this is routed by Ship's getGamePlayerId
     *  getter method
     * */

    @RequestMapping("gamePlayers/{gamePlayerId}")
    public Set<Ship> findShips(

            @PathVariable long gamePlayerId){

        /** First, we create a new instance of a GamePlayer*/

        GamePlayer gamePlayer = new GamePlayer();

        /** Then, we create a new empty set of type Ship instances*/
         Set<Ship> ships =  new HashSet<>() ;

        /** our gamePlayer instance gets filled
         *  with a concrete instance of GamePlayer
         *  defined by the concrete Player.id instance property
         *  stored in gamePlayerId */

        /** That is routed with the @-Auto-wired annotation earlier
         *  on the GamePlayerRepository instance: gamePlayerRepo
         *  All we're doing is calling the data from gamePlayerRepo
         *  and storing it into the instance. In this case just the
         *  gamePlayer's Id, according to the id given through url*/
        gamePlayer = gamePlayerRepo.findByPlayerId(gamePlayerId);

        /** Now all we have to do is fill our List<Ship> instance
         *  with the Ship instances collected through our
         *  concrete GamePlayer's instance method getShips()
         *  and return it to the routed endpoint*/
        ships= gamePlayer.getShips();

        return ships;
    }


    @RequestMapping("game_view/{gamePlayerId}")
    public Map<String, Object> gameView (

            @PathVariable long gamePlayerId ){

        GamePlayer gamePlayer = new GamePlayer();
        gamePlayer = gamePlayerRepo.findByPlayerId(gamePlayerId);
        Set<GamePlayer> gamePlayersSet = new HashSet<>();
        gamePlayersSet = gamePlayer.getGame().getGamePlayers();

        /** prepping the game players info for the DTO **/
        LinkedList<Object> gamePlayersList = new LinkedList<Object>();
        for (GamePlayer gp : gamePlayersSet) {

            Map<String, Object> newGamePlayer = new HashMap<>();
            Map<String, Object> playerInfo = new HashMap<>();


            newGamePlayer.put("gp_id", gp.getGamePlayerId());

            playerInfo.put("id", gp.getPlayer().getId());
            playerInfo.put("joining_date", gp.getPlayerJoinDate());
            playerInfo.put("username", gp.getPlayer().getUsername());
            playerInfo.put("email", gp.getPlayer().getEmail());

            newGamePlayer.put("player", playerInfo);

            gamePlayersList.add(newGamePlayer);


        }

        /** Creating and filling the endpoint's DTO*/
        Map<String, Object> game_viewDTO = new LinkedHashMap<String, Object>() {
        };

        game_viewDTO.put("id", gamePlayer.getGamePlayerId());
        game_viewDTO.put("created", gamePlayer.getGame().getCreationDate());
        game_viewDTO.put("gamePlayers", gamePlayersList);
        /** Getting an instance of the game player's ships  */
        Set<Ship> playerShips = new HashSet<>();
        playerShips = gamePlayer.getShips();
        /** Creating and filling a List with the desired ship instance data*/
        List<Object> playerShipsList = new LinkedList<>();
//        for(Ship ship : playerShips ){ playerShipsList.add(ship.getShipType());}
//        game_viewDTO.put("ships", playerShipsList );

        for(Ship ship : playerShips){
            Map<String, Object> playerShipsMap = new LinkedHashMap<>();
            playerShipsMap.put("type", ship.getShipType());
            playerShipsMap.put("locations", ship.getShipLocations());
            playerShipsList.add(playerShipsMap);
        }
        game_viewDTO.put("ships", playerShipsList);
                return game_viewDTO;
    };


    }
