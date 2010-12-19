/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.common.exception;

/**
 * class FileUploadException records file upload related exceptions.
 * 
 * @author (Fei) John Chen
 */
public class FileUploadException extends GenericException {
    /**
     * Generated serialVersionUID
     */
    private static final long serialVersionUID = 0L;
    
    public FileUploadException (Throwable cause) {
        super(cause);
    }
    
    /**
     * happens when <tt>maximum.total.bytes.per.upload.request</tt> limit is violated.
     */
    public boolean causedByFileSizeLimitExceeded() {
    	String exClassName = (getCause() != null)?getCause().getClass().getName():null;
    	return exClassName.equals("org.apache.commons.fileupload.FileUploadBase$FileSizeLimitExceededException");
    }
    
    /**
     * happens when <tt>maximum.bytes.per.uploaded.file</tt> limit is violated.
     */
    public boolean causedByRequestSizeLimitExceeded() {
    	String exClassName = (getCause() != null)?getCause().getClass().getName():null;
    	return exClassName.equals("org.apache.commons.fileupload.FileUploadBase$SizeLimitExceededException");
    }
    
    public String getMessage() {
    	return (getCause() != null)?getCause().getMessage():null;
    }
}
