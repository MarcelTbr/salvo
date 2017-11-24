package salvo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import salvo.entities.*;
import salvo.repositories.*;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by marceltauber on 14/12/16.
 */

@RequestMapping("/api")
@RestController
public class SalvoController {

    //Calendar now = new GregorianCalendar(TimeZone.getTimeZone("ES")); // NICE TO HAVE: Refactor Date (format at endpoint)

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

    @Autowired
    private GameStateRepository gameStateRepo;

    /** ======== ENDPOINTS ======== */

    @RequestMapping("/tests/{gpId}")
    public Map<String, Object> getOutput(@PathVariable long gpId, Authentication auth){ //List<Map<String, Object>>


        long gp_id = gpId; // passed through url
        //Optional<Map<String, Object> > LegacyGameHistoryDTO = getGameHistoryDTO(gp_id); //LEGACY
        GamePlayer gamePlayer = gamePlayerRepo.findById(gp_id);
        Set<Ship> fleet = gamePlayer.getShips();
        Optional<GamePlayer> enemyGamePlayer = getEnemyGamePlayer(gamePlayer);

        if(enemyGamePlayer.isPresent()) {

            List<Salvo> enemySalvos = salvoRepo.findByGamePlayer(enemyGamePlayer.get());



        }
        //return makeHistoryDTO(gp_id);
        HashMap<String, Long> stateOfGame = new HashMap<>();
        //stateOfGame.put("stateOfGame", gamePlayer.getStateOfGame());
        //gamePlayer.getGameState().setGameState( 1);
        //stateOfGame.put("stateOfGAme", gamePlayer.getGameState().getGameState());
        //System.out.println("L64: stateOFGame: " + gamePlayer.getStateOfGame());

        return makeMap("stateOfGame", stateOfGame);

    }
    //TODO wipe out inneccessary code
    public Map<String, Object> makeHistoryDTO(long gp_id) {
        /** making the game history DTO **/
        GamePlayer gamePlayer = gamePlayerRepo.findById(gp_id);
        Set<Ship> fleet = gamePlayer.getShips();
        Optional<GamePlayer> enemyGamePlayer = getEnemyGamePlayer(gamePlayer);

        Map<String, Object> shipHits = new HashMap<>();

        List<Map<String, Object>> fleetHits = new LinkedList<>();
        Map<String, Object> playerHistoryDTO = new HashMap<>();
        Map<String, Object> enemyHistoryDTO = new HashMap<>();
        if(enemyGamePlayer.isPresent()){

            List<Salvo> enemySalvos = salvoRepo.findByGamePlayer(enemyGamePlayer.get());
            //List<String> salvoLocations = enemySalvos.stream().findAny().get().getSalvoLocations();
            enemySalvos.stream().forEach(salvo -> {
               List<String> salvoLocs = salvo.getSalvoLocations();
                playerHistoryDTO.put(String.valueOf(salvo.getTurn()), makeTurnDTO(fleet, salvoLocs));
            });
            // LEGACY makeTurnDTO(fleet, salvoLocations);

            Set<Ship> enemyFleet = enemyGamePlayer.get().getShips();
            List<Salvo> playerSalvos = salvoRepo.findByGamePlayer(gamePlayer);
            playerSalvos.stream().forEach(salvo -> {
                List<String> playerSalvoLocs = salvo.getSalvoLocations();
                enemyHistoryDTO.put(String.valueOf(salvo.getTurn()), makeTurnDTO(enemyFleet, playerSalvoLocs));
            });

        }

        Map<String, Object> gameHistoryDTO = new HashMap<>();

        gameHistoryDTO.put("historyDTO", playerHistoryDTO);
        gameHistoryDTO.put("enemyHistoryDTO", enemyHistoryDTO);

        return gameHistoryDTO;
    }

