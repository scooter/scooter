/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.activerecord;

import com.scooterframework.orm.sqldataexpress.exception.BaseSQLException;

/**
 * class UnregisteredCategoryException
 *
 * @author (Fei) John Chen
 */
public class UnregisteredCategoryException extends BaseSQLException {
    /**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = -6756311541470869506L;

	public UnregisteredCategoryException(String unregisteredCategory) {
        this.unregisteredCategory = unregisteredCategory;
    }

    public String getUnregisteredCategory() {
        return unregisteredCategory;
    }

    public String toString() {
        StringBuilder returnSB = new StringBuilder();
        returnSB.append("Category ").append(unregisteredCategory);
        returnSB.append(" is not registered.");
        return returnSB.toString();
    }

    private String unregisteredCategory;
}
