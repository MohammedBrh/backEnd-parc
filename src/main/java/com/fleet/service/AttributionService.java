package com.fleet.service;

import com.fleet.model.*;
import com.fleet.repo.*;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AttributionService {

    public record Request(
            Long carId, Long driverId, Long personnelId,
            LocalDateTime startAt, LocalDateTime endAt,
            Integer passengers, String destination, String notes) {}

    private final AttributionRepository attributions;
    private final CarRepository cars;
    private final DriverRepository drivers;
    private final PersonnelRepository personnel;

    public AttributionService(AttributionRepository attributions, CarRepository cars,
                              DriverRepository drivers, PersonnelRepository personnel) {
        this.attributions = attributions;
        this.cars = cars;
        this.drivers = drivers;
        this.personnel = personnel;
    }

    @Transactional(readOnly = true)
    public List<Attribution> list(AttributionStatus status) {
        return status == null ? attributions.findAllByOrderByStartAtDesc()
                              : attributions.findByStatusOrderByStartAtAsc(status);
    }

    @Transactional(readOnly = true)
    public Attribution get(Long id) {
        return attributions.findById(id).orElseThrow(() -> ApiException.notFound("Attribution"));
    }

    @Transactional
    public Attribution create(Request r) {
        if (r.carId() == null || r.driverId() == null || r.personnelId() == null
                || r.startAt() == null || r.endAt() == null) {
            throw ApiException.bad("Personne, voiture, chauffeur et dates sont obligatoires.");
        }
        if (!r.endAt().isAfter(r.startAt())) {
            throw ApiException.bad("La fin doit être après le début.");
        }
        int pax = r.passengers() == null ? 1 : r.passengers();
        if (pax < 1) throw ApiException.bad("Au moins 1 passager.");

        Car car = cars.findByIdAndActiveTrue(r.carId()).orElseThrow(() -> ApiException.notFound("Voiture"));
        Driver driver = drivers.findByIdAndActiveTrue(r.driverId()).orElseThrow(() -> ApiException.notFound("Chauffeur"));
        Personnel person = personnel.findByIdAndActiveTrue(r.personnelId()).orElseThrow(() -> ApiException.notFound("Personne"));

        // Business rules
        if (car.status != CarStatus.AVAILABLE) throw ApiException.conflict("La voiture " + car.matricule + " est en maintenance.");
        if (driver.status != DriverStatus.AVAILABLE) throw ApiException.conflict(driver.name + " est indisponible.");
        if (pax > car.places) throw ApiException.bad("La voiture a " + car.places + " places pour " + pax + " passagers.");
        if (person.category == PersonnelCategory.VIP
                && car.category != CarCategory.VIP && car.category != CarCategory.SERVICE_CLASS) {
            throw ApiException.bad("Une personne VIP nécessite une voiture VIP ou Service classe.");
        }

        // No double booking
        if (attributions.countCarOverlap(car.id, r.startAt(), r.endAt()) > 0)
            throw ApiException.conflict("La voiture " + car.matricule + " est déjà attribuée sur cette période.");
        if (attributions.countDriverOverlap(driver.id, r.startAt(), r.endAt()) > 0)
            throw ApiException.conflict(driver.name + " a déjà une mission sur cette période.");
        if (attributions.countPersonnelOverlap(person.id, r.startAt(), r.endAt()) > 0)
            throw ApiException.conflict(person.name + " a déjà une voiture sur cette période.");

        Attribution a = new Attribution();
        a.car = car;
        a.driver = driver;
        a.personnel = person;
        a.startAt = r.startAt();
        a.endAt = r.endAt();
        a.passengers = pax;
        a.destination = blankToNull(r.destination());
        a.notes = blankToNull(r.notes());
        return attributions.save(a);
    }

    @Transactional
    public Attribution complete(Long id) {
        return changeStatus(id, AttributionStatus.COMPLETED);
    }

    @Transactional
    public Attribution cancel(Long id) {
        return changeStatus(id, AttributionStatus.CANCELLED);
    }

    private Attribution changeStatus(Long id, AttributionStatus to) {
        Attribution a = get(id);
        if (a.status != AttributionStatus.ACTIVE) throw ApiException.conflict("Cette attribution est déjà clôturée.");
        a.status = to;
        return a;
    }

    private static String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }
}
