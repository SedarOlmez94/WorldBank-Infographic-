package com.navy.walk.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.navy.walk.R;
import com.navy.walk.model.Indicator;

/**
 * @author  Sedar Olmez
 * @version 1.0
 * An adapter for the listView of indicators, that is used in the left drawer
 */
public class DrawerAdapterIndicators extends BaseAdapter {

    private Context context;
    private Indicator[] indicators;

    int[] images = {R.drawable.summmaryiconyellow,R.drawable.watericon2, R.drawable.blooddripicon, R.drawable.measlesiconbest};

    /**
     * Default constructor that gets the reference for the context and array of indicators
     * @param indicators    The indicators in the application
     * @param context       The current context
     */
    public DrawerAdapterIndicators(Context context, Indicator[] indicators){
        this.context = context;
        this.indicators = indicators;
    }
    /**
    * Returns the number of indicators plus the summary
    * @return The number of indicators plus the summary
    */
    @Override
    public int getCount() {
        return indicators.length+1;
    }

    /**
     * Returns an indicator at given position, adjusted so that if required summary it returns the first indicator
     * @param position position of the wanted indicator
     * @return  The indicator at the given position
     */
    @Override
    public Object getItem(int position) {
        if (position!=0)
            position--;
        return indicators[position];
    }

    /**
     * Returns the position of the indicator as the id of the indicator
     * @param position position of the indicator
     * @return  The id/position of the indicator in the list
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Constructs the view for one element in the listView, in this case it sets the indicator name and
     * an image that goes together with this indicator
     *
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
        }else{
            row = convertView;
        }

        TextView titletextView1 = (TextView) row.findViewById(R.id.textView1);
        ImageView titleImageView = (ImageView)row.findViewById(R.id.imageView1);

        if (position == 0)
            titletextView1.setText("Summary");
        else
            titletextView1.setText(indicators[position-1].getIndicatorName());

        titleImageView.setImageResource(images[position]);
        return row;
    }


}
