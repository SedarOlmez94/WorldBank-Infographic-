package com.navy.walk.view;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.navy.walk.MainActivity;
import com.navy.walk.R;
import com.navy.walk.database.CashingDB;
import com.navy.walk.database.NotInSchemaException;
import com.navy.walk.database.schema.DatabaseConstants;
import com.navy.walk.model.Country;
import com.navy.walk.model.Indicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Adam Chlupacek
 * @version 1.0
 * A default display for the indicators, displays the name, description and a bar chart for this indicator
 */
public class BarChartDisplay implements Indicator.DisplayIndicator {

    private static final String UPPER_BOUND = "UPPER_BOUND";
    private static final String LOWER_BOUND = "LOWER_BOUND";

    private String tableName;
    private Indicator indicator;

    /**
     * Constructor that sets the name of the table used to query the database for the indicator data
     * @param tableName The name of the table to be queried for the data about this indicator
     */
    public BarChartDisplay(String tableName) {
        this.tableName = tableName;
    }

    /**
     * Registers the given indicator with this display object
     * @param indicator The indicator this object will represent
     */
    @Override
    public void setIndicator(Indicator indicator){
        this.indicator = indicator;
    }

    /**
     * Displays the indicator, that this object is register to, onto the screen
     *
     * Populates the data onto the graph, adds the country name and hooks up the spinners with controller
     *
     * @param db        The caching database, where is stored the most recent data
     * @param country   The country to display
     * @param view      The view that is to be modified and displayed the data on
     * @param activity  The current activity
     * @param args      The arguments for this country, that may adjust the way how the indicator is displayed
     * @return          The modified view tha now represents the indicator
     */
    @Override
    public View display(CashingDB db, final Country country, View view, Activity activity, final Map<String,String> args) {
        LinearLayout lview = ((LinearLayout) view);
        lview.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(activity);
        View waterView = inflater.inflate(R.layout.water_screen, (ViewGroup) view, true);

        //Getting the lower and upper bounds from args or default values
        int lowerBound = 0;
        int upperBound = 15;

        String posLower = args.get(LOWER_BOUND);
        if (posLower!= null)
            lowerBound = Integer.parseInt(posLower);

        String posUpper = args.get(UPPER_BOUND);
        if (posUpper!= null)
            upperBound = Integer.parseInt(posUpper);

        //Populating bar chart
        BarChart barChart = (BarChart) waterView.findViewById(R.id.water_chart);
        barChart.setData(constructBarData(getFromDB(db, country), lowerBound, upperBound));
        barChart.animateY(5000);

        //Populating the name of the country
        String countryName = String.format(activity.getResources().getString(R.string.country_name),country.getCountryCode(),country.getCountryName());
        ((TextView)view.findViewById(R.id.country_name)).setText(countryName);

        //Setting up background image
        view.findViewById(R.id.indicatorInfo).setBackgroundResource(country.getDrawable());

        //Setting up indicator information
        ((TextView)view.findViewById(R.id.indicatorName)).setText(indicator.getIndicatorName());
        ((TextView)view.findViewById(R.id.indicatorDescription)).setText(indicator.getDescription());

        //Setting up spinners
        setSpinner(country.getCountryCode(),(Spinner)view.findViewById(R.id.to),upperSelection(lowerBound),upperBound,UPPER_BOUND,activity,args);
        setSpinner(country.getCountryCode(),(Spinner)view.findViewById(R.id.from),lowerSelection(upperBound),lowerBound,LOWER_BOUND,activity,args);

        return lview;
    }


