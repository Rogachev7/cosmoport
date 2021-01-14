package com.space.service;

import com.space.model.Ship;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface ShipService {

    Page<Ship> getAllShips (Specification<Ship> specification, Pageable sortByName);

    List<Ship> getAllShips (Specification<Ship> specification);

    Ship createShip(Ship requestShip);

    Ship editShip (Long id, Ship ship);

    Ship getShip (Long id);

    void deleteById (Long id);

    boolean existsById (long id);

}
