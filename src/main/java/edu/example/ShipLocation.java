package edu.example;


import javax.persistence.Embeddable;


/**
 * Created by marceltauber on 19/12/16.
 */

@Embeddable
public class ShipLocation {

    //@ElementCollection
    //@Column(name="ship_id")
    private long shipId;


    //@ElementCollection
    //@Column(name="ship_cells")
    //private List<String> shipCells = new ArrayList<>();

    //private ArrayList shipCells;
    private String shipCells;


    public  ShipLocation() {}

    public ShipLocation( Ship ship) { //Before String shipCells, long shipId,
        //this.shipId = shipId;
        this.shipId = ship.getId();
        //this.shipCells = shipCells;
        this.shipCells = ship.getShipCells();
    }


    public long getShipId(Ship ship){

        return ship.getId();
    }

   // public void ShipLocation(Ship ship, String cell1, String cell2, String cell3, String cell4 ){




    }







