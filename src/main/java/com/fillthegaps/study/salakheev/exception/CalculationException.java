package com.fillthegaps.study.salakheev.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

@ResponseStatus(SERVICE_UNAVAILABLE)
public class CalculationException extends RuntimeException {

    public CalculationException() {
    }

    public CalculationException(String message) {
        super(message);
    }
}
