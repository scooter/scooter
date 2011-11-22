package blog.models;

import com.scooterframework.orm.activerecord.ActiveRecord;

/**
 * Ttt class represents a ttt record in database.
 */
public class Ttt extends ActiveRecord {

	public String getTableName() {
		return "test_table";
	}
}
