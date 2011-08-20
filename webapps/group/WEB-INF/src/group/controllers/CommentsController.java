package group.controllers;

import static com.scooterframework.web.controller.ActionControl.*;

import com.scooterframework.admin.Constants;
import com.scooterframework.admin.FilterManagerFactory;
import com.scooterframework.orm.activerecord.ActiveRecord;
import com.scooterframework.orm.misc.Paginator;
import com.scooterframework.web.util.R;

import group.models.Comment;

/**
 * CommentsController class handles comments related access.
 */
public class CommentsController extends ApplicationController {

    /**
     * Constructor
     */
    public CommentsController() {
    }

    /**
     * <tt>index</tt> method returns a list of <tt>comments</tt> records.
     * If the value of <tt>paged</tt> parameter is <tt>true</tt>, a paginated list is returned.
     */
    public String index() {
        if ("true".equals(p(Constants.PAGED))) {
            Paginator page = jdbcPaginator(Comment.class, params());
            setViewData("comment_page", page);
            return renderView("paged_list");
        }
        setViewData("comments", Comment.findAll());
        return null;
    }
    
    /**
     * <tt>show</tt> method returns a <tt>comment</tt> record.
     */
    public String show() {
        ActiveRecord comment = Comment.findByPK(p("id"));
        if (comment == null) {
            flash("notice", "There is no comment record with primary key id as " + p("id") + ".");
        }
        else {
            setViewData("comment", comment);
        }
        return null;
    }
    
    /**
     * <tt>add</tt> method prepares meta data for adding a new <tt>comment</tt> record.
     */
    public String add() {
        setViewData("comment", Comment.newRecord());
        return null;
    }
    
    /**
     * <tt>create</tt> method creates a new <tt>comment</tt> record.
     */
    public String create() {
        ActiveRecord newComment = null;
        try {
            newComment = Comment.newRecord();
            newComment.setData(params());
            newComment.save();
            flash("notice", "Comment was successfully created.");
            
            return redirectTo(R.resourcePath("comments"));
        }
        catch(Exception ex) {
            log.error("Error in create() caused by " + ex.getMessage());
            flash("error", "There was a problem creating the comment record.");
        }
        
        setViewData("comment", newComment);
        return renderView("add");
    }
    
    /**
     * <tt>edit</tt> method prepares data for editing an existing <tt>comment</tt> record.
     */
    public String edit() {
        return show();
    }
    
    /**
     * <tt>update</tt> method updates an existing <tt>comment</tt> record.
     */
    public String update() {
        ActiveRecord comment = null;
        try {
            comment = Comment.findByPK(p("id"));
            if (comment != null) {
                comment.setData(params());
                comment.update();
                flash("notice", "Comment was successfully updated.");
                
                return redirectTo(R.resourceRecordPath("comments", comment));
            }
            else {
                flash("notice", "There is no comment record with primary key id as " + p("id") + ".");
            }
        }
        catch(Exception ex) {
            log.error("Error in update() caused by " + ex.getMessage());
            flash("error", "There was a problem updating the comment record.");
        }
        
        setViewData("comment", comment);
        return renderView("edit");
    }
    
    /**
     * <tt>delete</tt> method deletes a <tt>comment</tt> record.
     */
    public String delete() {
        ActiveRecord comment = Comment.findByPK(p("id"));
        if (comment != null) {
            comment.delete();
            flash("notice", "Comment was successfully deleted.");
        }
        else {
            flash("notice", "There is no comment record with primary key id as " + p("id") + ".");
        }
        
        return redirectTo(R.resourcePath("comments"));
    }
}
