package com.petapi.controller;

import com.petapi.exception.ExternalApiException;
import com.petapi.exception.PetNotFoundException;
import com.petapi.model.PetCreateResponse;
import com.petapi.model.PetRequest;
import com.petapi.model.PetResponse;
import com.petapi.service.PetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.net.SocketTimeoutException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Web layer tests for PetController with a mocked service. */
@WebMvcTest(PetController.class)
class PetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PetService petService;

    @Test
    void getPetReturns200WithIdNameAndStatus() throws Exception {
        when(petService.getPet(10L)).thenReturn(new PetResponse(10L, "doggie", "available"));

        mockMvc.perform(get("/api/pet/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("doggie"))
                .andExpect(jsonPath("$.status").value("available"));
    }

    @Test
    void getPetReturns404WhenPetDoesNotExist() throws Exception {
        when(petService.getPet(999L)).thenThrow(new PetNotFoundException(999L));

        mockMvc.perform(get("/api/pet/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Pet not found with id: 999"))
                .andExpect(jsonPath("$.path").value("/api/pet/999"));
    }

    @Test
    void getPetReturns400WhenPetIdIsNotNumeric() throws Exception {
        mockMvc.perform(get("/api/pet/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void getPetReturns502WhenExternalApiFails() throws Exception {
        when(petService.getPet(10L))
                .thenThrow(new ExternalApiException("Error calling Petstore API to get pet 10",
                        new SocketTimeoutException("timeout")));

        mockMvc.perform(get("/api/pet/10"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.status").value(502));
    }

    @Test
    void createPetReturns200WithEnrichedResponse() throws Exception {
        when(petService.createPet(any(PetRequest.class))).thenReturn(new PetCreateResponse(
                "c76e9683-eb63-48ac-9f6b-679fc783a8bc", "2026-07-14T00:32:47", "available", "Rex"));

        mockMvc.perform(post("/api/pet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":987654,\"name\":\"Rex\",\"status\":\"available\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value("c76e9683-eb63-48ac-9f6b-679fc783a8bc"))
                .andExpect(jsonPath("$.dateCreated").value("2026-07-14T00:32:47"))
                .andExpect(jsonPath("$.status").value("available"))
                .andExpect(jsonPath("$.name").value("Rex"));
    }

    @Test
    void createPetReturns400WhenRequiredFieldsAreMissing() throws Exception {
        mockMvc.perform(post("/api/pet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("name: must not be blank; status: must not be blank"));
    }

    @Test
    void createPetReturns400WhenBodyIsMalformed() throws Exception {
        mockMvc.perform(post("/api/pet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("not-json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Malformed or missing request body"));
    }
}
