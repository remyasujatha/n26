package com.tech26.robotfactory.beans;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.json.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import com.tech26.robotfactory.Exceptions.FileOperationsException;
import com.tech26.robotfactory.utils.RobotFactoryConstants;
import com.tech26.robotfactory.utils.RobotFactoryErrorCodes;
import com.tech26.robotfactory.utils.RobotFactoryExceptionHandler;

/**
 * @author Remya
 *
 *         Stock entity to store Stock details
 */
@Component
public class Stock {

	private String stockFileLocation;

	@Autowired
	private Stock(@Value("${path.stock.fileName}") String stockFileLocation) {
		this.stockFileLocation = stockFileLocation;
	}

	/**
	 * Returns current stock list
	 */
	public synchronized JSONObject getStockListInRepo() throws FileOperationsException {
		File stockFile = null;
		try {
			stockFile = new File(stockFileLocation);
			if (!stockFile.exists()) {
				File initialStockFile = ResourceUtils.getFile(RobotFactoryConstants.INITIAL_STOCK_FILE_LOCATION);
				try (OutputStream os = Files.newOutputStream(stockFile.toPath())) {
					Files.copy(initialStockFile.toPath(), os);
				}
			}
			JSONParser parser = new JSONParser();

			Object obj = parser.parse(new FileReader(stockFile));
			JSONObject jsonObject = (JSONObject) obj;
			return jsonObject;
		} catch (IOException | ParseException e) {
			RobotFactoryErrorCodes errorCode;
			if (e instanceof ParseException) {
				errorCode = RobotFactoryErrorCodes.STOCK_FILE_PARSE_EXCEPTION;
			} else {
				errorCode = RobotFactoryErrorCodes.STOCK_FILE_READ_EXCEPTION;
			}
			String errorMessage = RobotFactoryExceptionHandler.getMessage(errorCode,
					"Failed to get current Stocks. Please try later.");
			throw new FileOperationsException(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorMessage);
		}
	}

	public Map<String, Map<String, JSONObject>> getItemsInStock(JSONObject currentStockItems,
			boolean isMandatoryAlone) {
		Map<String, Map<String, JSONObject>> stockItemsMap = new HashMap();
		JSONArray itemsArray = (JSONArray) currentStockItems.get(RobotFactoryConstants.JSON_KEY_ITEMS);
		for (Object item : itemsArray) {
			JSONObject itemObject = (JSONObject) item;
			String itemName = itemObject.get(RobotFactoryConstants.JSON_KEY_ID).toString();
			boolean isItemMandatory = (boolean) itemObject.get(RobotFactoryConstants.JSON_KEY_MANDATORY);
			JSONArray listItems = (JSONArray) itemObject.get(RobotFactoryConstants.JSON_KEY_LIST);
			Map<String, JSONObject> listItemsMap;

			if (stockItemsMap.get(itemName) != null) {
				listItemsMap = stockItemsMap.get(itemName);
			} else {
				listItemsMap = new HashMap();
			}
			for (Object listItem : listItems) {
				JSONObject listItemObject = (JSONObject) listItem;
				JSONObject productObject = new JSONObject();
				String itemCode = listItemObject.get(RobotFactoryConstants.JSON_KEY_CODE).toString();
				productObject.put(RobotFactoryConstants.JSON_KEY_QUANTITY,
						(long) listItemObject.get(RobotFactoryConstants.JSON_KEY_QUANTITY));
				productObject.put(RobotFactoryConstants.JSON_KEY_PRICE,
						Double.parseDouble(listItemObject.get(RobotFactoryConstants.JSON_KEY_PRICE).toString()));
				productObject.put(RobotFactoryConstants.JSON_KEY_NAME,
						listItemObject.get(RobotFactoryConstants.JSON_KEY_NAME).toString());
				listItemsMap.put(itemCode, productObject);
			}
			if (isMandatoryAlone) {
				if (isItemMandatory) {
					stockItemsMap.put(itemName, listItemsMap);
				}
			} else {
				stockItemsMap.put(itemName, listItemsMap);
			}
		}

		return stockItemsMap;
	}

}
