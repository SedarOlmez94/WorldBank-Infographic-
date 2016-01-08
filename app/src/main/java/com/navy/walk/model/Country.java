package com.navy.walk.model;

import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;

/**
 * @author Adam Chlupacek
 * @version 1.0
 * A representation of a country
 */
public class Country {

    private String countryCode;
    private String countryName;
    private @IdRes int id;
    private @DrawableRes int drawable;

    /**
     * A default constructor for country
     * @param countryCode   The ISO country code for this country
     * @param countryName   The name of the country
     * @param id            The id of this country that will be used for fragment id
     * @param drawable      The image of this country
     */
    public Country(String countryCode, String countryName,@IdRes int id, @DrawableRes int drawable) {
        this.countryCode = countryCode;
        this.countryName = countryName;
        this.id = id;
        this.drawable = drawable;
    }

    /**
     * A getter for the country code, ISO format
     * @return The ISO country code
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * Getter for the country name
     * @return  The country name
     */
    public String getCountryName() {
        return countryName;
    }

    /**
     * Getter for the fragment id of this country
     * @return  The fragment id corresponding to this country
     */
    public @IdRes int getId() {
        return id;
    }

    /**
     * Getter for the image of this country
     * @return  The image of this country as a resource
     */
    public @DrawableRes int getDrawable() {
        return drawable;
    }
}
