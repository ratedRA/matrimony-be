package com.matrimony.common.exceptionhandling;

import com.matrimony.common.ResponseBuilder;
import com.matrimony.common.exceptionhandling.customexceptions.DuplicateUserException;
import com.matrimony.common.exceptionhandling.customexceptions.OtpExpiredException;
import com.matrimony.common.exceptionhandling.customexceptions.OtpInvalidException;
import com.matrimony.common.exceptionhandling.customexceptions.TokenExpiredException;
import com.matrimony.common.exceptionhandling.customexceptions.TokenInvalidException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    private static final String DEFAULT_ERROR_VIEW = "error";

    @Autowired
    private ResponseBuilder responseBuilder;

    protected ResponseEntity<ApiError> customHandleMethodArgumentNotValid(
            MethodArgumentNotValidException ex) {
        List<String> errors = new ArrayList<String>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }

        ApiError apiError =
                new ApiError(ex.getLocalizedMessage(), errors);

        return new ResponseEntity(responseBuilder.returnFailure(apiError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {TokenInvalidException.class})
    @ResponseBody
    public final ResponseEntity<Object> handleTokenInvalidException(TokenInvalidException ex) {
        return new ResponseEntity(responseBuilder.returnFailure(ex.getLocalizedMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {TokenExpiredException.class})
    @ResponseBody
    public final ResponseEntity<Object> handleTokenExpiredException(TokenExpiredException ex) {
        return new ResponseEntity(responseBuilder.returnFailure(ex.getLocalizedMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {OtpInvalidException.class})
    @ResponseBody
    public final ResponseEntity<Object> handleOtpInvalidException(OtpInvalidException ex) {
        return new ResponseEntity(responseBuilder.returnFailure(ex.getLocalizedMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {OtpExpiredException.class})
    @ResponseBody
    public final ResponseEntity<Object> handleOtpExpiredException(OtpExpiredException ex) {
        return new ResponseEntity(responseBuilder.returnFailure(ex.getLocalizedMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {NullPointerException.class})
    @ResponseBody
    public final ResponseEntity<Object> handleNullPointerException(NullPointerException ex) {
        return new ResponseEntity(responseBuilder.returnFailure(ex.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {DuplicateUserException.class})
    @ResponseBody
    public final ResponseEntity<Object> handleDuplicateUser(DuplicateUserException ex) {
        return new ResponseEntity(responseBuilder.returnFailure(ex.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {AccessDeniedException.class})
    @ResponseBody
    public final ResponseEntity<Object> accessDebiedException(AccessDeniedException ex) {
        return new ResponseEntity(responseBuilder.returnFailure(ex.getLocalizedMessage()), HttpStatus.FORBIDDEN);
    }

    // Total control - setup a model and return the view name yourself
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public final ResponseEntity<Object> defaultErrorHandler(Exception e) {
        if(e.getLocalizedMessage() != null){
            return new ResponseEntity(responseBuilder.returnFailure(e.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(responseBuilder.returnFailure(e.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String error = "No com.demo.handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL();

        ApiError apiError = new ApiError(ex.getLocalizedMessage(), error);
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }


}
