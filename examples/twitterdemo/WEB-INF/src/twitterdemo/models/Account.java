package twitterdemo.models;

import com.scooterframework.orm.activerecord.ActiveRecord;

/**
 * Account class represents a account record in database.
 */
public class Account extends ActiveRecord {
    public void registerRelations() {
        hasMany("chases", "mapping:id=follower_id; model:followship; reverse:follower; cascade:delete");
        hasManyThrough("followings", "chases");
        
        hasMany("attractions", "mapping:id=following_id; model:followship; reverse:following; cascade:delete");
        hasManyThrough("followers", "attractions");
        
        hasMany("tweets", "cascade:delete");
        hasManyThrough("followings_tweets", "chases", "order_by: tweets.created_at desc");
    }
}