package com.tech26.robotfactory.Exceptions;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;

public class InvalidOrderExcception extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int errorCode = HttpStatus.BAD_REQUEST.value();
	private String errorMessage;
	
	public InvalidOrderExcception() {
		super();
	}
	
	public InvalidOrderExcception(Throwable throwable) {
		super(throwable);
	}
	
	public InvalidOrderExcception(String errorMessage) {
		super(errorMessage);
	}
	
	public InvalidOrderExcception(String errorMessage, Throwable throwable) {
		super(errorMessage, throwable);
	}

	public InvalidOrderExcception(int errorCode){
		this.errorCode = errorCode; 
	}
	
	public InvalidOrderExcception(int errorCode, String erroMessage) {
		this.errorCode = errorCode; 
		this.errorMessage = erroMessage;
	}
	
	public int getCode() {
		return errorCode;
	}
	
	public String getMessage() {
		return errorMessage;
	}
}
