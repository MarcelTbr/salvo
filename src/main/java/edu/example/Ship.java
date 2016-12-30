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

//    @ElementCollection
//    @Column (name="ship_type")  // This does not need to have an extra table, It's just one value
    private String shipType;

    /** Ship Constructor **/

    public Ship() {}

    public Ship(GamePlayer gamePlayer, List<String> shipLocations, String shipType){
        this.gamePlayer = gamePlayer;
        this.shipLocations = shipLocations;
        this.shipType = shipType;
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
}
