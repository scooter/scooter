/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.activerecord;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

import com.scooterframework.orm.sqldataexpress.service.SqlServiceClient;
import com.scooterframework.test.ScooterTestHelper;
import com.scooterframework.test.models.Owner;
import com.scooterframework.test.models.Pet;
import com.scooterframework.test.models.Specialty;
import com.scooterframework.test.models.Vet;
import com.scooterframework.test.models.VetSpecialty;
import com.scooterframework.test.models.Visit;
import com.scooterframework.web.util.O;

/**
 * Test relations.
 * 
 * @author (Fei) John Chen
 */
public class ActiveRecordRelationTest extends ScooterTestHelper {
	
	private Object getNextPetID() {
		String findNextID = "SELECT (max(id)+1) FROM pets";
		return SqlServiceClient.retrieveObjectBySQL(findNextID);
	}
	
	private Object getNextSpecialityID() {
		String findNextID = "SELECT (max(id)+1) FROM specialties";
		return SqlServiceClient.retrieveObjectBySQL(findNextID);
	}
	
	private Object getNextVisitID() {
		String findNextID = "SELECT (max(id)+1) FROM visits";
		return SqlServiceClient.retrieveObjectBySQL(findNextID);
	}
	
	@Test
	public void test_hasManyThrough_add_and_detach() {
		ActiveRecord linda = Vet.findFirst("first_name='Linda'");
		AssociatedRecords assocs = linda.allAssociated("specialties");
		assertEquals("total specialties for Linda", 2, assocs.size());
		assertEquals("total specialties before add", 3, Specialty.findAll().size());
		assertEquals("total vetSpecialties before add", 5, VetSpecialty.findAll().size());
		
		ActiveRecord newSpecialty = Specialty.newRecord();
		newSpecialty.setData("id", getNextSpecialityID());
		newSpecialty.setData("name", "Java");
		assocs.add(newSpecialty);
		
		assertEquals("total specialties for Linda after add", 3, assocs.size());
		assertEquals("total specialties after add", 4, Specialty.findAll().size());
		assertEquals("total vetSpecialties after add", 6, VetSpecialty.findAll().size());
		
		//should delete the joint record, but keep the newly added specialty
		assocs.detach(newSpecialty);
		assertEquals("total specialties for Linda after detach", 2, assocs.count());
		assertEquals("total specialties after detach", 4, Specialty.findAll().size());
		assertEquals("total vetSpecialties after detach", 5, VetSpecialty.findAll().size());
		
		newSpecialty.delete();
		assertEquals("total specialties after delete", 3, Specialty.findAll().size());
	}
	
	@Test
	public void test_hasManyThrough_add_and_delete() {
		ActiveRecord linda = Vet.findFirst("first_name='Linda'");
		AssociatedRecords assocs = linda.allAssociated("specialties");
		assertEquals("total specialties for Linda", 2, assocs.size());
		assertEquals("total specialties before add", 3, Specialty.findAll().size());
		assertEquals("total vetSpecialties before add", 5, VetSpecialty.findAll().size());

		ActiveRecord newSpecialty = Specialty.newRecord();
		newSpecialty.setData("id", getNextSpecialityID());
		newSpecialty.setData("name", "Php");
		assocs.add(newSpecialty);
		
		assertEquals("total specialties for Linda after add", 3, assocs.size());
		assertEquals("total specialties after add", 4, Specialty.findAll().size());
		assertEquals("total vetSpecialties after add", 6, VetSpecialty.findAll().size());
				
		assocs.delete(newSpecialty);
		assertEquals("total specialties for Linda after delete", 2, assocs.count());
		assertEquals("total specialties after delete", 3, Specialty.findAll().size());
		assertEquals("total vetSpecialties after delete", 5, VetSpecialty.findAll().size());
	}
	
