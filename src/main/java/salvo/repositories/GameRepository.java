package salvo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import salvo.entities.Game;

/**
 * Created by marceltauber on 13/12/16.
 */

@RepositoryRestResource
public interface GameRepository extends JpaRepository<Game, Long> {

    Game findById(long id);

}


