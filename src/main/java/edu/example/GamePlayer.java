package edu.example;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Date;

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

    public GamePlayer () {}

    public GamePlayer (Date date, Player player, Game game){

        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        this.playerJoinDate = df.format(date);
        this.player = player;
        this.game = game;

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

}
