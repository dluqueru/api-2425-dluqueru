package com.checkpointBlog.exception;

public class DatabaseErrorException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public DatabaseErrorException(String message) {
		super(message);
	}

}