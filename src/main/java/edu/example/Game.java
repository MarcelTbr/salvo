package edu.example;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
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


    //testing
    //public ArrayList GameArray = new ArrayList<String>();

    //public Stream GameStream = new Stream<String>(Stream.of(Game.getCreationDate()));









}
