package com.fleet.web;

import com.fleet.model.AttributionStatus;
import com.fleet.model.Personnel;
import com.fleet.repo.AttributionRepository;
import com.fleet.repo.PersonnelRepository;
import com.fleet.service.ApiException;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/personnel")
public class PersonnelController {

    private final PersonnelRepository repo;
    private final AttributionRepository attributions;

    public PersonnelController(PersonnelRepository repo, AttributionRepository attributions) {
        this.repo = repo;
        this.attributions = attributions;
    }

    @GetMapping
    public List<Personnel> list() {
        return repo.findByActiveTrueOrderByNameAsc();
    }

    @PostMapping
    public Personnel create(@Valid @RequestBody Personnel in) {
        Personnel p = new Personnel();
        copy(in, p);
        return repo.save(p);
    }

    @PutMapping("/{id}")
    @Transactional
    public Personnel update(@PathVariable Long id, @Valid @RequestBody Personnel in) {
        Personnel p = repo.findByIdAndActiveTrue(id).orElseThrow(() -> ApiException.notFound("Personne"));
        copy(in, p);
        return p;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void delete(@PathVariable Long id) {
        Personnel p = repo.findByIdAndActiveTrue(id).orElseThrow(() -> ApiException.notFound("Personne"));
        if (attributions.existsByPersonnelIdAndStatusAndEndAtAfter(id, AttributionStatus.ACTIVE, LocalDateTime.now()))
            throw ApiException.conflict("Cette personne a une attribution en cours ou à venir. Annulez-la d'abord.");
        p.active = false;
    }

    private static void copy(Personnel from, Personnel to) {
        to.name = from.name.trim();
        to.phone = from.phone;
        to.category = from.category;
    }
}
