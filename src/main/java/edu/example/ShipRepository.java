package edu.example;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Created by marceltauber on 19/12/16.
 */


@RepositoryRestResource
public interface ShipRepository extends JpaRepository<Ship, Long> {
//    List<Ship> findByShipId(long id); // To retrieve a ship of the list by passing
                                        // it's id as a parameter

}
