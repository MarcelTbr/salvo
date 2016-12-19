package edu.example;

import javax.persistence.*;

/**
 * Created by marceltauber on 19/12/16.
 */
@Entity
public class Ship {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_player_id")
    private GamePlayer gamePlayer;


    public Ship() {}

    public Ship(GamePlayer gamePlayer){
        this.gamePlayer = gamePlayer;
    }

    public long getGamePlayerId () {
        return gamePlayer.getGamePlayerId();

    }

    public long getShipId(){

        return this.id;
    }

}
