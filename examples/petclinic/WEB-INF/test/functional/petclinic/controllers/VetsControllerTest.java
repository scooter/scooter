package petclinic.controllers;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.HashMap;

import org.junit.Test;

import com.scooterframework.common.http.HTTPResponse;
import com.scooterframework.common.logging.LogUtil;
import com.scooterframework.orm.activerecord.ActiveRecord;
import com.scooterframework.test.FunctionalTestHelper;

import petclinic.models.Vet;

/**
 * VetsControllerTest class contains tests for vets.
 */
public class VetsControllerTest extends FunctionalTestHelper {
	private LogUtil log = LogUtil.getLogger(this.getClass().getName());

	/**
	 * Test <tt>index()</tt> method.
	 */
	@Test
	public void test_index() {
		String uri = "/vets";
		HTTPResponse response = fireHttpGetRequest(uri);
		assertSuccess(response);
	}

	/**
	 * Test <tt>crud()</tt> method.
	 */
	@Test
	public void test_crud() {
		try {
			HTTPResponse response = null;

			String id = "100";
			Map data = new HashMap();
			data.put("id", id);

			String addURI = "/vets/add";
			response = fireHttpGetRequest(addURI);
			assertSuccess(response);
			log.debug("add response: " + response.getContentAsString());

			String createURI = "/vets";
			response = fireHttpPostRequest(createURI, data);
			assertRedirectSuccess(response);
			log.debug("create response: " + response.getContentAsString());
		
			String showURI = "/vets/" +id;
			response = fireHttpGetRequest(showURI);
			assertSuccess(response);
			log.debug("show response: " + response.getContentAsString());

			String editURI = "/vets/" +id + "/edit";
			response = fireHttpGetRequest(editURI);
			assertSuccess(response);
			log.debug("edit response: " + response.getContentAsString());

			String updateURI = "/vets/" +id;
			response = fireHttpPutRequest(updateURI, data);
			assertRedirectSuccess(response);
			log.debug("update response: " + response.getContentAsString());

			String deleteURI = "/vets/" +id;
			response = fireHttpDeleteRequest(deleteURI);
			assertRedirectSuccess(response);
			log.debug("delete response: " + response.getContentAsString());

		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}
