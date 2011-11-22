package blog.models;

import com.scooterframework.orm.activerecord.ActiveRecord;

/**
 * Message class represents a message record in database.
 */
public class Message extends ActiveRecord {

	public String getTableName() {
		return "testmessage";
	}
}
