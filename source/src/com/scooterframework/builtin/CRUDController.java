/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.builtin;

import static com.scooterframework.web.controller.ActionControl.*;

import java.util.List;
import java.util.Map;

import com.scooterframework.admin.Constants;
import com.scooterframework.admin.EnvConfig;
import com.scooterframework.common.logging.LogUtil;
import com.scooterframework.common.util.Converters;
import com.scooterframework.orm.activerecord.ActiveRecord;
import com.scooterframework.orm.activerecord.ActiveRecordUtil;
import com.scooterframework.orm.misc.JdbcPageListSource;
import com.scooterframework.orm.misc.Paginator;
import com.scooterframework.web.controller.ACH;
import com.scooterframework.web.controller.ActionResult;

/**
 * CRUDController class serves as default controller class for a model.
 * 
 * @author (Fei) John Chen
 */
public class CRUDController {

    public String index() {
        return ("true".equals(params(Constants.PAGED)))?paged_list():list();
    }
    
    public String list() {
        String model = getModel();
        ActiveRecord recordHome = generateActiveRecordHomeInstance(model);
        List<ActiveRecord> recordList = ActiveRecordUtil.getGateway(recordHome).findAll(ACH.getAC().getParameterDataAsMap());
        setViewData(model + "_list", recordList);
        return null;
    }
    
    public String paged_list() {
        String model = getModel();
        ActiveRecord recordHome = generateActiveRecordHomeInstance(model);
        Map<String, Object> requestParametersMap = ACH.getAC().getParameterDataAsMap();
        Paginator page = new Paginator(new JdbcPageListSource(recordHome.getClass()), requestParametersMap);
        setViewData("paged_" + model + "_list", page);
        return ActionResult.forwardTo(viewPath("paged_list"));
    }
    
    public String show() {
        String model = getModel();
        ActiveRecord recordHome = generateActiveRecordHomeInstance(model);
        Map<String, Object> pkDataMap = ACH.getAC().retrievePrimaryKeyDataMapFromRequest(recordHome.getPrimaryKeyNames());
        ActiveRecord record = ActiveRecordUtil.getGateway(recordHome).findFirst(pkDataMap);
        if (record == null) {
            flash("notice", "There is no " + model + 
            " record represented by key \"" + pkDataMap + "\".");
        }
        else {
            setViewData(model, record);
        }
        return null;
    }

    public String add() {
        String model = getModel();
        ActiveRecord recordHome = generateActiveRecordHomeInstance(model);
        setViewData(model, recordHome);
        return null;
    }

    public String create() {
        ActiveRecord newRecord = null;
        String model = getModel();
        try {
            @SuppressWarnings("unused")
			ActiveRecord recordHome = generateActiveRecordHomeInstance(model);
            newRecord = generateActiveRecordInstance(model);
            Map<String, Object> requestParameters = ACH.getAC().getParameterDataAsMap();
            newRecord.setData(requestParameters);
            newRecord.save();
            flash("notice", "Successfully created a new " + model + " record.");
            return ActionResult.redirectTo(actionPath("index"));
        }
        catch(Exception ex) {
            log.error("Error in create() caused by " + ex.getMessage());
            setViewData(model, newRecord);
            flash("error", "There was a problem creating the " + model + " record.");
        }
        
        setViewData(model, newRecord);
        return ActionResult.forwardTo(viewPath("add"));
    }
    
    public String edit() {
        return show();
    }
    
    public String update() {
        ActiveRecord record = null;
        String model = getModel();
        Map<String, Object> pkDataMap =  null;
        try {
            ActiveRecord recordHome = generateActiveRecordHomeInstance(model);
            Map<String, Object> requestParameters = ACH.getAC().getParameterDataAsMap();
            pkDataMap = ACH.getAC().retrievePrimaryKeyDataMapFromRequest(recordHome.getPrimaryKeyNames());
            record = ActiveRecordUtil.getGateway(recordHome).findFirst(pkDataMap);
            if (record != null) {
                record.setData(requestParameters);
                record.update();
                flash("notice", "Successfully updated the " + model + 
                " record represented by key \"" + pkDataMap + "\".");
                return ActionResult.redirectTo(actionPath("show", record));
            }
            else {
                flash("notice", "There is no " + model + 
                " record represented by key \"" + pkDataMap + "\".");
            }
        }
        catch(Exception ex) {
            log.error("Error in update() caused by " + ex.getMessage());
            setViewData(model, record);
            flash("error", "There was a problem updating the " + model + 
            " record represented by key \"" + pkDataMap + "\".");
        }
        
       setViewData(model, record);
        return ActionResult.forwardTo(viewPath("edit"));
    }
    
