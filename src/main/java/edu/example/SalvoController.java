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

    @RequestMapping("/scores")
    public Map<String, List<Double>> getScores(){

        Map<String, List<Double>> scoresDTO = new HashMap<>();
        List<Player> players = playerRepo.findAll();

        players.stream().forEach(p -> scoresDTO.put (p.getId()+": " + p.getUsername(), getTotalScoresList(p, p.getGameScoresSet())));

        return scoresDTO;

    }

    private List<Double>  getTotalScoresList(Player p, Set<GameScore> gameScoreSet){
       List<Double> totalsArray = new LinkedList<>();
            totalsArray.add(getTotalScore(gameScoreSet ));
            totalsArray.add(getTotalWins(gameScoreSet));
            totalsArray.add(getTotalLosses(gameScoreSet));
            totalsArray.add(getTotalTies(gameScoreSet));
        return totalsArray;
    }

    private double getTotalWins(Set<GameScore> gameScoreSet){

        double totalWins = 0;
        List<Double> winsList = gameScoreSet.stream().filter( gs -> gs.getScore() == 1)
                .map(gs -> gs.getScore()).collect(Collectors.toList());

        for(double i = 0; i < winsList.size();i++){ totalWins++;}
        return totalWins;
    }

    private double getTotalLosses(Set<GameScore> gameScoreSet){
        double totalLosses = 0;
        List<Double> winsList = gameScoreSet.stream().filter( gs -> gs.getScore() == 0)
                .map(gs -> gs.getScore()).collect(Collectors.toList());
        for(double i = 0; i < winsList.size();i++){ totalLosses++;}
        return totalLosses;
    }
    private double getTotalTies(Set<GameScore> gameScoreSet){
        double totalTies = 0;
        List<Double> winsList = gameScoreSet.stream().filter( gs -> gs.getScore() == 0.5)
                .map(gs -> gs.getScore()).collect(Collectors.toList());
        for(double i = 0; i < winsList.size();i++){ totalTies++;}
        return totalTies;
    }

    private double getTotalScore(Set<GameScore> gameScoreList) {
        ArrayList<Double> scores = new ArrayList<>();
        gameScoreList.stream().forEach(gs -> scores.add(addScore(gs)) );
        double totalScore = 0.0;
        for(double d : scores)
            totalScore += d;
        return totalScore;
    }

    private double addScore(GameScore gs) {

        if ( isNull(gs.getScore()) ){return 0.0; } else {return gs.getScore();}
    }

    private boolean isNull(Double aDouble) {
        return aDouble == null;
    }

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

        long playerId = getPlayerId(gp);
        Player playerInGame = playerRepo.findById(playerId);
        Map<String, Object> playerDTO = new LinkedHashMap<>();
        playerDTO.put("id", playerInGame.getId());
        playerDTO.put("username", playerInGame.getUsername());
        playerDTO.put("email", playerInGame.getEmail());
        playerDTO.put("joining_date", gp.getPlayerJoinDate());
        return playerDTO;
    }

    private long getPlayerId(GamePlayer gp) {

        return gp.getPlayer().getId();
    }

    /** Here we're mapping the endpoint for
     *  each GamePlayer instance
     *  with a PathVariable called gamePlayerId
     *  this is routed by Ship's getGamePlayerId
     *  getter method
     * */

    @RequestMapping("gamePlayers/{gamePlayerId}")
    public Map<String, Object> gamePlayerData(

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
//         gamePlayer = gamePlayerRepo.findByPlayerId(gamePlayerId);
        gamePlayer = gamePlayerRepo.findById(gamePlayerId);
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

//        GamePlayer gamePlayer = gamePlayerRepo.findByPlayerId(gameViewPlayerId);
        GamePlayer gamePlayer = gamePlayerRepo.findById(gamePlayerId);
        GamePlayer enemyPlayer = new GamePlayer();

        /** prepping the game players info for the DTO **/
        Set<GamePlayer> gamePlayersSet = gamePlayer.getGame().getGamePlayers();
//Note: this is a List of Maps. It must be defined this way List<Object> won't do the trick
        LinkedList<Map<String, Object>> gamePlayersList = new LinkedList<Map<String, Object>>();
        for (GamePlayer gp : gamePlayersSet) {
            Map<String, Object> GamePlayerDTO = getGamePlayerDTO(gp);
            gamePlayersList.add(GamePlayerDTO);
            if(gp.getGamePlayerId() != gamePlayerId){enemyPlayer = gp;}
        }
        /** Creating and filling the endpoint's game_viewDTO*/
        Map<String, Object> game_viewDTO = getGameViewDTO(gamePlayer, enemyPlayer, gamePlayersList);
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

    private Map<String, Object> getGameViewDTO(GamePlayer gamePlayer, GamePlayer enemyPlayer, LinkedList<Map<String, Object>> gamePlayersList) {
        Map<String, Object> game_viewDTO = new LinkedHashMap<String, Object>() {};
        game_viewDTO.put("game_id", gamePlayer.getGame().getId());
        game_viewDTO.put("created", gamePlayer.getGame().getCreationDate());
        game_viewDTO.put("gamePlayers", gamePlayersList);
        /** Getting an instance of the players' ships  */
        Set<Ship> playerShips =  gamePlayer.getShips();
        /** Creating and filling a List with the desired ship instance data*/
        List<Object> playerShipsList = getPlayerShipsList(playerShips);
        List<Object> enemyShipsList = getPlayerShipsList(enemyPlayer.getShips());
        game_viewDTO.put("ships", playerShipsList);
        game_viewDTO.put("enemy_ships", enemyShipsList);
        /** getting the salvoesDTO {"turn" : "salvoes-array" }*/
        Object salvoesDTO = getAllPlayerSalvos(gamePlayer);
        game_viewDTO.put("salvoes", salvoesDTO);
        game_viewDTO.put("enemy_salvoes", getAllPlayerSalvos(enemyPlayer));

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
    public List<Object> getSalvosDTO() {

                List<Game> gameList = gameRepo.findAll();
               List<Object> salvosDTO = new LinkedList<>();

                gameList.stream()
                        .map(g -> salvosDTO.add(getGameSalvoDTO(g)) )
                        .collect(Collectors.toList());

                return salvosDTO;
    }

    private Map<String, Object> getGameSalvoDTO(Game g) {
                Map<String, Object> gameSalvoDTO = new LinkedHashMap<>();
                long game_id = g.getId();
                gameSalvoDTO.put("game", game_id);
                Set<GamePlayer> gamePlayerSet = g.getGamePlayers();
                List<Object> plyrSalvosList = new LinkedList<>();
                List<Player> playerList = g.getPlayers();
                gamePlayerSet.stream()
                        .map(gp -> plyrSalvosList.add(getAllPlayerSalvos(gp)) )
                        .collect(Collectors.toList());

                gameSalvoDTO.put(playerList.get(0).getUsername(),  plyrSalvosList.get(0));
                gameSalvoDTO.put(playerList.get(1).getUsername(), plyrSalvosList.get(1) );
                return gameSalvoDTO;
    }

    private Object getAllPlayerSalvos(GamePlayer gp) {
                Map<String, Object> allPlayerSalvos = new HashMap<>();
                Set<Salvo> salvoSet = gp.getSalvos();
                salvoSet.stream()
                        .map (salvo -> allPlayerSalvos.put(String.valueOf(salvo.getTurn()), salvo.getSalvoLocations() ) )
                        .collect(Collectors.toList());

                return allPlayerSalvos;
    }


    }
