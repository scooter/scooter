package blog.models;

import com.scooterframework.orm.activerecord.ActiveRecord;

/**
 * Post class represents a post record in database.
 */
public class Post extends ActiveRecord {
    public void validatesRecord() {
    	validators().validatesPresenceOf("name, title, content");
    	validators().validatesLengthMaximum("name", 10);
    	validators().validatesLengthMaximum("content", 140);
        //Our posts are twitter friendly.
    }

    public void registerRelations() {
        hasMany("comments", "cascade:delete");
    }
}
