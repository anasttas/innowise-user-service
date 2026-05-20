package com.kharlamova.user_service.exceptions;

public class PaymentCardNotFoundException extends RuntimeException {
    public PaymentCardNotFoundException(String message) {
        super(message);
    }
}
