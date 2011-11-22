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
import com.scooterframework.transaction.Transaction;
import com.scooterframework.transaction.TransactionManager;
import com.scooterframework.transaction.TransactionManagerUtil;

/**
 * PostsController class handles posts related access.
 */
public class TestController extends ApplicationController {
	List<Long> counts = new ArrayList<Long>();
	private int skip = 3;
	
	private String avg(long period) {
		counts.add(period);
		
		long min = Long.MAX_VALUE;
		long max = 0;
		long value = 0;
		int total = counts.size();
		long sum = 0;
		for (int i = skip; i < total; i++) {
			value = counts.get(i);
			sum += value;
			if (min > value) min = value;
			if (max < value) max = value;
		}
		
		return (total > skip) ? ("TOTAL: " + (total - skip) + "; AVG: " + 
				sum / (total - skip) + "; MAX: " + max + "; MIN: " + min)
				: "Skipped";
	}

	/**
	 * Constructor
	 */
	public TestController() {
	}

	public String arSelectOne() {
		long before = System.currentTimeMillis();
		ActiveRecord post = Post.where("id = 1").getRecord();
		long after = System.currentTimeMillis();
		return render("arSelectOne -- " + post.getField("name")
				+ " selects cost: " + (after - before) + " ms");
	}
	
	//http://localhost:8080/blog/test/activerecord?cycle=100
	public String activerecord() {
		String cycle = p("cycle");
		int total = (cycle != null && !"".equals(cycle)) ? Integer.valueOf(cycle) : 1;

		long before = System.currentTimeMillis();

		List<String> names = new ArrayList<String>();
		for (int j = 0; j < total; j++) {
			for (int i = 1; i <= 100; i++) {
				ActiveRecord post = Post.where("id = " + i).getRecord();
				names.add((String) post.getField("name"));
			}
		}

		long after = System.currentTimeMillis();
		// setViewData("names", names);
		return render("activerecord -- " + (total * 100) + " selects cost: "
				+ (after - before) + " ms -- " + avg(after - before));
	}
	
	/*
	 * Same as activerecord() but using transaction.
	 */
	//http://localhost:8080/blog/test/tran_activerecord?cycle=100
	public String tran_activerecord() {
		String cycle = p("cycle");
		int total = (cycle != null && !"".equals(cycle)) ? Integer.valueOf(cycle) : 1;

		long before = System.currentTimeMillis();
		
		TransactionManager tm = TransactionManagerUtil.getTransactionManager();
		tm.beginTransaction(Transaction.JDBC_TRANSACTION_TYPE);

		List<String> names = new ArrayList<String>();
		for (int j = 0; j < total; j++) {
			for (int i = 1; i <= 100; i++) {
				ActiveRecord post = Post.where("id = " + i).getRecord();
				names.add((String) post.getField("name"));
			}
		}
		tm.releaseResources();

		long after = System.currentTimeMillis();
		// setViewData("names", names);
		return render("tran_activerecord -- " + (total * 100) + " selects cost: "
				+ (after - before) + " ms -- " + avg(after - before));
	}
	
	//http://localhost:8080/blog/test/arInsert?cycle=100
	public String arInsert() {
		String cycle = p("cycle");
		int total = (cycle != null && !"".equals(cycle)) ? Integer.valueOf(cycle) : 1;

		long before = System.currentTimeMillis();

		for (int j = 0; j < total; j++) {
			for (int i = 1; i <= 100; i++) {
				ActiveRecord post = Post.newRecord();
				post.setData("name", "name-" + i);
				post.setData("title", "title-" + i);
				post.setData("content", "content-" + i);
				post.save();
			}
		}

		long after = System.currentTimeMillis();
		// setViewData("names", names);
		return render("arInsert -- " + (total * 100) + " inserts cost: "
				+ (after - before) + " ms -- " + avg(after - before));
	}
	
	/*
	 * Same as arInsert() but using transaction over multiple inserts.
	 */
	//http://localhost:8080/blog/test/tran_arInsert?cycle=100
	public String tran_arInsert() {
		String cycle = p("cycle");
		int total = (cycle != null && !"".equals(cycle)) ? Integer.valueOf(cycle) : 1;

		long before = System.currentTimeMillis();

		TransactionManager tm = TransactionManagerUtil.getTransactionManager();
		tm.beginTransaction(Transaction.JDBC_TRANSACTION_TYPE);
		for (int j = 0; j < total; j++) {
			for (int i = 1; i <= 100; i++) {
				ActiveRecord post = Post.newRecord();
				post.setData("name", "name-" + i);
				post.setData("title", "title-" + i);
				post.setData("content", "content-" + i);
				post.save();
			}
		}
		tm.commitTransaction();
		tm.releaseResources();

		long after = System.currentTimeMillis();
		// setViewData("names", names);
		return render("tran_arInsert -- " + (total * 100) + " inserts cost: "
				+ (after - before) + " ms -- " + avg(after - before));
	}
	
