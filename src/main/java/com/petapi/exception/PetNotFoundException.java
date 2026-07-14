package com.petapi.exception;

/** Thrown when the external Petstore API reports that a pet does not exist. */
public class PetNotFoundException extends RuntimeException {

    public PetNotFoundException(Long petId) {
        super("Pet not found with id: " + petId);
    }
}