    /**
     * Gets the data for the years from the database, and stores them into a map year -> float value
     * @param db        The caching database with the data about the indicators
     * @param country   The country that is to be queried about this indicator
     * @return          A map with year->value pair
     */
    private Map<String,Float> getFromDB(CashingDB db, Country country){
        Map<String,String> primaryKeys = new HashMap<>();
        primaryKeys.put(DatabaseConstants.COUTNTRY_CODE, country.getCountryCode());

        Map<String,Float> values = new HashMap<>();
        try {
            List<Map<String,String>> dbValues = db.getWholeByPrimaryPartial(tableName, primaryKeys);
            for (Map<String, String> dbValue : dbValues) {
                if (!dbValue.containsKey(DatabaseConstants.VALUE) || !dbValue.containsKey(DatabaseConstants.YEAR))
                    continue;
                try {
                    values.put(dbValue.get(DatabaseConstants.YEAR),Float.parseFloat(dbValue.get(DatabaseConstants.VALUE)));
                } catch (NumberFormatException ignored) {} //IGNORES THIS, INVALID DATABASE DATA
            }
        } catch (NotInSchemaException e) {
            e.printStackTrace(); //INVALID REQUEST, no data will be retrieved
        }
        return values;
    }

    /**
     * Makes a BarData for the BarChart, that means it creates the different bars and labels to them
     * @param values        The values that are supposed to be displayed on the chart
     * @param lowerBound    The lower-bound of the time selection for the chart
     * @param upperBound    The upper-bound of the time selection for the chart
     * @return              The data to be displayed on the chart
     */
    private BarData constructBarData(Map<String,Float> values, int lowerBound, int upperBound){
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        for(int index = lowerBound ; index <= (upperBound); index ++ ) {

            String year = "20"+(index<10?"0"+index:""+index);
            Float entry = values.get(year);
            labels.add(index-lowerBound,year);
            if (entry == null)
                continue;
            entries.add(new BarEntry(entry, index-lowerBound));
        }

        BarDataSet dataset = new BarDataSet(entries, indicator.getIndicatorName());
        dataset.setColors(ColorTemplate.COLORFUL_COLORS);
        return new BarData(labels, dataset);
    }

    /**
     * A convenience method create a possible lower bound values for the lower bound value spinner,
     * takes the upper bound into consideration so that we do not flip the timeline
     * @param upperBound    The currently selected upper bound
     * @return              An array with possible lower bounds
     */
    private String[] lowerSelection(int upperBound){
        String[] res = new String[upperBound];
        for (int i = 0; i < res.length; i++) {
            res[i] = "20"+(i<10?"0"+i:""+i);
        }
        return res;
    }

    /**
     * A convenience method create a possible upper bound values for the upper bound value spinner,
     * takes the lower bound into consideration so that we do not flip the timeline
     * @param lowerBound    The currently selected lower bound
     * @return              An array with possible upper bounds
     */
    private String[] upperSelection(int lowerBound){
        String[] res = new String[16-lowerBound-1];
        for (int i = lowerBound+1; i < res.length+lowerBound+1; i++) {
            res[i-(lowerBound+1)] = "20"+(i<10?"0"+i:""+i);
        }
        return res;
    }

    /**
     * Returns an index of a given string in an array
     * @param array The array that contains the given string
     * @param find  The string to be found in the array
     * @return      The index of the string in the array or -1 if not found
     */
    private int getIndexOf(String[] array, String find){
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(find))
                return i;
        }
        return -1;
    }

    /**
     * Sets the spinner to represent the current selection, and hooks the spinner
     * with the controller {@link com.navy.walk.controller.IndicatorController}
     * @param cc            The ISO country code of the current country
     * @param spinner       The reference to the spinner that we are modifying
     * @param items         The array of possible selections for the spinner
     * @param selectedYear  The currently selected year on the spinner
     * @param boundName     The name of the bound that this spinner represents
     * @param activity      The current activity
     * @param args          The arguments for this country
     */
    private void setSpinner(final String cc,final Spinner spinner, final String[] items, int selectedYear, final String boundName, Activity activity, final Map<String,String> args){
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(activity,android.R.layout.simple_spinner_item, items);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setSelection(getIndexOf(items,"20"+(selectedYear<10?"0"+selectedYear:""+selectedYear)));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String newSel = items[position].substring(2);
                if (!newSel.equals(args.get(boundName))) {
                    MainActivity.controller.setArgForCountry(cc, boundName, items[position].substring(2));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
}
