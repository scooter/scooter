/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.admin;

import java.io.File;
import java.io.FileFilter;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import com.scooterframework.common.logging.LogUtil;

/**
 * <p>
 * DirChangeMonitor class monitors directory file changes and also notifies the 
 * changes to associated observers. 
 * </p>
 * 
 * <p>
 * The default monitor interval is 2000 miliseconds. This can be changed by 
 * System property <tt>property.load.interval</tt>.
 * </p>
 * 
 * @author (Fei) John Chen
 */
public class DirChangeMonitor {
    private LogUtil log = LogUtil.getLogger(this.getClass().getName());
    
    private Map observables = new HashMap();
    
    private static long oneHundredDays = 8640000;
    private boolean periodicReading = false;
    private Date oldDate = null;
    private Timer timer = null;
    
    /**
     * Time interval in milliseconds between successive task executions
     */
    private long loadInterval = 2000L;
    
    private static DirChangeMonitor fcm;
    
    public static final String DEFAULT_RUNNING_ENVIRONMENT = "DEVELOPMENT";
    
    static {
        fcm = new DirChangeMonitor();
    }
    
    private DirChangeMonitor() {
        String runningEnvironment = System.getProperty("running.environment", DEFAULT_RUNNING_ENVIRONMENT);
        
        String loadDelay = System.getProperty("property.load.interval");
        if (loadDelay != null) {
            try {
                loadInterval = (new Integer(loadDelay)).intValue();
            }
            catch(NumberFormatException ex) {
                log.warn("System property property.load.interval has wrong integer format. Use default value " + loadInterval + " milliseconds.");
            }
        }
        
        if (DEFAULT_RUNNING_ENVIRONMENT.equals(runningEnvironment) && 
            loadInterval > 0 && 
            ApplicationConfig.getInstance().isWebApp()) {
            periodicReading = true;
        }
        
        if (periodicReading) {
            oldDate = new Date();
            oldDate.setTime(oldDate.getTime()-oneHundredDays);
            
            timer = new Timer();
            start();
        }
    }
    
    public static synchronized DirChangeMonitor getInstance() {
        return fcm;
    }
    
    public void start() {
        DirChangeMonitorTimerTask task = 
            new DirChangeMonitorTimerTask();
        schedule(task, loadInterval);
    }
    
    public void stop() {
        if (timer != null) {
            timer.cancel();
            log.debug("Dir files change monitor stopped.");
        }
    }
    
    /**
     * Register an observer for a file of a directory path.
     * 
     * @param observer
     * @param path directory path
     * @param fileName the file to watch
     */
    public void registerObserverForFileName(Observer observer, String path, String fileName) {
        FileFilter filter = new FileFilterSameName(fileName);
        registerObserver(observer, path, filter);
    }
    
    /**
     * Register an observer for all files of the same prefix of a directory path.
     * 
     * @param observer
     * @param path directory path
     * @param filePrefix the file prefix to watch
     */
    public void registerObserverForFilePrefix(Observer observer, String path, String filePrefix) {
        FileFilter filter = new FileFilterSamePrefix(filePrefix);
        registerObserver(observer, path, filter);
    }
    
    /**
     * Register an observer for all files of the same suffix of a directory path.
     * 
     * @param observer
     * @param path directory path
     * @param fileSuffix the file prefix to watch
     */
    public void registerObserverForFileSuffix(Observer observer, String path, String fileSuffix) {
        FileFilter filter = new FileFilterSameSuffix(fileSuffix);
        registerObserver(observer, path, filter);
    }
    
    /**
     * Register an observer of a directory path.
     * 
     * @param observer
     * @param path directory path
     */
    public void registerObserverForDir(Observer observer, String path) {
        registerObserver(observer, path, null);
    }
    
    /**
     * Register an observer of a directory path with a file filter.
     * 
     * @param observer
     * @param path directory path
     * @param filter file filter
     */
    public void registerObserver(Observer observer, String path, FileFilter filter) {
        if (!ApplicationConfig.getInstance().isWebApp()) return;
        
        if (path == null) {
            log.error("Can not watch the directory " + path + ", because the input path is null.");
            return;
        }
        
        File dir = new File(path);
        
        if (!dir.isDirectory()) {
            log.error("Can not watch the directory " + path + ", because it does not exist.");
            return;
        }
        
        if (filter == null) {
            log.debug("monitoring dirctory: " + path);
        }
        else {
            log.debug("monitoring dirctory: " + path + " with filter " + filter.toString());
        }
        
        String observableKey = getObservableKey(path, filter);
        Observable observable = (Observable)observables.get(observableKey);
        if (observable == null) {
            observable = new DirObservable(path, filter);
            observables.put(observableKey, observable);
        }
        observable.addObserver(observer);
    }
    
    private String getObservableKey(String path, FileFilter filter) {
        return (filter != null)?(path + "_" + filter.toString()):path;
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
     * DirChangeMonitorTimerTask is responsible for scanning files in the directory.
     */
    public class DirChangeMonitorTimerTask extends TimerTask {
        public DirChangeMonitorTimerTask() {
            super();
        }
        
        public void run() {
            Iterator it = observables.keySet().iterator();
            while(it.hasNext()) {
                DirObservable observable = (DirObservable)observables.get(it.next());
                observable.checkChange();
            }
        }
    }
}
