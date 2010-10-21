package customerservice.controllers;

import com.scooterframework.admin.Constants;
import com.scooterframework.orm.activerecord.ActiveRecord;
import com.scooterframework.orm.misc.Paginator;
import com.scooterframework.web.util.R;

import customerservice.models.Entry;

/**
 * EntriesController class handles entries related access.
 */
public class EntriesController extends ApplicationController {
	public void registerFilters() {
		super.beforeFilter(SignonController.class, "loginRequired");
	}

    /**
     * <tt>index</tt> method returns a list of <tt>entries</tt> records.
     * If the value of <tt>paged</tt> parameter is <tt>true</tt>, a paginated list is returned.
     */
    public String index() {
        if ("true".equals(params(Constants.PAGED))) {
            Paginator page = jdbcPaginator(Entry.class, params());
            storeToRequest("entry_page", page);
            return forwardTo(viewPath("paged_list"));
        }
        storeToRequest("entries", findAll(Entry.class));
        return null;
    }
    
    /**
     * <tt>show</tt> method returns a <tt>entry</tt> record.
     */
    public String show() {
        ActiveRecord entry = findRecordByPrimaryKey(Entry.class, pkparams(Entry.class));
        if (entry == null) {
            flash("notice", "There is no entry record with primary key as " + super.pkparams(Entry.class));
        }
        else {
            storeToRequest("entry", entry);
        }
        return null;
    }
    
    /**
     * <tt>add</tt> method prepares meta data for adding a new <tt>entry</tt> record.
     */
    public String add() {
        storeToRequest("entry", homeInstance(Entry.class));
        return null;
    }
    
    /**
     * <tt>create</tt> method creates a new <tt>entry</tt> record.
     */
    public String create() {
        ActiveRecord newEntry = null;
        try {
            newEntry = newRecord(Entry.class, params());
            newEntry.save();
            flash("notice", "Entry was successfully created.");
            
            return redirectTo(R.resourcePath("entries"));
        }
        catch(Exception ex) {
            log.error("Error in create() caused by " + ex.getMessage());
            flash("error", "There was a problem creating the entry record.");
        }
        
        storeToRequest("entry", newEntry);
        return forwardTo(viewPath("add"));
    }
    
    /**
     * <tt>edit</tt> method prepares data for editing an existing <tt>entry</tt> record.
     */
    public String edit() {
        return show();
    }
    
    /**
     * <tt>update</tt> method updates an existing <tt>entry</tt> record.
     */
    public String update() {
        ActiveRecord entry = null;
        try {
            entry = findRecordByPrimaryKey(Entry.class, pkparams(Entry.class));
            if (entry != null) {
                entry.setData(params());
                entry.update();
                flash("notice", "Entry was successfully updated.");
                
                return redirectTo(R.resourceRecordPath("entries", entry));
            }
            else {
                flash("notice", "There is no entry record with primary key as " + pkparams(Entry.class) + ".");
            }
        }
        catch(Exception ex) {
            log.error("Error in update() caused by " + ex.getMessage());
            flash("error", "There was a problem updating the entry record.");
        }
        
        storeToRequest("entry", entry);
        return forwardTo(viewPath("edit"));
    }
    
    /**
     * <tt>delete</tt> method deletes a <tt>entry</tt> record.
     */
    public String delete() {
        ActiveRecord entry = findRecordByPrimaryKey(Entry.class, pkparams(Entry.class));
        if (entry != null) {
            entry.delete();
            flash("notice", "Entry was successfully deleted.");
        }
        else {
            flash("notice", "There is no entry record with primary key as " + pkparams(Entry.class) + ".");
        }
        
        return redirectTo(R.resourcePath("entries"));
    }

}