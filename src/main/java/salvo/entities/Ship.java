package salvo.entities;

import javax.persistence.*;
import java.util.LinkedList;
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

    @ElementCollection
    @Column(name="turn_hit_locations")
    private List<String> turnHitLocations;

    @ElementCollection
    @Column(name="all_hit_locations")
    private List<String> allHitLocations;

    // This does not need to have an extra table, It's just one value
    private String shipType;

    private long shipSize;

    private long hits_num;

    private boolean sunkShip;

    /** Ship Constructor **/

    public Ship() {}

    public Ship(GamePlayer gamePlayer, List<String> shipLocations, String shipType){
        this.gamePlayer = gamePlayer;
        this.shipLocations = shipLocations;
        this.shipType = shipType;
        this.shipSize = shipLocations.size();
        this.hits_num = 0;
        this.sunkShip = false;
        this.turnHitLocations = new LinkedList<>();
        this.allHitLocations = new LinkedList<>();
        //TODO: maybe we have to set it to null for better conditional checks
    }

    /** methods **/

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

    public long getShipHitsNum() { return this.hits_num; }
    public void setShipHitsNum(long hits) {this.hits_num = hits_num + hits;}

    public boolean isSunkShip() {
        return sunkShip;
    }

    public void setSunkShip(){ this.sunkShip = true;}

    public List<String> getTurnHitLocations() {
        return turnHitLocations;
    }

    public void setTurnHitLocations(List<String> salvoLocations) {

        List<String> shipHitsArr = new LinkedList<>();
        List<String> sh_loc_list = this.shipLocations;

        System.out.println("SALVO LOCS: " + salvoLocations);

        for(int sl = 0; sl < salvoLocations.size(); sl++){

            String salvo_loc = salvoLocations.get(sl);

            sh_loc_list.forEach(sh_loc -> {if(sh_loc.equals(salvo_loc)){

                        shipHitsArr.add(salvo_loc);

            }

            });
            //System.out.println("shipHitsArray: " + shipHitsArr);

        }

        this.turnHitLocations = shipHitsArr;
    }

    public void updateShipHitsNum(){

        this.hits_num = this.turnHitLocations.size();

    }

    public void updateAllHitLocations(List<String> turnHitLocations){

        for (String turnHitLocation : turnHitLocations) {

            this.allHitLocations.add(turnHitLocation);

        }

    }

    public List<String> getAllHitLocations() {
        return this.allHitLocations;
    }
}