	@Test
	public void test_hasMany_add_and_delete_child() {
		/**
		 * SELECT OWNERS.ID AS OWNERS_ID, OWNERS.FIRST_NAME AS OWNERS_FIRST_NAME, 
		 * OWNERS.LAST_NAME AS OWNERS_LAST_NAME, OWNERS.ADDRESS AS OWNERS_ADDRESS, 
		 * OWNERS.CITY AS OWNERS_CITY, OWNERS.TELEPHONE AS OWNERS_TELEPHONE, 
		 * PETS.ID AS PETS_ID, PETS.NAME AS PETS_NAME, PETS.BIRTH_DATE AS PETS_BIRTH_DATE, 
		 * PETS.TYPE_ID AS PETS_TYPE_ID, PETS.OWNER_ID AS PETS_OWNER_ID, 
		 * VISITS.ID AS VISITS_ID, VISITS.PET_ID AS VISITS_PET_ID, 
		 * VISITS.VISIT_DATE AS VISITS_VISIT_DATE, VISITS.DESCRIPTION AS VISITS_DESCRIPTION, 
		 * OWNERS_PETS.ID AS OWNERS_PETS_ID, OWNERS_PETS.NAME AS OWNERS_PETS_NAME, 
		 * OWNERS_PETS.BIRTH_DATE AS OWNERS_PETS_BIRTH_DATE, 
		 * OWNERS_PETS.TYPE_ID AS OWNERS_PETS_TYPE_ID, 
		 * OWNERS_PETS.OWNER_ID AS OWNERS_PETS_OWNER_ID, 
		 * TYPES.ID AS TYPES_ID, TYPES.NAME AS TYPES_NAME 
		 * FROM OWNERS LEFT OUTER JOIN PETS ON OWNERS.ID=PETS.OWNER_ID 
		 *             LEFT OUTER JOIN VISITS ON PETS.ID=VISITS.PET_ID 
		 *             LEFT OUTER JOIN PETS OWNERS_PETS ON OWNERS.ID=OWNERS_PETS.OWNER_ID 
		 *             LEFT OUTER JOIN TYPES ON OWNERS_PETS.TYPE_ID=TYPES.ID 
		 * WHERE OWNERS.ID = 6
		 */
		ActiveRecord owner6 = Owner.findFirst("id=6", "include:pets=>visits, pets=>type");
		assertEquals("first name of owner #6", "Jean", owner6.getField("first_name").toString());
		AssociatedRecords assocs = owner6.allAssociated("pets");
		assertEquals("total pets for owner #6 before add a pet in assocs", 2, assocs.size());
		assertEquals("total pets for owner #6 before add a pet", "2", ""+owner6.getField("pets_count"));
		assertEquals("total visits for owner #6 before add a pet", 4, O.allAssociatedRecordsOf(owner6, "pets.visits").size());
		
		ActiveRecord wonda = Pet.newRecord();
		wonda.setData("id", getNextPetID());
		wonda.setData("name", "wonda");
		wonda.setData("birth_date", new Date());
		wonda.setData("type_id", "5");//bird
		wonda.setData("owner_id", "6");
		
		/**
		 * INSERT INTO PETS (NAME, BIRTH_DATE, TYPE_ID, OWNER_ID) VALUES ('wonda', '2009-09-02', 5, 6)
		 * UPDATE OWNERS SET PETS_COUNT = 3 WHERE ID = 6
		 */
		//add the new pet to the owner
		assocs.add(wonda);
		assertEquals("total pets for owner #6 after add a pet in assocs", 3, assocs.size());
		assertEquals("total pets for owner #6 after add a pet", "3", ""+owner6.getField("pets_count"));
		
		/**
		 * SELECT VISITS.* FROM VISITS WHERE PET_ID = 14
		 */
		assertEquals("total visits for owner #6 after add a pet", 4, O.allAssociatedRecordsOf(owner6, "pets.visits").size());
		
		/**
		 * INSERT INTO VISITS (PET_ID, VISIT_DATE, DESCRIPTION) VALUES (14, null, 'visit 1 for wonda')
		 */
		//create a new visit for the new pet
		ActiveRecord wondaVisit = Visit.newRecord();
		wondaVisit.setData("id", getNextVisitID());
		wondaVisit.setData("pet_id", wonda.getField("id"));
		wondaVisit.setData("description", "visit 1 for wonda");
		wondaVisit.create();
		
		/**
		 * SELECT PETS.* FROM PETS WHERE OWNER_ID = 6
		 * SELECT VISITS.* FROM VISITS WHERE PET_ID = 7
		 * SELECT VISITS.* FROM VISITS WHERE PET_ID = 8
		 * SELECT VISITS.* FROM VISITS WHERE PET_ID = 14
		 */
		assertEquals("total visits for owner #6 after add a pet", 5, O.allAssociatedRecordsOf(owner6, "pets.visits", true).size());
		
		/**
		 * SELECT VISITS.* FROM VISITS WHERE PET_ID = 14
		 * DELETE FROM VISITS WHERE ID = 5
		 * DELETE FROM PETS WHERE ID = 14
		 * UPDATE OWNERS SET PETS_COUNT = 2 WHERE ID = 6
		 */
		//delete the new pet, should also delete its visits.
		assocs.delete(wonda);
		assertEquals("total pets for owner #6 after delete a pet", 2, assocs.size());
		assertEquals("total visits for owner #6 after delete a pet", 4, O.allAssociatedRecordsOf(owner6, "pets.visits").size());
	}
	
