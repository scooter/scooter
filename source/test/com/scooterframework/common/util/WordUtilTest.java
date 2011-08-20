/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.common.util;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

/**
 * WordUtilTest class
 * 
 * @author (Fei) John Chen
 *
 */
public class WordUtilTest {
    
    @Test public void test_map_plural2singles() {
    	for (Map.Entry<String, String> entry : WordUtil.plural2singles.entrySet()) {
    		String pword = entry.getKey();
    		String sword = entry.getValue();
    		String result = WordUtil.pluralize(sword);
    		assertEquals(pword, result);
    	}
    }
    
    @Test public void test_camelize() {
    	String word = "";
    	String expect = "";
    	String result = "";
    	word = "hello"; expect = "Hello"; result = WordUtil.camelize(word);
    	assertEquals(expect, result);

    	word = "hello world"; expect = "Hello world"; result = WordUtil.camelize(word);
    	assertEquals(expect, result);

    	word = "active_record"; expect = "ActiveRecord"; result = WordUtil.camelize(word);
    	assertEquals(expect, result);

    	word = "active.record"; expect = "Active.record"; result = WordUtil.camelize(word);
    	assertEquals(expect, result);

    	word = "active/record"; expect = "Active/record"; result = WordUtil.camelize(word);
    	assertEquals(expect, result);

    	word = "active.Record"; expect = "Active.Record"; result = WordUtil.camelize(word);
    	assertEquals(expect, result);

    	word = "active_record"; expect = "activeRecord"; result = WordUtil.camelize(word, true);
    	assertEquals(expect, result);
    	
    	word = "a"; expect = "A"; result = WordUtil.camelize(word);
    	assertEquals(expect, result);
    	
    	word = "a_b"; expect = "AB"; result = WordUtil.camelize(word);
    	assertEquals(expect, result);
    	
    	word = "a - b"; expect = "A - b"; result = WordUtil.camelize(word);
    	assertEquals(expect, result);
    	
    	word = "a - b_"; expect = "A - b"; result = WordUtil.camelize(word);
    	assertEquals(expect, result);
    	
    	word = "_a - b_"; expect = "A - b"; result = WordUtil.camelize(word);
    	assertEquals(expect, result);
    	
    	word = "Hello"; expect = "Hello"; result = WordUtil.camelize(word);
    	assertEquals(expect, result);
    	
    	word = "HelloURL"; expect = "HelloURL"; result = WordUtil.camelize(word);
    	assertEquals(expect, result);
    }
	
