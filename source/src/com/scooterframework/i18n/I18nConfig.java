/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.i18n;

import java.io.File;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import com.scooterframework.admin.ApplicationConfig;
import com.scooterframework.admin.DirChangeMonitor;
import com.scooterframework.admin.EnvConfig;
import com.scooterframework.admin.FileChangeNotice;
import com.scooterframework.web.controller.ActionContext;

/**
 * I18nConfig class has methods to load and process message properties files.
 * 
 * @author (Fei) John Chen
 */
public class I18nConfig implements Observer {
    private static I18nConfig me;
    static MessageResourcesManager mrm;
    
    static {
        me = new I18nConfig();
    }
    
    private I18nConfig() {      
        init();
    }
    
    private void init() {
        String configPath = ApplicationConfig.getInstance().getPropertyFileLocationPath();
        String messagePath = "";
        if (configPath == null || "".equals(configPath)) {
            messagePath = "locales";
        }
        else {
            messagePath = configPath + File.separator + "locales";
        }
        
        String baseName = EnvConfig.getInstance().getMessageResourcesFileBase();
        
        mrm = new MessageResourcesManager(messagePath, baseName);
        
        //initialize locale
        Locale locale = ActionContext.getGlobalLocale();
        if (locale == null) locale = Locale.getDefault();
        mrm.loadLocale(locale);
        
        //register all message files in the config directory
        DirChangeMonitor.getInstance().registerObserverForFilePrefix(this, messagePath, baseName);
    }
    
    private void added(File file) {
        mrm.fileAdded(file);
    }
    
    private void modified(File file) {
        mrm.fileUpdated(file);
    }
    
    private void deleted(File file) {
        mrm.fileDeleted(file);
    }


    public static synchronized I18nConfig getInstance() {
        return me;
    }
    
    public void update(Observable o, Object arg) {
        FileChangeNotice notice = (FileChangeNotice)arg;
        if (FileChangeNotice.ADD_FILE.equals(notice.getAction())) {
            added(notice.getFile());
        }
        else if (FileChangeNotice.MODIFY_FILE.equals(notice.getAction())) {
            modified(notice.getFile());
        }
        else if (FileChangeNotice.DELETE_FILE.equals(notice.getAction())) {
            deleted(notice.getFile());
        }
    }
}
