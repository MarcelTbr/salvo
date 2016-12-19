package edu.example;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import java.util.ArrayList;
import java.util.List;

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

    public ShipLocation(String shipCells, long shipId) { //Before String shipCells
        this.shipId = shipId;
        this.shipCells = shipCells;
    }

   // public void ShipLocation(Ship ship, String cell1, String cell2, String cell3, String cell4 ){




    }







