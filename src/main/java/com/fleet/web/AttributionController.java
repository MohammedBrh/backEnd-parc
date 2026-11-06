package com.fleet.web;

import com.fleet.model.Attribution;
import com.fleet.model.AttributionStatus;
import com.fleet.service.AttributionService;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attributions")
public class AttributionController {

    private final AttributionService service;

    public AttributionController(AttributionService service) {
        this.service = service;
    }

    /** ?status=ACTIVE|COMPLETED|CANCELLED ; without it, returns everything (newest first). */
    @GetMapping
    public List<Attribution> list(@RequestParam(required = false) AttributionStatus status) {
        return service.list(status);
    }

    @GetMapping("/{id}")
    public Attribution get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    public Attribution create(@RequestBody AttributionService.Request request) {
        return service.create(request);
    }

    @PostMapping("/{id}/complete")
    public Attribution complete(@PathVariable Long id) {
        return service.complete(id);
    }

    @PostMapping("/{id}/cancel")
    public Attribution cancel(@PathVariable Long id) {
        return service.cancel(id);
    }
}
