package edu.example;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;




/**
 * Created by marceltauber on 13/12/16.
 */

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    /** long extends the number limit of int */
    private long id;



    private String creationDate;






    public Game()  { }


    public Game (Date date){
        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        this.creationDate = df.format(date);
    }


    public String getCreationDate() {
        return creationDate;
    }





}
