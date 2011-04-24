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
import petclinic.models.Visit;

import com.scooterframework.orm.activerecord.ActiveRecord;
import com.scooterframework.web.util.R;

/**
 * VisitsController class handles visits related access.
 */
public class VisitsController extends ApplicationController {

    /**
     * <tt>add</tt> method prepares meta data for adding a new <tt>visit</tt> record.
     */
    public String add() {
    	setViewData("pet", Pet.where("pets.id=" + p("pet_id")).includes("owner, type").getRecord());
        setViewData("visits", Visit.where("pet_id=" + p("pet_id")).getRecords());
        return null;
    }

    /**
     * <tt>create</tt> method creates a new <tt>visit</tt> record.
     */
    public String create() {
    	ActiveRecord owner = Owner.where("id=" + p("owner_id")).getRecord();
        ActiveRecord newVisit = null;
        try {
            newVisit = Visit.newRecord();
            newVisit.setData(params());
            newVisit.save();
            flash("notice", "Visit was successfully created.");

            return redirectTo(R.resourceRecordPath("owners", owner));
        }
        catch(Exception ex) {
            log.error("Error in create() caused by " + ex.getMessage());
            flash("error", "There was a problem creating the visit record.");
        }

        setViewData("visit", newVisit);
        return renderView("add");
    }
}