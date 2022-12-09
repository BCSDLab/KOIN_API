package koreatech.in.controller;

import koreatech.in.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = BadRequestException.class)
    public @ResponseBody
    ResponseEntity BadRequestException(BadRequestException e) {
        return new ResponseEntity<Map<String, Object>>(e.getErrorMessage().getMap(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = UnauthorizeException.class)
    public @ResponseBody
    ResponseEntity UnauthorizeException(UnauthorizeException e) {
        return new ResponseEntity<Map<String, Object>>(e.getErrorMessage().getMap(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = ForbiddenException.class)
    public @ResponseBody
    ResponseEntity ForbiddenException(ForbiddenException e) {
        return new ResponseEntity<Map<String, Object>>(e.getErrorMessage().getMap(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = NotFoundException.class)
    public @ResponseBody
    ResponseEntity NotFoundException(NotFoundException e) {
        return new ResponseEntity<Map<String, Object>>(e.getErrorMessage().getMap(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = ConflictException.class)
    public @ResponseBody
    ResponseEntity ConflictException(ConflictException e) {
        return new ResponseEntity<Map<String, Object>>(e.getErrorMessage().getMap(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = PreconditionFailedException.class)
    public @ResponseBody
    ResponseEntity PreconditionFailedException(PreconditionFailedException e) {
        return new ResponseEntity<Map<String, Object>>(e.getErrorMessage().getMap(), HttpStatus.PRECONDITION_FAILED);
    }

    @ExceptionHandler(value = ValidationException.class)
    public @ResponseBody
    ResponseEntity ValidationException(ValidationException e) {
        return new ResponseEntity<Map<String, Object>>(e.getErrorMessage().getMap(), HttpStatus.UNPROCESSABLE_ENTITY);
    }

//    @ExceptionHandler(value = Exception.class)
//    public String handleException(Exception e){
//        System.out.println("global server error except handle");
//        return e.getMessage();
//    }
}