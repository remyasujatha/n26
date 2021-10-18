package com.tech26.robotfactory.acceptance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.notNull;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.util.ResourceUtils;

import com.tech26.robotfactory.utils.RobotFactoryConstants;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class RobotFactoryServicesApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private MockMvc mockMvc;


	
	@Test
	void contextLoads() {

	}
	
	// TODO Call common method, emptyOrderRequest in every test
	//	
	/**
	 * Payload :  {"components": ["A","I","D","F"]}
	 */
	@Test
	public void shouldOrderARobot() {
				try {
//					 String fileName = "stock.json";
//					    MockMultipartFile sampleFile = new MockMultipartFile(
//					      "uploaded-file",
//					      fileName, 
//					      "application/json",
//					      "This is the file content".getBytes()
//					    );
//					    File initialStockFile = ResourceUtils.getFile(RobotFactoryConstants.INITIAL_STOCK_FILE_LOCATION);
//						try (OutputStream os = Files.newOutputStream(sampleFile.toPath())) {
//							Files.copy(initialStockFile.toPath(), os);
//						}
//					    MockMultipartHttpServletRequestBuilder multipartRequest =
//					      MockMvcRequestBuilders.multipart("/api/files/upload");

					
			mockMvc.perform(post("/orders").content("{\"components\": [\"A\",\"I\",\"D\",\"F\"] }")
					.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isCreated()).andExpect(jsonPath("$.total").value(160.11))
					.andExpect(jsonPath("$.order_id").isNumber());
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getCause());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Payload :  {}
	 */
	@Test
	public void failToMissingComponentsPayload() {
		try {
			mockMvc.perform(post("/orders").content("{}")).andExpect(status().isBadRequest());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Payload :  ["components": ["A","I","D","G"]]
	 */
	@Test
	public void failToInvalidJSONStructureInOrderPayload() {
		try {
			mockMvc.perform(post("/orders").content("[\"components\" : [\"A\",\"I\",\"D\",\"G\"]]"))
					.andExpect(status().isBadRequest());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Payload :  {"components": "A"}
	 */
	@Test
	public void failToInvalidComponentsInPayload() {
		try {
			mockMvc.perform(post("/orders").content("{\"components\":\"A\"}")).andExpect(status().isBadRequest());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Payload :  {"components": ["A", "A","D","G"]}
	 */
	@Test
	public void failToOrderDuplicateMandatoryItems() {
		try {
			mockMvc.perform(post("/orders").content("{\"components\": [\"A\",\"A\",\"D\",\"G\"]}"))
					.andExpect(status().isUnprocessableEntity());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Payload :  {"components": ["C","I","D","G"]}
	 * with C out of stock
	 */
	@Test
	public void failToOrderOutOfStockItem() {
		try {
			mockMvc.perform(post("/orders").content("{\"components\": [\"C\",\"I\",\"D\",\"G\"]}"))
					.andExpect(status().isForbidden());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Empty Payload :  
	 */
	@Test
	public void failToEmptyOrderRequest() throws Exception {
		ResultActions resultActions = performTest("/orders", "", "POST");
		resultActions.andExpect(status().isBadRequest());
	}

	/**
	 * Special characters in Payload :  @#$@#%
	 */
	@Test
	public void failToOrderWithSpecialCharactersRequest() throws Exception {
		ResultActions resultActions = performTest("/orders", "@#$@#%", "POST");
		resultActions.andExpect(status().isBadRequest());
	}

	/**
	 * null Payload :  null
	 */
	@Test
	public void failNullOrder() throws Exception {
		ResultActions actions = performTest("/orders", "null", "POST");
		actions.andExpect(status().isBadRequest());
	}

	/**
	 * Payload :  {"components": ["C","C","I","D","G"]}
	 * with C out of stock
	 * @throws Exception 
	 */
	@Test
	public void failToOrderMultipleOutOfStockItem() throws Exception {
			ResultActions actions = performTest("/orders","{\"components\": [\"C\",\"C\",\"D\",\"G\"]}","POST");
			actions.andExpect(status().isUnprocessableEntity());
			}
	
	/**
	 * Payload :  {"components": ["a","I","D","G"]}
	 * with C out of stock
	 * @throws Exception 
	 */
	@Test
	public void shouldOrderWithLowerCaseForCode() throws Exception {
			ResultActions actions = performTest("/orders","{\"components\": [\"a\",\"I\",\"D\",\"G\"]}","POST");
			actions.andExpect(status().isCreated());
			}
	
	public ResultActions performTest(String url, String content, String requestMethod) {

		ResultActions actions = null;
		try {

			if (requestMethod.toUpperCase().equals("POST")) {
				actions = mockMvc.perform(post(url).content(content));
			} else if (requestMethod.equals("GET")) {
				actions = mockMvc.perform(get(url));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return actions;
	}
}
