package twitterdemo.models;

import com.scooterframework.orm.activerecord.ActiveRecord;

/**
 * Tweet class represents a tweet record in database.
 */
public class Tweet extends ActiveRecord {
    public void validatesRecord() {
    	validators().validatesLengthMaximum("message", 140);
    }

    public void registerRelations() {
        belongsTo("account", "counter_cache:true");
    }
}