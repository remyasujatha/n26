package com.tech26.robotfactory.Exceptions;

import java.io.IOException;

import org.springframework.http.HttpStatus;

/**
 * @author Remya
 *
 */
public class FileOperationsException extends IOException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int errorCode = HttpStatus.BAD_REQUEST.value();
	private String errorMessage;
	
	public FileOperationsException() {
		super();
	}
	
	public FileOperationsException(Throwable throwable) {
		super(throwable);
	}
	
	public FileOperationsException(String errorMessage) {
		super(errorMessage);
	}
	
	public FileOperationsException(String errorMessage, Throwable throwable) {
		super(errorMessage, throwable);
	}

	public FileOperationsException(int errorCode){
		this.errorCode = errorCode; 
	}
	
	public FileOperationsException(int errorCode, String erroMessage) {
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
