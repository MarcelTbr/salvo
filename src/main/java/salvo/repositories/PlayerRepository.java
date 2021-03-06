package salvo.repositories;

/**
 * Created by marceltauber on 7/12/16.
 */
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
/**
 * Making it a REST Repository "import ....RepositoryRestResource" + the annotation: "@RepositoryRestResource"
 * */
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import salvo.entities.Player;

@RepositoryRestResource
public interface PlayerRepository extends JpaRepository<Player, Long> {
    List<Player> findByUsername(String username);
    List<Player> findByEmail(String email);
    Player findById(long id);
}