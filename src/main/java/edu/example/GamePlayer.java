package edu.example;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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

    public List<HashMap> getPlayerInfo(GamePlayer gp_in_game){


        /**
         * It has to be an ArrayList in order to be instantiated correctly
         *  */
        List<HashMap> game_players = new ArrayList<HashMap>();




        HashMap<String, Object> PlayerInfoMap = new HashMap<String, Object>() {
           // PlayerInfo.put("player_id", game.getPlayer().getId())
                    //return "fdhsklfa";
        };

        PlayerInfoMap.put("player_id", gp_in_game.getPlayer().getId());
        PlayerInfoMap.put("player_username", gp_in_game.getPlayer().getUsername());
        PlayerInfoMap.put("player_email", gp_in_game.getPlayer().getEmail());


        game_players.add(PlayerInfoMap);



        return game_players;
    };




    /*public Set<GamePlayer> getGamePlayer(List<GamePlayer> gamePlayers){

        return gamePlayers.stream()
                .map(gp -> gp.player + gp.playerJoinDate + gp.game )
                .collect(Collectors.toSet());
    }*/


}
