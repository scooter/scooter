/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.test;

import com.scooterframework.orm.activerecord.ActiveRecord;
import com.scooterframework.orm.activerecord.ActiveRecordUtil;
import com.scooterframework.test.models.Owner;
import com.scooterframework.test.models.Pet;
import com.scooterframework.test.models.Specialty;
import com.scooterframework.test.models.Type;
import com.scooterframework.test.models.Vet;
import com.scooterframework.test.models.VetSpecialty;
import com.scooterframework.test.models.Visit;

/**
 * We use petclinic as a data store for the test.
 * 
 * @author (Fei) John Chen
 */
public class ScooterApplicationTest extends ApplicationTest {
	
	//
	//pet clinic related
	//
	protected ActiveRecord vetHome = null;
	protected ActiveRecord specialtyHome = null;
	protected ActiveRecord vetSpecialtyHome = null;
	protected ActiveRecord ownerHome = null;
	protected ActiveRecord petHome = null;
	protected ActiveRecord visitHome = null;
	protected ActiveRecord typeHome = null;
	
	protected void setUp() {
		super.setUp();
		
		if (vetHome == null) vetHome = ActiveRecordUtil.getHomeInstance(Vet.class);
		if (specialtyHome == null) specialtyHome = ActiveRecordUtil.getHomeInstance(Specialty.class);
		if (vetSpecialtyHome == null) vetSpecialtyHome = ActiveRecordUtil.getHomeInstance(VetSpecialty.class);
		if (ownerHome == null) ownerHome = ActiveRecordUtil.getHomeInstance(Owner.class);
		if (petHome == null) petHome = ActiveRecordUtil.getHomeInstance(Pet.class);
		if (visitHome == null) visitHome = ActiveRecordUtil.getHomeInstance(Visit.class);
		if (typeHome == null) typeHome = ActiveRecordUtil.getHomeInstance(Type.class);
	}
}