	@Test
	public void test_hasMany_add_and_delete_child_strict_include() {
		/**
		 * SELECT OWNERS.ID AS OWNERS_ID, OWNERS.FIRST_NAME AS OWNERS_FIRST_NAME, 
		 * OWNERS.LAST_NAME AS OWNERS_LAST_NAME, OWNERS.ADDRESS AS OWNERS_ADDRESS, 
		 * OWNERS.CITY AS OWNERS_CITY, OWNERS.TELEPHONE AS OWNERS_TELEPHONE, 
		 * PETS.ID AS PETS_ID, PETS.NAME AS PETS_NAME, PETS.BIRTH_DATE AS PETS_BIRTH_DATE, 
		 * PETS.TYPE_ID AS PETS_TYPE_ID, PETS.OWNER_ID AS PETS_OWNER_ID, 
		 * VISITS.ID AS VISITS_ID, VISITS.PET_ID AS VISITS_PET_ID, 
		 * VISITS.VISIT_DATE AS VISITS_VISIT_DATE, VISITS.DESCRIPTION AS VISITS_DESCRIPTION, 
		 * OWNERS_PETS.ID AS OWNERS_PETS_ID, OWNERS_PETS.NAME AS OWNERS_PETS_NAME, 
		 * OWNERS_PETS.BIRTH_DATE AS OWNERS_PETS_BIRTH_DATE, 
		 * OWNERS_PETS.TYPE_ID AS OWNERS_PETS_TYPE_ID, 
		 * OWNERS_PETS.OWNER_ID AS OWNERS_PETS_OWNER_ID, 
		 * TYPES.ID AS TYPES_ID, TYPES.NAME AS TYPES_NAME 
		 * FROM OWNERS INNER JOIN PETS ON OWNERS.ID=PETS.OWNER_ID 
		 *             INNER JOIN VISITS ON PETS.ID=VISITS.PET_ID 
		 *             INNER JOIN PETS OWNERS_PETS ON OWNERS.ID=OWNERS_PETS.OWNER_ID 
		 *             INNER JOIN TYPES ON OWNERS_PETS.TYPE_ID=TYPES.ID 
		 * WHERE OWNERS.ID = 6
		 */
		ActiveRecord owner6 = Owner.findFirst("id=6", "strict_include:pets=>visits, pets=>type");
		assertEquals("first name of owner #6", "Jean", owner6.getField("first_name").toString());
		AssociatedRecords assocs = owner6.allAssociated("pets");
		assertEquals("total pets for owner #6 before add a pet in assocs", 2, assocs.size());
		assertEquals("total pets for owner #6 before add a pet", "2", ""+owner6.getField("pets_count"));
		assertEquals("total visits for owner #6 before add a pet", 4, O.allAssociatedRecordsOf(owner6, "pets.visits").size());
		
		ActiveRecord wonda = Pet.newRecord();
		wonda.setData("id", getNextPetID());
		wonda.setData("name", "wonda");
		wonda.setData("birth_date", new Date());
		wonda.setData("type_id", "5");//bird
		wonda.setData("owner_id", "6");
		
		/**
		 * INSERT INTO PETS (NAME, BIRTH_DATE, TYPE_ID, OWNER_ID) VALUES ('wonda', '2009-09-02', 5, 6)
		 * UPDATE OWNERS SET PETS_COUNT = 3 WHERE ID = 6
		 */
		//add the new pet to the owner
		assocs.add(wonda);
		assertEquals("total pets for owner #6 after add a pet in assocs", 3, assocs.size());
		assertEquals("total pets for owner #6 after add a pet", "3", ""+owner6.getField("pets_count"));
		
		/**
		 * SELECT VISITS.* FROM VISITS WHERE PET_ID = 14
		 */
		assertEquals("total visits for owner #6 after add a pet", 4, O.allAssociatedRecordsOf(owner6, "pets.visits").size());
		
		/**
		 * INSERT INTO VISITS (PET_ID, VISIT_DATE, DESCRIPTION) VALUES (14, null, 'visit 1 for wonda')
		 */
		//create a new visit for the new pet
		ActiveRecord wondaVisit = Visit.newRecord();
		wondaVisit.setData("id", getNextVisitID());
		wondaVisit.setData("pet_id", wonda.getField("id"));
		wondaVisit.setData("description", "visit 1 for wonda");
		wondaVisit.create();
		
		/**
		 * SELECT PETS.* FROM PETS WHERE OWNER_ID = 6
		 * SELECT VISITS.* FROM VISITS WHERE PET_ID = 7
		 * SELECT VISITS.* FROM VISITS WHERE PET_ID = 8
		 * SELECT VISITS.* FROM VISITS WHERE PET_ID = 14
		 */
		assertEquals("total visits for owner #6 after add a pet", 5, O.allAssociatedRecordsOf(owner6, "pets.visits", true).size());
		
		/**
		 * SELECT VISITS.* FROM VISITS WHERE PET_ID = 14
		 * DELETE FROM VISITS WHERE ID = 5
		 * DELETE FROM PETS WHERE ID = 14
		 * UPDATE OWNERS SET PETS_COUNT = 2 WHERE ID = 6
		 */
		//delete the new pet, should also delete its visits.
		assocs.delete(wonda);
		assertEquals("total pets for owner #6 after delete a pet", 2, assocs.size());
		assertEquals("total visits for owner #6 after delete a pet", 4, O.allAssociatedRecordsOf(owner6, "pets.visits").size());
	}
	
