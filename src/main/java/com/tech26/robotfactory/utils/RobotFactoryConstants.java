package com.tech26.robotfactory.utils;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public final class RobotFactoryConstants {
	
	public static final String STOCK_FILE_LOCATION = "./stock.json";
	public static final String INITIAL_STOCK_FILE_LOCATION = "classpath:stock.json";
	public static final String STOCK_FILE = "stock.json";
	public static final String ORDER_FILE_LOCATION = "./orders.json";
	
	public static final String JSON_KEY_ERROR_CODE = "errorCode";
	public static final  String JSON_KEY_ERROR_MESSAGE = "errorMessage";
	public static final String JSON_KEY_COMPONENTS = "components";
	public static final String JSON_KEY_ORDER_ID = "order_id";
	public static final String JSON_KEY_TOTAL = "total";
	public static final String JSON_KEY_QUANTITY = "quantity";
	public static final String JSON_KEY_PRICE = "price";
	public static final String JSON_KEY_ITEMS = "items";
	public static final String JSON_KEY_LIST = "list";
	public static final String JSON_KEY_NAME = "name";
	public static final String JSON_KEY_ID = "id";
	public static final String JSON_KEY_MANDATORY = "mandatory";
	public static final String JSON_KEY_CODE = "code";
	public static final long FIRST_ORDER_ID = 0;

	public static final String DEFAULT_ERROR_MESAGE = "oops! Something went wrong. Failed to place the order";
	
	public static final JSONObject getSampleOrderPayload() {
		JSONObject sampleOrderPayload = new JSONObject();
		ArrayList<String> componentsArray = new ArrayList<String>(Arrays.asList("A","I","D","F"));
		sampleOrderPayload.put(JSON_KEY_COMPONENTS, componentsArray);
		return sampleOrderPayload;
	}
}
