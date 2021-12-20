package ua.com.serverhelp.simplemonitoring.rest;

import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import ua.com.serverhelp.simplemonitoring.rest.exceptions.BadRequestException;
import ua.com.serverhelp.simplemonitoring.rest.exceptions.NotFoundException;

@ControllerAdvice
public class ControllerExceptionHandler {
    @ResponseBody
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String notFoundHandler(NotFoundException ex) {
        return ex.getMessage();
    }
    @ResponseBody
    @ExceptionHandler(JSONException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String parseJSONErrorHandler(Exception ex) {
        return "Parse JSON error: "+ex.getMessage();
    }
    @ResponseBody
    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String badRequestHandler(Exception ex) {
        return ex.getMessage();
    }
}
