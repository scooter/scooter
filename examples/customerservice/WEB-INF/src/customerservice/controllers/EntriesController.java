package customerservice.controllers;

import static com.scooterframework.web.controller.ActionControl.*;

import com.scooterframework.admin.Constants;
import com.scooterframework.admin.FilterManagerFactory;
import com.scooterframework.orm.activerecord.ActiveRecord;
import com.scooterframework.orm.misc.Paginator;
import com.scooterframework.web.util.R;

import customerservice.models.Entry;

/**
 * EntriesController class handles entries related access.
 */
public class EntriesController extends ApplicationController {

    static {
        filterManagerFor(EntriesController.class).declareBeforeFilter(
            SignonController.class, "loginRequired");
    }

    /**
     * Constructor
     */
    public EntriesController() {
    }

    /**
     * <tt>index</tt> method returns a list of <tt>entries</tt> records.
     * If the value of <tt>paged</tt> parameter is <tt>true</tt>, a paginated list is returned.
     */
    public String index() {
        if ("true".equals(p(Constants.PAGED))) {
            Paginator page = jdbcPaginator(Entry.class, params());
            setViewData("entry_page", page);
            return renderView("paged_list");
        }
        setViewData("entries", Entry.findAll());
        return null;
    }

    /**
     * <tt>show</tt> method returns a <tt>entry</tt> record.
     */
    public String show() {
        ActiveRecord entry = Entry.findById(p("id"));
        if (entry == null) {
            flash("notice", "There is no entry record with primary key id as " + p("id") + ".");
        }
        else {
            setViewData("entry", entry);
        }
        return null;
    }

    /**
     * <tt>add</tt> method prepares meta data for adding a new <tt>entry</tt> record.
     */
    public String add() {
        setViewData("entry", Entry.newRecord());
        return null;
    }

    /**
     * <tt>create</tt> method creates a new <tt>entry</tt> record.
     */
    public String create() {
        ActiveRecord newEntry = null;
        try {
            newEntry = Entry.newRecord();
            newEntry.setData(params());
            newEntry.save();
            flash("notice", "Entry was successfully created.");

            return redirectTo(R.resourcePath("entries"));
        }
        catch(Exception ex) {
            log.error("Error in create() caused by " + ex.getMessage());
            flash("error", "There was a problem creating the entry record.");
        }

        setViewData("entry", newEntry);
        return renderView("add");
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
            entry = Entry.findById(p("id"));
            if (entry != null) {
                entry.setData(params());
                entry.update();
                flash("notice", "Entry was successfully updated.");

                return redirectTo(R.resourceRecordPath("entries", entry));
            }
            else {
                flash("notice", "There is no entry record with primary key id as " + p("id") + ".");
            }
        }
        catch(Exception ex) {
            log.error("Error in update() caused by " + ex.getMessage());
            flash("error", "There was a problem updating the entry record.");
        }

        setViewData("entry", entry);
        return renderView("edit");
    }

    /**
     * <tt>delete</tt> method deletes a <tt>entry</tt> record.
     */
    public String delete() {
        ActiveRecord entry = Entry.findById(p("id"));
        if (entry != null) {
            entry.delete();
            flash("notice", "Entry was successfully deleted.");
        }
        else {
            flash("notice", "There is no entry record with primary key id as " + p("id") + ".");
        }

        return redirectTo(R.resourcePath("entries"));
    }
}
