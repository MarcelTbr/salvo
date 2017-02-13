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

    // This does not need to have an extra table, It's just one value
    private String shipType;

    private long shipSize;

    private long hits;

    private boolean sunkShip;

    /** Ship Constructor **/

    public Ship() {}

    public Ship(GamePlayer gamePlayer, List<String> shipLocations, String shipType){
        this.gamePlayer = gamePlayer;
        this.shipLocations = shipLocations;
        this.shipType = shipType;
        this.shipSize = shipLocations.size();
        this.hits = 0;
        this.sunkShip = false;
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

    public String getShipType(){ return this.shipType;
    };
    public void setShipType(String shipType){

        this.shipType = shipType;
    }

    public long getShipHits() { return this.hits; }
    public void setShipHits(long hits) {this.hits = hits;}

    public boolean isSunkShip() {
        return sunkShip;
    }

    public void setSunkShip(){ this.sunkShip = true;}
}
