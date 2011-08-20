package greeting.controllers;

import static com.scooterframework.web.controller.ActionControl.*;

public class HelloController {
    public String index() {
        return "xml=><?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
               "<book><title>Java Programming</title><price>$50</price></book>";
    }
}