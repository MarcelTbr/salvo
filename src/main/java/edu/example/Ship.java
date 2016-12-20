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

//    @ElementCollection
//    @Column(name="ship_id")
//    private long shipId;

    //public Map<String, Object> shipId;


//    @ElementCollection
//    @Column(name="ship_cells")
    private String shipCells;

    public Ship() {}

    public Ship(GamePlayer gamePlayer, String shipCells){
        this.gamePlayer = gamePlayer;
        this.gamePlayer.ships = gamePlayer.getShips();

//        System.out.println("ShipCells: ");
        //System.out.println(shipCells);

//        this.shipCells = shipCells;

        //this.setShipLocation(shipCells);
//        this.shipId = this.setShipId(this);
    }

    public void setGamePlayer(Ship ship){
        ship.gamePlayer = gamePlayer;
       // POST gamePlayers/ship.gamePlayer.getGamePlayerId();
    }

//    public long setShipId(Ship ship){
//
//        ship.shipId = ship.id;
//        return ship.id;
//    }

//    public void setShipLocation( String shCells){
//
//
//        this.shipCells = shCells;
//
//    }

    public long getGamePlayerId () {
        return gamePlayer.getGamePlayerId();

    }

//   public Map getShipId(){
//        Map<String, Object>  idMap = new HashMap<>();
//
//        idMap.put("shipId", this );
//
//
//        return idMap;
//    }

    public long getId(){

        return id;
    }

    public String getShipCells() {
        return this.shipCells;
    }
}
