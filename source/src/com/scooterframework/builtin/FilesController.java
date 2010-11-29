/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.builtin;

import static com.scooterframework.web.controller.ActionControl.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.scooterframework.admin.ApplicationConfig;
import com.scooterframework.admin.EnvConfig;
import com.scooterframework.common.logging.LogUtil;
import com.scooterframework.common.util.FileUtil;
import com.scooterframework.web.controller.UploadFile;

/**
 * FilesController class manages files and folders on the server.
 * 
 * @author (Fei) John Chen
 */
public class FilesController {
	private LogUtil log = LogUtil.getLogger(getClass().getName());

	private static String appPath = ApplicationConfig.getInstance().getApplicationPath();

	static {
		filterManagerFor(FilesController.class).declareBeforeFilter(
				AdminSignonController.class, "loginRequired");
		
		filterManagerFor(FilesController.class).declareBeforeFilter(
				"validatePath", "except", "create, update, doCopy, doUpload, doReplace");
	}

	public String validatePath() {
		String path = p("f");
		if (path == null) {
			path = "/";
			storeToRequest("f", path);
		}
		
		File file = getFile(path);
		if (!file.exists()) {
			flash("error", "There is no file related to '" + path + "'.");
			return forwardTo(EnvConfig.getInstance().getErrorPageURI());
		}
		return null;
	}

	private File getFile(String path) {
		String fullPath = appPath + File.separatorChar + path;
		return new File(fullPath);
	}

	/**
	 * Returns a list of files or folders.
	 */
	public String list() {
		String path = p("f");
		File requestFile = getFile(path);
		setViewData("requestFile", requestFile);

		List<FileInfo> files = new ArrayList<FileInfo>();
		if (requestFile != null) {
			if (requestFile.isDirectory()) {
				File[] children = requestFile.listFiles();
				if (children != null) {
					for (File f : children) {
						if (f.isDirectory())
							files.add(new FileInfo(f));
					}
					for (File f : children) {
						if (!f.isDirectory())
							files.add(new FileInfo(f));
					}
				}
			} else if (requestFile.isFile()) {
				files.add(new FileInfo(requestFile));
			}
		}

		setViewData("files", files);

		return null;
	}

	/**
	 * Returns a file.
	 */
	public String show() {
		String path = p("f");
		File requestFile = getFile(path);
		setViewData("requestFile", requestFile);

		if (isTextFile(requestFile)) {
			StringBuilder sb = new StringBuilder();
			String lineBreak = System.getProperty("line.separator");

			try {
				List<String> contentLines = FileUtil.readContent(requestFile);
				for (String line : contentLines) {
					sb.append(line).append(lineBreak);
				}
			} catch (Exception ex) {
				log.error("Failed to read file " + path);
				flash("error", "Failed to read file " + path);
			}

			String classCode = getFileExtension(requestFile);
			if (EnvConfig.getInstance().isHighlightable(classCode)) {
				classCode = classCode + "_code";
			} else {
				classCode = "code";
			}
			
			setViewData("classCode", classCode);
			setViewData("fileContent", sb.toString());
		} else {
			publishFile(requestFile);
		}
		return null;
	}

	public String add() {
		String path = p("f");
		File requestFile = getFile(path);
		setViewData("requestFile", requestFile);
		return null;
	}

	public String create() {
		String path = p("f");
		File requestFile = getFile(path);
		setViewData("requestFile", requestFile);

		String name = p("name");
		String newPath = path + "/" + name;
		File newFile = getFile(newPath);
		String content = p("fileContent");
		try {
			FileUtil.updateFile(newFile, content);
			flash("notice", "File was successfully created.");
			return redirectTo(BuiltinHelper.FILE_BROWSER_LINK_PREFIX_FILE + newPath);
		} catch (Exception ex) {
			log.error("Error in create() caused by " + ex.getMessage());
			flash("error", "There was a problem creating the file.");
		}
		return forwardTo(viewPath("add"));
	}

	public String edit() {
		String path = p("f");
		File requestFile = getFile(path);
		setViewData("requestFile", requestFile);

		if (isTextFile(requestFile)) {
			StringBuilder sb = new StringBuilder();
			String lineBreak = System.getProperty("line.separator");

			try {
				List<String> contentLines = FileUtil.readContent(requestFile);
				for (String line : contentLines) {
					sb.append(line).append(lineBreak);
				}
			} catch (Exception ex) {
				log.error("Failed to read file " + path);
				flash("error", "Failed to read file " + path);
			}

			setViewData("fileContent", sb.toString());
		} else {
			flash("error", "You can not edit a non-text file.");
		}
		return null;
	}

