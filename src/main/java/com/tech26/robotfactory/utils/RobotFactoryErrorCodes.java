
package com.tech26.robotfactory.utils;

/**
 * @author Remya
 * Error codes used in application
 *
 */
public enum RobotFactoryErrorCodes {
	
	NO_ORDER_MATCHING_ORDER_ID(4000),
	
	DEFAULT_ERROR_CODE(5000),
	
	INVALID_ORDER_PAYLOAD(6000),
	INVALID_ORDER_CODE(6001),
	INVALID_ORDER_COMPONENTS_ARRAY(6002),
	NULL_ORDER_PAYLOD(6003),
	
	
	OUT_OF_STOCK_ERROR(7000),
	
	STOCK_FILE_READ_EXCEPTION(8000),
	STOCK_FILE_PARSE_EXCEPTION(8001),
	STOCK_FILE_UPDATE_EXCEPTION(8002),
	ORDER_FILE_PARSE_EXCEPTION(8003),
	ORDER_FILE_READ_EXCEPTION(8004),
	ORDER_FILEUPDATE_EXCEPTION(8005);
	
	private final long errorCode;
	
	private RobotFactoryErrorCodes(long errorCode) {
		this.errorCode = errorCode;
	}
	
	public long getValue(){
		return errorCode;
	}

}
