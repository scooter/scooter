/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.activerecord;

import com.scooterframework.common.util.Util;
import com.scooterframework.test.ApplicationTest;
import com.scooterframework.test.models.Vet;

/**
 * ActiveRecordCalculatorTest class
 * 
 * @author (Fei) John Chen
 *
 */
public class ActiveRecordCalculatorTest extends ApplicationTest {
	private ActiveRecord vetHome = null;
	private Calculator cal = null;
	
	protected void setUp() {
		super.setUp();
		
		if (vetHome == null) {
			vetHome = ActiveRecordUtil.getHomeInstance(Vet.class);
		}
		
		if (cal == null) {
			cal = new Calculator(vetHome);
		}
	}
	
	public void test_count() {
		assertEquals("total vets", 6, cal.count());
	}
	
	public void test_sum() {
		assertEquals("sum vet ids", 21, Util.getSafeIntValue(cal.sum("id")));
	}
	
	public void test_avg() {
		assertEquals("avg vet ids", (21/6.0), Util.getSafeDoubleValue(cal.average("id")), 0);
	}
	
	public void test_max() {
		assertEquals("max vet ids", 6, Util.getSafeIntValue(cal.maximum("id")));
	}
	
	public void test_min() {
		assertEquals("min vet ids", 1, Util.getSafeIntValue(cal.minium("id")));
	}
}
