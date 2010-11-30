package uploadexample.controllers;

import static com.scooterframework.web.controller.ActionControl.*;

import com.scooterframework.admin.FilterManagerFactory;
import com.scooterframework.web.controller.UploadFile;


/**
 * FilesController class handles files related access.
 */
public class FilesController extends ApplicationController {

	/**
	 * Constructor
	 */
	public FilesController() {
	}


	/**
	 * index() method
	 */
	public String index() {
		return null;
	}

	/**
	 * upload() method
	 */
	public String upload() {
        try {
            UploadFile uf1 = pFile("file1");
            uf1.writeTo(applicationPath() + "/static/docs");
            flash("notice", "You have successfully uploaded a file.");

            setViewData("file", uf1.getFileName());
        } catch (Exception ex) {
            flash("error", "There is a problem with upload.");
        }
        return null;
    }

}
