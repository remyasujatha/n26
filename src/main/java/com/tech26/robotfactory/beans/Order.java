package com.tech26.robotfactory.beans;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.util.ResourceUtils;

import com.tech26.robotfactory.Exceptions.FileOperationsException;
import com.tech26.robotfactory.utils.RobotFactoryConstants;
import com.tech26.robotfactory.utils.RobotFactoryErrorCodes;
import com.tech26.robotfactory.utils.RobotFactoryExceptionHandler;

public class Order {

	private JSONArray components;
	private long orderID;
	private double total;

	/**
	 * @return the orderID
	 */
	public long getOrderID() {
		return orderID;
	}

	/**
	 * @param orderID the orderID to set
	 */
	public void setOrderID(long orderID) {
		this.orderID = orderID;
	}

	/**
	 * @return the total
	 */
	public double getTotal() {
		return total;
	}

	/**
	 * @param total the total to set
	 */
	public void setTotal(double total) {
		this.total = total;
	}

	/**
	 * @return the order
	 */
	public JSONArray getComponents() {
		return components;
	}

	/**
	 * @param order the order to set
	 */
	public void setComponents(JSONArray components) {
		this.components = components;
	}

	public JSONObject getSuccessOrderList() throws FileOperationsException {
		JSONObject successfullOrders = new JSONObject();
		File ordersFile = null;
		try {
			ordersFile = new File(RobotFactoryConstants.ORDER_FILE_LOCATION);
			if (ordersFile.exists()) {
				JSONParser parser = new JSONParser();
				successfullOrders = (JSONObject) parser.parse(new FileReader(ordersFile));
			}
			return successfullOrders;
		} catch (IOException | ParseException e) {
			RobotFactoryErrorCodes errorCode;
			if (e instanceof ParseException) {
				errorCode = RobotFactoryErrorCodes.ORDER_FILE_PARSE_EXCEPTION;
			} else {
				errorCode = RobotFactoryErrorCodes.ORDER_FILE_READ_EXCEPTION;
			}
			String errorMessage = RobotFactoryExceptionHandler.getMessage(errorCode,
					"Failed to get orders. Please try later.");
			throw new FileOperationsException(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorMessage);
		}
	}
//
//	public JSONArray getSortedOrderList(JSONArray orders) {
//		return getSortedOrderList(orders, "asc");
//
//	}
//
//	public JSONArray getSortedOrderList(JSONArray orders, String orderBy) {
////		List<JSONObject> orderList = (ArrayList<JSONObject>) orders.stream().collect(Collectors.toList());
////		Collections.sort(orderList, new Comparator<JSONObject>() {
////
////			@Override
////			public int compare(JSONObject order1, JSONObject order2) {
////				if (orderBy.equalsIgnoreCase("desc")) {
////					return (int) ((long) order2.get(RobotFactoryConstants.JSON_KEY_ORDER_ID)
////							- (long) order1.get(RobotFactoryConstants.JSON_KEY_ORDER_ID));
////				} else {
////					return (int) ((long) order1.get(RobotFactoryConstants.JSON_KEY_ORDER_ID)
////							- (long) order2.get(RobotFactoryConstants.JSON_KEY_ORDER_ID));
////				}
////			}
////
////		});
////		orderList.stream().map((item)=> )
////		return (JSONArray) orderList;
//		
//		orders.stream().sorted( new Comparator<JSONObject>() {
//
//			@Override
//			public int compare(JSONObject order1, JSONObject order2) {
//				if (orderBy.equalsIgnoreCase("desc")) {
//					return (int) ((long) order2.get(RobotFactoryConstants.JSON_KEY_ORDER_ID)
//							- (long) order1.get(RobotFactoryConstants.JSON_KEY_ORDER_ID));
//				} else {
//					return (int) ((long) order1.get(RobotFactoryConstants.JSON_KEY_ORDER_ID)
//							- (long) order2.get(RobotFactoryConstants.JSON_KEY_ORDER_ID));
//				}
//			}
//		});
//		
////		orders.stream().sorted(Comparator.comparing((item)-> {
////			 ((long)(JSONObject)(item)).get(RobotFactoryConstants.JSON_KEY_ORDER_ID), Comparator.re
////		}))
//		return orders;
//	}
	
	public long getMaxOrderId(JSONArray orders) {
		long maxValue = 0;
		for (Object object : orders) {
			JSONObject item = (JSONObject)object;
			long orderId = (long) item.get(RobotFactoryConstants.JSON_KEY_ORDER_ID);
			if(orderId > maxValue) {
				maxValue = orderId;
			}
		}
		return maxValue;
		
	}

}
