package customerservice.controllers;

import com.scooterframework.common.logging.LogUtil;
import com.scooterframework.web.controller.ActionControl;

/**
 * ApplicationController class has methods that are available to all subclass
 * controllers. This is a place to add application-wide action methods and filters.
 */
public class ApplicationController extends ActionControl {
    //
    // Add more application-wide methods/filters here.
    //

    /**
     * Declares a <tt>log</tt> instance that are available to all subclasses.
     */
    protected LogUtil log = LogUtil.getLogger(getClass().getName());
}
