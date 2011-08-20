package ajaxtodo.controllers;

import static com.scooterframework.web.controller.ActionControl.*;

public class TimeController extends ApplicationController {
    public String current() {
        String message = "The current time is: " + (new java.util.Date());
        return text(message);
    }
}
