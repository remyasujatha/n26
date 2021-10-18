package com.tech26.robotfactory.services;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.tech26.robotfactory.Exceptions.PurchaseOrderException;
import com.tech26.robotfactory.Exceptions.FileOperationsException;
import com.tech26.robotfactory.Exceptions.InvalidOrderExcception;
import com.tech26.robotfactory.beans.Order;
import com.tech26.robotfactory.beans.Stock;

public interface RobotFactoryOrderService {
	
	public boolean isValidOrder(Order order) throws PurchaseOrderException, FileOperationsException;
	public String purchase(String order) throws PurchaseOrderException, FileOperationsException, InvalidOrderExcception;
	

}
