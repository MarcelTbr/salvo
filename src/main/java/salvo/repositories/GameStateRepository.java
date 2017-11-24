package salvo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import salvo.entities.GameState;

/**
 * Created by marceltauber on 23/11/17.
 */
public interface GameStateRepository extends JpaRepository<GameState, Long> {
            GameState findByGamePlayerId (long gamePlayerId);

}
