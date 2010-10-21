package blog.controllers;

import blog.models.Comment;
import blog.models.Post;

import com.scooterframework.orm.activerecord.ActiveRecord;
import com.scooterframework.web.util.R;

/**
 * CommentsController class handles comments related access.
 */
public class CommentsController extends ApplicationController {
    
    /**
     * <tt>create</tt> method creates a new <tt>comment</tt> record.
     */
    public String create() {
    	ActiveRecord post = findRecord(Post.class, "id=" + params("post_id"));
    	storeToRequest("post", post);
    	
        ActiveRecord newComment = null;
        try {
            newComment = newRecord(Comment.class, params());
            newComment.save();
            flash("notice", "Comment was successfully created.");
            
            return redirectTo(R.resourceRecordPath("posts", post));
        }
        catch(Exception ex) {
            log.error("Error in create() caused by " + ex.getMessage());
            flash("error", "There was a problem creating the comment record.");
        }
        
        storeToRequest("comment", newComment);
        return forwardTo(viewPath("posts", "show"));
    }

}