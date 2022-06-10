package com.example.demo.exception;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.hibernate.exception.DataException;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.NestedServletException;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

	private String incorrectRequest = "INCORRECT_REQUEST";
	private String badRequest = "BAD_REQUEST";
	private String serverError = "INTERNAL_SERVER_ERROR";
	
	@ExceptionHandler(InvalidParamException.class)
	public final ResponseEntity<ErrorResponse> handleInvalidParamException(InvalidParamException ex,
			WebRequest request) {
		List<String> details = new ArrayList<>();
		details.add(ex.getLocalizedMessage());
		ErrorResponse error = new ErrorResponse(badRequest, details);
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(NoSuchIdException.class)
	public final ResponseEntity<ErrorResponse> handleNoSuchIdException(NoSuchIdException ex,
			WebRequest request) {
		List<String> details = new ArrayList<>();
		details.add(ex.getLocalizedMessage());
		ErrorResponse error = new ErrorResponse(incorrectRequest, details);
		return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(MethodNotSupportedException.class)
	public final ResponseEntity<ErrorResponse> handleMethodNotSupportedException(MethodNotSupportedException ex,
			WebRequest request) {
		List<String> details = new ArrayList<>();
		details.add(ex.getLocalizedMessage());
		ErrorResponse error = new ErrorResponse(serverError, details);
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public final ResponseEntity<ErrorResponse> handleConstraintViolation(
			ConstraintViolationException ex,
			WebRequest request) {
		List<String> details = ex.getConstraintViolations()
				.parallelStream()
				.map(ConstraintViolation::getMessage)
				.collect(Collectors.toList());

		ErrorResponse error = new ErrorResponse("BAD_REQUEST", details);
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	// Generic exception handler
	
	@ExceptionHandler({
		NumberFormatException.class, 
		DataException.class, 
		DataIntegrityViolationException.class, 
		MethodArgumentTypeMismatchException.class, 
		NestedServletException.class, 
		ConversionFailedException.class,
		RuntimeException.class
	})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<String> badRequestException(
		Exception exception
	) {
		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(exception.getMessage());
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<String> exceptionTrust(
		Exception exception
	) {
		return ResponseEntity
		.status(HttpStatus.INTERNAL_SERVER_ERROR)
		.body(exception.getMessage());
	}

}
