package com.upgrad.quora.api.exception;

import com.upgrad.quora.api.model.ErrorResponse;
import com.upgrad.quora.service.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(SignUpRestrictedException.class)
    public ResponseEntity<ErrorResponse> signUpRestrictedException(SignUpRestrictedException exe, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ErrorResponse> authenticationFailedException(AuthenticationFailedException exe, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(SignOutRestrictedException.class)
    public ResponseEntity<ErrorResponse> signOutRestrictedException(SignOutRestrictedException exe, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthorizationFailedException.class)
    public ResponseEntity<ErrorResponse> authorizationFailedException(AuthorizationFailedException exe, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> userNotFoundException(UserNotFoundException exe, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidQuestionException.class)
    public ResponseEntity<ErrorResponse> userNotFoundException(InvalidQuestionException exe, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.NOT_FOUND);
    }
}