    public Map<String, Object> makeTurnDTO(Set<Ship> fleet, List<String> salvoLocations) {
        /** find hit ships for a turn and store the salvo_hit_locs in the ship class **/
        storeFleetHitLocations(fleet, salvoLocations);

//          LEGACY    System.out.println("  "+ fleet.stream().findAny().get().getTurnHitLocations());

        List<Map<String, Object>> turnHitsMap =  makeTurnHitsMap(fleet);
        List<String> sunkShipList = makeSunkShipsList(fleet);
//           LEGACY fleetHits = makeFleetHitsArray(fleet, salvoLocations);

        Map<String, Object> turnDTO = new HashMap<>();
        turnDTO.put("hits", turnHitsMap);
        turnDTO.put("sinks", sunkShipList);

        return turnDTO;

    }

    /** Post method for createGame */
    @RequestMapping(value="/new_games", method = RequestMethod.POST)
    public ResponseEntity<Map<String,Object>> createNewGame(){

        Date newDate = new Date();
        Game newGame = new Game(newDate); gameRepo.save(newGame);
        GamePlayer newGamePlayer = new GamePlayer(newDate, playerRepo.findById(1), newGame);
        gamePlayerRepo.save(newGamePlayer);

        //newGamePlayer.setStateOfGame((long) 1);
        //System.out.println("GAME STATE L 134: " + newGamePlayer.getStateOfGame());

        return new ResponseEntity<Map<String, Object>>(makeMap("submitted_salvos", newGame ),HttpStatus.CREATED);
    }


    public Optional<Map<String, Object>>getGameHistoryDTO(long gp_id) {
        //[A] Get needed Objects
        GamePlayer gamePlayer = gamePlayerRepo.findById(gp_id);
        Set<Ship> playerFleet = gamePlayer.getShips();
        List<Salvo> gpSalvos = salvoRepo.findByGamePlayer(gamePlayer);

        Optional<GamePlayer> enemyGp = getEnemyGamePlayer(gamePlayer);
        if( enemyGp.isPresent()) {
            Optional<List<Salvo>> enemyGpSalvos = Optional.of(salvoRepo.findByGamePlayer(enemyGp.get()));
            Optional<Set<Ship>> enemyFleet = Optional.of(enemyGp.get().getShips());

            //[B] get turn historyMap for both players
            Map<Long, Object> turnHistoryMap = makeTurnHistoryMap(enemyFleet.get(), gpSalvos);
            Map<Long, Object> enemyTurnHistoryMap = makeTurnHistoryMap(playerFleet, enemyGpSalvos.get());

            //[C] make gameHistoryDTO
            Map<String, Object> gameHistoryDTO = new LinkedHashMap<>();
            gameHistoryDTO.put("Player", turnHistoryMap);
            gameHistoryDTO.put("Enemy", enemyTurnHistoryMap);
            return Optional.of(gameHistoryDTO);
        } else {

            return Optional.empty();
        }


    }

    public Map<Long, Object> makeTurnHistoryMap(Set<Ship> fleet, List<Salvo> gpSalvos) {
        return gpSalvos.stream()
                .sorted(Comparator.comparing(Salvo::getTurn)) //sorting by Turn
                .collect(Collectors.toMap(Salvo::getTurn, salvo -> makeHistoryTurnObj(fleet, salvo)));
    }

    public Map<Long, Object>  makeLongIndexMap(long number, Object object) {
        Map<Long, Object> longIndexMap = new LinkedHashMap<>();
        longIndexMap.put(number, object);
        return longIndexMap;
    }


    public Map<String, Object> makeHistoryTurnObj(Set<Ship> fleet, Salvo salvo) {
        //[A] Getting Hit Ships
        ArrayList<Ship> hitShips = getHitShips(fleet, salvo);

        // [B] find repeated ships in hitShips list and return a List of the shipTypes
        Set<Ship> repeatedShips = findRepeatedShips(hitShips);

        // [C] get single hit ships
        Set<Ship> singleHitShips = getSingleHitShips(hitShips);

        // [D]
        updateHits(singleHitShips, repeatedShips, hitShips);

        // [E] make a history map object for 1 player in 1 turn
        return makeHistoryTurnObj(hitShips, repeatedShips, singleHitShips);
    }

