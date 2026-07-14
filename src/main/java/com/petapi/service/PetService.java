package com.petapi.service;

import com.petapi.client.PetStoreClient;
import com.petapi.model.PetCreateResponse;
import com.petapi.model.PetRequest;
import com.petapi.model.PetResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/** Service layer handling business logic, console logging, and response enrichment. */
@Service
public class PetService {

    private static final Logger log = LoggerFactory.getLogger(PetService.class);

    private final PetStoreClient petStoreClient;

    public PetService(PetStoreClient petStoreClient) {
        this.petStoreClient = petStoreClient;
    }
    /** Retrieves a pet by ID and prints the result to the console before returning it. */
    public PetResponse getPet(Long petId) {
        PetResponse pet = petStoreClient.getPetById(petId);
        String info = "Pet found: id=" + pet.getId()
                + ", name=" + pet.getName()
                + ", status=" + pet.getStatus();
        System.out.println(info);
        log.info(info);
        return pet;
    }
    /** Creates a pet, prints the result to the console, and returns an enriched response
     *  with a UUIDv4 transactionId and the current system date as dateCreated. */
    public PetCreateResponse createPet(PetRequest request) {
        PetResponse pet = petStoreClient.createPet(request);
        String info = "Pet created: id=" + pet.getId()
                + ", name=" + pet.getName()
                + ", status=" + pet.getStatus();
        System.out.println(info);
        log.info(info);

        // Fall back to the request data if the external API echoes incomplete data.
        String name = pet.getName() != null ? pet.getName() : request.getName();
        String status = pet.getStatus() != null ? pet.getStatus() : request.getStatus();

        return new PetCreateResponse(
                UUID.randomUUID().toString(),
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                status,
                name);
    }
}
