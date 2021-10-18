package com.tech26.robotfactory.services;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.tech26.robotfactory.Exceptions.FileOperationsException;

public interface RobotFactoryStockService {
	public boolean updateStock(JSONArray userOrderArray) throws FileOperationsException;
	public JSONObject getStock() throws FileOperationsException;
	
}
