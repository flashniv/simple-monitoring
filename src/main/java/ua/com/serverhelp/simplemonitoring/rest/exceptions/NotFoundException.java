package ua.com.serverhelp.simplemonitoring.rest.exceptions;

public class NotFoundException extends RuntimeException{
    public NotFoundException(String message) {
        super(message);
    }
}