	@Test
	public void test_hasMany_add_and_detach_child() {
		/**
		 * SELECT OWNERS.* FROM OWNERS WHERE ID = 6
		 */
		ActiveRecord owner6 = Owner.findFirst("id=6");
		assertEquals("first name of owner #6", "Jean", owner6.getField("first_name").toString());
		
		/**
		 * SELECT PETS.* FROM PETS WHERE OWNER_ID = 6
		 */
		AssociatedRecords assocs = owner6.allAssociated("pets");
		assertEquals("total pets for owner #6 before add a pet in assocs", 2, assocs.size());
		assertEquals("total pets for owner #6 before add a pet", "2", ""+owner6.getField("pets_count"));
		
		/**
		 * SELECT VISITS.* FROM VISITS WHERE PET_ID = 7
		 * SELECT VISITS.* FROM VISITS WHERE PET_ID = 8
		 */
		assertEquals("total visits for owner #6 before add a pet", 4, O.allAssociatedRecordsOf(owner6, "pets.visits").size());
		
		/**
		 * SELECT PETS.* FROM PETS WHERE ID = 8
		 */
		ActiveRecord pet8 = Pet.findFirst("id=8");
		
		/**
		 * UPDATE OWNERS SET PETS_COUNT = 1 WHERE ID = 6
		 * UPDATE PETS SET NAME = 'Max', BIRTH_DATE = '1995-09-04', TYPE_ID = 1, OWNER_ID = null  WHERE ID = 8
		 */
		//detach the pet from the owner
		assocs.detach(pet8);
		assertEquals("total pets for owner #6 after detach a pet in assocs", 1, assocs.size());
		assertEquals("total pets for owner #6 after detach a pet", "1", ""+owner6.getField("pets_count"));
		assertEquals("pet8 owner id null", null, pet8.getField("owner_id"));
		
		/**
		 * No sql query.
		 */
		assertEquals("total visits for owner #6 after detach a pet", 2, O.allAssociatedRecordsOf(owner6, "pets.visits").size());

		/**
		 * UPDATE PETS SET NAME = 'Max', BIRTH_DATE = '1995-09-04', TYPE_ID = 1, OWNER_ID = 6  WHERE ID = 8
		 * UPDATE OWNERS SET PETS_COUNT = 2 WHERE ID = 6
		 */
		//add the pet8 back
		assocs.add(pet8);
		assertEquals("total pets for owner #6 after add a pet in assocs", 2, assocs.size());
		assertEquals("total pets for owner #6 after add a pet", "2", ""+owner6.getField("pets_count"));
	}
	
