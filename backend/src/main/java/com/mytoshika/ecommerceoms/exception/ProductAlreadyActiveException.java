package com.mytoshika.ecommerceoms.exception;

public class ProductAlreadyActiveException extends RuntimeException {
    public ProductAlreadyActiveException(String message) {
        super(message);
    }
}
