//package edu.example;
//
//
//import javax.persistence.Embeddable;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//
//
///**
// * Created by marceltauber on 19/12/16.
// */
//
//@Embeddable
//public class ShipLocation {
//
////    @ElementCollection
////    @Column(name="ship_id")
//    private long shipId = 0;
////    @Id
////    @GeneratedValue(strategy = GenerationType.AUTO)
////    private long id;
//
//    //@ElementCollection
//    //@Column(name="ship_cells")
//    //private List<String> shipCells = new ArrayList<>();
//
//    //private ArrayList shipCells;
//    private String shipCells; // Set<String> shipLocation = new LinkedHashSet<String>();
//
//
//    public  ShipLocation() {}
//
//    public ShipLocation( Ship ship, String shipCells) { //Before String shipCells, long shipId,
////        this.shipId = ship.getId();
//        this.setShipId(ship);
//        this.shipCells = shipCells;
////        this.shipCells = ship.getShipCells();
//    }
//
//
//    public long getShipId(Ship ship){
//
//        return ship.getId();
//    }
//
//    public void setShipId(Ship ship){
//
//        this.shipId = ship.getId();
//
//    }
//
//   // public void ShipLocation(Ship ship, String cell1, String cell2, String cell3, String cell4 ){
//
//
//
//
//    }
//
//
//
//
//
//
//
