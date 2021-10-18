package com.tech26.robotfactory.Exceptions;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;

public class PurchaseOrderException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int errorCode = HttpStatus.BAD_REQUEST.value();
	private String errorMessage;
	
	public PurchaseOrderException() {
		// TODO Auto-generated constructor stub
	}
	public PurchaseOrderException(int errorCode){
		this.errorCode = errorCode; 
	}
	
	public PurchaseOrderException(int errorCode, String erroMessage) {
		this.errorCode = errorCode; 
		this.errorMessage = erroMessage;
	}
	
	public PurchaseOrderException(String errorMessage) {
		super(errorMessage);
	}
	
	public PurchaseOrderException(String errorMessage , Throwable throwable) {
		super(errorMessage, throwable);
	}

	public PurchaseOrderException(Throwable throwable) {
		super(throwable);
	}
	
	
	public int getCode() {
		return errorCode;
	}
	
	public String getMessage() {
		return errorMessage;
	}
}
