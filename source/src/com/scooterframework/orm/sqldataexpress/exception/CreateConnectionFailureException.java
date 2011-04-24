/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.exception;

/**
 * class DatabaseConnectionFailureException
 *
 * @author (Fei) John Chen
 */
public class CreateConnectionFailureException extends BaseSQLException {
    /**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = -5940981691248600388L;

	public CreateConnectionFailureException(String message) {
        super(message);
    }

    public CreateConnectionFailureException(Exception exception) {
        super(exception);
    }

    public CreateConnectionFailureException(String message, Exception exception) {
        super(message, exception);
    }

    public String getConnectionName() {
        return connName;
    }

    public String toString() {
        StringBuilder returnString = new StringBuilder();

        returnString.append( super.toString() );

        returnString.append( "connName = " + connName );

        return returnString.toString();
    }

    private String connName = null;
}
