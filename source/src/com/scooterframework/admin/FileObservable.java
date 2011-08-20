/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.admin;

import java.io.File;
import java.util.Observable;

import com.scooterframework.common.logging.LogUtil;

/**
 * FileObservable class is a subclass of <tt>Observable</tt>.
 *
 * @author (Fei) John Chen
 */
public class FileObservable extends Observable {
    private LogUtil log = LogUtil.getLogger(this.getClass().getName());
    private File source = null;
    private long lastSourceModifiedTime = 0L;

    public FileObservable(File file) {
        super();

        if (file == null) {
            throw new IllegalArgumentException("Input file is null.");
        }
        else if (!file.isFile()) {
            throw new IllegalArgumentException("Input file is not file or does not exist: " + file);
        }

        source = file;
        lastSourceModifiedTime = source.lastModified();
    }

    public File getSourceFile() {
        return source;
    }

    void checkChange() {
        long last = source.lastModified();
        if (lastSourceModifiedTime < last) {
            onChange();
        }
        lastSourceModifiedTime = last;
    }

    void onChange() {
        super.setChanged();
        log.debug("File " + source.getName() + " is modified.");
        notifyObservers(source.getName());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append("source=" + source).append(", ");
        sb.append("lastSourceModifiedTime=" + lastSourceModifiedTime);
        return sb.toString();
    }
}