    public String delete() {
        String model = getModel();
        ActiveRecord recordHome = generateActiveRecordHomeInstance(model);
        Map<String, Object> pkDataMap = ACH.getAC().retrievePrimaryKeyDataMapFromRequest(recordHome.getPrimaryKeyNames());
        ActiveRecord record = ActiveRecordUtil.getGateway(recordHome).findFirst(pkDataMap);
        if (record != null) {
            int count = record.delete();
            if (count == 1) {
                flash("notice", "Successfully deleted the " + model + 
                " record represented by key \"" + pkDataMap + "\".");
            }
            else if (count < 1) {
                flash("notice", "Record represented by key \"" + 
                pkDataMap + "\" was found but not deleted.");
            }
            else if (count > 1) {
                flash("error", "More than one record was deleted related to " + 
                "key \"" + pkDataMap + "\" was found but not deleted.");
            }
        }
        else {
            flash("notice", "There is no " + model + 
            " record represented by key \"" + pkDataMap + "\".");
        }
        return ActionResult.redirectTo(actionPath("index"));
    }


    /**
     * Generates an ActiveRecord instance of a model. If the class corresponding 
     * to the model does not exist, then use the <tt>DEFAULT_RECORD_CLASS</tt> 
     * class type for the <tt>model</tt>.
     * 
     * @param model model name
     * @return an ActiveRecord instance of the model
     */
    protected ActiveRecord generateActiveRecordInstance(String model) {
        ActiveRecord record = null;
        try {
            record = ActiveRecordUtil.generateActiveRecordInstance(EnvConfig.getInstance().getModelClassName(model), model);
        } catch (Exception ex) {
            record = ActiveRecordUtil.generateActiveRecordInstance(DEFAULT_RECORD_CLASS, model);
        }
        return record;
    }
    
    /**
     * Generates an ActiveRecord home instance of a model. 
     * See description of {@link #generateActiveRecordInstance(java.lang.String)} method.
     * 
     * @param model model name
     * @return an ActiveRecord home instance of the model
     */
    protected ActiveRecord generateActiveRecordHomeInstance(String model) {
        ActiveRecord record = generateActiveRecordInstance(model);
        ActiveRecordUtil.setHomeInstance(record);
        return record;
    }
    
    /**
     * Returns url path to the action. See {@link com.scooterframework.admin.EnvConfig#getActionUriFor(String)} 
     * for more details.
     */
    protected String actionPath(String action) {
        return EnvConfig.getActionUriFor(action);
    }
    
    /**
     * Returns url path to the action for a record. See {@link com.scooterframework.admin.EnvConfig#getActionUriFor(String)} 
     * for more details. Primary key values of the record are converted to 
     * a query string appended in the path.
     */
    protected String actionPath(String action, ActiveRecord record) {
        Map<String, Object> pkDataMap = record.getPrimaryKeyDataMap();
        String nameValuePairs = Converters.convertMapToUrlString(pkDataMap);
        if (nameValuePairs == null || "".equals(nameValuePairs)) {
            return actionPath(action);
        }
        return actionPath(action) + "?" + nameValuePairs;
    }
    
    /**
     * Returns url path to the view named "<tt>action</tt>".
     */
    protected String viewPath(String action) {
        return EnvConfig.getViewURI(getController(), action, getDefaultViewFilesDirectoryName());
    }
    
    /**
     * Returns default view file directory name. 
     * 
     * @return default view file directory name. 
     */
    protected String getDefaultViewFilesDirectoryName() {
        return EnvConfig.getInstance().getDefaultViewFilesDirectory();
    }
    
    public static final String DEFAULT_RECORD_CLASS = "com.scooterframework.orm.activerecord.ActiveRecord";
    
    protected LogUtil log = LogUtil.getLogger(getClass().getName());
}
