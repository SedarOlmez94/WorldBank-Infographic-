package com.navy.walk.controller;

import android.app.Activity;
import android.support.annotation.IdRes;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.navy.walk.R;
import com.navy.walk.database.CashingDB;
import com.navy.walk.model.Country;
import com.navy.walk.model.Indicator;
import com.navy.walk.view.SummaryView;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Adam Chlupacek
 * @version 1.0
 * An indicator controller, controls the display of indicators for particular country.
 * Updates the fragment views according to current selected indicator
 */
public class IndicatorController {

    private int selectedIndicator;
    private CashingDB db;
    private Country[] countries;
    private Indicator[] indicators;
    private Activity activity;
    private Map<String,Map<String,String>> arguments;

    /**
     * Constructor for indicator controller, initialises all its values and saves passed values
     * @param selectedIndicator The initial selected indicator
     * @param db                A reference to the caching database
     * @param countries         An array of tracked countries by this application
     * @param indicators        An array of tracked indicators by this application
     * @param activity          The current activity
     */
    public IndicatorController(int selectedIndicator, CashingDB db, Country[] countries, Indicator[] indicators, Activity activity) {
        this.selectedIndicator = selectedIndicator;
        this.db = db;
        this.countries = countries;
        this.indicators = indicators;
        this.activity = activity;
        this.arguments = new HashMap<>();
    }

    /**
     * Constructs a view for given country, the country is specified by its index in the array
     * @param countryIdx    The index in the tracked array of the country to be display
     * @param view          The fragment view to be modified
     * @return              An updated fragment view, now containing the data needed for the current indicator
     */
    public View getViewForCountry(int countryIdx, View view){
        Country country = countries[countryIdx];
        if (selectedIndicator !=0) {
            Map<String,String> args = arguments.get(country.getCountryCode());
            if (args == null)
                args = new HashMap<>();
            View res = indicators[selectedIndicator - 1].getDisplay().display(db, country, view, activity, args);
            res.setId(country.getId());
            return res;
        }
        else{
            View res = SummaryView.display(db, country, view, activity, indicators);
            res.setId(country.getId());
            return res;
        }
    }

    /**
     * Changes the indicator to a given indicator, the indicator is referenced by its index in the
     * tracked indicator array, once the new indicator is set, it updates all of the current fragments to
     * display the proper indicator
     * @param selected The index of indicator to be set as active
     */
    public void setIndicator(int selected){
        selectedIndicator = selected;
        ViewPager pager = (ViewPager)activity.findViewById(R.id.view_pager);
        for (int i = 0; i < pager.getChildCount(); i++) {
            View child = pager.getChildAt(i);
            Country country = countryForId(child.getId());

            if (country == null)
                continue;

            Map<String,String> args = arguments.get(country.getCountryCode());
            if (args == null)
                args = new HashMap<>();

            if (selectedIndicator != 0){
                indicators[selectedIndicator -1].getDisplay().display(db, country, child, activity, args);
            }else {
                SummaryView.display(db,country,child,activity,indicators);
            }
        }
    }

    /**
     * Sets an arguments for a country, that may affect its rendering. Used when the fragment needs to react
     * to some kind of user input
     * @param cc    The country code of the country that will have the arguments assigned
     * @param key   The key under which the argument will be stored
     * @param value The value of the argument
     */
    public void setArgForCountry(String cc, String key, String value){
        Map<String,String> mem = arguments.get(cc);
        if (mem == null)
            mem = new HashMap<>();

        mem.put(key, value);
        arguments.put(cc,mem);
        updateFragment(countryForCC(cc));
    }

    /**
     * Retest the arguments for given country, and then updates the fragment of that country
     * @param countryId The index in array of the country to reseted
     */
    public void resetCountry(int countryId){
        arguments.remove(countries[countryId].getCountryCode());
        updateFragment(countries[countryId]);
    }

    /**
     * Updates a fragment that belongs to a given country
     * @param country   Country that needs its fragment to update
     */
    private void updateFragment(Country country){
        Map<String,String> args = arguments.get(country.getCountryCode());
        if (args == null)
            args = new HashMap<>();
        View view = activity.findViewById(country.getId());
        if (view == null)
            return;

        if (selectedIndicator==0)
            SummaryView.display(db,country,view,activity,indicators);
        else
            indicators[selectedIndicator -1].getDisplay().display(db, country, view, activity, args);
    }

    /**
     * Convenience function that can give a country for its ID
     * @param id    The ID of the country
     * @return      Country with given ID
     */
    private Country countryForId(@IdRes int id){
        for (Country country : countries) {
            if (country.getId() == id)
                return country;
        }
        return null;
    }

    /**
     * Convenience function that can give a country for its coutry code
     * @param cc    The country code of the country
     * @return      Country with given country code
     */
    private Country countryForCC(String cc){
        for (Country country : countries) {
            if (country.getCountryCode().equals(cc))
                return country;
        }
        return null;
    }

    /**
     * Force updates all of the currently loaded fragments, by setting the indicator to the current indicator
     */
    public void forceUpdate() {
        setIndicator(selectedIndicator);
    }
}
