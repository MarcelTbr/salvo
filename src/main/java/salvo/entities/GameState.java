package salvo.entities;

import javax.persistence.*;

/**
 * Created by marceltauber on 23/11/17.
 */
@Entity
public class GameState {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    @OneToOne(mappedBy = "gameState", fetch = FetchType.EAGER)
    @JoinColumn( name="game_player")
    private GamePlayer gamePlayer;

    private long stateOfGame;

    public GameState() {}


    public long getGameState() {
        return stateOfGame;
    }

    public void setGameState(long gameState) {
        this.stateOfGame = gameState;
    }

}
