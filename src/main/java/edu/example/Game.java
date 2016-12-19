package edu.example;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.persistence.*;


/**
 * Created by marceltauber on 13/12/16.
 */

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    private String creationDate;

    @OneToMany(mappedBy="game", fetch=FetchType.EAGER)
    Set<GamePlayer> gamePlayers;

    public Game()  { }


    public Game (Date date){
        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        this.creationDate = df.format(date);
    }


    public String getCreationDate() {
        return creationDate;
    }

    /**
     * This method points to the GamePlayer of type Set
     * there we get a list of unique [gamePlayers] instances
     * independently of how many games a player has joined
     * he/she will only show once on the list
     * */

    public Set<GamePlayer> getPlayersList() {

        return gamePlayers;
    }

    public long getId() {
        return id;
    }











}
