package com.checkpointBlog.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(ElementNotFoundException.class)
	public ResponseEntity<ApiErrorDefault>
	handleElementNotFoundException(Exception e){
		ApiErrorDefault apiError = new ApiErrorDefault(HttpStatus.NOT_FOUND,
		e.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
	}
	
	@ExceptionHandler(DatabaseErrorException.class)
	public ResponseEntity<ApiErrorDefault>
	handleDatabaseErrorException(Exception e){
		ApiErrorDefault apiError = new ApiErrorDefault(HttpStatus.SERVICE_UNAVAILABLE,
		e.getMessage());
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(apiError);
	}
	
	@ExceptionHandler(BadCredentialException.class)
	public ResponseEntity<ApiErrorDefault>
	handleBadCredentialException(Exception e){
		ApiErrorDefault apiError = new ApiErrorDefault(HttpStatus.BAD_REQUEST,
		e.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
	}
}