package com.petapi.service;

import com.petapi.client.PetStoreClient;
import com.petapi.model.PetCreateResponse;
import com.petapi.model.PetRequest;
import com.petapi.model.PetResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Unit tests for the service layer with a mocked Petstore client. */
@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    private static final String UUID_V4_PATTERN =
            "^[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$";

    @Mock
    private PetStoreClient petStoreClient;

    @InjectMocks
    private PetService petService;

    @Test
    void getPetReturnsPetFromClient() {
        when(petStoreClient.getPetById(10L)).thenReturn(new PetResponse(10L, "doggie", "available"));

        PetResponse result = petService.getPet(10L);

        assertEquals(10L, result.getId());
        assertEquals("doggie", result.getName());
        assertEquals("available", result.getStatus());
        verify(petStoreClient).getPetById(10L);
    }

    @Test
    void createPetEnrichesResponseWithTransactionIdAndDateCreated() {
        PetRequest request = buildRequest(987654L, "Rex", "available");
        when(petStoreClient.createPet(any(PetRequest.class)))
                .thenReturn(new PetResponse(987654L, "Rex", "available"));

        PetCreateResponse result = petService.createPet(request);

        assertNotNull(result.getTransactionId());
        assertTrue(result.getTransactionId().matches(UUID_V4_PATTERN),
                "transactionId must be a UUIDv4 but was: " + result.getTransactionId());
        assertNotNull(result.getDateCreated());
        assertDoesNotThrow(() ->
                LocalDateTime.parse(result.getDateCreated(), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertEquals("available", result.getStatus());
        assertEquals("Rex", result.getName());
    }

    @Test
    void createPetFallsBackToRequestDataWhenClientEchoesIncompleteData() {
        PetRequest request = buildRequest(987654L, "Rex", "available");
        when(petStoreClient.createPet(any(PetRequest.class)))
                .thenReturn(new PetResponse(987654L, null, null));

        PetCreateResponse result = petService.createPet(request);

        assertEquals("Rex", result.getName());
        assertEquals("available", result.getStatus());
    }

    private PetRequest buildRequest(Long id, String name, String status) {
        PetRequest request = new PetRequest();
        request.setId(id);
        request.setName(name);
        request.setStatus(status);
        return request;
    }
}
