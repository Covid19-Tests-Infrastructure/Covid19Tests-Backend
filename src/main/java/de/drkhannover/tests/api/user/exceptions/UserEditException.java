package de.drkhannover.tests.api.user.exceptions;

public class UserEditException extends Exception {
    private static final long serialVersionUID = -6392177885876281701L;
    private String message;
    
    public UserEditException(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
}
