package salvo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import salvo.entities.GamePlayer;
import salvo.entities.Salvo;

import java.util.List;

/**
 * Created by marceltauber on 2/1/17.
 */

@RepositoryRestResource
public interface SalvoRepository extends JpaRepository<Salvo, Long> {
//    Salvo findBySalvoId(long id);
    List<Salvo> findByGamePlayer (GamePlayer gamePlayer);
}
