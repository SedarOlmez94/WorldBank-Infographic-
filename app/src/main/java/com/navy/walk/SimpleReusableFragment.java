package com.navy.walk;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Is used to create a fragment that is populated depending on what is the current selected indicator
 */
public class SimpleReusableFragment extends Fragment {

    public SimpleReusableFragment() {
        // Required empty public constructor
    }

    /**
     * Populats the fragment with required view according to the {@link com.navy.walk.controller.IndicatorController}
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return Return the View for the fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.page_fragment_layout, container, false);
        return MainActivity.controller.getViewForCountry(getArguments().getInt("pos", 0), view);
    }

    /**
     * Creates an instance of the fragment and then sets its creating arguments
     * @param position The position of this fragment in the viewPager
     * @return The newly created fragment
     */
    public static SimpleReusableFragment newInstance(int position){
        SimpleReusableFragment newSimpleReusableFragment = new SimpleReusableFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("pos", position);
        newSimpleReusableFragment.setArguments(bundle);
        return newSimpleReusableFragment;
    }
}





