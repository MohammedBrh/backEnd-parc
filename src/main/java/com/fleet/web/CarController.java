package com.fleet.web;

import com.fleet.model.AttributionStatus;
import com.fleet.model.Car;
import com.fleet.repo.AttributionRepository;
import com.fleet.repo.CarRepository;
import com.fleet.service.ApiException;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cars")
public class CarController {

    private final CarRepository repo;
    private final AttributionRepository attributions;

    public CarController(CarRepository repo, AttributionRepository attributions) {
        this.repo = repo;
        this.attributions = attributions;
    }

    @GetMapping
    public List<Car> list() {
        return repo.findByActiveTrueOrderByMatriculeAsc();
    }

    @GetMapping("/available")
    public List<Car> available(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return repo.findAvailable(start, end);
    }

    @PostMapping
    @Transactional
    public Car create(@Valid @RequestBody Car in) {
        in.matricule = in.matricule.trim().toUpperCase();
        // A previously deleted car with the same matricule is brought back instead of failing on the unique key.
        Car target = repo.findByMatricule(in.matricule).orElse(null);
        if (target != null && target.active) throw ApiException.conflict("Cette matricule existe déjà.");
        if (target == null) target = new Car();
        copy(in, target);
        target.active = true;
        return repo.save(target);
    }

    @PutMapping("/{id}")
    @Transactional
    public Car update(@PathVariable Long id, @Valid @RequestBody Car in) {
        Car c = repo.findByIdAndActiveTrue(id).orElseThrow(() -> ApiException.notFound("Voiture"));
        String mat = in.matricule.trim().toUpperCase();
        repo.findByMatricule(mat).filter(o -> !o.id.equals(id))
                .ifPresent(o -> { throw ApiException.conflict("Cette matricule existe déjà."); });
        in.matricule = mat;
        copy(in, c);
        return c;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void delete(@PathVariable Long id) {
        Car c = repo.findByIdAndActiveTrue(id).orElseThrow(() -> ApiException.notFound("Voiture"));
        if (attributions.existsByCarIdAndStatusAndEndAtAfter(id, AttributionStatus.ACTIVE, LocalDateTime.now()))
            throw ApiException.conflict("Cette voiture a une attribution en cours ou à venir. Annulez-la d'abord.");
        c.active = false; // soft delete
    }

    private static void copy(Car from, Car to) {
        to.matricule = from.matricule;
        to.places = from.places;
        to.type = from.type.trim();
        to.category = from.category;
        to.status = from.status;
    }
}
