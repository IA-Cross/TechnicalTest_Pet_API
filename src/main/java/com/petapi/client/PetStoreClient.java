package com.petapi.client;

import com.petapi.exception.ExternalApiException;
import com.petapi.exception.PetNotFoundException;
import com.petapi.model.PetRequest;
import com.petapi.model.PetResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/** Client that consumes the external Swagger Petstore API. */
@Component
public class PetStoreClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public PetStoreClient(RestTemplate restTemplate,
                          @Value("${petstore.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }
    /** Fetches a pet by ID from the Petstore API. */
    public PetResponse getPetById(Long petId) {
        String url = baseUrl + "/pet/" + petId;
        try {
            return restTemplate.getForObject(url, PetResponse.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new PetNotFoundException(petId);
        } catch (RestClientException ex) {
            throw new ExternalApiException("Error calling Petstore API to get pet " + petId, ex);
        }
    }
    /** Creates a new pet in the Petstore API. */
    public PetResponse createPet(PetRequest request) {
        String url = baseUrl + "/pet";
        try {
            return restTemplate.postForObject(url, request, PetResponse.class);
        } catch (RestClientException ex) {
            throw new ExternalApiException("Error calling Petstore API to create pet", ex);
        }
    }
}
