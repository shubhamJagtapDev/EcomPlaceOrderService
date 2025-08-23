package com.example.ecom.exceptions;

public class ProductInStockException extends RuntimeException {
    public ProductInStockException(String message) {
        super(message);
    }
}
