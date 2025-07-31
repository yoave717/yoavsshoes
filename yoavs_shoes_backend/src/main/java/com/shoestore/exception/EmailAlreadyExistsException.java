package com.shoestore.exception;

public class EmailAlreadyExistsException extends RuntimeException {

    private final String email;

    public EmailAlreadyExistsException(String email) {
        super(String.format("Email %s is already in use", email));
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}