    @Test public void test_pluralize() {
    	String word = "";
    	String expect = "";
    	String result = "";
    	
    	word = "Posts"; expect = "Posts"; result = WordUtil.pluralize(word);
    	assertEquals(expect, result);
    	
    	word = "Records"; expect = "Records"; result = WordUtil.pluralize(word);
    	assertEquals(expect, result);
    	
    	word = "records"; expect = "records"; result = WordUtil.pluralize(word);
    	assertEquals(expect, result);
    	
    	word = "posts"; expect = "posts"; result = WordUtil.pluralize(word);
    	assertEquals(expect, result);
    	
    	word = "post"; expect = "posts"; result = WordUtil.pluralize(word);
    	assertEquals(expect, result);
    	
    	word = "chief"; expect = "chiefs"; result = WordUtil.pluralize(word);
    	assertEquals(expect, result);
    	
    	word = "chef"; expect = "chefs"; result = WordUtil.pluralize(word);
    	assertEquals(expect, result);
    	
    	word = "stereo"; expect = "stereos"; result = WordUtil.pluralize(word);
    	assertEquals(expect, result);
    	
    	word = "hero"; expect = "heroes"; result = WordUtil.pluralize(word);
    	assertEquals(expect, result);
    	
    	word = "zoo"; expect = "zoos"; result = WordUtil.pluralize(word);
    	assertEquals(expect, result);
    	
    	word = "octopus"; expect = "octopi"; result = WordUtil.pluralize(word);
    	assertEquals(expect, result);
    	
    	word = "box"; expect = "boxes"; result = WordUtil.pluralize(word);
    	assertEquals(expect, result);
    	
    	word = "church"; expect = "churches"; result = WordUtil.pluralize(word);
    	assertEquals(expect, result);
    	
    	word = "tray"; expect = "trays"; result = WordUtil.pluralize(word);
    	assertEquals(expect, result);
    	
    	word = "enemy"; expect = "enemies"; result = WordUtil.pluralize(word);
    	assertEquals(expect, result);
    	
    	word = "baby"; expect = "babies"; result = WordUtil.pluralize(word);
    	assertEquals(expect, result);
    	
    	word = "synopsis"; expect = "synopses"; result = WordUtil.pluralize(word);
    	assertEquals(expect, result);
    	
    	word = "thesis"; expect = "theses"; result = WordUtil.pluralize(word);
    	assertEquals(expect, result);
    	
    	word = "knife"; expect = "knives"; result = WordUtil.pluralize(word);
    	assertEquals(expect, result);
    	
    	word = "self"; expect = "selves"; result = WordUtil.pluralize(word);
    	assertEquals(expect, result);
    	
    	word = "cave"; expect = "caves"; result = WordUtil.pluralize(word);
    	assertEquals(expect, result);
    	
    	word = "word"; expect = "words"; result = WordUtil.pluralize(word);
    	assertEquals(expect, result);
    	
    	word = "mailman"; expect = "mailmen"; result = WordUtil.pluralize(word);
    	assertEquals(expect, result);
    	
    	word = "CamelOctopus"; expect = "CamelOctopi"; result = WordUtil.pluralize(word);
    	assertEquals(expect, result);
    	
    	word = "sex"; expect = "sexes"; result = WordUtil.pluralize(word);
    	assertEquals(expect, result);
    	
    	word = "wife"; expect = "wives"; result = WordUtil.pluralize(word);
    	assertEquals(expect, result);
    	
    	word = "man"; expect = "men"; result = WordUtil.pluralize(word);
    	assertEquals(expect, result);
    	
    	word = "child"; expect = "children"; result = WordUtil.pluralize(word);
    	assertEquals(expect, result);
    	
    	word = "person"; expect = "people"; result = WordUtil.pluralize(word);
    	assertEquals(expect, result);
    	
    	word = "movie"; expect = "movies"; result = WordUtil.pluralize(word);
    	assertEquals(expect, result);
    	
    	word = "buffalo"; expect = "buffaloes"; result = WordUtil.pluralize(word);
    	assertEquals(expect, result);
    	
    	word = "salesman"; expect = "salesmen"; result = WordUtil.pluralize(word);
    	assertEquals(expect, result);
    	
    	word = "ATM"; expect = "ATMs"; result = WordUtil.pluralize(word);
    	assertEquals(expect, result);
    	
    	word = "bus"; expect = "buses"; result = WordUtil.pluralize(word);
    	assertEquals(expect, result);
    	
    	word = "play"; expect = "plays"; result = WordUtil.pluralize(word);
    	assertEquals(expect, result);
    	
    	word = "index"; expect = "indices"; result = WordUtil.pluralize(word);
    	assertEquals(expect, result);
    	
    	word = "stimulus"; expect = "stimuli"; result = WordUtil.pluralize(word);
    	assertEquals(expect, result);
    	
    	word = "thesis"; expect = "theses"; result = WordUtil.pluralize(word);
    	assertEquals(expect, result);
    	
    	word = "shoe"; expect = "shoes"; result = WordUtil.pluralize(word);
    	assertEquals(expect, result);
    	
    	word = "axis"; expect = "axes"; result = WordUtil.pluralize(word);
    	assertEquals(expect, result);
    	
    	word = "move"; expect = "moves"; result = WordUtil.pluralize(word);
    	assertEquals(expect, result);
    	
    	word = "archives"; expect = "archives"; result = WordUtil.pluralize(word);
    	assertEquals(expect, result);
    	
    	word = "URL"; expect = "URLs"; result = WordUtil.pluralize(word);
    	assertEquals(expect, result);
    	
    	word = "country"; expect = "countries"; result = WordUtil.pluralize(word);
    	assertEquals(expect, result);
    }
	