	//http://localhost:8080/blog/test/jdbc?cycle=100
	public String jdbc() {
		String cycle = p("cycle");
		int total = (cycle != null && !"".equals(cycle)) ? Integer.valueOf(cycle) : 1;

		long before = System.currentTimeMillis();

		List<String> names = new ArrayList<String>();
		Connection con = ConnectionUtil.createPooledConnection(
				"blog_development", 3600);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			for (int j = 0; j < total; j++) {
				for (int i = 1; i <= 100; i++) {
					ps = con.prepareStatement("select c.* from posts c where c.id = "
							+ i);
					rs = ps.executeQuery();
					while (rs.next()) {
						names.add(rs.getString("name"));
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				ps.close();
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		long after = System.currentTimeMillis();
		// setViewData("names", names);
		return render("jdbc -- " + (total * 100) + " selects cost: "
				+ (after - before) + " ms -- " + avg(after - before));
	}

	//http://localhost:8080/blog/test/jdbcInsert?cycle=100
	public String jdbcInsert() {
		String cycle = p("cycle");
		int total = (cycle != null && !"".equals(cycle)) ? Integer.valueOf(cycle) : 1;

		long before = System.currentTimeMillis();

		Connection con = ConnectionUtil.createPooledConnection("blog_development", 3600);
		PreparedStatement ps = null;
		try {
			String sql = "INSERT INTO posts (name, title, content) VALUES (?, ?, ?)";
			con.setAutoCommit(false);
			ps = con.prepareStatement(sql);
			for (int j = 0; j < total; j++) {
				for (int i = 1; i <= 100; i++) {
					ps.setString(1, "name" + i);
					ps.setString(2, "title" + i);
					ps.setString(3, "content" + i);
					ps.executeUpdate();
				}
			}
			con.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			try {
				ps.close();
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		long after = System.currentTimeMillis();
		// setViewData("names", names);
		return render("jdbcInsert -- " + (total * 100) + " inserts cost: "
				+ (after - before) + " ms -- " + avg(after - before));
	}

	/*
	 * Same as jdbcInsert() but commit after each insert: 10 times slower
	 */
	//http://localhost:8080/blog/test/jdbcInsertEach?cycle=100
	public String jdbcInsertEach() {
		String cycle = p("cycle");
		int total = (cycle != null && !"".equals(cycle)) ? Integer
				.valueOf(cycle) : 1;

		long before = System.currentTimeMillis();

		Connection con = ConnectionUtil.createPooledConnection("blog_development", 3600);
		PreparedStatement ps = null;
		try {
			String sql = "INSERT INTO posts (name, title, content) VALUES (?, ?, ?)";
			con.setAutoCommit(false);
			ps = con.prepareStatement(sql);
			for (int j = 0; j < total; j++) {
				for (int i = 1; i <= 100; i++) {
					ps.setString(1, "name" + i);
					ps.setString(2, "title" + i);
					ps.setString(3, "content" + i);
					ps.executeUpdate();
					con.commit();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			try {
				ps.close();
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		long after = System.currentTimeMillis();
		// setViewData("names", names);
		return render("jdbcInsertEach -- " + (total * 100) + " inserts cost: "
				+ (after - before) + " ms -- " + avg(after - before));
	}

	//http://localhost:8080/blog/test/jdbcBatchInsert?cycle=100
	public String jdbcBatchInsert() {
		String cycle = p("cycle");
		int total = (cycle != null && !"".equals(cycle)) ? Integer
				.valueOf(cycle) : 1;

		long before = System.currentTimeMillis();

		Connection con = ConnectionUtil.createPooledConnection("blog_development", 3600);
		PreparedStatement ps = null;
		try {
			String sql = "INSERT INTO posts (name, title, content) VALUES (?, ?, ?)";
			con.setAutoCommit(false);
			ps = con.prepareStatement(sql);
			for (int j = 0; j < total; j++) {
				for (int i = 1; i <= 100; i++) {
					ps.setString(1, "name" + i);
					ps.setString(2, "title" + i);
					ps.setString(3, "content" + i);
					ps.addBatch();
				}
				ps.executeBatch();
			}
			con.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			try {
				ps.close();
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		long after = System.currentTimeMillis();
		// setViewData("names", names);
		return render("jdbcBatchInsert -- " + (total * 100) + " inserts cost: "
				+ (after - before) + " ms -- " + avg(after - before));
	}
}