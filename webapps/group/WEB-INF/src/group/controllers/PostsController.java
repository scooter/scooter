package group.controllers;

import static com.scooterframework.web.controller.ActionControl.*;

import com.scooterframework.admin.Constants;
import com.scooterframework.admin.FilterManagerFactory;
import com.scooterframework.orm.activerecord.ActiveRecord;
import com.scooterframework.orm.misc.Paginator;
import com.scooterframework.web.util.R;

import group.models.Post;

/**
 * PostsController class handles posts related access.
 */
public class PostsController extends ApplicationController {

    /**
     * Constructor
     */
    public PostsController() {
    }

    /**
     * <tt>index</tt> method returns a list of <tt>posts</tt> records.
     * If the value of <tt>paged</tt> parameter is <tt>true</tt>, a paginated list is returned.
     */
    public String index() {
        if ("true".equals(p(Constants.PAGED))) {
            Paginator page = jdbcPaginator(Post.class, params());
            setViewData("post_page", page);
            return renderView("paged_list");
        }
        setViewData("posts", Post.findAll());
        return null;
    }

    /**
     * <tt>show</tt> method returns a <tt>post</tt> record.
     */
    public String show() {
        ActiveRecord post = Post.findByPK(p("id"));
        if (post == null) {
            flash("notice", "There is no post record with primary key id as " + p("id") + ".");
        }
        else {
            setViewData("post", post);
        }
        return null;
    }

    /**
     * <tt>add</tt> method prepares meta data for adding a new <tt>post</tt> record.
     */
    public String add() {
        setViewData("post", Post.newRecord());
        return null;
    }

    /**
     * <tt>create</tt> method creates a new <tt>post</tt> record.
     */
    public String create() {
        ActiveRecord newPost = null;
        try {
            newPost = Post.newRecord();
            newPost.setData(params());
            newPost.save();
            flash("notice", "Post was successfully created.");

            return redirectTo(R.resourcePath("posts"));
        }
        catch(Exception ex) {
            log.error("Error in create() caused by " + ex.getMessage());
            flash("error", "There was a problem creating the post record.");
        }

        setViewData("post", newPost);
        return renderView("add");
    }

    /**
     * <tt>edit</tt> method prepares data for editing an existing <tt>post</tt> record.
     */
    public String edit() {
        return show();
    }

    /**
     * <tt>update</tt> method updates an existing <tt>post</tt> record.
     */
    public String update() {
        ActiveRecord post = null;
        try {
            post = Post.findByPK(p("id"));
            System.out.println("1MMMMMMMM getModifiedFields(): " + post.getModifiedFields());
            if (post != null) {
                post.setData(params());
            System.out.println("2MMMMMMMM getModifiedFields(): " + post.getModifiedFields());
                post.update();
                flash("notice", "Post was successfully updated.");

                return redirectTo(R.resourceRecordPath("posts", post));
            }
            else {
                flash("notice", "There is no post record with primary key id as " + p("id") + ".");
            }
        }
        catch(Exception ex) {
            log.error("Error in update() caused by " + ex.getMessage());
            flash("error", "There was a problem updating the post record.");
        }

        setViewData("post", post);
        return renderView("edit");
    }

    /**
     * <tt>delete</tt> method deletes a <tt>post</tt> record.
     */
    public String delete() {
        ActiveRecord post = Post.findByPK(p("id"));
        if (post != null) {
            post.delete();
            flash("notice", "Post was successfully deleted.");
        }
        else {
            flash("notice", "There is no post record with primary key id as " + p("id") + ".");
        }

        return redirectTo(R.resourcePath("posts"));
    }
}