	public String update() {
		String path = p("f");
		File requestFile = getFile(path);
		String content = p("fileContent");
		try {
			FileUtil.updateFile(requestFile, content);
			flash("notice", "File was successfully updated.");
			return redirectTo(BuiltinHelper.FILE_BROWSER_LINK_PREFIX_FILE + path);
		} catch (Exception ex) {
			log.error("Error in update() caused by " + ex.getMessage());
			flash("error", "There was a problem updating the file.");
		}
		return forwardTo(viewPath("edit"));
	}

	public String delete() {
		String path = p("f");
		File requestFile = getFile(path);
		String parentPath = "/";
		if (requestFile != null) {
			parentPath = (new FileInfo(requestFile.getParentFile())).getRelativePath();
			
			boolean deleted = requestFile.delete();
			if (deleted) {
				flash("notice", "The file was successfully deleted.");
			} else {
				flash("error", "The file was not deleted.");
			}
		}
		return redirectTo(BuiltinHelper.FILE_BROWSER_LINK_PREFIX_DIR + parentPath);
	}

	public String copy() {
		String path = p("f");
		File requestFile = getFile(path);
		setViewData("requestFile", requestFile);
		return null;
	}

	public String doCopy() {
		String path = p("f");
		File requestFile = getFile(path);
		setViewData("requestFile", requestFile);

		String target = p("target");
		File targetFile = getFile(target);
		try {
			FileUtil.copy(requestFile, targetFile);
			flash("notice", "Copy was successful.");
			return (targetFile.isDirectory()) ? 
					redirectTo(BuiltinHelper.FILE_BROWSER_LINK_PREFIX_DIR + target) : 
					redirectTo(BuiltinHelper.FILE_BROWSER_LINK_PREFIX_FILE + target);
		} catch (Exception ex) {
			log.error("Error in doCopy() caused by " + ex.getMessage());
			flash("error", "There was a problem in copying.");
		}
		return forwardTo(viewPath("copy"));
	}

	public String rename() {
		String path = p("f");
		File requestFile = getFile(path);
		setViewData("requestFile", requestFile);
		return null;
	}

	public String doRename() {
		String path = p("f");
		File requestFile = getFile(path);
		setViewData("requestFile", requestFile);

		String target = p("target");
		File targetFile = getFile(target);
		try {
			boolean renamed = FileUtil.rename(requestFile, targetFile);
			if (renamed) {
				flash("notice", "Rename was successful.");
				return (targetFile.isDirectory()) ? 
						redirectTo(BuiltinHelper.FILE_BROWSER_LINK_PREFIX_DIR + target) : 
						redirectTo(BuiltinHelper.FILE_BROWSER_LINK_PREFIX_FILE + target);
			} else {
				flash("error", "Rename was not successful.");
			}
		} catch (Exception ex) {
			log.error("Error in doRename() caused by " + ex.getMessage());
			flash("error", "There was a problem in renaming.");
		}
		return forwardTo(viewPath("rename"));
	}

	public String upload() {
		String path = p("f");
		File requestFile = getFile(path);
		setViewData("requestFile", requestFile);
		return null;
	}

	public String doUpload() {
		String path = p("f");
		File requestFile = getFile(path);
		setViewData("requestFile", requestFile);
		
        try {
            UploadFile uf = pFile("theFile");
            uf.writeTo(appPath + "/" + path);
            flash("notice", "File was successfully uploaded.");
            return redirectTo(BuiltinHelper.FILE_BROWSER_LINK_PREFIX_DIR + path);
        } catch (Exception ex) {
			log.error("Error in doUpload() caused by " + ex.getMessage());
            flash("error", "There is a problem with upload.");
        }
		return forwardTo(viewPath("upload"));
	}

	public String replace() {
		String path = p("f");
		File requestFile = getFile(path);
		setViewData("requestFile", requestFile);
		return null;
	}

	public String doReplace() {
		String path = p("f");
		File requestFile = getFile(path);
		setViewData("requestFile", requestFile);
		
        try {
			String parentPath = (new FileInfo(requestFile.getParentFile())).getRelativePath();
            UploadFile uf = pFile("theFile");
            uf.writeTo(requestFile);
            flash("notice", "File was successfully replaced.");
            return redirectTo(BuiltinHelper.FILE_BROWSER_LINK_PREFIX_DIR + parentPath);
        } catch (Exception ex) {
			log.error("Error in doReplace() caused by " + ex.getMessage());
        }
        flash("error", "There is a problem with replace.");
		return forwardTo(viewPath("replace"));
	}
}
