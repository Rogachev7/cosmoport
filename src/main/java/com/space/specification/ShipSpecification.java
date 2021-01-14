package com.space.specification;

import com.space.model.Ship;
import com.space.model.ShipType;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;

public class ShipSpecification {

    public static Specification<Ship> getSpecification(String name,
                                                       String planet,
                                                       ShipType shipType,
                                                       Long after, Long before,
                                                       Boolean isUsed,
                                                       Double minSpeed, Double maxSpeed,
                                                       Integer minCrewSize, Integer maxCrewSize,
                                                       Double minRating, Double maxRating) {

        return Specification.where(ShipSpecification.shipsByName(name)
                .and(ShipSpecification.shipsByPlanet(planet)))
                .and(ShipSpecification.shipsByShipType(shipType))
                .and(ShipSpecification.shipsByDate(after, before))
                .and(ShipSpecification.shipsByUsage(isUsed))
                .and(ShipSpecification.shipsBySpeed(minSpeed, maxSpeed))
                .and(ShipSpecification.shipsByCrewSize(minCrewSize, maxCrewSize))
                .and(ShipSpecification.shipsByRating(minRating, maxRating));
    }

    private static Specification<Ship> shipsByName(String name) {
        return (r, q, cb) -> name == null ? null : cb.like(r.get("name"), ("%" + name + "%"));
    }

    private static Specification<Ship> shipsByPlanet(String planet) {
        return (r, q, cb) -> planet == null ? null : cb.like(r.get("planet"), ("%" + planet + "%"));
    }

    private static Specification<Ship> shipsByShipType(ShipType shipType) {
        return (r, q, cb) -> shipType == null ? null : cb.equal(r.get("shipType"), shipType);
    }

    private static Specification<Ship> shipsByDate(Long after, Long before) {
        return (r, q, cb) -> {
            if (after == null && before == null) {
                return null;
            }
            if (after == null) {
                return cb.lessThanOrEqualTo(r.get("prodDate"), new Date(before));
            }
            if (before == null) {
                return cb.greaterThanOrEqualTo(r.get("prodDate"), new Date(after));
            }

            return cb.between(r.get("prodDate"), new Date(after), new Date(before));
        };
    }

    private static Specification<Ship> shipsByUsage(Boolean isUsed) {
        return (r, q, cb) -> {
            if (isUsed == null) {
                return null;
            }
            if (isUsed) {
                return cb.isTrue(r.get("isUsed"));
            } else {
                return cb.isFalse(r.get("isUsed"));
            }
        };
    }

    private static Specification<Ship> shipsBySpeed(Double minSpeed, Double maxSpeed) {
        return (r, q, cb) -> {
            if (minSpeed == null && maxSpeed == null) {
                return null;
            }
            if (minSpeed == null) {
                return cb.lessThanOrEqualTo(r.get("speed"), maxSpeed);
            }
            if (maxSpeed == null) {
                return cb.greaterThanOrEqualTo(r.get("speed"), minSpeed);
            }
            return cb.between(r.get("speed"), minSpeed, maxSpeed);
        };
    }

    private static Specification<Ship> shipsByCrewSize(Integer minCrewSize, Integer maxCrewSize) {
        return (r, q, cb) -> {
            if (minCrewSize == null && maxCrewSize == null) {
                return null;
            }
            if (minCrewSize == null) {
                return cb.lessThanOrEqualTo(r.get("crewSize"), maxCrewSize);
            }
            if (maxCrewSize == null) {
                return cb.greaterThanOrEqualTo(r.get("crewSize"), minCrewSize);
            }
            return cb.between(r.get("crewSize"), minCrewSize, maxCrewSize);
        };
    }

    private static Specification<Ship> shipsByRating(Double minRating, Double maxRating) {
        return (r, q, cb) -> {
            if (minRating == null && maxRating == null) {
                return null;
            }
            if (minRating == null) {
                return cb.lessThanOrEqualTo(r.get("rating"), maxRating);
            }
            if (maxRating == null) {
                return cb.greaterThanOrEqualTo(r.get("rating"), minRating);
            }
            return cb.between(r.get("rating"), minRating, maxRating);
        };
    }
}
