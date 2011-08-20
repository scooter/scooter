/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package plugin.freemarker;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;

import com.scooterframework.admin.ApplicationConfig;
import com.scooterframework.admin.EnvConfig;
import com.scooterframework.admin.Plugin;
import com.scooterframework.common.logging.LogUtil;
import com.scooterframework.web.controller.TemplateHandler;

/**
 * FreeMarkerTemplateHandler handles freemarker templates.
 *
 * @author (Fei) John Chen
 */
public class FreeMarkerTemplateHandler extends Plugin implements TemplateHandler {
	private static LogUtil log = LogUtil.getLogger(FreeMarkerTemplateHandler.class.getName());

	private Configuration cfg;
	private String templateDir;
	private boolean customTemplateDir;

	public FreeMarkerTemplateHandler(Properties p) {
		super(p);
		
		String tmpDir = p.getProperty("templates.dir");
		if (tmpDir != null && !"".equals(tmpDir)) {
			templateDir = tmpDir;
			customTemplateDir = true;
		}
		else {
			templateDir = getTemplateDir();
		}
	}

	private String getTemplateDir() {
		String s = ApplicationConfig.getInstance().getApplicationPath() +
				   EnvConfig.getInstance().getWebPageDirectoryName();
		try {
			File dir = new File(s);
			s = dir.getCanonicalPath();
		}
		catch (Exception ex) {
			log.error("Failed to obtain canonical path of template dir because " + ex.getMessage());
		}
		return s;
	}

	public void onStart() {
		try {
			cfg = new Configuration();
			cfg.setDirectoryForTemplateLoading(new File(templateDir));
			cfg.setObjectWrapper(ObjectWrapper.DEFAULT_WRAPPER);
		}
		catch(Exception ex) {
			log.error("Failed to configure freemarker Configuration instance because " + ex.getMessage());
		}
	}

	public void onStop() {
		cfg = null;
	}

    public String getVersion() {
    	return "0.1.0";
    }

	/**
     * Handles processing the <tt>content</tt> with <tt>props</tt>.
	 *
	 * @param content  The content to be processed.
	 * @param props  properties (name/value pairs) to be used to process the content
	 * @return processed content as string
     */
    public String handle(String content, Map props) {
    	throw new UnsupportedOperationException("This method is not supported.");
    }

    /**
     * Handles processing the <tt>viewTemplate</tt> with <tt>props</tt>.
     *
     * @param templateFile
	 * @param props  properties (name/value pairs) to be used to process the content
     * @return processed content as string
     */
    public String handle(File templateFile, Map props) {
    	String newContent = "";
		try {
			String name = templateFile.getCanonicalPath();
			if (!name.startsWith(templateDir)) {
				if (customTemplateDir) {
					name = templateFile.getName();
				}
				else {
					throw new IllegalArgumentException("FreeMarkerTemplateHandler " +
							"is configured to handle template files under " +
							templateDir +
							" directory, but the input template file '" +
							name +"' is not under that directory.");
				}
			}
			else {
				name = name.substring(templateDir.length());
			}
			Template temp = cfg.getTemplate(name);
	    	Writer out = new StringWriter();
	    	temp.process(props, out);
	    	out.flush();
	    	newContent = out.toString();
		} catch (Exception ex) {
			throw new IllegalArgumentException("Failed to process template file '"
					+ templateFile + "': " + ex.getMessage());
		}
		return newContent;
    }
}
