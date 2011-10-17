package blog.controllers;

import static com.scooterframework.web.controller.ActionControl.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import blog.models.Post;

import com.scooterframework.orm.activerecord.ActiveRecord;
import com.scooterframework.orm.sqldataexpress.connection.ConnectionUtil;

/**
 * PostsController class handles posts related access.
 */
public class TestController extends ApplicationController {

    /**
     * Constructor
     */
    public TestController() {
    }

	public String activerecord() {
		List<String> names = new ArrayList<String>();
		for (int i = 30; i <= 47; i++) {
			ActiveRecord post = Post.where("id = " + i).getRecord();
			names.add((String) post.getField("name"));
		}
		setViewData("names", names);
		return render(names);
	}

	public String jdbc() {
		List<String> names = new ArrayList<String>();
		Connection con = ConnectionUtil.createPooledConnection("blog_development", 3600);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			for(int i = 30; i <= 47; i++) {
				ps = con.prepareStatement("select c.* from posts c where c.id = " + i);
				rs = ps.executeQuery();
				while(rs.next()) {
					names.add(rs.getString("name"));
				}
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				rs.close();
				ps.close();
				con.close();
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}
		setViewData("names", names);
		return render(names);
   }
}
