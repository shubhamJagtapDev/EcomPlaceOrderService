package com.example.ecom.exceptions;

public class OrderDoesNotBelongToUserException extends RuntimeException {
    public OrderDoesNotBelongToUserException(String message) {
        super(message);
    }
}
