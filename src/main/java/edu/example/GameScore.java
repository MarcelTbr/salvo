package edu.example;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by marceltauber on 5/1/17.
 */
@Entity
public class GameScore {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    private double score;

    private Date finish_date;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player player;



    public GameScore (){}

    public GameScore(Game game, Player player, double score){
        this.game = game;
        this.player = player;
        this.score = score;
        this.finish_date = new Date();

    }

    public Player getPlayer(){ return player; }

    public double getScore() { return score; }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
