package greeting.controllers;

import static com.scooterframework.web.controller.ActionControl.*;

import com.scooterframework.admin.FilterManagerFactory;


/**
 * RegistrationController class
 */
public class RegistrationController {

	/**
	 * process() method
	 */
	public String process() {
		System.out.println("Registration Info Start=====");
		System.out.println("     firstname: " + p("firstname"));
		System.out.println("      lastname: " + p("lastname"));
		System.out.println("      password: " + p("pwd"));
		System.out.println("           dob: " + p("dob"));
		System.out.println("           DOB: " + pDate("dob"));
		System.out.println("        gender: " + p("gender"));
		System.out.println("          mars: " + pBoolean("mars"));
		System.out.println("           bio: " + p("bio"));
		System.out.println("       hobby #: " + pArray("hobby").length);
		System.out.println("         hobby: " + parseArray(pArray("hobby")));
		System.out.println("     framework: " + pArray("framework")[0]);
		System.out.println("       tools #: " + pArray("tools").length);
		System.out.println("         tools: " + parseArray(pArray("tools")));
		System.out.println("Registration Info End  =====");
		return redirectTo("input");
	}

	/**
	 * index() method
	 */
	public String input() {
		return renderView("input");
	}

	private String parseArray(String[] ary) {
		if (ary.length == 0) return "";

		StringBuilder sb = new StringBuilder();
		for (String s: ary) {
			sb.append(s).append(" ");
		}
		return sb.toString();
	}
}