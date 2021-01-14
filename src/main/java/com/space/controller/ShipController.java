package com.space.controller;

import com.space.exception.BadRequestException;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import com.space.specification.ShipSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/rest")
public class ShipController {

    private ShipService shipService;

    @Autowired
    public void setShipService(ShipService shipService) {
        this.shipService = shipService;
    }

    @GetMapping(value = "/ships")
    @ResponseStatus(HttpStatus.OK)
    public List<Ship> getAllShips(@RequestParam(required = false) String name,
                                  @RequestParam(required = false) String planet,
                                  @RequestParam(required = false) ShipType shipType,
                                  @RequestParam(required = false) Long after,
                                  @RequestParam(required = false) Long before,
                                  @RequestParam(required = false) Boolean isUsed,
                                  @RequestParam(required = false) Double minSpeed,
                                  @RequestParam(required = false) Double maxSpeed,
                                  @RequestParam(required = false) Integer minCrewSize,
                                  @RequestParam(required = false) Integer maxCrewSize,
                                  @RequestParam(required = false) Double minRating,
                                  @RequestParam(required = false) Double maxRating,
                                  @RequestParam(required = false, defaultValue = "ID") ShipOrder order,
                                  @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
                                  @RequestParam(required = false, defaultValue = "3") Integer pageSize) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName()));
        Specification<Ship> specification = ShipSpecification.getSpecification(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);

        return shipService.getAllShips(specification, pageable).getContent();
    }

    @RequestMapping(value = "/ships/count", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Integer getCount(@RequestParam(required = false) String name,
                            @RequestParam(required = false) String planet,
                            @RequestParam(required = false) ShipType shipType,
                            @RequestParam(required = false) Long after,
                            @RequestParam(required = false) Long before,
                            @RequestParam(required = false) Boolean isUsed,
                            @RequestParam(required = false) Double minSpeed,
                            @RequestParam(required = false) Double maxSpeed,
                            @RequestParam(required = false) Integer minCrewSize,
                            @RequestParam(required = false) Integer maxCrewSize,
                            @RequestParam(required = false) Double minRating,
                            @RequestParam(required = false) Double maxRating) {
        Specification<Ship> specification = ShipSpecification.getSpecification(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);

        return shipService.getAllShips(specification).size();
    }

    @PostMapping(value = "/ships")
    @ResponseBody
    public ResponseEntity<Ship> addShip (@RequestBody Ship ship) {
        return new ResponseEntity<Ship>(shipService.createShip(ship), HttpStatus.OK);
    }

    @GetMapping(value = "/ships/{id}")
    @ResponseBody
    public ResponseEntity<Ship> getShip(@PathVariable String id) {

        Long longId = checkId(id);

        if (longId == 0) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        if (!shipService.existsById(longId)) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Ship>(shipService.getShip(longId), HttpStatus.OK);
    }

    @PostMapping(value = "/ships/{id}")
    @ResponseBody
    public ResponseEntity<Ship> editShip (@PathVariable String id, @RequestBody Ship ship) {
        Long longId = checkId(id);

        return new ResponseEntity<Ship>(shipService.editShip(longId, ship), HttpStatus.OK);
    }

    @DeleteMapping(value = "/ships/{id}")
    public void deleteShip (@PathVariable String id) {
        Long longId = checkId(id);
        shipService.deleteById(longId);
        new ResponseEntity<>(HttpStatus.OK);
    }

    public Long checkId (String id) {
        if (id == null || id.equals("") || id.equals("0")) {
            throw new BadRequestException("Wrong ID");
        }

        try {
            return Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new BadRequestException("ID not digit", e);
        }
    }
}
