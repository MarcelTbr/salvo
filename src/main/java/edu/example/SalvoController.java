package edu.example;


import jdk.nashorn.internal.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
    private ShipRepository shipRepo;

    @Autowired
    private SalvoRepository salvoRepo;

    /** ======== ENDPOINTS ======== */

    @RequestMapping("/tests")
    public List<Map<String, Object>> getOutput(Authentication auth){

        List<Player> playerList = playerRepo.findAll();
        List<Map<String, Object>> playerDTO =
                playerList.stream().map(pl -> getUserDTO(pl)).collect(Collectors.toList());


        return playerDTO;
    }

    @RequestMapping ( value="games/players/{gamePlayerId}/salvos", method= RequestMethod.POST )
    public ResponseEntity<Map<String, Object>> submitSalvos(@PathVariable long gamePlayerId,
                                                            @RequestBody Object salvos, Authentication auth){
        GamePlayer gamePlayer = gamePlayerRepo.findById(gamePlayerId);

        if (auth == null || incorrectUser(auth, gamePlayerId)) {
            return new ResponseEntity<Map<String, Object>>(makeMap("backend", "sorry, not authorized"), HttpStatus.UNAUTHORIZED);
        } else if (noSuchGP(gamePlayerId)){
            return new ResponseEntity<Map<String, Object>>(makeMap("backend", "sorry, game player doesn't exist"), HttpStatus.FORBIDDEN);
        }else {
            String salvos_str = salvos.toString();
            String turn = salvos_str.substring(1, salvos_str.indexOf('='));
            String salvo_locs = salvos_str.substring(salvos_str.indexOf('[')+1, salvos_str.indexOf(']'));

//            JSONParser parser = new JSONParser(); //TODO explore this parser method and JSON library

            String [] locations = salvo_locs.split(", "); //the blank space is important here!
            Salvo submitted_salvo = new Salvo(gamePlayer, Long.parseLong(turn), Arrays.asList(locations) );
            salvoRepo.save(submitted_salvo);
            System.out.println(salvos_str + " ; turn: " + turn + "; salvo_locs: " + salvo_locs);

            return new ResponseEntity<Map<String, Object>>(makeMap("submitted_salvos", salvos ),HttpStatus.CREATED);
        }

    }

    @RequestMapping ("games/players/{gamePlayerId}/salvos")
    public Object getPlayersSalvos (@PathVariable long gamePlayerId, Authentication auth){

        GamePlayer gamePlayer = gamePlayerRepo.findById(gamePlayerId);
        return getAllPlayerSalvos(gamePlayer);

    }

    @RequestMapping ("games/players/{gamePlayerId}/ships")
    public Set<Ship> getShips (@PathVariable long gamePlayerId, Authentication auth){
    if ( existingUsername(auth.getName()) && !incorrectUser(auth, gamePlayerId)) {
        GamePlayer gamePlayer = gamePlayerRepo.findById(gamePlayerId);
        return gamePlayer.getShips();
    }else { return new HashSet<Ship>();}

    }

    @RequestMapping (value="games/players/{gamePlayerId}/ships", method= RequestMethod.POST )
    public ResponseEntity<Map<String, Object>> saveShips(@PathVariable long gamePlayerId, @RequestBody List<Ship> ships, Authentication auth) { // @RequestBody Ship ship,
        String newLine = System.getProperty("line.separator");

        System.out.println("Ships found:");
        System.out.print(ships);
        System.out.println(newLine);

        if (auth == null || noSuchGP(gamePlayerId) || incorrectUser(auth, gamePlayerId)) {

            return new ResponseEntity<Map<String, Object>>(makeMap("backend", "sorry, not authorized"), HttpStatus.UNAUTHORIZED);
        } else if (shipsPlaced(gamePlayerId)){
            return new ResponseEntity<Map<String, Object>>(makeMap("backend", "Not possible, ships were already placed."), HttpStatus.FORBIDDEN);
        }else{
                for(Ship ship: ships) {
            ship.setGamePlayer(gamePlayerRepo.findById(gamePlayerId)); shipRepo.save(ship);
                }
            return new ResponseEntity<Map<String, Object>>(makeMap("backend", "ships created!"), HttpStatus.CREATED);
        }
    }



    @RequestMapping(value="games/{gameId}/players", method= RequestMethod.POST)
    public ResponseEntity <Map<String, Object>> saveGamePlayer( @PathVariable long gameId, Authentication auth){
    Game game = gameRepo.findById(gameId);
    //TODO: check if you can post two games with the same id
        System.out.print("game with id: " + gameId);
        System.out.print(game);
        String username = auth.getName();
    Player player = playerRepo.findByUsername(username).get(0);
    List<Player> players = game.getPlayers();
        //check if user is logged in
        if(auth != null){
            //check if game exists
            if( game != null) {
                System.out.print("game exists!");
                if(playerVersusHimself(username, players)) {
                    return new ResponseEntity<>(makeMap( "backend","Sorry, playing against yourself is no fun..."), HttpStatus.FORBIDDEN);
                }else if ( players.stream().count() < 2 ) {
                    //create and save game player
                    GamePlayer gamePlayer = new GamePlayer(new Date(), player, game);
                    gamePlayerRepo.save(gamePlayer);
                    return new ResponseEntity<>(makeMap("gp_id", gamePlayer.getGamePlayerId()), HttpStatus.CREATED);
                } else {
                    return new ResponseEntity<>(makeMap( "backend","Sorry, game is full!"), HttpStatus.FORBIDDEN);
                }
            }else{
                return new ResponseEntity<>(makeMap( "backend","Game not found!"), HttpStatus.FORBIDDEN);
            }
        } else { return new ResponseEntity<>(makeMap( "backend","User not logged in!"), HttpStatus.UNAUTHORIZED); }

    }

    private boolean playerVersusHimself(String username, List<Player> players) {

       long repeatedPlayers = players.stream().filter( pl -> pl.getUsername() == username).count();

       if(repeatedPlayers > 0){ return true; } else {return false;}

    }

    @RequestMapping(value="/players", method= RequestMethod.POST)
        public ResponseEntity<Map<String,Object>> savePlayer(@RequestParam("username") String username,
                                                  @RequestParam("email")String email,
                                                  @RequestParam("password") String password
                                                 ){

            return makePlayer(username, email, password);
        }

    @RequestMapping(value="/games", method= RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> saveGame(Authentication auth){
        Player user = playerRepo.findByUsername(auth.getName()).get(0);
        Game newGame = new Game(new Date());
        gameRepo.save(newGame);
        GamePlayer newGamePlayer = new GamePlayer(new Date(), user, newGame);
        gamePlayerRepo.save(newGamePlayer);

        return new ResponseEntity<Map<String, Object>>(makeMap("gp_id", newGamePlayer.getGamePlayerId()), HttpStatus.CREATED);
    }

    @RequestMapping("/games")
    public  Map<String, Object> getGames(Authentication auth) {
        Map<String, Object> gamesAuthDTO = new HashMap<>();

        if (auth != null) {
            List<Player> playerList = playerRepo.findByUsername(auth.getName());
            Player user = playerList.get(0);
            Map<String, Object> userDTO = getUserDTO(user);
            LinkedList<Object> userGamesDTO = getUserGamesDTO(user);
            gamesAuthDTO.put("player", userDTO);
            gamesAuthDTO.put("player_games", userGamesDTO);
            gamesAuthDTO.put("games",  getGamesDTO());
            gamesAuthDTO.put("auth", auth);
            return gamesAuthDTO;
        }else {
            gamesAuthDTO.put("games",  getGamesDTO());
            gamesAuthDTO.put("auth", auth);
                 return gamesAuthDTO;
        }



    }

    @RequestMapping("/scores")
    public Map<String, List<Double>> getScores(){

        Map<String, List<Double>> scoresDTO = new HashMap<>();
        List<Player> players = playerRepo.findAll();

        players.stream().forEach(p -> scoresDTO.put (p.getId()+": " + p.getUsername(), getTotalScoresList(p, p.getGameScoresSet())));

        return scoresDTO;
    }

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

    @RequestMapping("game_view/{gamePlayerId}")
    public ResponseEntity<Map<String, Object>> gameView (

            @PathVariable long gamePlayerId, Authentication auth ){

                if(gamePlayerAuthorization(gamePlayerId, auth)){

                    return new ResponseEntity<Map<String, Object>>(getGameViewDTO(gamePlayerId), HttpStatus.ACCEPTED);

                }else {
                    return new ResponseEntity<Map<String, Object>>(
                            makeMap("unauthorize", "it's not allowed to see other users data"),
                            HttpStatus.UNAUTHORIZED);
                }


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

    /** ======== METHODS ======== */

    private boolean shipsPlaced(long gamePlayerId) {
        long totalShips = gamePlayerRepo.findById(gamePlayerId).getShips().stream().count();
        if(totalShips < 5) { return false; } else { return true;}
    }

    private boolean incorrectUser(Authentication auth, long gamePlayerId) {
        Player player = playerRepo.findByUsername(auth.getName()).get(0);
        Set<GamePlayer> gamePlayerSet = player.getGamePlayers();
        String newLine = System.getProperty("line.separator");
        long matchingGP = gamePlayerSet.stream().filter(gp -> gp.getGamePlayerId() == gamePlayerId ).count();
        System.out.println("gamePlayer found! "); System.out.println("gamePlayer is: ");
        System.out.print(gamePlayerId + newLine);

        if(matchingGP == 1){ return false; } else {

            System.out.println("GamePlayer NOT Found");System.out.println(newLine);
            return true; }
    }

    private boolean noSuchGP(long gamePlayerId) {

        if ( gamePlayerRepo.findById(gamePlayerId) == null) {return  true; } else {return false; }

    }

    private boolean gamePlayerAuthorization(long gamePlayerId, Authentication auth) {

        if( gamePlayerRepo.findById(gamePlayerId).getPlayer().getUsername() == auth.getName() ) { return true; }
        else{ return false; }
    }

    private Map<String, Object> getGameViewDTO(@PathVariable long gamePlayerId) {
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

    private ResponseEntity<Map<String,Object>> makePlayer(String username, String email, String password){

        if(existingUsername(username)){
            return new ResponseEntity<>(makeMap("error",
                    "Username \"" + username + "\" is being used. Enter a different username."),
                    HttpStatus.UNAUTHORIZED);
        }else{
            playerRepo.save(new Player (username, email, password));
            return new ResponseEntity<>(makeMap("success",
                    "Username is available. New user \"" + username + "\" created."),
                    HttpStatus.ACCEPTED);
        }


    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    private boolean existingUsername(String username) {
       List<Player> existingPlayer = playerRepo.findByUsername(username);
        if (!existingPlayer.isEmpty()){return true;} else {return false; }
    }

    private LinkedList<Object> getUserGamesDTO(Player user) {
        LinkedList<Object> userGamesDTO = new LinkedList<>();
        List<Game> userGames =  user.getGamePlayers().stream()
                .map( gp ->   getGame(gp) ).collect(Collectors.toList());
        /** Getting a DTO of only the active user's games */
        userGames.stream()
                .forEach(g -> userGamesDTO.add(getNewGame(g)));
        return  userGamesDTO;
    }

    private Game getGame(GamePlayer gp) {
        return gameRepo.findById( gp.getGame().getId() );
    }

    private Map<String,Object> getUserDTO(Player user) {
        Map<String, Object> userDTO = new HashMap<>();
        userDTO.put("id",user.getId());
        userDTO.put("username", user.getUsername());
        userDTO.put("email", user.getEmail());
        return  userDTO;
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

        List<Double> winsList = gameScoreSet.stream().filter( gs -> gs.getScore() == 1)
                .map(gs -> gs.getScore()).collect(Collectors.toList());

        return winsList.size();
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


    private LinkedList<Object> getGamesDTO() {
        List<Game> gamesList = gameRepo.findAll();
        LinkedList<Object> gamesDTO = new LinkedList<Object>();
        gamesList.stream()
               .map(g -> gamesDTO.add(getNewGame(g)))
               .collect(Collectors.toList());
        return gamesDTO;
    }

    private Set<Object> getGamePlayersDTO(Game g) {
        Set<GamePlayer> gamePlayerSet = g.getGamePlayers();
        Set<Object> gamePlayersDTO = new LinkedHashSet<>();
        /** iterate gpSet, make & add playerDTO to gamePlayersDTO*/
        gamePlayerSet.stream()
                .map(gpl -> gamePlayersDTO.add(makePlayerDTO(gpl)))
                .collect(Collectors.toList());
        return gamePlayersDTO;
    }

    private Map<String, Object> getNewGame(Game g) {
        Map<String, Object> newGame = new LinkedHashMap<String, Object>();
        newGame.put("game_id", g.getId());
        newGame.put("game_date", g.getCreationDate());
        /** Here we're nesting a set of objects: [gamePlayersDTO]
         *  into a map: [newGame]*/
        newGame.put("game_players", getGamePlayersDTO(g));
        return newGame;
    }

    private Map<String, Object> makePlayerDTO(GamePlayer gp) {

        long playerId = getPlayerId(gp);
        Player playerInGame = playerRepo.findById(playerId);
        Map<String, Object> playerDTO = new LinkedHashMap<>();
        playerDTO.put("gp_id", gp.getGamePlayerId());
        playerDTO.put("pl_id", playerInGame.getId());
        playerDTO.put("username", playerInGame.getUsername());
        playerDTO.put("email", playerInGame.getEmail());
        playerDTO.put("joining_date", gp.getPlayerJoinDate());
        return playerDTO;
    }

    private long getPlayerId(GamePlayer gp) {

        return gp.getPlayer().getId();
    }

    private Map<String, Object> getGamePlayerDTO(@PathVariable long gamePlayerId, Set<Ship> ships, Set<Salvo> salvoes, Map<String, Object> player) {
        Map<String, Object> gamePlayerDTO = new LinkedHashMap<>();

        gamePlayerDTO.put("gp_id", gamePlayerId);
        gamePlayerDTO.put("player", player);
        gamePlayerDTO.put("ships", ships);
        gamePlayerDTO.put("salvoes", salvoes);
        return gamePlayerDTO;
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
