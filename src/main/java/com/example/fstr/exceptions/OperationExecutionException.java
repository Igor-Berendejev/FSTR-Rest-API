package com.example.fstr.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
public class OperationExecutionException extends Exception {

    public OperationExecutionException(String message) {
        super(message);
    }
}
