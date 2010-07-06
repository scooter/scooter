/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.autoloader;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.scooterframework.common.logging.LogUtil;

/**
 * FileMonitor is responsible for monitoring file changes. Changed files are
 * automatically recompiled.
 * 
 * @author (Fei) John Chen
 */
public class FileMonitor {
    private LogUtil log = LogUtil.getLogger(this.getClass().getName());
    
    private static long oneHundredDays = 8640000;
    private static boolean started = false;
    private Date oldDate = null;
    private Timer timer = null;
    private long period = 0L;
    private String sourcePath = "";
    private long lastScanTime = 0L;
    private static Map sourceMap = Collections.synchronizedMap(new HashMap());
    private Map modifiedSources = new HashMap();
    private long latestChange = 0L;
    
    private static FileMonitor fm;
    
    public static boolean turnOff = false;
    
    static {
        fm = new FileMonitor();
    }
    
    private FileMonitor() {
        sourcePath = AutoLoaderConfig.getInstance().getSourcePath();
        scanAllSources(sourcePath);
        
        oldDate = new Date();
        oldDate.setTime(oldDate.getTime()-oneHundredDays);
        
        AutoLoaderConfig.getInstance().registerFileMonitor(this);
    }
    
    public static FileMonitor getInstance() {
        return fm;
    }
    
    public void start() {
        if (turnOff || started) return;
        
        timer = new Timer();
        sourcePath = AutoLoaderConfig.getInstance().getSourcePath();
        period = AutoLoaderConfig.getInstance().getPeriod();
        
        if (period > 0) {
            SourceFileTimerTask sourceTask = 
                new SourceFileTimerTask(sourcePath);
            schedule(sourceTask, period);
            
            started = true;
            log.debug("Java source file change monitor started with an interval of " + period + " milliseconds.");
        }
    }
    
    /**
     * Terminates this loader, discarding any currently scheduled tasks.
     * 
     * @see  java.util.Timer#cancel()
     */
    public void stop() {
        if (timer != null) {
            timer.cancel();
            log.debug("Java source file change monitor stopped.");
        }
        started = false;
    }
    
    /**
     * Updates the FileMonitor, restarts the timer if the period is changed.
     */
    public void update() {
        long newPeriod = AutoLoaderConfig.getInstance().getPeriod();
        if (newPeriod != period) {
            stop();
            if (newPeriod > 0) {
                start();
            }
        }
    }
    
    public static boolean isStarted() {
        return started;
    }
    
    /**
     * Only those classes that are under src directory are monitored.
     * 
     * @param className
     * @return true if the class is monitored.
     */
    public static boolean isClassMonitored(String className) {
        return sourceMap.containsKey(className);
    }
    
    public static SourceFile getSourceFile(String className) {
        SourceFile sf = (SourceFile)sourceMap.get(className);
        if (sf == null) {
            sf = new SourceFile(new File(SourceFile.getSourceNameFromClassName(className)));
        }
        return sf;
    }
    
    public long getLastScanTime() {
        return lastScanTime;
    }
    
    private void schedule(TimerTask task, long period) {
        if (period > 0) {
            timer.schedule(task, oldDate, period);
        }
        else {
            timer.schedule(task, oldDate);
        }
    }
    
    private void scanAllSources(String sourceLocation) {
        File base = new File(sourceLocation);
        scanFiles(base);
        lastScanTime = (new Date()).getTime();
        
        if (modifiedSources.size() > 0) {
            recompile();
        }
    }
        
    private void scanFiles(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            int length = files.length;
            for (int i=0; i<length; i++) {
                File f = files[i];
                scanFiles(f);
            }
        }
        else {
            String fn = file.getName();
            if (fn.endsWith("java") && (fn.indexOf(' ') == -1)) {
                processJavaFile(file);
            }
        }
    }
    
    private void processJavaFile(File file) {
        String fileName = file.getName();
        String className = SourceFile.getClassName(file);
        if (sourceMap.containsKey(className)) {
            SourceFile sf = (SourceFile)sourceMap.get(className);
            if (sf.isUpdated(file) || sf.availableForRecompile()) {
                if (!modifiedSources.containsKey(fileName)) {
                    modifiedSources.put(fileName, sf);
                }
            }
        }
        else {
            sourceMap.put(className, new SourceFile(file));
        }
    }
    
    private void recompile() {
        //1. check if there is any change in source files
        int size = modifiedSources.size();
        List files = new ArrayList(size);
        Iterator it = modifiedSources.keySet().iterator();
        long sumTime = 0L;
        while(it.hasNext()) {
            Object fileName = it.next();
            SourceFile sf = (SourceFile)modifiedSources.get(fileName);
            
            if (sf.getSource().exists()) {
                files.add(sf.getSource());
                sumTime += sf.getLastSourceModifiedTime();
            }
        }
        
        if (sumTime == latestChange) return;
        
        //2. recompile
        log.debug("recompile classes: " + files);
        latestChange = sumTime;
        
        String error = JavaCompiler.compile(files);
        if (error != null) {
            log.error("Failed to compile. Error details: \n\r" + error);
        }
        else {
            modifiedSources.clear();
        }
    }
    
    /**
     * SourceFileTimerTask is responsible for scanning files.
     */
    public class SourceFileTimerTask extends TimerTask {
        private String sourceLocation = "";
        
        public SourceFileTimerTask(String sourceLocation) {
            super();
            this.sourceLocation = sourceLocation;
        }
        
        public void run() {
            if (started) scanAllSources(sourceLocation);
        }
    }
}
