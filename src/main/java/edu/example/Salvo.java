package edu.example;

import javax.persistence.*;
import java.util.List;

/**
 * Created by marceltauber on 2/1/17.
 */
@Entity
public class Salvo {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_player")
    private GamePlayer gamePlayer;

    private long turn;

    @ElementCollection
    @Column(name="salvo_location")
    private List<String> salvoLocations;

    public Salvo(){}

    public Salvo(GamePlayer gamePlayer, long turn, List<String> salvoLocations){

        this.gamePlayer = gamePlayer;
        this.turn = turn;
        this.salvoLocations = salvoLocations;

    }


    public long getId() { return this.id; }

    public void setGamePlayer(GamePlayer gamePlayer) { this.gamePlayer = gamePlayer; }

    public long getGamePlayerId(GamePlayer gamePlayer) { return gamePlayer.getGamePlayerId(); }

    public List<String> getSalvoLocations() { return this.salvoLocations; }

    public long getTurn() { return this.turn;}
}
