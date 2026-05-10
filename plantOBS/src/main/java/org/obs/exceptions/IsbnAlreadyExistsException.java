package org.obs.exceptions;

@Deprecated
public class IsbnAlreadyExistsException extends RuntimeException {
    public IsbnAlreadyExistsException(String isbn) {
        super("Book with ISBN=" + isbn + " already exists");
    }
}