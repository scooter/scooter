/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.activerecord;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.scooterframework.orm.sqldataexpress.service.SqlServiceClient;
import com.scooterframework.test.ScooterTestHelper;
import com.scooterframework.test.models.Pet;
import com.scooterframework.test.models.Vet;

/**
 * ActiveRecordCRUDTest class
 * 
 * @author (Fei) John Chen
 *
 */
public class ActiveRecordCRUDTest extends ScooterTestHelper {
	
	@Test public void test_findAll() {
		List allVets = Vet.findAll();
		assertEquals("total vets", 6, allVets.size());
	}
	
	@Test public void test_findById() {
		ActiveRecord vet6 = Vet.findById(new Integer(6));
		assertEquals("#6 vet's firstname", "Sharon", vet6.getField("first_name"));
	}
	
	@Test public void test_find() {
		ActiveRecord vet4 = Vet.findFirst("last_name='Ortega'");
		assertEquals("#4 Ortega's id", "4", ""+vet4.getField("id"));
	}

	@Test public void test_findByLastName() {
		String[] lnames = {"Stevens"};
		ActiveRecord vet5 = Vet.findFirstBy("last_name", lnames);
		assertEquals("#5 Stevens's id", "5", ""+vet5.getField("id"));
	}

	@Test public void test_findByLastNameAndFirstName() {
		String[] names = {"Stevens", "Henry"};
		ActiveRecord vet5 = Vet.findFirstBy("last_name_and_first_name", names);
		assertEquals("#5 Stevens's id", "5", ""+vet5.getField("id"));
	}

	@Test public void test_findAllByTypeAndOwner() {
		String[] values = {"2", "3"};
		List pets = Pet.findAllBy("type_id_and_owner_id", values);
		assertEquals("Eduardo's two dogs", 2, pets.size());
	}

	@Test public void test_findAllByTypeAndOwnerWithOptions() {
		String[] values = {"2", "3"};
		String options = "";
		List pets = Pet.findAllBy("type_id_and_owner_id", values, options);
		assertEquals("Eduardo's two dogs", 2, pets.size());
	}

	@Test public void test_findBySQL() {
		String sql = "select * from vets where last_name like '%s' ";
		Map inputs = new HashMap();
		List vets = Vet.findAllBySQL(sql, inputs);
		assertEquals("vets with last name ending with s", 3, vets.size());
	}

	@Test public void test_findBySQLKey() {
		String sqlKey = "getVetByLastName";
		Map inputs = new HashMap();
		inputs.put("1", "Stevens");
		List vets = Vet.findAllBySQLKey(sqlKey, inputs);
		assertEquals("vets with last name as Stevens", 1, vets.size());
		Iterator it = vets.iterator();
		while(it.hasNext()) {
			ActiveRecord vet = (ActiveRecord)it.next();
			assertEquals("id of the vet with last name as Stevens", "5", ""+vet.getField("id"));
		}
	}
	
	@Test public void test_createAndDelete() {
		String findNextID = "SELECT (max(id)+1) FROM vets";
		Object nextID = SqlServiceClient.retrieveObjectBySQL(findNextID);
		
		ActiveRecord vet7 = Vet.newRecord();
		vet7.setData("id", nextID);
		vet7.setData("first_name", "John");
		vet7.setData("last_name", "Chen");
		vet7.save();
		List allVets = Vet.findAll();
		assertEquals("total vets", 7, allVets.size());
		
		//find the newly created 
		String sqlKey = "getLatestVet";
		List vets = Vet.findAllBySQLKey(sqlKey, null);
		assertEquals("newly added vet", 1, vets.size());
		Iterator it = vets.iterator();
		while(it.hasNext()) {
			ActiveRecord vet = (ActiveRecord)it.next();
			assertEquals("id of the newly added vet", ""+vet7.getField("id"), ""+vet.getField("id"));
			assertEquals("first name of the newly added vet", "John", ""+vet.getField("first_name"));
		}
		
		vet7.delete();
		allVets = Vet.findAll();
		assertEquals("total vets", 6, allVets.size());
	}
    
    @Test public void test_updateAll() {
		ActiveRecord vet6 = Vet.findById(new Integer(6));
		assertEquals("#6 vet's firstname", "Sharon", vet6.getField("first_name"));
        
        String oldFirstName = (String)vet6.getField("first_name");
        String oldLastName = (String)vet6.getField("last_name");
        
        Map inputs = new HashMap();
        inputs.put("first_name", "Jenny");
        inputs.put("last_name", "Doe");
        
        String conditionsSQL = "last_name = 'XXX'";
        int count = Vet.updateAll(inputs, conditionsSQL);
		assertEquals("No person named XXX", 0, count);
        
        conditionsSQL = "id = 6";
        count = Vet.updateAll(inputs, conditionsSQL);
		assertEquals("Change #6 vet's firstname", 1, count);
        
		vet6 = Vet.findById(new Integer(6));
		assertEquals("#6 vet's firstname is changed", "Jenny", vet6.getField("first_name"));
		assertEquals("#6 vet's lastname is changed", "Doe", vet6.getField("last_name"));
        
        inputs.put("first_name", oldFirstName);
        inputs.put("last_name", oldLastName);
        conditionsSQL = "id = ?id";
        Map conditionsData = new HashMap();
        conditionsData.put("id", "6");
        
        count = Vet.updateAll(inputs, conditionsSQL, conditionsData);
		assertEquals("Change back #6 vet's firstname", 1, count);
        
		vet6 = Vet.findById(new Integer(6));
		assertEquals("Confirm #6 vet's firstname is back", oldFirstName, vet6.getField("first_name"));
		assertEquals("Confirm #6 vet's lastname is back", oldLastName, vet6.getField("last_name"));
    }
}
