package edu.example;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by marceltauber on 13/12/16.
 */

@Entity
public class GamePlayer {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    private String playerJoinDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game game;

    /** this annotation is for the ids*/
    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER)
    Set<Ship> ships = new HashSet<>();

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER)
    Set<Salvo> salvos = new HashSet<>();


    public GamePlayer () {}

    public GamePlayer (Date date, Player player, Game game){

        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        this.playerJoinDate = df.format(date);
        this.player = player;
        this.game = game;

    }

    //TODO change to getId?
    public long getGamePlayerId() {

        return id;

    }

    public Player getPlayer(){

        return player;
    }

    public Game getGame() {
        return game;
    }


    public String getPlayerJoinDate() {
        return playerJoinDate;
    }

    public void addShip(Ship ship){

        /** this can bind many ships to the GamePlayer
         *  this GamePlayer contains just one Game and one Player
         *  its' necessary for the Ship (N) - (1) GamePlayer relationship */
        ship.setGamePlayer(this); // basically takes the ship parameter and sets/stores
                                  // the gamePlayer that is adding the ship into the ship itself

        /** this adds the actual ship to the GamePlayer Set<Ship> in Java*/
        this.ships.add(ship);

    }

    public void setShips(Set<Ship> ships){

        this.ships = ships;

    }

    public Set<Ship> getShips(){

        return  ships;

    }

    public Set<Salvo> getSalvos() { return salvos; }

    public void addSalvo(Salvo salvo) {

        salvo.setGamePlayer(this); // stores this gamePlayer into salvo instance

        this.salvos.add(salvo); // adds a salvo to this gamePlayer's Set<Salvo> salvos

    }

}
