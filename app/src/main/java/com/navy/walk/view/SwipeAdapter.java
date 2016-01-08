package com.navy.walk.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.navy.walk.SimpleReusableFragment;
import com.navy.walk.model.Country;


/**
 * Created by SedarOlmez on 28/11/2015.
 * An adapter that handles the number of fragments and what the fragment should be created on given position
 */
public class SwipeAdapter extends FragmentPagerAdapter {

    private int countryNo;

    /**
     * Default constructor for the swipe adapter, sets the number of countries ie the number of fragments
     * @param fm        The fragment manager of this activity
     * @param countryNo The number of countries
     */
    public SwipeAdapter(FragmentManager fm, int countryNo) {
        super(fm);
        this.countryNo = countryNo;
    }

    /**
     * Gets a fragment at given position, in our case its all the same class, only with different
     * position argument.
     * @param position Position of the fragment to be returned
     * @return  Returns the fragment at the given position
     */
    @Override
    public Fragment getItem(int position) {

        if (position<0 || position > countryNo-1)
            return SimpleReusableFragment.newInstance(0);
        else
            return SimpleReusableFragment.newInstance(position);
    }

    /**
     * Getter for number of fragments ie the number of countries in our case
     * @return  The number of fragments
     */
    @Override
    public int getCount() {
        return countryNo;
    }
}
