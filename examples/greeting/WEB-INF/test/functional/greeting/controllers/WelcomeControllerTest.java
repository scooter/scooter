package greeting.controllers;

import static org.junit.Assert.*;

import org.junit.Test;

import com.scooterframework.common.http.HTTPResponse;
import com.scooterframework.test.FunctionalTestHelper;

/**
 * WelcomeControllerTest class contains tests for welcome.
 */
public class WelcomeControllerTest extends FunctionalTestHelper {

	/**
	 * Test sayit() method
	 */
	@Test 
	public void test_sayit() {
		String uri = "/welcome/sayit";
		HTTPResponse response = fireHttpGetRequest(uri);
		assertSuccess(response);
	}

}
