package com.tech26.robotfactory.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.tech26.robotfactory.Exceptions.FileOperationsException;
import com.tech26.robotfactory.Exceptions.InvalidOrderExcception;
import com.tech26.robotfactory.Exceptions.PurchaseOrderException;
import com.tech26.robotfactory.beans.Order;
import com.tech26.robotfactory.beans.Stock;
import com.tech26.robotfactory.utils.RobotFactoryConstants;
import com.tech26.robotfactory.utils.RobotFactoryErrorCodes;
import com.tech26.robotfactory.utils.RobotFactoryExceptionHandler;
import com.tech26.robotfactory.utils.RobotFactoryUtils;

@Component
public class RobotFactoryOrderImpl implements RobotFactoryOrderService {
	
	@Autowired
	public  RobotFactoryStockImpl robotFactoryStock;
	@Autowired
	public Stock currentStock;
	
	@Autowired
	public Order userOrder;

	@Override
	public boolean isValidOrder(String order)
			throws PurchaseOrderException, FileOperationsException, InvalidOrderExcception {
		userOrder.setComponents(getOrderItems(order));
		return isOrderPlacable();
	}

	private JSONArray getOrderItems(String order) throws InvalidOrderExcception {
		JSONParser parser = new JSONParser();
		Object orderObj;
		try {
			orderObj = parser.parse(order);
			if (orderObj == null) {
				String errorMessage = RobotFactoryExceptionHandler.getMessage(RobotFactoryErrorCodes.NULL_ORDER_PAYLOD,
						"Invalid payload!. Sample payload is"
								+ RobotFactoryConstants.getSampleOrderPayload().toString());
				throw new InvalidOrderExcception(HttpStatus.BAD_REQUEST.value(), errorMessage);

			}
		} catch (ParseException e) {
			String errorMessage = RobotFactoryExceptionHandler.getMessage(RobotFactoryErrorCodes.INVALID_ORDER_PAYLOAD,
					e + "");
			throw new InvalidOrderExcception(HttpStatus.BAD_REQUEST.value(), errorMessage);

		}

		JSONObject jsonObject = (JSONObject) orderObj;
		if (!jsonObject.containsKey(RobotFactoryConstants.JSON_KEY_COMPONENTS)) {
			String errorMessage = RobotFactoryExceptionHandler.getMessage(RobotFactoryErrorCodes.INVALID_ORDER_PAYLOAD,
					"Invalid payload!. Sample payload is " + RobotFactoryConstants.getSampleOrderPayload().toString());
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

		return orderArray;
	}

	@Override
	public JSONObject purchase(String order)
			throws PurchaseOrderException, FileOperationsException, InvalidOrderExcception {
		if (isValidOrder(order)) {
			if (robotFactoryStock.updateStock(userOrder.getComponents())) {
				JSONObject isOrderUpdated = updateOrderInRepository();
				if (isOrderUpdated != null) {
					return isOrderUpdated;
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

	private boolean isOrderPlacable() throws PurchaseOrderException, FileOperationsException {

		List<String> orderArray = userOrder.getComponents();
		// To remove duplicate orders
		orderArray = orderArray.stream().map(a->a.toLowerCase()).distinct().collect(Collectors.toList());
		Map<String, Map<String, JSONObject>> mandatoryItemsInStock = getCurrentStockList(true);
		int mandatoryItemsCount = mandatoryItemsInStock.size();
		if ((orderArray.size() != mandatoryItemsCount) || (orderArray.size() != orderArray.size())) {
			return false;
		}
		int orderItemsCount = orderArray.size();
		for (String itemKey : mandatoryItemsInStock.keySet()) {
			Map<String, JSONObject> itemsMap = mandatoryItemsInStock.get(itemKey);
			for (Object object : orderArray) {
				String code = object.toString();

				if (itemsMap.containsKey(code.toUpperCase())) {
					JSONObject item = itemsMap.get(code.toUpperCase());
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

	private Map<String, Map<String, JSONObject>> getCurrentStockList(boolean isMandatory)
			throws FileOperationsException {
		JSONObject currentStockItems = currentStock.getStockListInRepo();
		Map<String, Map<String, JSONObject>> itemsInStockMap = new HashMap<>();
		itemsInStockMap = currentStock.getItemsInStock(currentStockItems, isMandatory);
		return itemsInStockMap;

	}

	private double caculateOrderAmount() throws FileOperationsException {
		double orderAmount = 0;
		Map<String, Map<String, JSONObject>> itemsInStockMap = getCurrentStockList(false);
		for (Object item : userOrder.getComponents()) {
			String orderItemCode = item.toString();
			for (String itemKey : itemsInStockMap.keySet()) {
				Map<String, JSONObject> itemsMap = itemsInStockMap.get(itemKey);
				if (itemsMap.containsKey(orderItemCode)) {
					JSONObject itemObject = itemsMap.get(orderItemCode);
					double price = (double) itemObject.get(RobotFactoryConstants.JSON_KEY_PRICE);
					orderAmount += price;
					break;
				}
			}
		}
		return orderAmount;
	}

	public JSONObject getOrderDetails(String orderId) throws FileOperationsException, InvalidOrderExcception {
		if (orderId.matches("[0-9]")) {
			JSONObject orderObject = userOrder.getSuccessOrderList();
			if (!orderObject.isEmpty()) {

				JSONArray orderArray = (JSONArray) orderObject.get(RobotFactoryConstants.JSON_KEY_ITEMS);
				for (Object object : orderArray) {
					JSONObject item = (JSONObject) object;
					if ((long) item.get(RobotFactoryConstants.JSON_KEY_ORDER_ID) == Long.parseLong(orderId)) {
						return item;
					}
				}
			}

		}
		String errorMessage = RobotFactoryExceptionHandler.getMessage(RobotFactoryErrorCodes.NO_ORDER_MATCHING_ORDER_ID,
				"No orders matching " + orderId + " is found");
		throw new InvalidOrderExcception(HttpStatus.BAD_REQUEST.value(), errorMessage);

	}

	public JSONObject updateOrderInRepository() throws FileOperationsException {
		double totalAmount = caculateOrderAmount();
		JSONObject responseObject = new JSONObject();
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

		if (!RobotFactoryUtils.writeToFile(userOrder.getOrderFileLocation(), successfullOrders.toJSONString())) {
			return null;
		}

		return responseObject;
	}

	public long getNewOrderId() throws FileOperationsException {
		JSONObject successfullOrders = userOrder.getSuccessOrderList();
		JSONArray savedOrders = new JSONArray();
		long lastOrderId = RobotFactoryConstants.FIRST_ORDER_ID;
		if (successfullOrders.containsKey(RobotFactoryConstants.JSON_KEY_ITEMS)) {
			savedOrders = (JSONArray) successfullOrders.get(RobotFactoryConstants.JSON_KEY_ITEMS);
			lastOrderId = userOrder.getMaxOrderId(savedOrders);
		}
		return ++lastOrderId;
	}
}