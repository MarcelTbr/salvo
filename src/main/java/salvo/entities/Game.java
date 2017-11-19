package salvo.entities;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
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

    @OneToMany(mappedBy = "game", fetch=FetchType.EAGER)
    List<GameScore> gameScores;

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

    public Set<GamePlayer> getGamePlayers() {

        return gamePlayers;
    }

    public List<GameScore> getGameScores() {return gameScores;}

    public long getId() {
        return id;
    }

    public List<Player> getPlayers(){
        //returns both players of the game
        return gamePlayers.stream()
                .map(gp -> gp.getPlayer())
                .collect(Collectors.toList());
    }

}
