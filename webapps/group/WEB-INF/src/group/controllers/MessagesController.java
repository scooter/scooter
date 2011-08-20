package group.controllers;

import static com.scooterframework.web.controller.ActionControl.*;

import com.scooterframework.admin.Constants;
import com.scooterframework.admin.FilterManagerFactory;
import com.scooterframework.orm.activerecord.ActiveRecord;
import com.scooterframework.orm.misc.Paginator;
import com.scooterframework.web.util.R;

import group.models.Message;

/**
 * MessagesController class handles messages related access.
 */
public class MessagesController extends ApplicationController {

    /**
     * Constructor
     */
    public MessagesController() {
    }

    /**
     * <tt>index</tt> method returns a list of <tt>messages</tt> records.
     * If the value of <tt>paged</tt> parameter is <tt>true</tt>, a paginated list is returned.
     */
    public String index() {
        if ("true".equals(p(Constants.PAGED))) {
            Paginator page = jdbcPaginator(Message.class, params());
            setViewData("message_page", page);
            return renderView("paged_list");
        }
        setViewData("messages", Message.findAll());
        return null;
    }
    
    /**
     * <tt>show</tt> method returns a <tt>message</tt> record.
     */
    public String show() {
        ActiveRecord message = Message.findByPK(p("id"));
        if (message == null) {
            flash("notice", "There is no message record with primary key id as " + p("id") + ".");
        }
        else {
            setViewData("message", message);
        }
        return null;
    }
    
    /**
     * <tt>add</tt> method prepares meta data for adding a new <tt>message</tt> record.
     */
    public String add() {
        setViewData("message", Message.newRecord());
        return null;
    }
    
    /**
     * <tt>create</tt> method creates a new <tt>message</tt> record.
     */
    public String create() {
        ActiveRecord newMessage = null;
        try {
            newMessage = Message.newRecord();
            newMessage.setData(params());
            newMessage.save();
            flash("notice", "Message was successfully created.");
            
            return redirectTo(R.resourcePath("messages"));
        }
        catch(Exception ex) {
            log.error("Error in create() caused by " + ex.getMessage());
            flash("error", "There was a problem creating the message record.");
        }
        
        setViewData("message", newMessage);
        return renderView("add");
    }
    
    /**
     * <tt>edit</tt> method prepares data for editing an existing <tt>message</tt> record.
     */
    public String edit() {
        return show();
    }
    
    /**
     * <tt>update</tt> method updates an existing <tt>message</tt> record.
     */
    public String update() {
        ActiveRecord message = null;
        try {
            message = Message.findByPK(p("id"));
            if (message != null) {
                message.setData(params());
                message.update();
                flash("notice", "Message was successfully updated.");
                
                return redirectTo(R.resourceRecordPath("messages", message));
            }
            else {
                flash("notice", "There is no message record with primary key id as " + p("id") + ".");
            }
        }
        catch(Exception ex) {
            log.error("Error in update() caused by " + ex.getMessage());
            flash("error", "There was a problem updating the message record.");
        }
        
        setViewData("message", message);
        return renderView("edit");
    }
    
    /**
     * <tt>delete</tt> method deletes a <tt>message</tt> record.
     */
    public String delete() {
        ActiveRecord message = Message.findByPK(p("id"));
        if (message != null) {
            message.delete();
            flash("notice", "Message was successfully deleted.");
        }
        else {
            flash("notice", "There is no message record with primary key id as " + p("id") + ".");
        }
        
        return redirectTo(R.resourcePath("messages"));
    }
}
