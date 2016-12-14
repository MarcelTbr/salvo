package edu.example;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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



}
