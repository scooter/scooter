package blog.controllers;

import static com.scooterframework.web.controller.ActionControl.*;

import com.scooterframework.admin.FilterManagerFactory;

import blog.models.Comment;
import blog.models.Post;

import com.scooterframework.orm.activerecord.ActiveRecord;
import com.scooterframework.web.util.R;

/**
 * CommentsController class handles comments related access.
 */
public class CommentsController extends ApplicationController {

    /**
     * create method creates a new comment record.
     */
    public String create() {
    	ActiveRecord post = Post.findById(p("post_id"));
    	setViewData("post", post);

        ActiveRecord newComment = null;
        try {
            newComment = Comment.newRecord();
            newComment.setData("commenter", p("commenter"));
            newComment.setData("body", p("body"));
            newComment.setData("post_id", p("post_id"));
            newComment.save();
            flash("notice", "Comment was successfully created.");

            return redirectTo(R.resourceRecordPath("posts", post));
        }
        catch(Exception ex) {
            log.error("Error in create() caused by " + ex.getMessage());
            flash("error", "There was a problem creating the comment record.");
        }

        setViewData("comment", newComment);
        return forwardTo(viewPath("posts", "show"));
    }

}
