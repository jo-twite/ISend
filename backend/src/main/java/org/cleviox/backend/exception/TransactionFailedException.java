package org.cleviox.backend.exception;

public class TransactionFailedException extends RuntimeException {
    public TransactionFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
