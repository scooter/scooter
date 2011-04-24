/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.object;

import java.util.Collection;

/**
 * Function class.
 *
 * A function is essentially a stored procedure that returns a result.
 *
 * @author (Fei) John Chen
 */
public class Function extends StoredProcedure {
    public Function(String name) {
        super(name);
        this.name = name;
    }

    public String getReturnTypeName() {
        if (!processedReturn) {
            findReturnType();
        }
        return returnTypeName;
    }

    public int getReturnType() {
       if (!processedReturn) {
           findReturnType();
       }
       return returnType;
    }


    private void findReturnType() {
        processedReturn = true;
        Collection<Parameter> c = getParameters();
        for (Parameter p : c) {
            if (p.getMode() == Parameter.MODE_RETURN) {
                returnTypeName = p.getSqlDataTypeName();
                returnType = p.getSqlDataType();
                break;
            }
        }
        processedReturn = true;
    }

    /*
     * Display something like the following:
     * {? = call add_month(?,?)} // note: there is no package name.
     */
    protected String formatJavaAPIString()
    {
        StringBuilder buf = new StringBuilder();

        String questionMarkString = "";
        int colCount = parameters.size();
        for ( int i = 0; i < colCount-1; i++ )
        {
            questionMarkString = questionMarkString + "?,";
        }
        if (questionMarkString.endsWith(",")) questionMarkString = questionMarkString.substring(0, questionMarkString.length()-1);

        buf.append("{? = call ");
        if ( schema != null ) buf.append(schema).append(".");
        if ( catalog != null ) buf.append(catalog).append(".");
        buf.append(api);
        buf.append("(").append(questionMarkString).append(")}");

        return buf.toString();
    }

    private boolean processedReturn = false;
    private String returnTypeName;
    private int returnType;
}
