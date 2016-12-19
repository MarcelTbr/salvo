package edu.example;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

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
    @JoinColumn(name="game_player")
    Set<GamePlayer> gamePlayer;


    public GamePlayer () {}

    public GamePlayer (Date date, Player player, Game game){

        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        this.playerJoinDate = df.format(date);
        this.player = player;
        this.game = game;
//        this.gamePlayerId = this.id;

    }

    public long getGamePlayerId() {

        return id;

    }

    public Player getPlayer(){

        return player;
    }

    public Game getGame() {
        return game;
    }

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

    public void addShip(Ship ship, String shipCells){//String cell1, String cell2, String cell3, String cell4 ){

//        ArrayList<String> shipCells = new ArrayList<>();
//
//       shipCells.set(1, cell1);
//       shipCells.set(2, cell2);
//       shipCells.set(3, cell3);
//       shipCells.set(4, cell4);

        new ShipLocation(shipCells, ship.getShipId());
    }

}
