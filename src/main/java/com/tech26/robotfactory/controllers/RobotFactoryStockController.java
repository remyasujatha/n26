package com.tech26.robotfactory.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.tech26.robotfactory.RobotFatoryServicesApplication;
import com.tech26.robotfactory.Exceptions.FileOperationsException;
import com.tech26.robotfactory.Exceptions.InvalidOrderExcception;
import com.tech26.robotfactory.Exceptions.PurchaseOrderException;
import com.tech26.robotfactory.services.RobotFactoryOrderImpl;
import com.tech26.robotfactory.services.RobotFactoryStockImpl;

/**
 * @author Remya
 * 
 * Controller for handling Orders
 *
 */
@RestController
public class RobotFactoryStockController {
	
	public static RobotFactoryStockImpl robotFactoryStock;

	
	public RobotFactoryStockController() {
		robotFactoryStock = RobotFactoryStockImpl.getInstance();
	}

	@GetMapping("/stocks")
	public JSONObject getStockList() throws FileOperationsException {
		return robotFactoryStock.getStock();
	}
}
