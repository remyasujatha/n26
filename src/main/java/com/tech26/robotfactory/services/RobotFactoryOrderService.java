package com.tech26.robotfactory.services;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tech26.robotfactory.Exceptions.PurchaseOrderException;
import com.tech26.robotfactory.Exceptions.FileOperationsException;
import com.tech26.robotfactory.Exceptions.InvalidOrderExcception;
import com.tech26.robotfactory.beans.Order;
import com.tech26.robotfactory.beans.Stock;

/**
 * @author Remya
 * 
 * Order services provided by application
 *
 */
@Service
public interface RobotFactoryOrderService {
	
	
	/**
	 * To validate if the placed order is valid
	 * @param order placed by user
	 * @return
	 * @throws PurchaseOrderException
	 * @throws FileOperationsException
	 * @throws InvalidOrderExcception
	 */
	public boolean isValidOrder(String order) throws PurchaseOrderException, FileOperationsException, InvalidOrderExcception;
	
	/**
	 *  initiates the purchase request once order is placed
	 * @param order placed by user
	 * @return
	 * @throws PurchaseOrderException
	 * @throws FileOperationsException
	 * @throws InvalidOrderExcception
	 */
	public String purchase(String order) throws PurchaseOrderException, FileOperationsException, InvalidOrderExcception;
	
	/** If purchase is placed successfully, the order is updated to repo
	 * @param order placed by user
	 * @return
	 * @throws FileOperationsException
	 */
	public JSONObject updateOrderInRepository() throws FileOperationsException;
	

}
