package blog.controllers;

import static com.scooterframework.web.controller.ActionControl.*;

import com.scooterframework.admin.Constants;
import com.scooterframework.admin.FilterManagerFactory;
import com.scooterframework.orm.activerecord.ActiveRecord;
import com.scooterframework.orm.misc.Paginator;
import com.scooterframework.web.util.R;

import blog.models.Ttt;

/**
 * TttsController class handles ttts related access.
 */
public class TttsController extends ApplicationController {

    /**
     * Constructor
     */
    public TttsController() {
    }

    /**
     * <tt>index</tt> method returns a list of <tt>ttts</tt> records.
     * If the value of <tt>paged</tt> parameter is <tt>true</tt>, a paginated list is returned.
     */
    public String index() {
        if ("true".equals(p(Constants.PAGED))) {
            Paginator page = jdbcPaginator(Ttt.class, params());
            setViewData("ttt_page", page);
            return renderView("paged_list");
        }
        setViewData("ttts", Ttt.findAll());
        return null;
    }

    /**
     * <tt>show</tt> method returns a <tt>ttt</tt> record.
     */
    public String show() {
        ActiveRecord ttt = Ttt.findByPK(p("id"));
        if (ttt == null) {
            flash("notice", "There is no ttt record with primary key id as " + p("id") + ".");
        }
        else {
            setViewData("ttt", ttt);
        }
        System.out.println("XXXXXXXXXXXX ttt:" + ttt.getRowInfo());
        return null;
    }

    /**
     * <tt>add</tt> method prepares meta data for adding a new <tt>ttt</tt> record.
     */
    public String add() {
        setViewData("ttt", Ttt.newRecord());
        return null;
    }

    /**
     * <tt>create</tt> method creates a new <tt>ttt</tt> record.
     */
    public String create() {
        ActiveRecord newTtt = null;
        try {
            newTtt = Ttt.newRecord();
            newTtt.setData(params());
            newTtt.save();
            flash("notice", "Ttt was successfully created.");

            return redirectTo(R.resourcePath("ttts"));
        }
        catch(Exception ex) {
            log.error("Error in create() caused by " + ex.getMessage());
            flash("error", "There was a problem creating the ttt record.");
        }

        setViewData("ttt", newTtt);
        return renderView("add");
    }

    /**
     * <tt>edit</tt> method prepares data for editing an existing <tt>ttt</tt> record.
     */
    public String edit() {
        return show();
    }

    /**
     * <tt>update</tt> method updates an existing <tt>ttt</tt> record.
     */
    public String update() {
        ActiveRecord ttt = null;
        try {
            ttt = Ttt.findByPK(p("id"));
            if (ttt != null) {
                ttt.setData(params());
                ttt.update();
                flash("notice", "Ttt was successfully updated.");

                return redirectTo(R.resourceRecordPath("ttts", ttt));
            }
            else {
                flash("notice", "There is no ttt record with primary key id as " + p("id") + ".");
            }
        }
        catch(Exception ex) {
            log.error("Error in update() caused by " + ex.getMessage());
            flash("error", "There was a problem updating the ttt record.");
        }

        setViewData("ttt", ttt);
        return renderView("edit");
    }

    /**
     * <tt>delete</tt> method deletes a <tt>ttt</tt> record.
     */
    public String delete() {
        ActiveRecord ttt = Ttt.findByPK(p("id"));
        if (ttt != null) {
            ttt.delete();
            flash("notice", "Ttt was successfully deleted.");
        }
        else {
            flash("notice", "There is no ttt record with primary key id as " + p("id") + ".");
        }

        return redirectTo(R.resourcePath("ttts"));
    }
}
