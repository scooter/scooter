package twitterdemo.models;

import com.scooterframework.orm.activerecord.ActiveRecord;

/**
 * Followship class represents a followship record in database.
 */
public class Followship extends ActiveRecord {
    public void registerRelations() {
        belongsTo("follower", "model: account; counter_cache:followings_count");
        belongsTo("following", "model: account; counter_cache:followers_count");
        
        hasMany("followings_tweets", "model: tweet; mapping: following_id=account_id");
    }
}