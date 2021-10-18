package com.tech26.robotfactory.services;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import com.tech26.robotfactory.Exceptions.FileOperationsException;

/**
 * @author Remya
 * HAndles the Stock Service
 *
 */
@Service
public interface RobotFactoryStockService {
	/**
	 * updates the stock after a successful purchase is placed
	 * @param userOrderArray
	 * @return
	 * @throws FileOperationsException
	 */
	public boolean updateStock(JSONArray userOrderArray) throws FileOperationsException;
	
	/** gets the current stock list
	 * @return
	 * @throws FileOperationsException
	 */
	public JSONObject getStock() throws FileOperationsException;
	
}
