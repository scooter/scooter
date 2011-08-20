package ajaxtodo.controllers;

import static com.scooterframework.web.controller.ActionControl.*;

public class DataController extends ApplicationController {

    //return data in json format
    public String retrieve() {
        String jsonString = "{ \"foo\": \"The quick brown fox jumps over the lazy dog.\",  \"bar\": \"ABCDEFG\",  \"baz\": [52, 97]}";
        return render(jsonString);
    }
}