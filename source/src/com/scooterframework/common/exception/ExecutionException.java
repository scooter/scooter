/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.common.exception;

import java.lang.reflect.InvocationTargetException;

import com.scooterframework.common.util.StringUtil;

/**
 * class ExecutionException
 *
 * @author (Fei) John Chen
 */
public class ExecutionException extends GenericException {
    /**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = -5067594838142223952L;

	public ExecutionException(String className, String methodName, Object[] args, Throwable cause) {
        super(cause);
        this.className = className;
        this.methodName = methodName;
        this.args = args;
    }

    public String getMessage() {
        String parentMessage = null;
        if (super.getCause() instanceof InvocationTargetException) {
            parentMessage = super.getCause().getCause().getMessage();
        }
        else {
            parentMessage = super.getMessage();
        }

        StringBuilder result = new StringBuilder();
        result.append("Failed to invoke class \"").append(className).append("\" for method \"").append(methodName).append("\"");
        if (args != null) {
            result.append(" with these arguments \"").append(StringUtil.flattenArray(args)).append("\"");
        }
        if (parentMessage != null) result.append("\r\nDetails: ").append(parentMessage);
        return result.toString();
    }

    protected String className;
    protected String methodName;
    protected Object[] args;
}
