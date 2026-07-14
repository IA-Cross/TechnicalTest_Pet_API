package com.petapi.controller;

import com.petapi.model.PetCreateResponse;
import com.petapi.model.PetRequest;
import com.petapi.model.PetResponse;
import com.petapi.service.PetService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** REST controller exposing pet management endpoints. */
@RestController
@RequestMapping("/api/pet")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }
    /** GET /api/pet/{petId} — retrieves a pet by ID. */
    @GetMapping("/{petId}")
    public ResponseEntity<PetResponse> getPet(@PathVariable Long petId) {
        PetResponse response = petService.getPet(petId);
        return ResponseEntity.ok(response);
    }
    /** POST /api/pet — creates a pet and returns an enriched response. */
    @PostMapping
    public ResponseEntity<PetCreateResponse> createPet(@Valid @RequestBody PetRequest request) {
        PetCreateResponse response = petService.createPet(request);
        return ResponseEntity.ok(response);
    }
}
