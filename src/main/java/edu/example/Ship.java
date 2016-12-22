package edu.example;

import javax.persistence.*;
import java.util.List;


/**
 * Created by marceltauber on 19/12/16.
 */

@Entity
public class Ship {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_player")
    private GamePlayer gamePlayer;

    @ElementCollection
    @Column(name="ship_location")
    private List<String> shipLocations;

    private String shipCells;

    /** Ship Constructor **/

    public Ship() {}

    public Ship(GamePlayer gamePlayer, List<String> shipLocations){
        this.gamePlayer = gamePlayer;
        this.shipLocations = shipLocations;
    }

    public void setGamePlayer(GamePlayer gamePlayer){
        this.gamePlayer = gamePlayer;
    }

    public long getGamePlayerId () {
        return gamePlayer.getGamePlayerId();

    }

    public long getId(){

        return this.id;
    }

    public List<String> getShipLocations() {
        return this.shipLocations;
    }

    public void setShipLocation(List<String> location) {

        this.shipLocations = location;

    }
}
