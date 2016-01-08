package com.navy.walk;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.navy.walk.database.CashingDB;
import com.navy.walk.database.schema.DatabaseConstants;
import com.navy.walk.model.Country;
import com.navy.walk.model.Indicator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by adamellis on 01/12/2015.
 * A static class that fetches the desired indicators from the world bank
 */
public class APIFetching {

    private static int updateAfter;

    /**
     * Function that fetches the values from the world bank and then stores them in the caching database
     * @param context       The context where the the fetch is being made on
     * @param db            The database where to save the responses
     * @param countries     The countries that are being tracked
     * @param indicators    The indicators that are being tracked
     */
    public static void fetch(Context context, final CashingDB db, Country[] countries, Indicator[] indicators){
        RequestQueue queue = Volley.newRequestQueue(context);
        updateAfter = countries.length * indicators.length;
        for (final Indicator indicator:indicators) {
            for (Country country : countries) {

                String url = "http://api.worldbank.org/countries/" + country.getCountryCode() + "/indicators/" + indicator.getIndicatorId() + "?format=json&date=2000:2015";
                JsonArrayRequest jsonRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        indicator.getParse().parse(response, db);
                        forceUpdate();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Fail");
                        forceUpdate();
                    }
                });
                queue.add(jsonRequest);
            }
        }
    }

    /**
     * A convenience function that force updates the fragments after all data is fetched
     */
    private static void forceUpdate(){
        updateAfter--;
        if (updateAfter == 0)
            MainActivity.controller.forceUpdate();
    }
}
