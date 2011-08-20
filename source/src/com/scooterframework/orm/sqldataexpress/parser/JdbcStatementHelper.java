/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.scooterframework.common.logging.LogUtil;
import com.scooterframework.common.util.StringUtil;
import com.scooterframework.orm.sqldataexpress.object.Parameter;
import com.scooterframework.orm.sqldataexpress.util.SqlUtil;


/**
 * JdbcStatementHelper class has helper methods that are used by parser classes.
 *
 * @author (Fei) John Chen
 */
public class JdbcStatementHelper {

    // reset all the alias to original table name
    protected String resetAlias(String message) {
        // return if there is no alias
        if ( message.indexOf('.') == -1 ) return message;

        message = message.toUpperCase();

        message = resetSpace(message) + " ";

        List<String> aliasList = new ArrayList<String>();
        Map<String, String> aliasMap = new HashMap<String, String>();
        List<String> asList = new ArrayList<String>();

        /*
         * Note: I cannot add '()' to the tokenizer list, because inner view may
         * cause some problems. But I need to allow space around '()'.
         */
        message = StringUtil.replace(message, "(", " ( ");
        message = StringUtil.replace(message, ")", " ) ");
        message = StringUtil.replace(message, ",", " , ");
        StringTokenizer sti = new StringTokenizer(message, " |><={}+-*/");
        int totalTokens = sti.countTokens();
        String[] tokens = new String[totalTokens];
        int i = 0;
        while(sti.hasMoreTokens()) {
            tokens[i] = sti.nextToken();

            if (!tokens[i].startsWith("?")) {
                int dotIndex = tokens[i].indexOf('.');
                if (dotIndex != -1) {
                    // get the alias before dot
                    String alias = tokens[i].substring(0, dotIndex);
                    if ( !aliasList.contains(alias) ) aliasList.add(alias);
                }
            }
            i = i + 1;
        }
        //log.debug("alias: " + aliasList);

        // find the alias ref
        for ( int j = 1; j < totalTokens; j++ ) {
            String token = tokens[j];
            if ( aliasList.contains(token) &&
                 !aliasMap.containsKey(token)) {
                if ( !",".equals(tokens[j-1]) &&
                     !"UPDATE".equals(tokens[j-1]) &&
                     !"FROM".equals(tokens[j-1]) &&
                     !"AS".equals(tokens[j-1]) &&
                     !"JOIN".equals(tokens[j-1]) && //e.g. SELECT USERS.* FROM USERS INNER JOIN PROJECTS_USERS ON USERS.ID=PROJECTS_USERS.USER_ID
                     !")".equals(tokens[j-1])) {
                    aliasMap.put(token, tokens[j-1]);
                }
                else if ("AS".equalsIgnoreCase(tokens[j-1])) {
                    aliasMap.put(token, tokens[j-2]);
                    asList.add(token);
                }
            }
        }

        // replace all occurrences of alias in the message string with its ref
        for (Map.Entry<String, String> entry : aliasMap.entrySet()) {
            String alias = entry.getKey();
            String aliasRef = entry.getValue();
            if (aliasRef == null) continue;

            message = replaceWords(message, alias+".", aliasRef+".");
            message = replaceWords(message, aliasRef + " " + alias, aliasRef);
        }

        // replace all occurrences of alias in the message string with its ref
        for (String alias : asList) {
            message = replaceWords(message, "AS " + alias, "");
        }

        //log.debug("Leave resetAlias message=" + message);

        return message;
    }

    //allow only one space between words in the message
    protected String resetSpace(String message) {
        StringTokenizer sti = new StringTokenizer(message, " ");
        StringBuilder sb = new StringBuilder();
        while(sti.hasMoreTokens()) {
            sb.append(sti.nextToken()).append(' ');
        }
        return sb.toString();
    }

    //replace all occurrences of oldWord with newWord.
    protected String replaceWords(String message, String oldWord, String newWord) {

        String validChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_1234567890";
        if (message != null) {
            int strSize = message.length();
            int checkLength = oldWord.length();
            int checkIndex = 0;
            int k = 0;
            while(k<strSize) {
                checkIndex = message.indexOf(oldWord, k);
                if (checkIndex == -1) break;

                k = checkIndex + checkLength;
                if (k >= strSize) break;

                if (checkIndex == 0) {
                    message = newWord + message.substring(k);
                }
                else {
                    char c = message.charAt(checkIndex-1);
                    if (validChars.indexOf(c) == -1) {
                        message = message.substring(0, checkIndex) + newWord + message.substring(k);
                    }
                }

                strSize = message.length();
            }
        }

        return message;
    }

    //token must a string that starts with ?
    protected String getNameFromToken(int positionIndex, String token) {
        if (token == null || !token.startsWith("?")) return "";

        String name = "";

        if ("?".equals(token)) {
            name =  String.valueOf(positionIndex);
        }
        else if (token.length() > 1) {
            if (token.indexOf(':') != -1) {
                name = token.substring(1, token.indexOf(':'));
            }
            else {
                name = token.substring(1);
            }
        }

        return name;
    }

    //token must a string that starts with ?
    protected int getInlineSqlDataTypeFromToken(String token) {
        int sqlDataType = Parameter.UNKNOWN_SQL_DATA_TYPE;

        if (token.length() > 1 && token.indexOf(':') != -1) {
            String typeName = token.substring(token.indexOf(':') + 1);
            sqlDataType = SqlUtil.getSqlDataTypeFromDataTypeName(typeName);
        }

        return sqlDataType;
    }

    protected LogUtil log = LogUtil.getLogger(this.getClass().getName());
}
