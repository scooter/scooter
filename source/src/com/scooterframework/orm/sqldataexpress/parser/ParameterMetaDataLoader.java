/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.parser;

import java.sql.ParameterMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.StringTokenizer;

import com.scooterframework.orm.sqldataexpress.exception.BaseSQLException;
import com.scooterframework.orm.sqldataexpress.object.JdbcStatement;
import com.scooterframework.orm.sqldataexpress.object.JdbcStatementParameter;
import com.scooterframework.orm.sqldataexpress.object.Parameter;

/**
 * ParameterMetaDataLoader class.
 * 
 * @author (Fei) John Chen
 */
public class ParameterMetaDataLoader extends JdbcStatementHelper {
    public ParameterMetaDataLoader(ParameterMetaData pmd, JdbcStatement st) {
        this.pmd = pmd;
        this.st = st;
        
        parseJdbcStatementParameters();
    }
    
    /**
     * load some parameter properties from ParameterMetaData. 
     * 
     * Do not use this method if the underlying database driver does not support 
     * ParameterMetaData feature, such as Oracle.
     */
    public void loadParameterMetaData() {
        try {
            int pcount = pmd.getParameterCount();
            List<Parameter> parameters = st.getParameters();
            if (pcount != parameters.size()) 
                throw new BaseSQLException("ParameterMetaData size is " + pcount + 
                " while statement parameters size is " + parameters.size() + ".");
            
			for (int i = 1; i <= pcount; i++) {
                Parameter p = parameters.get(i-1);
                
                log.debug("i=" + i);
                log.debug("getParameterClassName : " + pmd.getParameterClassName(i));
                log.debug("getParameterMode : " + pmd.getParameterMode(i));
                log.debug("getParameterType : " + pmd.getParameterType(i));
                log.debug("getParameterTypeName : " + pmd.getParameterTypeName(i));
                log.debug("getPrecision : " + pmd.getPrecision(i));
                log.debug("getScale : " + pmd.getScale(i));
                log.debug("isNullable : " + pmd.isNullable(i));
                log.debug("isSigned : " + pmd.isSigned(i));
                //log.debug("parameterModeIn : " + pmd.parameterModeIn);
                //log.debug("parameterModeInOut : " + pmd.parameterModeInOut);
                //log.debug("parameterModeOut : " + pmd.parameterModeOut);
                //log.debug("parameterModeUnknown : " + pmd.parameterModeUnknown);
                //log.debug("parameterNoNulls : " + pmd.parameterNoNulls);
                //log.debug("parameterNullable : " + pmd.parameterNullable);
                //log.debug("parameterNullableUnknown : " + pmd.parameterNullableUnknown);
                
                p.setJavaClassName(pmd.getParameterClassName(i));
                p.setSqlDataType(pmd.getParameterType(i));
                p.setSqlDataTypeName(pmd.getParameterTypeName(i));
            }
            
            st.setLoadedParameterMetaData(true);
        }
        catch(SQLException ex) {
            log.error("Error loading parametermetadata: " + ex.getMessage());
        }
    }
    
    /**
     * counts parameters (? marks)
     */
    private void parseJdbcStatementParameters() {
        if (st == null) return;
        
        String jdbcStatementString = st.getOriginalJdbcStatementString();
        
        //no need to parse if there is no dynamic parameter
        if (jdbcStatementString.indexOf('?') == -1) {
            return;
        }
        
        //if ( jdbcStatementString.startsWith("INSERT") && 
        //     jdbcStatementString.indexOf("SELECT") == -1 ) 
        //{
            // This is a pure insert statement.
        //    parseInsertStatement();
        //    return;
        //}
        
        //
        // find all strings starting with ?
        //
        // step 1: convert the alias to regular table names
        String sql = jdbcStatementString;
        String modifiedSql = resetAlias(sql);
        
        //StringTokenizer sti = new StringTokenizer(modifiedSql, " ,><=(){}");
        StringTokenizer sti = new StringTokenizer(modifiedSql, " ,|><=(){}+-*/");
        
        int totalTokens = sti.countTokens();
        String[] tokens = new String[totalTokens];
        
        int i = 0;
        while(sti.hasMoreTokens()) {
            tokens[i] = sti.nextToken();
            i = i + 1;
        }
        
        // step 2: get the column name for each question mark
        int qmarkIndex = 1;
        for ( int j = 0; j < totalTokens; j++ ) {
            String token = tokens[j];
            
            if (token.startsWith("?")) {
                JdbcStatementParameter param = new JdbcStatementParameter();
                param.setIndex(qmarkIndex);
                param.setName(getNameFromToken(qmarkIndex, token));
                st.addParameter(param);
                
                qmarkIndex = qmarkIndex + 1;
            }
        }
    }
    
    private ParameterMetaData pmd;
    private JdbcStatement st;
}
