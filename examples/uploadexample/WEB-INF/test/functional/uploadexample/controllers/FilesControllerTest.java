package uploadexample.controllers;

import static org.junit.Assert.*;

import org.junit.Test;

import com.scooterframework.common.http.HTTPResponse;
import com.scooterframework.test.FunctionalTestHelper;

/**
 * FilesControllerTest class contains tests for files.
 */
public class FilesControllerTest extends FunctionalTestHelper {

	/**
	 * Test index() method
	 */
	@Test 
	public void test_index() {
		String uri = "/files/index";
		HTTPResponse response = fireHttpGetRequest(uri);
		assertSuccess(response);
	}

	/**
	 * Test upload() method
	 */
	@Test 
	public void test_upload() {
		String uri = "/files/upload";
		HTTPResponse response = fireHttpGetRequest(uri);
		assertSuccess(response);
	}

}
