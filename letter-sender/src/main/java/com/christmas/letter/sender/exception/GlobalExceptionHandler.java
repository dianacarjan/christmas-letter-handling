package com.christmas.letter.sender.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessagingException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collections;
import java.util.List;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> details = ex.getAllErrors().stream().map(MessageSourceResolvable::getDefaultMessage).toList();

        return ResponseEntity.badRequest().body(new ErrorResponse("Validation failed!", details));
    }

    @ExceptionHandler({MessagingException.class})
    public ResponseEntity<ErrorResponse> handleMessagingExceptions(MessagingException ex) {
        log.error("Error processing current request: {} ", ex.getMessage(), ex);

        return ResponseEntity.internalServerError().body(
                new ErrorResponse("Publishing message to SNS failed!", Collections.singletonList(ex.getMessage()))
        );
    }
}
