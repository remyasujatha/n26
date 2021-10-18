package com.tech26.robotfactory.utils;

import org.json.simple.JSONObject;

import com.tech26.robotfactory.interfaces.RobotFactory;

public abstract class RobotFactoryExceptionHandler {
	
	public static String getMessage(RobotFactoryErrorCodes invalidOrderCode, String errorMessage){
		
		JSONObject errorObject = new JSONObject();
		if(invalidOrderCode != null) {
			errorObject.put(RobotFactoryConstants.JSON_KEY_ERROR_CODE, invalidOrderCode.getValue());
		}else {
			errorObject.put(RobotFactoryConstants.JSON_KEY_ERROR_CODE, RobotFactoryErrorCodes.DEFAULT_ERROR_CODE.getValue());
		}
		if(errorMessage ==null ) {
			errorObject.put(RobotFactoryConstants.JSON_KEY_ERROR_MESSAGE, RobotFactoryConstants.DEFAULT_ERROR_MESAGE);
		}else {
			errorObject.put(RobotFactoryConstants.JSON_KEY_ERROR_MESSAGE, errorMessage);
		}
		
		return errorObject.toJSONString();
	}

}
