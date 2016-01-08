package com.navy.walk.parsers;

import com.navy.walk.database.CashingDB;
import com.navy.walk.database.schema.DatabaseConstants;
import com.navy.walk.model.Indicator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Adam Chlupacek
 * @version 1.0
 * A parser for default structure of the response from the server
 */
public class DefaultParser implements Indicator.ParseIndicator {
    private String tableName;

    /**
     * Constructs this parser and sets the table to which save the parsed data
     * @param tableName The name of table this parser should store data to
     */
    public DefaultParser(String tableName) {
        this.tableName = tableName;
    }

    /**
     * Parses the data from the server and then stores them in the database
     * @param data  The data from the server
     * @param db    The database that is used for caching
     */
    @Override
    public void parse(JSONArray data, CashingDB db) {
        try {
            String primaryKey = data.getJSONArray(1).getJSONObject(1).getJSONObject("country").get("id") + "";

            JSONArray indicators = data.getJSONArray(1);
            for (int j = 0; j < indicators.length(); j++) {
                Map<String, String> map = new TreeMap<String, String>();
                Map<String, String> primary = new HashMap<>();

                JSONObject currentIndicator = indicators.getJSONObject(j);
                String value = currentIndicator.get("value").toString();
                String date = currentIndicator.get("date").toString();

                //So that we do not populate database with random false data
                if (value.contains("null"))
                    continue;

                //Puts the values and primary keys to maps they belong to
                map.put(DatabaseConstants.VALUE, value);
                primary.put(DatabaseConstants.COUTNTRY_CODE, primaryKey);
                primary.put(DatabaseConstants.YEAR, date);

                db.insert(map, primary, tableName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            System.out.println("Error in API parsing for: " + data.toString());
        }
    }
}
