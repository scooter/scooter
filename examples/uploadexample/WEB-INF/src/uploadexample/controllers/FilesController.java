package uploadexample.controllers;

import static com.scooterframework.web.controller.ActionControl.*;

import com.scooterframework.admin.FilterManagerFactory;
import com.scooterframework.common.util.CurrentThreadCacheClient;
import com.scooterframework.web.controller.UploadFile;

/**
 * FilesController class handles files related access.
 */
public class FilesController {

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
		if (CurrentThreadCacheClient.hasError()) {
			flash("error", CurrentThreadCacheClient.getFirstError().getMessage());
			return renderView("index");
		}

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
