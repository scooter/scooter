package twitterdemo.controllers;

import static org.junit.Assert.*;

import org.junit.Test;

import com.scooterframework.common.http.HTTPResponse;
import com.scooterframework.test.FunctionalTestHelper;

/**
 * TweetsControllerTest class contains tests for tweets.
 */
public class TweetsControllerTest extends FunctionalTestHelper {

	/**
	 * Test followings_tweets() method
	 */
	@Test 
	public void test_followings_tweets() {
		String uri = "/tweets/followings_tweets";
		HTTPResponse response = fireHttpGetRequest(uri);
		assertSuccess(response);
	}

}