	@Test
	public void test_belongsTo_detach_and_attach() {
		/**
		 * SELECT PETS.* FROM PETS WHERE ID = 8
		 */
		ActiveRecord pet8 = Pet.findFirst("id=8");
		assertEquals("name of pet #8", "Max", pet8.getField("name").toString());
		assertEquals("owner id of pet #8", "6", pet8.getField("owner_id").toString());
		
		/**
		 * SELECT OWNERS.* FROM OWNERS WHERE ID = 2
		 */
		ActiveRecord owner2 = Owner.findFirst("id=2");
		assertEquals("first name of owner #2", "Betty", owner2.getField("first_name").toString());
		assertEquals("total pets for owner #2 before adding a pet", "1", ""+owner2.getField("pets_count"));
		
		/**
		 * SELECT OWNERS.* FROM OWNERS WHERE ID = 6
		 * UPDATE OWNERS SET PETS_COUNT = 1 WHERE ID = 6
		 * UPDATE PETS SET NAME = 'Max', BIRTH_DATE = '1995-09-04', TYPE_ID = 1, OWNER_ID = 2  WHERE ID = 8
		 * UPDATE OWNERS SET PETS_COUNT = 2 WHERE ID = 2
		 */
		//change parent
		pet8.associated("owner").attach(owner2);
		assertEquals("owner id of pet #8 after attaching", "2", pet8.getField("owner_id").toString());
		assertEquals("total pets for owner #2 after adding a pet", "2", ""+owner2.getField("pets_count"));
		
		/**
		 * SELECT OWNERS.* FROM OWNERS WHERE ID = 6
		 */
		ActiveRecord owner6 = Owner.findFirst("id=6");
		assertEquals("first name of owner #6", "Jean", owner6.getField("first_name").toString());
		assertEquals("total pets for owner #6 after detaching a pet", "1", ""+owner6.getField("pets_count"));
		
		/**
		 * UPDATE OWNERS SET PETS_COUNT = 1 WHERE ID = 2
		 * UPDATE PETS SET NAME = 'Max', BIRTH_DATE = '1995-09-04', TYPE_ID = 1, OWNER_ID = 6  WHERE ID = 8
		 * UPDATE OWNERS SET PETS_COUNT = 2 WHERE ID = 6
		 */
		pet8.associated("owner").attach(owner6);
		assertEquals("owner id of pet #8 after attaching", "6", pet8.getField("owner_id").toString());
		assertEquals("total pets for owner #2 after attaching a pet", "1", ""+owner2.getField("pets_count"));
		assertEquals("total pets for owner #6 after attaching a pet", "2", ""+owner6.getField("pets_count"));
	}
}