    @Test public void test_singularize() {
    	String word = "";
    	String expect = "";
    	String result = "";
    	word = "posts"; expect = "post"; result = WordUtil.singularize(word);
    	assertEquals(expect, result);
    	
    	word = "post"; expect = "post"; result = WordUtil.singularize(word);
    	assertEquals(expect, result);
    	
    	word = "sheep"; expect = "sheep"; result = WordUtil.singularize(word);
    	assertEquals(expect, result);
    	
    	word = "octopi"; expect = "octopus"; result = WordUtil.singularize(word);
    	assertEquals(expect, result);
    	
    	word = "words"; expect = "word"; result = WordUtil.singularize(word);
    	assertEquals(expect, result);
    	
    	word = "sexes"; expect = "sex"; result = WordUtil.singularize(word);
    	assertEquals(expect, result);
    	
    	word = "wives"; expect = "wife"; result = WordUtil.singularize(word);
    	assertEquals(expect, result);
    	
    	word = "children"; expect = "child"; result = WordUtil.singularize(word);
    	assertEquals(expect, result);
    	
    	word = "people"; expect = "person"; result = WordUtil.singularize(word);
    	assertEquals(expect, result);
    	
    	word = "movies"; expect = "movie"; result = WordUtil.singularize(word);
    	assertEquals(expect, result);
    	
    	word = "buffaloes"; expect = "buffalo"; result = WordUtil.singularize(word);
    	assertEquals(expect, result);
    	
    	word = "salesmen"; expect = "salesman"; result = WordUtil.singularize(word);
    	assertEquals(expect, result);
    	
    	word = "news"; expect = "news"; result = WordUtil.singularize(word);
    	assertEquals(expect, result);
    	
    	word = "boxes"; expect = "box"; result = WordUtil.singularize(word);
    	assertEquals(expect, result);
    	
    	word = "buses"; expect = "bus"; result = WordUtil.singularize(word);
    	assertEquals(expect, result);
    	
    	word = "bus"; expect = "bus"; result = WordUtil.singularize(word);
    	assertEquals(expect, result);
    	
    	word = "analyses"; expect = "analysis"; result = WordUtil.singularize(word);
    	assertEquals(expect, result);
    	
    	word = "theses"; expect = "thesis"; result = WordUtil.singularize(word);
    	assertEquals(expect, result);
    	
    	word = "mice"; expect = "mouse"; result = WordUtil.singularize(word);
    	assertEquals(expect, result);
    	
    	word = "tests"; expect = "test"; result = WordUtil.singularize(word);
    	assertEquals(expect, result);
    	
    	word = "crises"; expect = "crisis"; result = WordUtil.singularize(word);
    	assertEquals(expect, result);
    	
    	word = "aliases"; expect = "alias"; result = WordUtil.singularize(word);
    	assertEquals(expect, result);
    	
    	word = "oxen"; expect = "ox"; result = WordUtil.singularize(word);
    	assertEquals(expect, result);
    	
    	word = "quizes"; expect = "quiz"; result = WordUtil.singularize(word);
    	assertEquals(expect, result);
    	
    	word = "caves"; expect = "cave"; result = WordUtil.singularize(word);
    	assertEquals(expect, result);
    	
    	word = "moves"; expect = "move"; result = WordUtil.singularize(word);
    	assertEquals(expect, result);
    	
    	word = "archives"; expect = "archives"; result = WordUtil.singularize(word);
    	assertEquals(expect, result);
    	
    	word = "calves"; expect = "calf"; result = WordUtil.singularize(word);
    	assertEquals(expect, result);
    	
    	word = "chiefs"; expect = "chief"; result = WordUtil.singularize(word);
    	assertEquals(expect, result);
    	
    	word = "dwarves"; expect = "dwarf"; result = WordUtil.singularize(word);
    	assertEquals(expect, result);
    	
    	word = "halves"; expect = "half"; result = WordUtil.singularize(word);
    	assertEquals(expect, result);
    	
    	word = "loaves"; expect = "loaf"; result = WordUtil.singularize(word);
    	assertEquals(expect, result);
    	
    	word = "theories"; expect = "theory"; result = WordUtil.singularize(word);
    	assertEquals(expect, result);
    	
    	word = "skies"; expect = "sky"; result = WordUtil.singularize(word);
    	assertEquals(expect, result);
    	
    	word = "synopses"; expect = "synopsis"; result = WordUtil.singularize(word);
    	assertEquals(expect, result);
    	
    	word = "theses"; expect = "thesis"; result = WordUtil.singularize(word);
    	assertEquals(expect, result);
    	
    	word = "heroes"; expect = "hero"; result = WordUtil.singularize(word);
    	assertEquals(expect, result);
    	
    	word = "toes"; expect = "toe"; result = WordUtil.singularize(word);
    	assertEquals(expect, result);
    	
    	word = "series"; expect = "series"; result = WordUtil.singularize(word);
    	assertEquals(expect, result);
    	
    	word = "vertices"; expect = "vertex"; result = WordUtil.singularize(word);
    	assertEquals(expect, result);
    	
    	word = "stimuli"; expect = "stimulus"; result = WordUtil.singularize(word);
    	assertEquals(expect, result);
    	
    	word = "URLs"; expect = "URL"; result = WordUtil.singularize(word);
    	assertEquals(expect, result);
    	
    	word = "COOKS"; expect = "COOK"; result = WordUtil.singularize(word);
    	assertEquals(expect, result);
    	
    	word = "RECORDS"; expect = "RECORD"; result = WordUtil.singularize(word);
    	assertEquals(expect, result);
    	
    	word = "countries"; expect = "country"; result = WordUtil.singularize(word);
    	assertEquals(expect, result);
    	
    	word = "addresses"; expect = "address"; result = WordUtil.singularize(word);
    	assertEquals(expect, result);
    }
    
