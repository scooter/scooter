package ajaxtodo.controllers;

import static com.scooterframework.web.controller.ActionControl.*;

public class ListController extends ApplicationController {

    //return data in json format
    public String filter() {
        return render(buildListB(p("number")));
    }

    private String buildListB(String choice) {
        StringBuilder sb = new StringBuilder("<select size=\"10\">");
        for (int i = 0; i < 10; i++) {
			if (choice != null && choice.equals(i + "")) continue;
            sb.append("<option>").append(i).append("</option>");
        }
        sb.append("</select>");
        return sb.toString();
    }
}