package com.tech26.robotfactory.acceptance;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:test.properties")
class RobotFactoryServicesApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private MockMvc mockMvc;

	@Value("${path.stock.fileName}")
	private String stockFileLocation;

	@Value("${path.order.fileName}")
	private String orderFileLocation;

	@Test
	void contextLoads() {

	}

	@AfterEach
	@BeforeEach
	public void cleanUpFiles() {
		File targetFile = new File(stockFileLocation);
		targetFile.delete();
		targetFile = new File(orderFileLocation);
		targetFile.delete();
	}

	/**
	 * Payload : {"components": ["A","I","D","F"]}
	 *
	 * @throws Exception
	 */
	@Test
	public void shouldOrderARobot() throws Exception {

		ResultActions actions = performTest("/orders", "{\"components\": [\"A\",\"I\",\"D\",\"F\"] }", "POST");
		actions.andExpect(status().isCreated()).andExpect(jsonPath("$.total").value(160.11));
		actions.andExpect(jsonPath("$.order_id").isNumber());
	}

	/**
	 * Payload : {}
	 *
	 * @throws Exception
	 */
	@Test
	public void failToMissingComponentsPayload() throws Exception {
		ResultActions actions = performTest("/orders", "{}", "POST");
		actions.andExpect(status().isBadRequest());
	}

	/**
	 * Payload : ["components": ["A","I","D","G"]]
	 *
	 * @throws Exception
	 */
	@Test
	public void failToInvalidJSONStructureInOrderPayload() throws Exception {
		ResultActions actions = performTest("/orders", "[\"components\" : [\"A\",\"I\",\"D\",\"G\"]]", "POST");
		actions.andExpect(status().isBadRequest());
	}

	/**
	 * Payload : {"components": "A"}
	 *
	 * @throws Exception
	 */
	@Test
	public void failToInvalidComponentsInPayload() throws Exception {
		ResultActions actions = performTest("/orders", "{\"components\":\"A\"}", "POST");
		actions.andExpect(status().isBadRequest());
	}

	/**
	 * Payload : {"components": ["A", "A","D","G"]}
	 *
	 * @throws Exception
	 */
	@Test
	public void failToOrderDuplicateMandatoryItems() throws Exception {
		ResultActions actions = performTest("/orders", "{\"components\": [\"A\",\"A\",\"D\",\"G\"]}", "POST");
		actions.andExpect(status().isUnprocessableEntity());
	}

	/**
	 * Payload : {"components": ["C","I","D","G"]} with C out of stock
	 *
	 * @throws Exception
	 */
	@Test
	public void failToOrderOutOfStockItem() throws Exception {
		ResultActions actions = performTest("/orders", "{\"components\": [\"C\",\"I\",\"D\",\"G\"]}", "POST");
		actions.andExpect(status().isForbidden());
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
	 * Special characters in Payload : @#$@#%
	 */
	@Test
	public void failToOrderWithSpecialCharactersRequest() throws Exception {
		ResultActions resultActions = performTest("/orders", "@#$@#%", "POST");
		resultActions.andExpect(status().isBadRequest());
	}

	/**
	 * null Payload : null
	 */
	@Test
	public void failNullOrder() throws Exception {
		ResultActions actions = performTest("/orders", "null", "POST");
		actions.andExpect(status().isBadRequest());
	}

	/**
	 * Payload : {"components": ["C","C","I","D","G"]} with C out of stock
	 *
	 * @throws Exception
	 */
	@Test
	public void failToOrderMultipleOutOfStockItem() throws Exception {
		ResultActions actions = performTest("/orders", "{\"components\": [\"C\",\"C\",\"D\",\"G\"]}", "POST");
		actions.andExpect(status().isUnprocessableEntity());
	}

	/**
	 * Payload : {"components": ["a","I","D","G"]} with C out of stock
	 *
	 * @throws Exception
	 */
	@Test
	public void shouldOrderWithLowerCaseForCode() throws Exception {
		ResultActions actions = performTest("/orders", "{\"components\": [\"a\",\"I\",\"D\",\"G\"]}", "POST");
		actions.andExpect(status().isCreated());
		actions.andExpect(status().isCreated()).andExpect(jsonPath("$.total").value(174.19));
		actions.andExpect(jsonPath("$.order_id").isNumber());
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