    @Test public void test_classify() {
    	String word = "";
    	String expect = "";
    	String result = "";
    	word = "people"; expect = "Person"; result = WordUtil.classify(word);
    	assertEquals(expect, result);

    	word = "line_items"; expect = "LineItem"; result = WordUtil.classify(word);
    	assertEquals(expect, result);
    }
    
    @Test public void test_ordinalize() {
    	int number = 0;
    	String expect = "";
    	String result = "";
    	number = 1; expect = "1st"; result = WordUtil.ordinalize(number);
    	assertEquals(expect, result);

    	number = 12; expect = "12nd"; result = WordUtil.ordinalize(number);
    	assertEquals(expect, result);

    	number = 100; expect = "100th"; result = WordUtil.ordinalize(number);
    	assertEquals(expect, result);

    	number = 1003; expect = "1003rd"; result = WordUtil.ordinalize(number);
    	assertEquals(expect, result);
    }
    
    @Test public void test_tableize() {
    	String word = "";
    	String expect = "";
    	String result = "";
    	word = "Person"; expect = "people"; result = WordUtil.tableize(word);
    	assertEquals(expect, result);

    	word = "LineItem"; expect = "line_items"; result = WordUtil.tableize(word);
    	assertEquals(expect, result);

    	word = "UserAccount"; expect = "user_accounts"; result = WordUtil.tableize(word);
    	assertEquals(expect, result);
    }
    
    @Test public void test_titleize() {
    	String word = "";
    	String expect = "";
    	String result = "";
    	//word = "/report/posts"; expect = "/report/posts"; result = WordUtil.titleize(word);
    	//assertEquals(expect, result);
    	
    	word = "Hello world"; expect = "Hello World"; result = WordUtil.titleize(word);
    	assertEquals(expect, result);
    	
    	word = "TITLE"; expect = "Title"; result = WordUtil.titleize(word);
    	assertEquals(expect, result);
    	
    	word = "ORDER_ID"; expect = "Order"; result = WordUtil.titleize(word);
    	assertEquals(expect, result);

    	word = "SCOOTER ch 1:  Java-ActiveRecordIsFun"; expect = "Scooter Ch 1:  Java Active Record Is Fun"; result = WordUtil.titleize(word);
    	assertEquals(expect, result);
    }
    
    @Test public void test_underscore() {
    	String word = "";
    	String expect = "";
    	String result = "";
    	
    	word = "Hello world"; expect = "hello world"; result = WordUtil.underscore(word);
    	assertEquals(expect, result);

    	word = "ActiveRecord"; expect = "active_record"; result = WordUtil.underscore(word);
    	assertEquals(expect, result);

    	word = "The RedCross"; expect = "the red_cross"; result = WordUtil.underscore(word);
    	assertEquals(expect, result);

    	word = "UserURL"; expect = "user_url"; result = WordUtil.underscore(word);
    	assertEquals(expect, result);

    	word = "ABCD"; expect = "abcd"; result = WordUtil.underscore(word);
    	assertEquals(expect, result);

    	word = "A-B-C-D"; expect = "a_b_c_d"; result = WordUtil.underscore(word);
    	assertEquals(expect, result);
    }
}
