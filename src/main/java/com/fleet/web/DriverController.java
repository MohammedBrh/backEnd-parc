package com.fleet.web;

import com.fleet.model.AttributionStatus;
import com.fleet.model.Driver;
import com.fleet.repo.AttributionRepository;
import com.fleet.repo.DriverRepository;
import com.fleet.service.ApiException;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    private final DriverRepository repo;
    private final AttributionRepository attributions;

    public DriverController(DriverRepository repo, AttributionRepository attributions) {
        this.repo = repo;
        this.attributions = attributions;
    }

    @GetMapping
    public List<Driver> list() {
        return repo.findByActiveTrueOrderByNameAsc();
    }

    @GetMapping("/available")
    public List<Driver> available(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return repo.findAvailable(start, end);
    }

    @PostMapping
    public Driver create(@Valid @RequestBody Driver in) {
        Driver d = new Driver();
        copy(in, d);
        return repo.save(d);
    }

    @PutMapping("/{id}")
    @Transactional
    public Driver update(@PathVariable Long id, @Valid @RequestBody Driver in) {
        Driver d = repo.findByIdAndActiveTrue(id).orElseThrow(() -> ApiException.notFound("Chauffeur"));
        copy(in, d);
        return d;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void delete(@PathVariable Long id) {
        Driver d = repo.findByIdAndActiveTrue(id).orElseThrow(() -> ApiException.notFound("Chauffeur"));
        if (attributions.existsByDriverIdAndStatusAndEndAtAfter(id, AttributionStatus.ACTIVE, LocalDateTime.now()))
            throw ApiException.conflict("Ce chauffeur a une mission en cours ou à venir. Annulez-la d'abord.");
        d.active = false;
    }

    private static void copy(Driver from, Driver to) {
        to.name = from.name.trim();
        to.phone = from.phone;
        to.licenseNumber = from.licenseNumber == null ? null : from.licenseNumber.trim();
        to.status = from.status;
    }
}
