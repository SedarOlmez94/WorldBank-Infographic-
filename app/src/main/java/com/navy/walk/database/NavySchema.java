package com.navy.walk.database;

import com.navy.walk.database.schema.DataType;
import com.navy.walk.database.schema.DatabaseSchema;
import com.navy.walk.database.schema.TableSchema;
import com.navy.walk.model.Indicator;

import static com.navy.walk.database.schema.DatabaseConstants.COUTNTRY_CODE;
import static com.navy.walk.database.schema.DatabaseConstants.DATABASE_NAME;
import static com.navy.walk.database.schema.DatabaseConstants.VALUE;
import static com.navy.walk.database.schema.DatabaseConstants.YEAR;

/**
 * @author Adam Chlupacek
 * @version 1.0
 * The schema for our database
 */
public class NavySchema extends DatabaseSchema {
    /**
     * Constructor for the database schema, gives the database a name and current version
     *
     * @param indicators   The indicators that this database will store, auto generates a standard table
     *                     for all indicators in this array
     * @param version      The version of the database
     */
    public NavySchema(Indicator[] indicators,int version) {
        super(DATABASE_NAME, version);

        for (Indicator indicator : indicators) {
            TableSchema table = new TableSchema(COUTNTRY_CODE,indicator.getTableName());
            table.addPrimaryKey(YEAR);
            table.addValue(VALUE, DataType.Text);
            addTable(table);
        }
    }
}
