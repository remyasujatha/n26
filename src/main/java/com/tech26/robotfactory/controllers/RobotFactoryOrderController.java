package com.tech26.robotfactory.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.tech26.robotfactory.RobotFatoryServicesApplication;
import com.tech26.robotfactory.Exceptions.InvalidOrderExcception;
import com.tech26.robotfactory.Exceptions.PurchaseOrderException;
import com.tech26.robotfactory.Exceptions.FileOperationsException;
import com.tech26.robotfactory.beans.Order;
import com.tech26.robotfactory.beans.Stock;
import com.tech26.robotfactory.services.RobotFactoryOrderService;
import com.tech26.robotfactory.services.RobotFactoryOrderImpl;

/**
 * @author Remya
 * 
 *         Controller for handling stocks
 *
 */
@RestController
public class RobotFactoryOrderController {
	@Autowired
	public RobotFactoryOrderImpl robotFactoryOrder;

	@GetMapping("/orders/{orderId}")
	public JSONObject getOrderDetails(@PathVariable(value = "orderId") String orderId,
			final HttpServletResponse response) throws IOException {
		try {
			return robotFactoryOrder.getOrderDetails(orderId);
		} catch (FileOperationsException e) {
			response.sendError(e.getCode(), e.getMessage());
		} catch (InvalidOrderExcception e) {
			response.sendError(e.getCode(), e.getMessage());
		}
		return null;
	}

	@PostMapping("/orders")
	@ResponseStatus(HttpStatus.CREATED)
	public JSONObject purchaseRobot(final HttpServletResponse response, @RequestBody String order) throws IOException {
		JSONObject orderMessage = null;
		try {
			orderMessage = robotFactoryOrder.purchase(order);
		} catch (PurchaseOrderException e) {
			response.sendError(e.getCode(), e.getMessage());
		} catch (InvalidOrderExcception e) {
			response.sendError(e.getCode(), e.getMessage());
		}
		return orderMessage;

	}

}
