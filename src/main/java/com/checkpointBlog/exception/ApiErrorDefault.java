package com.checkpointBlog.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

public class ApiErrorDefault {

	private HttpStatus status;
	
	@JsonFormat(shape = Shape.STRING, pattern = "dd/MM/yyyy hh:mm:ss")
	private LocalDateTime date;
	
	private String message;
	
	public ApiErrorDefault() {
		super();
	}

	public ApiErrorDefault(HttpStatus status, String message) {
		super();
		this.status = status;
		this.date = LocalDateTime.now();
		this.message = message;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
