package salvo.entities;


import javax.persistence.*;
import java.util.Set;



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
    private String password;


    /**
     * JAVA Class default constructor, for SpringBoot to use
     * necessary because we declared a user constructor below
     */
    public Player() {
    }

    /**
     * JAVA Class user constructor
     */
    public Player(String usr, String eml, String password) {

        /**
         * "this."property is assumed
         * because parameter and property names
         * are different
         * */

        username = usr;
        email = eml;
        this.password = password;

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

    public String getPassword() { return password;}

    public void setPassword (String password) { this.password = password;}

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
    public Set<GamePlayer> getGamePlayers() { return gamePlayers;}
    public Set<GameScore> getGameScoresSet (){ return gameScores;}


}