/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package petclinic.controllers;

import static com.scooterframework.web.controller.ActionControl.*;

import java.util.List;

import petclinic.models.Owner;

import com.scooterframework.admin.Constants;
import com.scooterframework.orm.activerecord.ActiveRecord;
import com.scooterframework.orm.misc.Paginator;
import com.scooterframework.web.util.R;

/**
 * OwnersController class handles owners related access.
 */
public class OwnersController extends ApplicationController {

    /**
     * <tt>search</tt> method returns found owners.
     */
    public String search() {
    	String lastName = p("last_name");
        List owners = (lastName == null || "".equals(lastName))?
                Owner.findAll((String)null, "include:pets"):
        		Owner.findAll("last_name='" + lastName + "'", "include:pets");

        if (owners != null) {
        	if (owners.size() > 1) {
        		setViewData("owners", owners);
        		return renderView("index");
        	}
        	else if (owners.size() == 1) {
        		//1 owner found
        		ActiveRecord owner = (ActiveRecord)owners.iterator().next();
        		setViewData("owner", owner);
        		return redirectTo(R.resourceRecordPath("owners", owner));
        	}
        }

        flash("notice", "No owner found.");
        return redirectTo("/findOwners");
    }

    /**
     * <tt>index</tt> method returns a list of <tt>owners</tt> records.
     * If the value of <tt>paged</tt> parameter is <tt>true</tt>, a paginated list is returned.
     */
    public String index() {
        if ("true".equals(p(Constants.PAGED))) {
            Paginator page = jdbcPaginator(Owner.class, params());
            setViewData("owner_page", page);
            return renderView("paged_list");
        }
        setViewData("owners", Owner.findAll((String)null, "include:pets"));
        return null;
    }

    /**
     * <tt>show</tt> method returns a <tt>owner</tt> record.
     */
    public String show() {
        ActiveRecord owner = Owner.findFirst("owners.id=" + p("id"), "include:pets=>visits, pets=>type");
        if (owner == null) {
            flash("notice", "There is no owner record with primary key as " + p("id"));
        }
        else {
            setViewData("owner", owner);
        }
        return null;
    }

    /**
     * <tt>add</tt> method prepares meta data for adding a new <tt>owner</tt> record.
     */
    public String add() {
        setViewData("owner", Owner.newRecord());
        return null;
    }

    /**
     * <tt>create</tt> method creates a new <tt>owner</tt> record.
     */
    public String create() {
        ActiveRecord newOwner = null;
        try {
            newOwner = Owner.newRecord();
            newOwner.setData(params());
            newOwner.save();
            flash("notice", "Owner was successfully created.");

            return redirectTo(R.resourcePath("owners"));
        }
        catch(Exception ex) {
            log.error("Error in create() caused by " + ex.getMessage());
            flash("error", "There was a problem creating the owner record.");
        }

        setViewData("owner", newOwner);
        return renderView("add");
    }

    /**
     * <tt>edit</tt> method prepares data for editing an existing <tt>owner</tt> record.
     */
    public String edit() {
    	ActiveRecord owner = Owner.findFirst("id=" + p("id"));
        if (owner == null) {
            flash("notice", "There is no owner record with primary key as " + p("id"));
        }
        else {
            setViewData("owner", owner);
        }
        return null;
    }

    /**
     * <tt>update</tt> method updates an existing <tt>owner</tt> record.
     */
    public String update() {
        ActiveRecord owner = null;
        try {
            owner = Owner.findFirst("id=" + p("id"));
            if (owner != null) {
                owner.setData(params());
                owner.update();
                flash("notice", "Owner was successfully updated.");

                return redirectTo(R.resourceRecordPath("owners", owner));
            }
            else {
                flash("notice", "There is no owner record with primary key as " + p("id") + ".");
            }
        }
        catch(Exception ex) {
            log.error("Error in update() caused by " + ex.getMessage());
            flash("error", "There was a problem updating the owner record.");
        }

        setViewData("owner", owner);
        return renderView("edit");
    }
}