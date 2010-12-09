/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package petclinic.controllers;

import static com.scooterframework.web.controller.ActionControl.*;

import petclinic.models.Vet;

/**
 * VetsController class handles vets related access.
 */
public class VetsController extends ApplicationController {

    /**
     * <tt>index</tt> method returns a list of <tt>vets</tt> records.
     * If the value of <tt>paged</tt> parameter is <tt>true</tt>, a paginated list is returned.
     */
    public String index() {
        setViewData("vets", Vet.findAll("", "include:specialties; order_by: vets.last_name"));
        return null;
    }
}