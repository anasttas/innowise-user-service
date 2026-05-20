package com.kharlamova.user_service.exceptions;

public class PaymentCardAlreadyExistsException extends RuntimeException {
    public PaymentCardAlreadyExistsException(String message) {
        super(message);
    }
}
