package com.tech26.robotfactory.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.catalina.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;

import com.tech26.robotfactory.Exceptions.FileOperationsException;
import com.tech26.robotfactory.Exceptions.InvalidOrderExcception;
import com.tech26.robotfactory.Exceptions.PurchaseOrderException;
import com.tech26.robotfactory.beans.Order;
import com.tech26.robotfactory.beans.Stock;
import com.tech26.robotfactory.utils.RobotFactoryConstants;
import com.tech26.robotfactory.utils.RobotFactoryErrorCodes;
import com.tech26.robotfactory.utils.RobotFactoryExceptionHandler;
import com.tech26.robotfactory.utils.RobotFactoryUtils;

public class RobotFactoryOrderImpl implements RobotFactoryOrderService {
	public static RobotFactoryOrderImpl robotFactoryOrder;
	public static RobotFactoryStockImpl robotFactoryStock;
	public static Stock currentStock;

	private RobotFactoryOrderImpl() {
		currentStock = Stock.getInstance();
		robotFactoryStock = RobotFactoryStockImpl.getInstance();
	}

	public static RobotFactoryOrderImpl getInstance() {
		if (robotFactoryOrder == null) {
			synchronized (RobotFactoryOrderImpl.class) {
				robotFactoryOrder = new RobotFactoryOrderImpl();
			}
		}
		return robotFactoryOrder;
	}

	

	@Override
	public boolean isValidOrder(Order order) throws PurchaseOrderException, FileOperationsException {
		return validateOrder(order);
	}

	@Override
	public String purchase(String order)
			throws PurchaseOrderException, FileOperationsException, InvalidOrderExcception {

		JSONParser parser = new JSONParser();
		Order userOrder;
		try {
			Object orderObj = parser.parse(order);
			if (orderObj == null) {
				String errorMessage = RobotFactoryExceptionHandler.getMessage(RobotFactoryErrorCodes.NULL_ORDER_PAYLOD,
						"Invalid payload!. Sample payload is"
								+ RobotFactoryConstants.getSampleOrderPayload().toString());
				throw new InvalidOrderExcception(HttpStatus.BAD_REQUEST.value(), errorMessage);

			}
			JSONObject jsonObject = (JSONObject) orderObj;
			if (!jsonObject.containsKey(RobotFactoryConstants.JSON_KEY_COMPONENTS)) {
				String errorMessage = RobotFactoryExceptionHandler
						.getMessage(RobotFactoryErrorCodes.INVALID_ORDER_PAYLOAD, "Invalid payload!. Sample payload is "
								+ RobotFactoryConstants.getSampleOrderPayload().toString());
				throw new InvalidOrderExcception(HttpStatus.BAD_REQUEST.value(), errorMessage);

			}
			Object componentsObject = jsonObject.get(RobotFactoryConstants.JSON_KEY_COMPONENTS);
			if (!(componentsObject instanceof JSONArray)) {
				String errorMessage = RobotFactoryExceptionHandler.getMessage(
						RobotFactoryErrorCodes.INVALID_ORDER_COMPONENTS_ARRAY,
						"Invalid payload!. \"components\" should be array. Sample payload is "
								+ RobotFactoryConstants.getSampleOrderPayload().toString());
				throw new InvalidOrderExcception(HttpStatus.BAD_REQUEST.value(), errorMessage);
			}

			JSONArray orderArray = (JSONArray) jsonObject.get(RobotFactoryConstants.JSON_KEY_COMPONENTS);
			userOrder = new Order();
			userOrder.setComponents(orderArray);
		} catch (ParseException e) {
			String errorMessage = RobotFactoryExceptionHandler.getMessage(RobotFactoryErrorCodes.INVALID_ORDER_PAYLOAD,
					e + "");
			throw new InvalidOrderExcception(HttpStatus.BAD_REQUEST.value(), errorMessage);
		}
		boolean isOrderValid = isValidOrder(userOrder);
		if (isOrderValid) {
			double totalAmount = caculateOrderAmount(userOrder);
			JSONObject responseObject = new JSONObject();
			long orderID;
			JSONObject successfullOrders = userOrder.getSuccessOrderList();
			JSONArray savedOrders = new JSONArray();
			long lastOrderId = RobotFactoryConstants.FIRST_ORDER_ID;
			if (successfullOrders.containsKey(RobotFactoryConstants.JSON_KEY_ITEMS)) {
				savedOrders = (JSONArray) successfullOrders.get(RobotFactoryConstants.JSON_KEY_ITEMS);
				lastOrderId = userOrder.getMaxOrderId(savedOrders);
			}
			responseObject.put(RobotFactoryConstants.JSON_KEY_ORDER_ID, ++lastOrderId);
			responseObject.put(RobotFactoryConstants.JSON_KEY_TOTAL, Math.round(totalAmount * 100) / 100.0d);
			savedOrders.add(responseObject);
			successfullOrders.put(RobotFactoryConstants.JSON_KEY_ITEMS, savedOrders);
			boolean isStockUpdated = robotFactoryStock.updateStock(userOrder.getComponents());
			if (isStockUpdated) {
				boolean isOrderUpdated = RobotFactoryUtils.writeToFile(RobotFactoryConstants.ORDER_FILE_LOCATION,
						successfullOrders.toJSONString());

				if (isOrderUpdated) {
					return responseObject.toString();
				} else {
					String errorMessage = RobotFactoryExceptionHandler.getMessage(
							RobotFactoryErrorCodes.ORDER_FILEUPDATE_EXCEPTION,
							"oops! Some error in saving the order. Please check with administrator");
					throw new FileOperationsException(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorMessage);
				}
			} else {
				String erroMessage = RobotFactoryExceptionHandler.getMessage(
						RobotFactoryErrorCodes.STOCK_FILE_UPDATE_EXCEPTION,
						"Failed to update Stock. Please try again later.");
				throw new PurchaseOrderException(HttpStatus.INTERNAL_SERVER_ERROR.value(), erroMessage);
			}
		} else {
			String erroMessage = RobotFactoryExceptionHandler.getMessage(RobotFactoryErrorCodes.INVALID_ORDER_CODE,
					"Invald Order. Please make sure only one of every mandatory items are added in your order");
			throw new PurchaseOrderException(HttpStatus.UNPROCESSABLE_ENTITY.value(), erroMessage);
		}
	}

