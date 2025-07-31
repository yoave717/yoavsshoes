package com.shoestore.exception;

public class FileStorageException extends RuntimeException {

    private final String fileName;
    private final String operation;

    public FileStorageException(String message) {
        super(message);
        this.fileName = null;
        this.operation = null;
    }

    public FileStorageException(String message, String fileName, String operation) {
        super(message);
        this.fileName = fileName;
        this.operation = operation;
    }

    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
        this.fileName = null;
        this.operation = null;
    }

    public String getFileName() {
        return fileName;
    }

    public String getOperation() {
        return operation;
    }
}