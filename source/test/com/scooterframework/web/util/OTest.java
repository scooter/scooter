/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.web.util;

import java.util.List;

import com.scooterframework.orm.activerecord.ActiveRecord;
import com.scooterframework.test.ScooterApplicationTest;
import com.scooterframework.web.controller.ACH;

/**
 * OTest class
 * 
 * @author (Fei) John Chen
 *
 */
public class OTest extends ScooterApplicationTest {
	
	public void test_allAssociatedRecordsOf() {
		ActiveRecord owner6 = ownerHome.findFirst("id=6");
		List records = O.allAssociatedRecordsOf(owner6, "pets.visits");
		assertEquals("total visits of owner #6", 4, records.size());
	}
	
	public void test_allAssociatedRecordsOf_eager_loading() {
		ActiveRecord owner6 = ownerHome.findFirst("id=6", "include: pets=>visits");
		List records = O.allAssociatedRecordsOf(owner6, "pets.visits");
		assertEquals("total visits of owner #6", 4, records.size());
	}
	
	public void test_associatedRecordOf() {
		ActiveRecord visit1 = visitHome.findFirst("id=1");
		String firstName = (String)O.associatedRecordOf(visit1, "pet.owner").getField("first_name");
		assertEquals("first name of owner of visit#1", "Jean", firstName);
	}
	
	public void test_associatedRecordOf_eager_loading() {
		ActiveRecord visit1 = visitHome.findFirst("id=1", "include: pet=>owner");
		String firstName = (String)O.associatedRecordOf(visit1, "pet.owner").getField("first_name");
		assertEquals("first name of owner of visit#1", "Jean", firstName);
	}
	
	public void test_property() {
		ActiveRecord visit1 = visitHome.findFirst("id=1");
		String firstName = (String)O.property(visit1, "pet.owner.first_name");
		assertEquals("first name of owner of visit#1", "Jean", firstName);
	}
	
	public void test_property_eager_loading() {
		ActiveRecord visit1 = visitHome.findFirst("id=1", "include: pet=>owner");
		String firstName = (String)O.property(visit1, "pet.owner.first_name");
		assertEquals("first name of owner of visit#1", "Jean", firstName);
	}
	
	public void test_value() {
		ActiveRecord visit1 = visitHome.findFirst("id=1");
		ACH.getAC().storeToRequest("visit1", visit1);
		String firstName = (String)O.value("visit1.pet.owner.first_name");
		assertEquals("first name of owner of visit#1", "Jean", firstName);
	}
	
	public void test_value_eager_loading() {
		ActiveRecord visit1 = visitHome.findFirst("id=1", "include: pet=>owner");
		ACH.getAC().storeToRequest("visit1", visit1);
		String firstName = (String)O.value("visit1.pet.owner.first_name");
		assertEquals("first name of owner of visit#1", "Jean", firstName);
	}
}
