package com.navy.walk.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.navy.walk.R;
import com.navy.walk.model.Country;

/**
 * @author Adam Chlupacek
 * @version 1.0
 * An adapter for the listView of countries, that is used in the right drawer
 */
public class DrawerAdapterCountries extends BaseAdapter {

    private Country[] countries;
    private Context context;

    /**
     * Default constructor that gets the reference for the context and array of countries
     * @param countries The countries in the application
     * @param context   The current context
     */
    public DrawerAdapterCountries(Country[] countries, Context context) {
        this.countries = countries;
        this.context = context;
    }

    /**
     * Returns the number of countries
     * @return The number of countries
     */
    @Override
    public int getCount() {
        return countries.length;
    }

    /**
     * Returns a country at given position
     * @param position position of the wanted country
     * @return  The country at the given position
     */
    @Override
    public Object getItem(int position) {
        return countries[position];
    }

    /**
     * Returns the position of the country as the id of the country
     * @param position position of the country
     * @return  The id/position of the country in the list
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Constructs the view for one element in the listView, in this case it sets the country name
     * @param position The position of the item within the adapter's data set of the item whose view
     *        we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *        is non-null and of an appropriate type before using. If it is not possible to convert
     *        this view to display the correct data, this method can create a new view.
     *        Heterogeneous lists can specify their number of view types, so that this View is
     *        always of the right type (see {@link #getViewTypeCount()} and
     *        {@link #getItemViewType(int)}).
     * @param parent The parent that this view will eventually be attached to
     * @return The view of the row.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        if(convertView == null){
            LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflator.inflate(R.layout.custom_row,null);
        }else
            row = convertView;

        TextView titletextView1 = (TextView) row.findViewById(R.id.textView1);
        titletextView1.setText(countries[position].getCountryName());

        return row;
    }
}
