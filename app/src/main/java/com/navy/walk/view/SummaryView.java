package com.navy.walk.view;

import android.app.Activity;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.navy.walk.R;
import com.navy.walk.database.CashingDB;
import com.navy.walk.database.NotInSchemaException;
import com.navy.walk.database.schema.DatabaseConstants;
import com.navy.walk.model.Country;
import com.navy.walk.model.Indicator;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Adam Chlupacek
 * @version 1.0
 * An mock up of {@link com.navy.walk.model.Indicator.DisplayIndicator}, for the static display of the summary
 * for all indicators with their daltas over the years
 */
public class SummaryView{

    /**
     * Displays the indicator summary
     * @param db            The caching database, where is stored the most recent data
     * @param country       The country to display
     * @param view          The view that is to be modified and displayed the data on
     * @param activity      The current activity
     * @param indicators    An array of all tracked indicators
     * @return              The modified view tha now represents the indicator
     */
    public static View display(CashingDB db, Country country, View view, Activity activity, Indicator[] indicators) {
        LinearLayout lview = ((LinearLayout) view);
        lview.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(activity);
        View summary = inflater.inflate(R.layout.summary, (ViewGroup) view, true);
        LinearLayout grid = (LinearLayout) summary.findViewById(R.id.summaries);

        //Sets the name of the country
        String countryName = String.format(activity.getResources().getString(R.string.country_name),country.getCountryCode(),country.getCountryName());
        ((TextView) view.findViewById(R.id.country_name)).setText(countryName);

        //Creates a primary key for querying
        Map<String,String> primaryKey = new HashMap<>();
        primaryKey.put(DatabaseConstants.COUTNTRY_CODE,country.getCountryCode());

        //Constructs all of the indicator fields
        for (Indicator indicator : indicators) {
            buildIndicator(indicator,db,grid,inflater,primaryKey,activity);
        }

        return lview;
    }

    /**
     * Build a summary view for one given indicator
     * @param indicator     The indicator that this summary is for
     * @param db            The caching database, where is stored the most recent data
     * @param grid          The view to which the summary is added once created
     * @param inflater      An inflater to create the view from xml
     * @param primaryKey    The primary key to query the database
     * @param activity      The current activity
     */
    private static void buildIndicator(Indicator indicator, CashingDB db, LinearLayout grid, LayoutInflater inflater, Map<String,String> primaryKey, Activity activity){
        View v = inflater.inflate(R.layout.summary_indicator, grid, false);
        ((TextView)v.findViewById(R.id.indicatorName)).setText(indicator.getIndicatorName());

        //Build the delta with oldest and youngest recorded year
        try {
            Pair<Integer,Float> top = null;
            Pair<Integer,Float> bottom = null;
            for (Map<String, String> stringStringMap : db.getWholeByPrimaryPartial(indicator.getTableName(), primaryKey)) {
                int year = Integer.parseInt(stringStringMap.get(DatabaseConstants.YEAR));
                float value = Float.parseFloat(stringStringMap.get(DatabaseConstants.VALUE));
                if (top == null)
                    top = new Pair<>(year,value);
                if (bottom == null)
                    bottom = new Pair<>(year,value);
                if (year<bottom.first)
                    bottom = new Pair<>(year,value);
                else if (year>top.first)
                    top = new Pair<>(year,value);
            }

            if (top == null)
                return;

            //Sets the text of delta and continues on
            String delta = String.format(activity.getResources().getString(R.string.delta),(top.second-bottom.second));
            ((TextView)v.findViewById(R.id.indicatorValue)).setText(delta);
            grid.addView(v);
        } catch (NotInSchemaException ignored) {}
    }
}
