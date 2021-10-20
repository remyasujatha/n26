package com.tech26.robotfactory.services;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tech26.robotfactory.Exceptions.FileOperationsException;
import com.tech26.robotfactory.beans.Stock;
import com.tech26.robotfactory.utils.RobotFactoryConstants;
import com.tech26.robotfactory.utils.RobotFactoryUtils;

/**
 * @author Remya
 *
 */
@Component
public class RobotFactoryStockImpl implements RobotFactoryStockService {

//	public static RobotFactoryStockImpl robotFactoryStock;

	@Autowired
	public Stock currentStock;

	@Override
	public JSONObject getStock() throws FileOperationsException {
		return currentStock.getStockListInRepo();

	}

	@Override
	public boolean updateStock(JSONArray userOrderArray) throws FileOperationsException {
		JSONObject currentStockList = currentStock.getStockListInRepo();
		JSONArray currentStockItems = (JSONArray) currentStockList.get(RobotFactoryConstants.JSON_KEY_ITEMS);
		userOrderArray.stream().forEachOrdered((itemCode) -> {
			for (int index = 0; index < currentStockItems.size(); index++) {
				JSONObject category = (JSONObject) currentStockItems.get(index);
				JSONArray items = (JSONArray) category.get(RobotFactoryConstants.JSON_KEY_LIST);
				for (int i = 0; i < items.size(); i++) {
					JSONObject item = (JSONObject) items.get(i);
					String stockItemCode = item.get(RobotFactoryConstants.JSON_KEY_CODE).toString();
					if (stockItemCode.equals(itemCode)) {
						long quantity = (long) item.get(RobotFactoryConstants.JSON_KEY_QUANTITY);
						item.put(RobotFactoryConstants.JSON_KEY_QUANTITY, --quantity);
						break;
					}
				}
			}
		});

		return RobotFactoryUtils.writeToFile(RobotFactoryConstants.STOCK_FILE, currentStockList.toString());

	}

}
