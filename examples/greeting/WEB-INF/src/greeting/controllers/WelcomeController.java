package greeting.controllers;

import static com.scooterframework.web.controller.ActionControl.*;

import com.scooterframework.admin.FilterManagerFactory;


/**
 * WelcomeController class handles welcome related access.
 */
public class WelcomeController extends ApplicationController {

	/**
	 * Constructor
	 */
	public WelcomeController() {
	}


	/**
	 * sayit() method
	 */
	public String sayit() {
		flash("notice", "Successfully found the message");
		setViewData("content", "Java programming is fun!");
		return null;
	}

}
