package salvo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import salvo.entities.GameScore;

/**
 * Created by marceltauber on 5/1/17.
 */

@RepositoryRestResource
public interface GameScoreRepository extends JpaRepository<GameScore, Long>{

//        GameScore findByPlayerId(long player_id);

        }