package edu.example;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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

    public Set<GamePlayer> getPlayersList() {

        return gamePlayers;
    }

    public long getId() {
        return id;
    }

    //testing
   /* public Set<Game> getGame(List<Game> games){

        return games.stream()
                .collect(Collectors.toSet());
    }*/


    //public ArrayList GameArray = new ArrayList<String>();

    //public Stream GameStream = new Stream<String>(Stream.of(Game.getCreationDate()));









}
