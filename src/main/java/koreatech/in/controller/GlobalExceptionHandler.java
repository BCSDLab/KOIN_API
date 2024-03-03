package koreatech.in.controller;

import koreatech.in.dto.ExceptionResponse;
import koreatech.in.dto.RequestDataInvalidResponse;
import koreatech.in.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

import static koreatech.in.exception.ExceptionInformation.REQUEST_DATA_INVALID;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BadRequestException.class)
    public @ResponseBody
    ResponseEntity BadRequestException(BadRequestException e) {
        return new ResponseEntity<Map<String, Object>>(e.getErrorMessage().getMap(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizeException.class)
    public @ResponseBody
    ResponseEntity UnauthorizeException(UnauthorizeException e) {
        return new ResponseEntity<Map<String, Object>>(e.getErrorMessage().getMap(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ForbiddenException.class)
    public @ResponseBody
    ResponseEntity ForbiddenException(ForbiddenException e) {
        return new ResponseEntity<Map<String, Object>>(e.getErrorMessage().getMap(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NotFoundException.class)
    public @ResponseBody
    ResponseEntity NotFoundException(NotFoundException e) {
        return new ResponseEntity<Map<String, Object>>(e.getErrorMessage().getMap(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConflictException.class)
    public @ResponseBody
    ResponseEntity ConflictException(ConflictException e) {
        return new ResponseEntity<Map<String, Object>>(e.getErrorMessage().getMap(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(PreconditionFailedException.class)
    public @ResponseBody
    ResponseEntity PreconditionFailedException(PreconditionFailedException e) {
        return new ResponseEntity<Map<String, Object>>(e.getErrorMessage().getMap(), HttpStatus.PRECONDITION_FAILED);
    }

    @ExceptionHandler(ValidationException.class)
    public @ResponseBody
    ResponseEntity ValidationException(ValidationException e) {
        return new ResponseEntity<Map<String, Object>>(e.getErrorMessage().getMap(), UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(BaseException.class)
    public @ResponseBody
    ResponseEntity<ExceptionResponse> BaseException(BaseException e) {
        HttpStatus httpStatus = e.getHttpStatus();
        if (httpStatus.equals(UNPROCESSABLE_ENTITY)) {
            return new ResponseEntity<>(RequestDataInvalidResponse.of(
                    REQUEST_DATA_INVALID.getCode(),
                    REQUEST_DATA_INVALID.getMessage(),
                    Collections.singletonList(e.getMessage())
            ),
                    REQUEST_DATA_INVALID.getHttpStatus()
            );
        }

        return new ResponseEntity<>(ExceptionResponse.of(e.getErrorCode(), e.getMessage()), e.getHttpStatus());
    }

    // ValidParameters AOP에서 throw
    @ExceptionHandler(RequestDataInvalidException.class)
    public @ResponseBody
    ResponseEntity<RequestDataInvalidResponse> RequestDataInvalidException(RequestDataInvalidException e) {
        return new ResponseEntity<>(RequestDataInvalidResponse.of(e.getErrorCode(), e.getMessage(), e.getViolations()), e.getHttpStatus());
    }
}