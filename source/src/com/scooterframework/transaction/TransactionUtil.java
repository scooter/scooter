/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.transaction;

import javax.transaction.Status;
import javax.transaction.UserTransaction;

/**
 * TransactionUtil class provides convenient methods about transaction. 
 * 
 * @author (Fei) John Chen
 */
public class TransactionUtil {
    public static boolean isUserTransactionActive(UserTransaction ut) 
    {
        boolean utActive = false;
        try {
            if (ut != null && ut.getStatus() == Status.STATUS_ACTIVE) {
                utActive = true;
            }
        }
        catch (Exception ex) {
            utActive = false;
        }
        return utActive;
    }
}
