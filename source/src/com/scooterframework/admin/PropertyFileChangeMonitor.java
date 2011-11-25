/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.admin;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import com.scooterframework.common.logging.LogUtil;

/**
 * <p>
 * PropertyFileChangeMonitor class monitors property file changes and also 
 * notifies the changes to associated observers. 
 * </p>
 * 
 * <p>
 * The default monitor interval is 2000 milliseconds. This can be changed by 
 * System property <tt>property.load.interval</tt>.
 * </p>
 * 
 * <p>
 * <code>ApplicationConfig.getInstance()</code> must be called before this class 
 * can be used.
 * </p>
 * 
 * @author (Fei) John Chen
 */
public class PropertyFileChangeMonitor {
    private LogUtil log = LogUtil.getLogger(this.getClass().getName());
    
    private Map<File, Observable> observables = new HashMap<File, Observable>();
    
    private static long oneHundredDays = 8640000;
    private boolean periodicReading = false;
    private Date oldDate = null;
    private Timer timer = null;
    
    /**
     * Time interval in milliseconds between successive task executions
     */
    private long loadInterval = 2000L;
    
    private String propertyFilePath = "";
    
    private static final PropertyFileChangeMonitor fcm = new PropertyFileChangeMonitor();
    
    private PropertyFileChangeMonitor() {
        propertyFilePath = ApplicationConfig.getInstance().getPropertyFileLocationPath();
        
        String loadDelay = System.getProperty("property.load.interval");
        if (loadDelay != null) {
            try {
                loadInterval = (new Integer(loadDelay)).intValue();
            }
            catch(NumberFormatException ex) {
                log.warn("System property property.load.interval has wrong integer format. Use default value " + loadInterval + " milliseconds.");
            }
        }
        
        if (ApplicationConfig.getInstance().isWebApp() && 
        		ApplicationConfig.getInstance().isInDevelopmentEnvironment() && 
        		loadInterval > 0) {
            periodicReading = true;
        }
    }
    
    public static PropertyFileChangeMonitor getInstance() {
        return fcm;
    }
    
    public void start() {
        if (periodicReading) {
            oldDate = new Date();
            oldDate.setTime(oldDate.getTime()-oneHundredDays);
            
            timer = new Timer();
            
            PropertyFileChangeMonitorTimerTask task = 
                new PropertyFileChangeMonitorTimerTask();
            schedule(task, loadInterval);
        }
    }
    
    public void stop() {
        if (timer != null) {
            timer.cancel();
            log.debug("Property file change monitor stopped.");
        }
    }

    public void registerObserver(Observer observer, String fileName) {
        registerObserver(observer, new File(getFullFileName(fileName)));
    }
    
    public void registerObserver(Observer observer, File file) {
        if (!(ApplicationConfig.getInstance().isWebApp() && 
        		ApplicationConfig.getInstance().isInDevelopmentEnvironment())) return;
        
        if (file == null) {
            log.error("Can not watch the file, because the input file is null.");
            return;
        }
        else {
            if (!file.exists()) {
                log.error("Can not watch the file " + file.getName() + ", because it does not exist.");
                return;
            }
        }

        log.debug("monitoring file: " + file.getName());
        
        Observable observable = observables.get(file);
        if (observable == null) {
            observable = new FileObservable(file);
            observables.put(file, observable);
        }
        observable.addObserver(observer);
    }
    
    public String getFullFileName(String fileName) {
        if (fileName.startsWith("/") || fileName.startsWith("\\")) {
            fileName = fileName.substring(1);
        }
        return (propertyFilePath != null && !"".equals(propertyFilePath))?
            (propertyFilePath + File.separatorChar + fileName):
            (fileName);
    }
    
    private void schedule(TimerTask task, long period) {
        if (period > 0) {
            timer.schedule(task, oldDate, period);
        }
        else {
            timer.schedule(task, oldDate);
        }
    }
    
    /**
     * FileChangeMonitorTimerTask is responsible for scanning files.
     */
    public class PropertyFileChangeMonitorTimerTask extends TimerTask {
        public PropertyFileChangeMonitorTimerTask() {
            super();
        }
        
        public void run() {
            for (Map.Entry<File, Observable> entry : observables.entrySet()) {
                FileObservable observable = (FileObservable)observables.get(entry.getKey());
                observable.checkChange();
            }
        }
    }
}
