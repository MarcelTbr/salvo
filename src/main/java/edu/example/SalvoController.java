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

    @Autowired
    private PlayerRepository playerRepo;

    @Autowired
    private SalvoRepository salvoRepo;

    /**
     * it has to return an object (we want the ids)
     * because it can have many properties and methods
     * */
    @RequestMapping("/games")
    public LinkedList<Object> getGames() {

        /**
         * first we get a list of Game instances through the GameRepository
         * Then we create an empty List of Objects to store the newGames (a gameDTO)
         * to show at the endpoint
         * */
        LinkedList<Object> gamesDTO = getGamesDTO();
        return gamesDTO;
    }

    private LinkedList<Object> getGamesDTO() {
        List<Game> gamesList = gameRepo.findAll();
        LinkedList<Object> gamesDTO = new LinkedList<Object>();
        gamesList.stream()
               .map(g -> gamesDTO.add(getNewGame(g)))
               .collect(Collectors.toList());
        return gamesDTO;
    }

    private Set<Object> getGamePlayersDTO(Game g) {
        Set<GamePlayer> gamePlayerSet = new LinkedHashSet<>();
        gamePlayerSet = g.getGamePlayers();
        Set<Object> gamePlayersDTO = new LinkedHashSet<>();
        /** iterate gpSet, make & add playerDTO to gamePlayersDTO*/
        gamePlayerSet.stream()
                .map(gpl -> gamePlayersDTO.add(makePlayerDTO(gpl)))
                .collect(Collectors.toList());
        return gamePlayersDTO;
    }

    private Map<String, Object> getNewGame(Game g) {
        Set<Object> gamePlayersDTO = getGamePlayersDTO(g);
        Map<String, Object> newGame = new LinkedHashMap<String, Object>();
        newGame.put("game_id", g.getId());
        newGame.put("game_date", g.getCreationDate());
        /** Here we're nesting a set of objects: [gamePlayersDTO]
         *  into a map: [newGame]*/
        newGame.put("game_players", gamePlayersDTO);
        return newGame;
    }

    private Map<String, Object> makePlayerDTO(GamePlayer gp) {

        Player playerInGame = playerRepo.findById(gp.getGamePlayerId());
        Map<String, Object> playerDTO = new LinkedHashMap<>();
        playerDTO.put("id", playerInGame.getId());
        playerDTO.put("username", playerInGame.getUsername());
        playerDTO.put("email", playerInGame.getEmail());
        playerDTO.put("joining_date", gp.getPlayerJoinDate());
        return playerDTO;
    }

    /** Here we're mapping the endpoint for
     *  each GamePlayer instance
     *  with a PathVariable called gamePlayerId
     *  this is routed by Ship's getGamePlayerId
     *  getter method
     * */

    @RequestMapping("gamePlayers/{gamePlayerId}")
    public Map<String, Object> gamePlayerData( //Set<Ship> findShips

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

        /** Get salvoes */
        Set<Salvo> salvoes = new LinkedHashSet<Salvo>() {};
        salvoes = gamePlayer.getSalvos();

        /** Get player DTO*/
        Map<String,Object> player = makePlayerDTO(gamePlayer);

        /** Creating and filling the DTO to be displayed at this endpoint */
        Map<String, Object> gamePlayerDTO = getGamePlayerDTO(gamePlayerId, ships, salvoes, player);
        return gamePlayerDTO;
    }

    private Map<String, Object> getGamePlayerDTO(@PathVariable long gamePlayerId, Set<Ship> ships, Set<Salvo> salvoes, Map<String, Object> player) {
        Map<String, Object> gamePlayerDTO = new LinkedHashMap<>();

        gamePlayerDTO.put("gp_id", gamePlayerId);
        gamePlayerDTO.put("player", player);
        gamePlayerDTO.put("ships", ships);
        gamePlayerDTO.put("salvoes", salvoes);
        return gamePlayerDTO;
    }


    @RequestMapping("game_view/{gamePlayerId}")
    public Map<String, Object> gameView (

            @PathVariable long gamePlayerId ){

        GamePlayer gamePlayer = gamePlayerRepo.findByPlayerId(gamePlayerId);

        /** prepping the game players info for the DTO **/
        Set<GamePlayer> gamePlayersSet = gamePlayer.getGame().getGamePlayers();
//Note: this is a List of Maps. It must be defined this way List<Object> won't do the trick
        LinkedList<Map<String, Object>> gamePlayersList = new LinkedList<Map<String, Object>>();
        for (GamePlayer gp : gamePlayersSet) {
            Map<String, Object> GamePlayerDTO = getGamePlayerDTO(gp);
            gamePlayersList.add(GamePlayerDTO);
        }
        /** Creating and filling the endpoint's game_viewDTO*/
        Map<String, Object> game_viewDTO = getGameViewDTO(gamePlayer, gamePlayersList);
        return game_viewDTO;
    }

    private Map<String, Object> getGamePlayerDTO(GamePlayer gp) {
        Map<String, Object> GamePlayerDTO = new HashMap<>();
        GamePlayerDTO.put("gp_id", gp.getGamePlayerId());
        Map<String, Object> playerDTO = getPlayerDTO(gp);
        GamePlayerDTO.put("player", playerDTO);
        return GamePlayerDTO;
    }

    private Map<String, Object> getPlayerDTO(GamePlayer gp) {
        Map<String, Object> playerInfo = new HashMap<>();
        playerInfo.put("id", gp.getPlayer().getId());
        playerInfo.put("joining_date", gp.getPlayerJoinDate());
        playerInfo.put("username", gp.getPlayer().getUsername());
        playerInfo.put("email", gp.getPlayer().getEmail());
        return playerInfo;
    }

    private Map<String, Object> getGameViewDTO(GamePlayer gamePlayer, LinkedList<Map<String, Object>> gamePlayersList) {
        Map<String, Object> game_viewDTO = new LinkedHashMap<String, Object>() {};
        game_viewDTO.put("game_id", gamePlayer.getGame().getId());
        game_viewDTO.put("created", gamePlayer.getGame().getCreationDate());
        game_viewDTO.put("gamePlayers", gamePlayersList);
        /** Getting an instance of the game player's ships  */
        Set<Ship> playerShips =  gamePlayer.getShips();
        /** Creating and filling a List with the desired ship instance data*/
        List<Object> playerShipsList = getPlayerShipsList(playerShips);
        game_viewDTO.put("ships", playerShipsList);

        Set<Salvo> playerSalvoes = gamePlayer.getSalvos();
        List<Object> playerSalvoesList = getPlayerSalvoesList(playerSalvoes);
        game_viewDTO.put("salvoes", playerSalvoesList);

        return game_viewDTO;
    }

    private List<Object> getPlayerSalvoesList(Set<Salvo> playerSalvoes) {
                List<Object> playerSalvoesList = new LinkedList<>();
                playerSalvoes.stream()
                        .map(salvo -> playerSalvoesList.add(salvo))
                        .collect(Collectors.toList());

                return playerSalvoesList;
    }

    private List<Object> getPlayerShipsList(Set<Ship> playerShips) {
        List<Object> playerShipsList = new LinkedList<>();
        for(Ship ship : playerShips){
            Map<String, Object> playerShipsMap = new LinkedHashMap<>();
            playerShipsMap.put("type", ship.getShipType());
            playerShipsMap.put("locations", ship.getShipLocations());
            playerShipsList.add(playerShipsMap);
        }
        return playerShipsList;
    }


    @RequestMapping("/salvos")
    public List<Salvo> salvos(){


//        Salvo salvo = salvoRepo.findBySalvoId(salvoId);
        List<Salvo> salvoList = salvoRepo.findAll();

        return salvoList;

    }


    }
