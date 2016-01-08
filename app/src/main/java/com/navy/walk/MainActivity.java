package com.navy.walk;


import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.navy.walk.controller.IndicatorController;
import com.navy.walk.database.CashingDB;
import com.navy.walk.database.NavySchema;
import com.navy.walk.database.schema.DatabaseConstants;
import com.navy.walk.model.Country;
import com.navy.walk.model.Indicator;
import com.navy.walk.view.DrawerAdapterCountries;
import com.navy.walk.view.DrawerAdapterIndicators;
import com.navy.walk.view.SwipeAdapter;

/**
 * The main activity that is controlling this whole application, we enter and leave on this activity
 */
public class MainActivity extends AppCompatActivity {

    /**
     * The countries that are being tracked, ie countries that we fetch the data for
     */
    private Country[] trackedCountries = {
            new Country("AO","Angola",R.id.AO,R.drawable.angolanew)
            , new Country("ET","Ethiopia",R.id.ET,R.drawable.ethiopianew)
            , new Country("GH","Ghana",R.id.GH,R.drawable.ghananew)
            , new Country("SD","Sudan",R.id.SD,R.drawable.sudannew)
            , new Country("TZ","Tanzania",R.id.KE,R.drawable.tanzanianew)
    };

    /**
     * Tracked indicators, ie indicators for the countries that are being fetched and displayed
     */
    private Indicator[] trackedIndicators = {
            new Indicator("Water access (%)", "SH.H2O.SAFE.ZS", R.string.waterDesc, DatabaseConstants.WATER_TABLE)
            , new Indicator("Prevalence of anemia among children (%)", "SH.ANM.CHLD.ZS", R.string.anemiaDesc, DatabaseConstants.ANEMIA_CHILDREN_TABLE)
            , new Indicator("Immunisation measles (%)", "SH.IMM.MEAS", R.string.measlesDesc, DatabaseConstants.IMUNIT_MEASELS_TABLE)
    };


    public static IndicatorController controller;

    /**
     * Native android function, called when creating this activity, as such serves as constructor of sorts
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Set up the basic controlling features of the app
        CashingDB db = new CashingDB(this, new NavySchema(trackedIndicators, 3)); //Change int whenever you make change to schema
        controller = new IndicatorController(1, db, trackedCountries, trackedIndicators, this);
        APIFetching.fetch(this, db, trackedCountries, trackedIndicators);

        //Initialise drawer with indicators
        ListView indicatorDrawer = (ListView) findViewById(R.id.drawerList);
        indicatorDrawer.setAdapter(new DrawerAdapterIndicators(this, trackedIndicators));
        indicatorDrawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                controller.setIndicator(position);
                ((DrawerLayout) findViewById(R.id.swipeMain)).closeDrawers();
            }
        });

        //Initialise the fragment view pager
        final ViewPager pager = ((ViewPager) findViewById(R.id.view_pager));
        pager.setAdapter(new SwipeAdapter(getSupportFragmentManager(), trackedCountries.length));
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private int past = 0;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}//NO-OP

            @Override
            public void onPageSelected(int position) {
                controller.resetCountry(past);
                past = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {} //NO-OP
        });

        //Initialise drawer with countries
        ListView countriesDrawer = (ListView) findViewById(R.id.countriesListView);
        countriesDrawer.setAdapter(new DrawerAdapterCountries(trackedCountries,this));
        countriesDrawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pager.setCurrentItem(position, true);
                ((DrawerLayout) findViewById(R.id.swipeMain)).closeDrawers();
            }
        });

        //Adds the possibility to open countries drawer with a button
        findViewById(R.id.right_drawer_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DrawerLayout) findViewById(R.id.swipeMain)).openDrawer(Gravity.RIGHT);
            }
        });

        //Adds the possibility to open indicator drawer with a button
        findViewById(R.id.left_drawer_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DrawerLayout) findViewById(R.id.swipeMain)).openDrawer(Gravity.LEFT);
            }
        });

        Toast.makeText(this,"Swipe right/left for countries",Toast.LENGTH_LONG).show();
    }
}





