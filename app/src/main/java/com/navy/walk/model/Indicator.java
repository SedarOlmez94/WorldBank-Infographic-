package com.navy.walk.model;

import android.app.Activity;
import android.view.View;

import com.navy.walk.database.CashingDB;
import com.navy.walk.parsers.DefaultParser;
import com.navy.walk.view.BarChartDisplay;

import org.json.JSONArray;

import java.util.Map;

/**
 * @author Adam Chlupacek
 * @version 1.0
 * A representation of an indicator, including a description of how to pare this indicator from server
 * response and how to display the indicator on the screen
 */
public class Indicator {

    private String indicatorName;
    private String indicatorId;
    private int description;
    private ParseIndicator parse;
    private DisplayIndicator display;
    private String tableName;

    /**
     * The default constructor for the indicator where you have to specify all of the values manually
     * @param indicatorName The name of the indicator
     * @param indicatorId   The World bank API of the indicator
     * @param description   The description of this indicator
     * @param parse         A description how to parse and store the response from server to the database
     * @param display       A description how to display the given indicator to the screen
     * @param tableName     The name of the table of this indicator in the database
     */
    public Indicator(String indicatorName, String indicatorId, int description, ParseIndicator parse, DisplayIndicator display, String tableName) {
        this.indicatorName = indicatorName;
        this.indicatorId = indicatorId;
        this.description = description;
        this.parse = parse;
        this.display = display;
        this.tableName = tableName;
        this.display.setIndicator(this);
    }

    /**
     * A convenience constructor that sets the default parser {@link DefaultParser} and barChart display {@link BarChartDisplay}
     * @param indicatorName The name of the indicator
     * @param indicatorId   The World bank API of the indicator
     * @param description   The description of this indicator
     * @param tableName     The name of the table of this indicator in the database
     */
    public Indicator(String indicatorName, String indicatorId, int description, String tableName) {
        this(indicatorName, indicatorId, description, new DefaultParser(tableName), new BarChartDisplay(tableName),tableName);
    }

    /**
     * Getter for the name of the indicator
     * @return The name of the indicator
     */
    public String getIndicatorName() {
        return indicatorName;
    }

    /**
     * Getter for the world bank id of the indicator
     * @return The world bank id of the indicator
     */
    public String getIndicatorId() {
        return indicatorId;
    }

    /**
     * Getter for the description of the indicator
     * @return The description of the indicator
     */
    public int getDescription() {
        return description;
    }

    /**
     * Getter for the database table name of the indicator
     * @return The database table name of the indicator
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * A getter for the parser of the server response for this indicator
     * @return The parser of the server response for this indicator
     */
    public ParseIndicator getParse() {
        return parse;
    }

    /**
     * A getter for the display for this indicator
     * @return The display for this indicator
     */
    public DisplayIndicator getDisplay() {
        return display;
    }

    /**
     * A class that describes how to deal with the response from the server and how to store
     * the accepted data in the database
     */
    public interface ParseIndicator{
        /**
         * Parses the data from the server and then stores them in the database
         * @param data  The data from the server
         * @param db    The database that is used for caching
         */
        void parse(JSONArray data, CashingDB db);
    }

    /**
     * A class that describes how to display particular indicator on the screen
     */
    public interface DisplayIndicator{
        /**
         * Displays the indicator, that this object is register to, onto the screen
         * @param db        The caching database, where is stored the most recent data
         * @param country   The country to display
         * @param view      The view that is to be modified and displayed the data on
         * @param activity  The current activity
         * @param args      The arguments for this country, that may adjust the way how the indicator is displayed
         * @return          The modified view tha now represents the indicator
         */
        View display(CashingDB db, Country country, View view, Activity activity, Map<String,String> args);

        /**
         * Registers the given indicator with this display object
         * @param indicator The indicator this object will represent
         */
        void setIndicator(Indicator indicator);
    }
}
