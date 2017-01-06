package edu.example;


import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Tells Spring to create the table Players
 */
@Entity
public class Player {


    /**
     * long extends the number limit of int
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String username;
    private String email;


    /**
     * JAVA Class default constructor, for SpringBoot to use
     * necessary because we declared a user constructor below
     */
    public Player() {
    }

    /**
     * JAVA Class user constructor
     */
    public Player(String usr, String eml) {

        /**
         * "this."property is assumed
         * because parameter and property names
         * are different
         * */

        username = usr;
        email = eml;

    }

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    Set<GamePlayer> gamePlayers;

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    Set<GameScore> gameScores;

    public String getUsername() {
        return username;
    }

    public void setUsername(String usr) {
        this.username = usr;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String eml) {
        this.email = eml;
    }

    /**
     * Tell Java Library to use this method instead of the standard one
     */
    @Override
    public String toString() {
        return username + " " + email;
    }


    public long getId() {
        return id;

    }

    public Set<GameScore> getGameScoresSet (){ return gameScores;}


}