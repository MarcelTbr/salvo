package edu.example;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 * Created by marceltauber on 13/12/16.
 */


@RepositoryRestResource
public interface GamePlayerRepository extends JpaRepository<GamePlayer, Long> {
    List<Player> findByPlayerJoinDate(String playerJoinDate);


}