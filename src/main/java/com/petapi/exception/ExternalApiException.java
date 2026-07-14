package com.petapi.exception;

/** Thrown when the external Petstore API fails (5xx, timeout, connection error). */
public class ExternalApiException extends RuntimeException {

    public ExternalApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