    private void updateHits(Set<Ship> singleHitShips, Set<Ship> repeatedShips, ArrayList<Ship> hitShips) {

        singleHitShips.stream()
                .forEach(s-> s.setShipHitsNum(s.getShipHitsNum()+1));
        repeatedShips.stream()
                .forEach(rep_s -> rep_s.setShipHitsNum(rep_s.getShipHitsNum() + getNumberOfShipHits(rep_s.getShipType(), hitShips)) );
    }

    public Map<String, Object> makeHistoryTurnObj(ArrayList<Ship> hitShips, Set<Ship> repeatedShips, Set<Ship> singleHitShips) {
        Map<String, Object> historyTurnMap = new HashMap<>();

        singleHitShips.stream()
                .forEach(s -> historyTurnMap.put(s.getShipType(), 1L));
        repeatedShips.stream()
                .forEach(rep_s -> historyTurnMap.put(rep_s.getShipType(), getNumberOfShipHits(rep_s.getShipType(), hitShips)));

        LinkedList<String> sunkShips= new LinkedList<>();
        singleHitShips.stream()
                .filter(s -> s.getShipHitsNum() == s.getShipLocations().size())
                .forEach(sunk_ship -> sunkShips.add(sunk_ship.getShipType()));
        repeatedShips.stream()
                .filter(rep_s -> rep_s.getShipHitsNum() == rep_s.getShipLocations().size())
                .forEach(sunk_ship -> sunkShips.add(sunk_ship.getShipType()));

        historyTurnMap.put("sunk", sunkShips);

        //change Ship Sunk attribute
        // TODO extract this as an independent method?
        singleHitShips.stream()
                .filter(s -> s.getShipHitsNum() == s.getShipLocations().size())
                .forEach(Ship::setSunkShip);
        repeatedShips.stream()
                .filter(s -> s.getShipHitsNum() == s.getShipLocations().size())
                .forEach(Ship::setSunkShip);

        return historyTurnMap;
    }

    public ArrayList<Ship> getHitShips(Set<Ship> fleet, Salvo salvo) {
        //[A] Get a clean Salvo Hit Array
        List<String> salvoHitLocations = getSalvoHits(salvo, fleet);

        // [B] Stream salvo Hit locations and get all Ships from the fleet
        ArrayList<Ship> hitShips = new ArrayList<>();

        salvoHitLocations.stream()
            .forEach(s_hit -> hitShips.add( getShipFromSalvoHit(s_hit, fleet)));
        return hitShips;
    }

    public Set<Ship> getSingleHitShips(ArrayList<Ship> hitShips) {
        return hitShips.stream()
                .filter(s -> getNumberOfShipHits(s.getShipType(), hitShips) == 1)
                .collect(Collectors.toSet());
    }

    private Long getNumberOfShipHits(String rep_ship, ArrayList<Ship> hitShips) {

        return hitShips.stream()
                .filter( s -> s.getShipType() == rep_ship).count();

    }

    private Set<Ship> findRepeatedShips(ArrayList<Ship> hitShips) {
        return hitShips.stream()
                .filter(s -> hitShips.stream().filter(hit_ship -> hit_ship == s).count() > 1) //filter repeated ships
                .collect(Collectors.toSet());

    }

    private Ship getShipFromSalvoHit(String salvo_loc, Set<Ship> fleet) {
       return fleet.stream()
               .filter( s -> salvoHitShip(s, salvo_loc).isPresent())
               .collect(Collectors.toList()).get(0);
    }

    //TODO: use or erase
    // this method is useful for a single salvo_loc, not while streaming a salvo_loc array/list
    private ArrayList<Ship> getHitShips(Set<Ship> fleet, String salvo_loc) {

        ArrayList<Ship> hitShips = new ArrayList<>();
        fleet.stream()
                .filter(s -> salvoHitShip(s, salvo_loc).isPresent())
                .forEach(hit_ship -> hitShips.add(hit_ship));

        return hitShips;

    }

