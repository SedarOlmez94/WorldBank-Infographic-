package com.navy.walk;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.navy.walk.database.CashingDB;
import com.navy.walk.database.NotInSchemaException;
import com.navy.walk.database.schema.DataType;
import com.navy.walk.database.schema.DatabaseSchema;
import com.navy.walk.database.schema.TableSchema;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Adam Chlupacek
 * @version 1.0
 * Test for the database sqllite abstraction and cashing
 */
public class CachingInstrumentationTest extends ActivityInstrumentationTestCase2<MainActivity> {
    public CachingInstrumentationTest() {
        super(MainActivity.class);
    }

    CashingDB db;

    /**
     * Sets up the class, creates a new database, drops the old one and populates the new database
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        DatabaseSchema schema = new DatabaseSchema("ANIMALS",Math.abs((int)System.currentTimeMillis()));
        TableSchema catTable = new TableSchema("CAT_ID","CATS");

        catTable.addPrimaryKey("MOTHER_CAT_ID");

        catTable.addValue("age", DataType.Text);
        catTable.addValue("colour", DataType.Text);

        schema.addTable(catTable);

        db = new CashingDB(getActivity(),schema);

        for (int i = 0; i < 5; i++) {
            Map<String,String> values = new HashMap<>();
            values.put("age", i+"a");
            values.put("colour", i+"c");

            Map<String,String> primary = new HashMap<>();
            primary.put("CAT_ID", i+"id");
            primary.put("MOTHER_CAT_ID",i+"m");
            db.insert(values, primary, "CATS");
        }

        for (int i = 5; i < 10; i++) {
            Map<String,String> primary = new HashMap<>();
            primary.put("CAT_ID", "1id");
            primary.put("MOTHER_CAT_ID", i+"m");

            Map<String,String> values = new HashMap<>();
            values.put("age", i+"a");
            values.put("colour", i+"c");
            db.insert(values,primary,"CATS");

        }
    }

    /**
     * Tests inserting to the database
     */
    public void testInsert(){
        Map<String,String> values = new HashMap<>();
        values.put("age", "12");
        values.put("colour", "blackish");

        Map<String,String> primary = new HashMap<>();
        primary.put("CAT_ID", "123ADF3");
        primary.put("MOTHER_CAT_ID", "123ADF2");
        try {
            db.insert(values, primary, "CATS");
            Map<String,String> rets = db.getWholeByPrimary("CATS",primary);
            assertEquals("blackish",rets.get("colour"));
            assertEquals("12",rets.get("age"));
            assertEquals("123ADF3",rets.get("CAT_ID"));
            assertEquals("123ADF2",rets.get("MOTHER_CAT_ID"));
        } catch (NotInSchemaException e) {
            e.printStackTrace();
            fail("Schema not valid");
        }
    }

    /**
     * Tests getting a value for column where primary for all rows, not necessary the whole primary key has to be present
     */
    public void testAllColumnByPrimary(){
        try {
            Map<String,String> primary = new HashMap<>();
            primary.put("CAT_ID", "1id");

            List<String> rets = db.getAllColumnByPrimary("CATS", primary, "age");
            assertEquals(6,rets.size());
            assertEquals("1a",rets.get(0));
            assertEquals("5a", rets.get(1));
            assertEquals("6a",rets.get(2));
            assertEquals("7a",rets.get(3));
            assertEquals("8a",rets.get(4));
            assertEquals("9a",rets.get(5));


        } catch (NotInSchemaException e) {
            e.printStackTrace();
            fail("Schema not valid");
        }
    }

    /**
     * Tests getting a value for column where primary
     */
    public void testColumnByPrimary(){
        try {
            Map<String,String> primary = new HashMap<>();
            primary.put("CAT_ID", "1id");
            primary.put("MOTHER_CAT_ID", "1m");

            assertEquals("1a",db.getColumnByPrimary("CATS", primary, "age"));
        } catch (NotInSchemaException e) {
            e.printStackTrace();
            fail("Schema not valid");
        }
    }

    /**
     * Getting a columns for all rows in a table
     */
    public void testGetAllColumn(){
        try {
            List<String> rets = db.getAllColumn("CATS", "age");
            assertEquals(10,rets.size());
            for (int i = 0; i < rets.size(); i++) {
                assertEquals(i+"a",rets.get(i));
            }

        } catch (NotInSchemaException e) {
            e.printStackTrace();
            fail("Schema not valid");
        }
    }

    /**
     * Tests getting all rows in a table
     */
    public void testGetAll(){
        try {
            List<Map<String,String>> rets = db.getAll("CATS");
            assertEquals(10,rets.size());
            for (int i = 0; i < rets.size(); i++) {
                Map<String,String> cat = rets.get(i);
                if (i>1 && i < 7)
                    assertEquals("1id",cat.get("CAT_ID"));
                else if(i >= 7)
                    assertEquals((i-5)+"id",cat.get("CAT_ID"));
                else
                    assertEquals(i+"id",cat.get("CAT_ID"));
            }

        } catch (NotInSchemaException e) {
            e.printStackTrace();
            fail("Schema not valid");
        }
    }
}
