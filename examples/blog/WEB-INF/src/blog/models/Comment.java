package blog.models;

import com.scooterframework.orm.activerecord.ActiveRecord;

/**
 * Comment class represents a comment record in database.
 */
public class Comment extends ActiveRecord {

    public void validatesRecord() {
    	validators().validatesPresenceOf("commenter, body");
    }
    
    public void registerRelations() {
        belongsTo("post", "counter_cache:true");
    }
}