package blog.models;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.scooterframework.orm.activerecord.ActiveRecord;
import com.scooterframework.orm.activerecord.ActiveRecordUtil;
import com.scooterframework.test.UnitTestHelper;

import blog.models.Post;

/**
 * PostTest class contains tests for post.
 *
 */
public class PostTest extends UnitTestHelper {
	
	/**
	 * This is a sample test method.
	 * 
	 * Tests record retrieval by findBy method.
	 */
	//@Test public void test_findByLastNameAndFirstName() {
	//	String[] names = {"Stevens", "Henry"};
	//	ActiveRecord vet5 = Vet.findBy("last_name_and_first_name", names);
	//	assertEquals("#5 Stevens's id", "5", ""+vet5.getField("id"));
	//}

	@Test 
	public void test_dummary() {
		//assertTrue(true); // do nothing;
	}

	@Test 
	public void test_index() {
		List<ActiveRecord> posts = Post.findAll();
		assertEquals("total posts", 6, posts.size());
	}
}
