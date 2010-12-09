/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package petclinic.controllers;

import static com.scooterframework.web.controller.ActionControl.*;

import petclinic.models.Owner;
import petclinic.models.Pet;
import petclinic.models.Type;

import com.scooterframework.admin.Constants;
import com.scooterframework.orm.activerecord.ActiveRecord;
import com.scooterframework.orm.misc.Paginator;
import com.scooterframework.web.util.R;

/**
 * PetsController class handles pets related access.
 */
public class PetsController extends ApplicationController {
	static {
		filterManagerFor(PetsController.class).declareBeforeFilter("loadOwner", "only", "add, create");
		filterManagerFor(PetsController.class).declareBeforeFilter("loadTypes", "only", "add, edit");
	}

	public void loadOwner() {
		setViewData("owner", Owner.findFirst("id=" + p("owner_id")));
	}

	public void loadTypes() {
		setViewData("types", Type.findAll());
	}

    /**
     * <tt>index</tt> method returns a list of <tt>pets</tt> records.
     * If the value of <tt>paged</tt> parameter is <tt>true</tt>, a paginated list is returned.
     */
    public String index() {
        if ("true".equals(params(Constants.PAGED))) {
            Paginator page = jdbcPaginator(Pet.class, params());
            storeToRequest("pet_page", page);
            return forwardTo(viewPath("paged_list"));
        }
        storeToRequest("pets", Pet.findAll());
        return null;
    }

    /**
     * <tt>show</tt> method returns a <tt>pet</tt> record.
     */
    public String show() {
        ActiveRecord pet = Pet.findFirst("id=" + p("id"), "include:owner");
        if (pet == null) {
            flash("notice", "There is no pet record with primary key as " + p("id"));
        }
        else {
            setViewData("pet", pet);
        }
        return null;
    }

    /**
     * <tt>add</tt> method prepares meta data for adding a new <tt>pet</tt> record.
     */
    public String add() {
        setViewData("pet", Pet.newRecord());
        return null;
    }

    /**
     * <tt>create</tt> method creates a new <tt>pet</tt> record.
     */
    public String create() {
    	ActiveRecord owner = (ActiveRecord)getFromRequestData("owner");
        ActiveRecord newPet = null;
        try {
            newPet = Pet.newRecord();
            newPet.setData(params());
            newPet.save();
            flash("notice", "Pet was successfully created.");

            return redirectTo(R.resourceRecordPath("owners", owner));
        }
        catch(Exception ex) {
            log.error("Error in create() caused by " + ex.getMessage());
            flash("error", "There was a problem creating the pet record.");
        }

        storeToRequest("pet", newPet);
        return forwardTo(viewPath("add"));
    }

    /**
     * <tt>edit</tt> method prepares data for editing an existing <tt>pet</tt> record.
     */
    public String edit() {
        return show();
    }

    /**
     * <tt>update</tt> method updates an existing <tt>pet</tt> record.
     */
    public String update() {
        ActiveRecord pet = null;
        try {
            pet = Pet.findFirst("id=" + p("id"), "include:owner");
            if (pet != null) {
                pet.setData(params());
                pet.update();
                flash("notice", "Pet was successfully updated.");

                return redirectTo(R.resourceRecordPath("owners", pet.associated("owner").getRecord()));
            }
            else {
                flash("notice", "There is no pet record with primary key as " + p("id") + ".");
            }
        }
        catch(Exception ex) {
            log.error("Error in update() caused by " + ex.getMessage());
            flash("error", "There was a problem updating the pet record.");
        }

        storeToRequest("pet", pet);
        return forwardTo(viewPath("edit"));
    }
}