	public static boolean validateOrder(Order order) throws PurchaseOrderException, FileOperationsException {

		JSONArray orderArray = order.getComponents();
		// To remove duplicate orders
		Set<String> orderSet = (Set<String>) orderArray.stream().map(s -> s.toString().toUpperCase())
				.collect(Collectors.toSet());

		JSONObject currentStockItems = currentStock.getStockList();
		Map<String, Map<String, JSONObject>> mandatoryItemsInStock = new HashMap();
		mandatoryItemsInStock = currentStock.getItemsInStock(currentStockItems, true);
		int mandatoryItemsCount = mandatoryItemsInStock.size();
		if ((orderSet.size() != mandatoryItemsCount) || (orderSet.size() != orderArray.size())) {
			return false;
		}
		int orderItemsCount = orderArray.size();
		for (String itemKey : mandatoryItemsInStock.keySet()) {
			Map<String, JSONObject> itemsMap = mandatoryItemsInStock.get(itemKey);
			for (Object object : orderArray) {
				String code = object.toString();

				if (itemsMap.containsKey(code.toUpperCase())) {
					JSONObject item = (JSONObject) itemsMap.get(code.toUpperCase());
					long quantity = (long) item.get(RobotFactoryConstants.JSON_KEY_QUANTITY);
					if (quantity > 0) {
						orderItemsCount--;
						break;
					} else {
						String errorMessage = RobotFactoryExceptionHandler
								.getMessage(RobotFactoryErrorCodes.OUT_OF_STOCK_ERROR, code + " is out of stock");
						throw new PurchaseOrderException(HttpStatus.FORBIDDEN.value(), errorMessage);
					}

				}
			}

		}
		if (orderItemsCount == 0) {
			return true;
		}
		return false;
	}

	private double caculateOrderAmount(Order order) throws FileOperationsException {
		double orderAmount = 0;
		JSONObject currentStockItems = currentStock.getStockList();
		Map<String, Map<String, JSONObject>> itemsInStockMap = new HashMap();
		itemsInStockMap = currentStock.getItemsInStock(currentStockItems, false);
		JSONArray orderArray = order.getComponents();
		for (Object item : orderArray) {
			String orderItemCode = item.toString();
			for (String itemKey : itemsInStockMap.keySet()) {
				Map<String, JSONObject> itemsMap = itemsInStockMap.get(itemKey);
				if (itemsMap.containsKey(orderItemCode)) {
					JSONObject itemObject = (JSONObject) itemsMap.get(orderItemCode);
					double price = (double) itemObject.get(RobotFactoryConstants.JSON_KEY_PRICE);
					orderAmount += price;
					break;
				}
			}
		}
		return orderAmount;
	}

	public JSONObject getOrderDetails(String orderId) throws FileOperationsException, InvalidOrderExcception {
		if(orderId.matches("[0-9]")) {
			
		Order userOrder = new Order();
		JSONObject orderObject = userOrder.getSuccessOrderList();
		if (!orderObject.isEmpty()) {
		
		JSONArray orderArray = (JSONArray)orderObject.get(RobotFactoryConstants.JSON_KEY_ITEMS); 
		for (Object object : orderArray) {
			JSONObject item = (JSONObject) object;
			if((long)item.get(RobotFactoryConstants.JSON_KEY_ORDER_ID) == Long.parseLong(orderId)) {
			 return item;	
			}
		}
		}
		
		}
		String errorMessage = RobotFactoryExceptionHandler.getMessage(
				RobotFactoryErrorCodes.NO_ORDER_MATCHING_ORDER_ID, "No orders matching " + orderId + " is found");
		throw new InvalidOrderExcception(HttpStatus.BAD_REQUEST.value(),errorMessage);
	
	}
}
