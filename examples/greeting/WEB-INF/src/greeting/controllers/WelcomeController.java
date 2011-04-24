package greeting.controllers;

import static com.scooterframework.web.controller.ActionControl.*;

import com.scooterframework.admin.FilterManagerFactory;


/**
 * WelcomeController class handles welcome related access.
 */
public class WelcomeController {
	/**
	 * sayit() method
	 */
	public String sayit() {
		flash("notice", "Successfully found the message");
		setViewData("content", "Java programming is fun!");

		//render default view (.jsp)
		return renderView("sayit");

		//render FreeMarker view
		//return renderView("sayit.ftl");

		//render StringTemplate view
		//return renderView("sayit.st");
	}
}