    private Optional salvoHitShip(Ship ship, String salvoLocation) {

        return ship.getShipLocations().stream()
                .filter(s_loc -> s_loc == salvoLocation).findAny();


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

            // JSONParser parser = new JSONParser(); //NICE TO HAVE: explore this parser method and JSON library

            String [] locations = salvo_locs.split(", "); //the blank space is important here!
            Salvo submitted_salvo = new Salvo(gamePlayer, Long.parseLong(turn), Arrays.asList(locations) );
            salvoRepo.save(submitted_salvo);
                System.out.println(salvos_str + " ; turn: " + turn + "; salvo_locs: " + salvo_locs);
                System.out.println("submitted_salvo: " + Arrays.asList(locations) );

            return new ResponseEntity<Map<String, Object>>(makeMap("submitted_salvos", salvos ),HttpStatus.CREATED);
        }

    }

    @RequestMapping ("games/players/{gamePlayerId}/salvos")
    public ResponseEntity<Map<String, Object>> getPlayersSalvos (@PathVariable long gamePlayerId, Authentication auth){

        GamePlayer gamePlayer = gamePlayerRepo.findById(gamePlayerId);

        long number_of_players = gamePlayer.getGame().getPlayers().stream().count();

        if(number_of_players < 1){

            return new ResponseEntity<Map<String, Object>>(makeMap("backend", "sorry, no players yet"), HttpStatus.NO_CONTENT);
        } else if (number_of_players == 1){

            return new ResponseEntity<Map<String, Object>>(makeMap("backend", "sorry, wait until " +
                    "an enemy player joins the game"), HttpStatus.ACCEPTED); //FORBIDDEN TODO fix this BUG
        } else {
            //getting all player salvos
            Object playerSalvos = getAllPlayerSalvos(gamePlayer);
            System.out.println("playerSalvos: " + playerSalvos);
            Set<Salvo> playerSalvosSet = gamePlayer.getSalvos();
            //get enemy's Fleet
            GamePlayer enemyGamePlayer = getEnemyGamePlayer(gamePlayer).get(); //it's an optional

            Map<Long, Object> salvos_dto = makeUserSalvosDTO(playerSalvosSet, enemyGamePlayer);
            // make enemy salvos dto
            Map<Long, Object> e_salvos_dto = makeEnemySalvosDTO(gamePlayer, enemyGamePlayer);

            Map<String, Object> salvos_response = new HashMap<>();
            salvos_response.put("salvosDTO", salvos_dto);
            salvos_response.put("enemySalvosDTO", e_salvos_dto);

            //return the salvos_dto object and an accepted HttpStatus Response
            return new ResponseEntity<Map<String, Object>>(salvos_response, HttpStatus.ACCEPTED );

        }

    }

    private Map<Long, Object> makeEnemySalvosDTO(GamePlayer gamePlayer, GamePlayer enemyGamePlayer) {
        Map<Long, Object> e_salvos_dto = new HashMap<>();
        enemyGamePlayer.getSalvos().stream()
                .forEach(e_salvo ->
                e_salvos_dto.put(e_salvo.getTurn(), makeSalvoDTO(e_salvo, gamePlayer.getShips()))
                );
        System.out.println("enemy_salvos_dto: " + e_salvos_dto);
        return e_salvos_dto;
    }

    private Map<Long, Object> makeUserSalvosDTO(Set<Salvo> playerSalvosSet, GamePlayer enemyGamePlayer) {
        Set<Ship> enemyShips = enemyGamePlayer.getShips();
        Map<Long, Object> salvos_dto = new HashMap<>();
        // stream the player's salvo set
        playerSalvosSet.stream()
                .forEach(pl_salvo ->
                //storing each turns results as a value. Turn's number is the key.
                salvos_dto.put(pl_salvo.getTurn(), makeSalvoDTO(pl_salvo, enemyShips))
                );

        System.out.println("salvos_dto: " + salvos_dto);
        return salvos_dto;
    }

    private Object makeSalvoDTO(Salvo pl_salvo, Set<Ship> enemyShips) {
        Map<String, List<String>> turn_dto = new LinkedHashMap<>();
        turn_dto.put("hits", getSalvoHits(pl_salvo, enemyShips));
        turn_dto.put("misses", getMisses(pl_salvo, enemyShips));
        return turn_dto;
    }

    private List<String> getMisses(Salvo testSalvo, Set<Ship> enemyShips) {
        List<String> misses = testSalvo.getSalvoLocations().stream()
                .filter(s_loc -> !salvoHitAnyShip(s_loc, enemyShips))
                .collect(Collectors.toList());

        System.out.println("misses: " + misses);
        return misses;
    }

    private List<String> getSalvoHits(Salvo salvo, Set<Ship> fleet) {
        List<String> hits = salvo.getSalvoLocations().stream()
                .filter(s_loc -> salvoHitAnyShip(s_loc, fleet)) //filters out the salvo that missed the target
                .collect(Collectors.toList());

        List<String> salvoHits = new LinkedList<>();
                salvo.getSalvoLocations().stream()
                        .forEach(sl -> {
                            if(salvoHitAnyShip(sl, fleet)){
                                salvoHits.add(sl);
                            }
                        });
        return salvoHits;
        // System.out.println("hits: " + hits);
        //return hits;
    }

    private boolean salvoHitAnyShip(String salvo_loc, Set<Ship> fleet) {
        Optional<String> any = fleet.stream()
                .map(Ship::getShipLocations)
                .flatMap(Collection::stream)
                .filter(e_loc -> e_loc.equals(salvo_loc) )
                .findAny();

        return any.isPresent();


    }

    private Optional<GamePlayer> getEnemyGamePlayer(GamePlayer gamePlayer) {

        //REFACTOR: use findFirst + Optional
       Optional< GamePlayer > enemyGamePlayer = gamePlayer.getGame().getGamePlayers().stream()
                .filter(gp -> !Objects.equals(gp.getGamePlayerId(), gamePlayer.getGamePlayerId()))
                .collect(Collectors.toList()).stream().findAny();

        return enemyGamePlayer;
    }

    @RequestMapping ("games/players/{gamePlayerId}/ships")
    public Set<Ship> getShips (@PathVariable long gamePlayerId, Authentication auth){
    if ( existingUsername(auth.getName()) && !incorrectUser(auth, gamePlayerId)) {
        GamePlayer gamePlayer = gamePlayerRepo.findById(gamePlayerId);
        return gamePlayer.getShips();
    }else { return new HashSet<Ship>();}

    }

    @RequestMapping("history/{gpId}")
    public Map<String, Object> getHistory (@PathVariable long gpId, Authentication auth){
        if ( existingUsername(auth.getName()) && !incorrectUser(auth, gpId)) {

            return makeHistoryDTO(gpId);

        }else {

            return new HashMap<String, Object>();
        }

    }

    @RequestMapping (value="games/players/{gamePlayerId}/ships", method= RequestMethod.POST )
    public ResponseEntity<Map<String, Object>> saveShips(@PathVariable long gamePlayerId, @RequestBody List<Ship> ships,
                                                         Authentication auth) { // @RequestBody Ship ship,
        String newLine = System.getProperty("line.separator");

        System.out.println("Ships found:");
        System.out.print(ships);
        System.out.println(newLine);

        if (auth == null || noSuchGP(gamePlayerId) || incorrectUser(auth, gamePlayerId)) {

            return new ResponseEntity<Map<String, Object>>(makeMap("backend", "sorry, not authorized"), HttpStatus.UNAUTHORIZED);
        } else if (shipsPlaced(gamePlayerId) ){ //|| gameState(gamePlayerId) == (long) 2
            return new ResponseEntity<Map<String, Object>>(makeMap("backend", "Not possible, ships were already placed."), HttpStatus.FORBIDDEN);
        }else{

            GamePlayer gamePlayer = gamePlayerRepo.findById(gamePlayerId);

                for(Ship ship: ships) {
            ship.setGamePlayer(gamePlayer); shipRepo.save(ship);
                }
                /** change the state of game to "waiting for enemy ships" **/
                //gamePlayer.setStateOfGame((long)2);

                //System.out.println("StateOfGame: " + gamePlayer.getStateOfGame());

            return new ResponseEntity<Map<String, Object>>(makeMap("backend", "ships created!"), HttpStatus.CREATED);
        }
    }

