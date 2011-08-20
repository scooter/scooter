package blog.controllers;

import static org.junit.Assert.*;

import org.junit.Test;

import com.scooterframework.common.http.HTTPResponse;
import com.scooterframework.test.FunctionalTestHelper;

/**
 * CommentsControllerTest class contains tests for comments.
 */
public class CommentsControllerTest extends FunctionalTestHelper {

	/**
	 * Test create() method
	 */
	@Test 
	public void test_create() {
		String uri = "/comments/create";
		HTTPResponse response = fireHttpGetRequest(uri);
		assertSuccess(response);
	}

}
