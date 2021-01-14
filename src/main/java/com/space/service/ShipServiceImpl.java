package com.space.service;

import com.space.exception.BadRequestException;
import com.space.exception.ShipNotFoundException;
import com.space.model.Ship;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.List;

@Service
public class ShipServiceImpl implements ShipService {

    private ShipRepository shipRepository;

    @Autowired
    public void setShipRepository(ShipRepository shipRepository) {
        this.shipRepository = shipRepository;
    }

    @Override
    public Page<Ship> getAllShips(Specification<Ship> specification, Pageable sortByName) {
        return shipRepository.findAll(specification, sortByName);
    }

    @Override
    public List<Ship> getAllShips(Specification<Ship> specification) {
        return shipRepository.findAll(specification);
    }

    @Override
    public Ship createShip(Ship ship) {
        if (ship.getName() == null
                || ship.getPlanet() == null
                || ship.getShipType() == null
                || ship.getProdDate() == null
                || ship.getSpeed() == null
                || ship.getCrewSize() == null) {
            throw new BadRequestException("One of Ship params is null");
        }

        checkParams(ship);

        if (ship.getUsed() == null) {
            ship.setUsed(false);
        }

        Double rating = calcRating(ship);
        ship.setRating(rating);

        return shipRepository.saveAndFlush(ship);
    }

    @Override
    public Ship editShip(Long id, Ship ship) {
        checkParams(ship);

        if (!existsById(id)) {
            throw new ShipNotFoundException("Ship not found");
        }

        Ship editShip = shipRepository.findById(id).get();

        if (ship.getName() != null) {
            editShip.setName(ship.getName());
        }

        if (ship.getPlanet() != null) {
            editShip.setPlanet(ship.getPlanet());
        }

        if (ship.getShipType() != null) {
            editShip.setShipType(ship.getShipType());
        }

        if (ship.getProdDate() != null) {
            editShip.setProdDate(ship.getProdDate());
        }

        if (ship.getUsed() != null) {
            editShip.setUsed(ship.getUsed());
        }

        if (ship.getSpeed() != null) {
            editShip.setSpeed(ship.getSpeed());
        }

        if (ship.getCrewSize() != null) {
            editShip.setCrewSize(ship.getCrewSize());
        }

        Double rating = calcRating(editShip);
        editShip.setRating(rating);

        return shipRepository.save(editShip);
    }

    @Override
    public Ship getShip(Long id) {
        if (!shipRepository.existsById(id)) {
            throw new ShipNotFoundException("Ship not found");
        }
        return shipRepository.findById(id).get();
    }

    @Override
    public void deleteById(Long id) {
        if (existsById(id)) {
            shipRepository.deleteById(id);
        } else {
            throw new ShipNotFoundException("Ship not found");
        }
    }

    @Override
    public boolean existsById(long id) {
        return shipRepository.existsById(id);
    }

    private void checkParams(Ship ship) {
        if (ship.getName() != null && (ship.getName().length() < 1 || ship.getName().length() > 50)) {
            throw new BadRequestException("Wrong name");
        }
        if (ship.getPlanet() != null && (ship.getPlanet().length() < 1 || ship.getPlanet().length() > 50)) {
            throw new BadRequestException("Wrong planet");
        }
        if (ship.getCrewSize() != null && (ship.getCrewSize() < 1 || ship.getCrewSize() > 9999)) {
            throw new BadRequestException("Wrong crewSize");
        }
        if (ship.getSpeed() != null && (ship.getSpeed() < 0.01D || ship.getSpeed() > 0.99D)) {
            throw new BadRequestException("Wrong speed");
        }
        if (ship.getProdDate() != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(ship.getProdDate());
            if (cal.get(Calendar.YEAR) < 2800 || cal.get(Calendar.YEAR) > 3019) {
                throw new BadRequestException("Wrong date");
            }
        }
    }

    private Double calcRating(Ship ship) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(ship.getProdDate());
        int year = calendar.get(Calendar.YEAR);

        BigDecimal rating = new BigDecimal((80 * ship.getSpeed() * (ship.getUsed() ? 0.5 : 1)) / (3019 - year + 1));
        return rating.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