//    private long gameState(long gamePlayerId) {
//
//        return gamePlayerRepo.findById(gamePlayerId).getStateOfGame();
//    }

//    @RequestMapping("game_state/{gpId}")
//    public long getGameState (@PathVariable long gpId, Authentication auth){
//
//           return gameState(gpId);
//
//
//    }

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
                    /** change the game state **/
                    gamePlayer.setStateOfGame((long) 1);
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

        System.out.println("USER==>: " + user.getUsername());
        GamePlayer gamePlayer = gamePlayerRepo.findById(newGamePlayer.getGamePlayerId());
        gamePlayer.setStateOfGame(1);
        //newGamePlayer.setStateOfGame((long) 1);
        if(gamePlayer.getStateOfGame() > 0) {
            System.out.println("GAME STATE L 134: " + gamePlayer.getStateOfGame());
        }
        gamePlayerRepo.save(gamePlayer);
        //        GameState newGameState = new GameState();//gamePlayerRepo.findById(newGamePlayer.getGamePlayerId()).getGameState();
//
//        gameStateRepo.save(newGameState);
//        //newGameState.setGameState((long)1);
//       GameState gameStateTest =  gameStateRepo.findByGamePlayerId(gamePlayerRepo.findById(newGamePlayer.getGamePlayerId()).getGamePlayerId());
//        gameStateTest.setGameState(1);
//        System.out.println("GAME STATE TEST ===> " + gameStateTest.getGameState() );
//        System.out.println("GAME STATE ==> " + gamePlayerRepo.findById(newGamePlayer.getGamePlayerId()).getGameState().getGameState());
//        System.out.println("GAME STATE NEW ==> " + newGameState.getGameState());
        return new ResponseEntity<Map<String, Object>>(makeMap("gp_id", newGamePlayer.getGamePlayerId()), HttpStatus.CREATED);
    }

    @RequestMapping("/games")
    public  Map<String, Object> getGames(Authentication auth) {
        Map<String, Object> gamesAuthDTO = new HashMap<>();

        if (!Objects.equals(auth, null)) { // auth != null
            List<Player> playerList = playerRepo.findByUsername(auth.getName());
            Player user = playerList.get(0);
            Map<String, Object> userDTO = getUserDTO(user);
            LinkedList<Object> userGamesDTO = getUserGamesDTO(user);
            gamesAuthDTO.put("player", userDTO);
            gamesAuthDTO.put("player_games", userGamesDTO);
            gamesAuthDTO.put("games",  getGamesDTO());
            gamesAuthDTO.put("auth", auth.getName() + " " + auth.isAuthenticated()); //auth
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

        players.stream().forEach(p -> scoresDTO.put (/*p.getId()+": " +*/ p.getUsername(), getTotalScoresList(p, p.getGameScoresSet())));

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

                    String username = gamePlayerRepo.findById(gamePlayerId).getPlayer().getUsername();

                    //System.out.println( "Username AUTH: " + Objects.equals(username, auth.getName() ));

                    //return new ResponseEntity<Map<String, Object>>(getGameViewDTO(gamePlayerId), HttpStatus.ACCEPTED);

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

        String username = gamePlayerRepo.findById(gamePlayerId).getPlayer().getUsername();

                    //  System.out.println( "Username AUTH: " + Objects.equals(username, auth.getName() ));
                    System.out.println("Username: "+ username + " Auth: " + auth.getName());
        if( Objects.equals(username, auth.getName() ) ) { return true; }
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

            boolean differentGamePlayer = !Objects.equals(gp.getGamePlayerId(),gamePlayerId);

            if( differentGamePlayer ){

                enemyPlayer = gp;

            }

            // if(gp.getGamePlayerId() != gamePlayerId){enemyPlayer = gp;}
        }
        /** Creating and filling the endpoint's game_viewDTO*/
        Map<String, Object> game_viewDTO = getGameViewDTO(gamePlayer, enemyPlayer, gamePlayersList, gamePlayerId);
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

    private Map<String, Object> getGameViewDTO(GamePlayer gamePlayer, GamePlayer enemyPlayer,
                                               LinkedList<Map<String, Object>> gamePlayersList,
                                               long gp_id) {
        Map<String, Object> game_viewDTO = new LinkedHashMap<String, Object>() {};
        game_viewDTO.put("game_id", gamePlayer.getGame().getId());
        game_viewDTO.put("created", gamePlayer.getGame().getCreationDate());
        game_viewDTO.put("gamePlayers", gamePlayersList);
        /** Getting an instance of the players' ships  */
        Set<Ship> playerShips =  gamePlayer.getShips();
        /** Creating and filling a List with the desired ship instance data*/
        List<Object> playerShipsList = getPlayerShipsList(playerShips).get();
        Optional<List<Object>> enemyShipsList = getPlayerShipsList(enemyPlayer.getShips());
        game_viewDTO.put("ships", playerShipsList);
                /** get the current state of the game, to handle placing ships, turns & salvos **/
                long gameState = gamePlayer.getStateOfGame();
                    System.out.println("gameState=====> " + gameState);
                game_viewDTO.put("gameState", gameState);

                long enemyGameState = enemyPlayer.getStateOfGame();
                game_viewDTO.put("enemyGameState", enemyGameState);
//        if (enemyShipsList.isPresent()) {
//            game_viewDTO.put("enemy_ships", enemyShipsList);
//        } else {
//            game_viewDTO.put("enemy_ships", "");
//        }
//        /** getting the salvoesDTO {"turn" : "salvoes-array" }*/
//        Object salvoesDTO = getAllPlayerSalvos(gamePlayer);
//        game_viewDTO.put("salvoes", salvoesDTO);
//        game_viewDTO.put("enemy_salvoes", getAllPlayerSalvos(enemyPlayer));
        //game_viewDTO.put("gameHistory",  getGameHistoryDTO(gp_id));

        return game_viewDTO;
    }

    private List<Object> getPlayerSalvoesList(Set<Salvo> playerSalvoes) {
                List<Object> playerSalvoesList = new LinkedList<>();
                playerSalvoes.stream()
                        .map(salvo -> playerSalvoesList.add(salvo))
                        .collect(Collectors.toList());

                return playerSalvoesList;
    }

    private Optional<List<Object>> getPlayerShipsList(Set<Ship> playerShips) {
        List<Object> playerShipsList = new LinkedList<>();
        for(Ship ship : playerShips){
            Map<String, Object> playerShipsMap = new LinkedHashMap<>();
            playerShipsMap.put("type", ship.getShipType());
            playerShipsMap.put("locations", ship.getShipLocations());
            playerShipsList.add(playerShipsMap);
        }
        return Optional.ofNullable(playerShipsList);
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
                        .map (salvo -> allPlayerSalvos.put(String.valueOf(salvo.getTurn()), salvo.getSalvoLocations() ) );
                        //.collect(Collectors.toList());

                return allPlayerSalvos;
    }


    public Map<String,Object> makeShipHitsObj(Ship ship, List<String> salvo_locs) {

        List<String> shipHitsArr = new ArrayList<>();
        List<String> sh_loc_list = ship.getShipLocations();

        System.out.println("SALVO LOCS: " + salvo_locs);

        for(int sl = 0; sl < salvo_locs.size(); sl++){

            String salvo_loc = salvo_locs.get(sl);
            String hit = sh_loc_list.stream()
                    .filter(sh_loc -> sh_loc.equals(salvo_loc))
                    .collect(Collectors.toList()).get(0);


                shipHitsArr.add(hit);

        }

        System.out.println("ShipHITSArray ===>" + shipHitsArr);


        return makeMap(ship.getShipType().replaceAll("\\s",""), shipHitsArr);

    }

    public List<Map<String, Object>> makeFleetHitsArray (Set<Ship> fleet, List<String> salvo_locs){

        List<Map<String, Object>>fleetHitsArray = new LinkedList<>();

        fleetHitsArray = fleet.stream().map(ship -> makeShipHitsObj(ship, salvo_locs))
        .collect(Collectors.toList());

        for(Ship  ship : fleet) {

            Optional<Map<String, Object>> shipHitsObj = Optional.of(makeShipHitsObj(ship, salvo_locs)) ;

            if(shipHitsObj.isPresent()){

                fleetHitsArray.add(shipHitsObj.get());
            }

        }

        return fleetHitsArray;
    }

    private void storeFleetHitLocations(Set<Ship> fleet, List<String> salvoLocations){

        fleet.stream().forEach(ship-> ship.setTurnHitLocations(salvoLocations));


    }

    private List<Map<String, Object>> makeTurnHitsMap (Set<Ship> fleet) {

//        Map<String, Object> turnHitsList = new HashMap<>();
//        fleet.forEach(ship-> turnHitsList.put(ship.getShipType(), ship.getTurnHitLocations()) );
//        ...better solution, but FE was already written to meet the format below

    //        List<Map<String, Object>> turnHitsList = fleet.stream()
    //                .map(ship-> makeMap(ship.getShipType().replaceAll("\\s",""), ship.getTurnHitLocations()))
    //                .collect(Collectors.toList());


        List<Map<String, Object>> turnHitsList = new LinkedList<>();

        fleet.forEach(ship -> {

            long numTurnHits = ship.getTurnHitLocations().size();

            if(numTurnHits > 0){

                turnHitsList.add(makeMap(ship.getShipType().replaceAll("\\s",""), ship.getTurnHitLocations()));

            }


        });

        /** updating all of the hits, to know when a ship is sunk */
        updateShipHitsNum(fleet);


        fleet.stream().forEach(ship -> ship.updateAllHitLocations(ship.getTurnHitLocations()));

        updateShipSinkState(fleet);
        //fleet.stream().forEach(ship -> ship.updateShipHitsNum());

        System.out.println("turnHitsList: " + turnHitsList);

        return turnHitsList;

    }

    private void updateShipSinkState(Set<Ship> fleet) {

        fleet.stream().forEach(

                ship -> {
                    long hits =  ship.getAllHitLocations().size(); //ship.getShipHitsNum();
                    long size = ship.getShipLocations().size();

                    System.out.println("SHIP: " + ship.getShipType() + " Hits: " + ship.getAllHitLocations() + " Ship SIZE " + ship.getShipLocations().size() );

                    if (Objects.equals(hits, size )) {

                        ship.setSunkShip();

                    }

                }

        );

    }

    private void updateShipHitsNum(Set<Ship> fleet) {

        fleet.stream().forEach(ship-> {ship.setShipHitsNum( ship.getTurnHitLocations().size() );
            System.out.println(ship.getShipType() + " hit_num: " + ship.getShipHitsNum() + " " + ship.getTurnHitLocations()); }
        );
        //fleet.stream().forEach(Ship::updateShipHitsNum);
    }

    private List<String> makeSunkShipsList(Set<Ship> fleet) {

        List<String> sunkenShips = new LinkedList<>();

//        updateShipHitsNum(fleet);
//
//        /** updating all of the hits, to know when a ship is sunk */
//        fleet.stream().forEach(ship -> ship.updateAllHitLocations(ship.getTurnHitLocations()));
//
//        updateShipSinkState(fleet);

        fleet.stream().forEach(ship -> {

            if(ship.isSunkShip()){
                /** be sure to have the space between ship name words removed, for DOM selecting purposes */
                sunkenShips.add(ship.getShipType().replaceAll("\\s",""));
            }

        });

        System.out.println("sunkenSHips: "+sunkenShips);
        return sunkenShips;
    }
}
