package com.christmas.letter.processor.exception;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(HandlerMethodValidationException.class)
	public ResponseEntity<ErrorResponse> handleMethodValidationException(
			HandlerMethodValidationException ex) {
		List<Map<String, String>> errors =
				ex.getAllErrors().stream().map(this::getValidations).toList();

		return ResponseEntity.badRequest().body(new ErrorResponse("Validation failure", errors));
	}

	private Map<String, String> getValidations(MessageSourceResolvable messageSourceResolvable) {
		Map<String, String> error = new HashMap<>();
		String parameterValue =
				((MessageSourceResolvable)
								Objects.requireNonNull(messageSourceResolvable.getArguments())[0])
						.getDefaultMessage();
		error.put(parameterValue, messageSourceResolvable.getDefaultMessage());

		return error;
	}
}
