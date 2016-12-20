package edu.example;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpMethod.POST;

/**
 * Created by marceltauber on 13/12/16.
 */

@Entity
public class GamePlayer {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    private String playerJoinDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game game;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name="ships") //Before name="game_player" Set<GamePlayer> gamePlayer
    Set<Ship> ships = new HashSet<>();


    public GamePlayer () {}

    public GamePlayer (Date date, Player player, Game game){

        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        this.playerJoinDate = df.format(date);
        this.player = player;
        this.game = game;

    }




    //TODO change to getId?
    public long getGamePlayerId() {

        return id;

    }

    public Player getPlayer(){

        return player;
    }

    public Game getGame() {
        return game;
    }


    //TODO move to controller
    public Object getPlayerInfo(GamePlayer gp_in_game){


        LinkedHashMap<String, Object> PlayerInfoMap = new LinkedHashMap<String, Object>();

        PlayerInfoMap.put("player_id", gp_in_game.getPlayer().getId());
        PlayerInfoMap.put("player_username", gp_in_game.getPlayer().getUsername());
        PlayerInfoMap.put("player_email", gp_in_game.getPlayer().getEmail());
        PlayerInfoMap.put("player_join_date", gp_in_game.getPlayerJoinDate());


        return PlayerInfoMap;
    }

    public String getPlayerJoinDate() {
        return playerJoinDate;
    }

    public void addShip(Ship ship){//String cell1, String cell2, String cell3, String cell4 ){

        ship.setGamePlayer(ship); //necessary for the Ship (N) - (1) GamePlayer relationship
        //ship.setShipCells(shipCells);
        this.ships.add(ship);


        //new ShipLocation(shipCells, ship.getShipId());

    }

    public Set<Ship> getShips(){

        return  ships;

    }
}
