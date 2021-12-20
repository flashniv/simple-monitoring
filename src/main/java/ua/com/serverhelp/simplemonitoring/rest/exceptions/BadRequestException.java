package ua.com.serverhelp.simplemonitoring.rest.exceptions;

public class BadRequestException extends RuntimeException{
    public BadRequestException(String message) {
        super(message);
    }
}
