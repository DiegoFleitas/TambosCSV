package com.example.diego.tabbedswipe;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;


/**
 * Created by Diego on 24/06/2018.
 */

public class SectionsPagerAdapter extends SmartFragmentStatePagerAdapter   {

    //    debug
    private String TAG = "DIEGO";

    private int BRETES = 0;
    private boolean MUESTRA = false;
    private boolean CARAVANA = false;
    private int VACAS = 0;

    private static int NUM_ITEMS = 1;

    public void setN(int N) {
        NUM_ITEMS = N;
    }

    public SectionsPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    SectionsPagerAdapter(FragmentManager fm, Bundle extras) {
        super(fm);

        Log.i(TAG, "SectionsPagerAdapter");

        if (extras != null) {
            //The key argument here must match that used in the other activity
            BRETES = extras.getInt("BRETES");
            MUESTRA = extras.getBoolean("MUESTRA");
            CARAVANA = extras.getBoolean("CARAVANA");
            VACAS = extras.getInt("VACAS");

            if (VACAS / BRETES > 0)
                NUM_ITEMS = (VACAS / BRETES) + 1;

        }

//        Log.i(TAG,"BRETES "+BRETES);
//        Log.i(TAG,"MUESTRA "+MUESTRA);
//        Log.i(TAG,"CARAVANA "+CARAVANA);
//        Log.i(TAG,"VACAS "+VACAS);
    }

    @Override
    // Returns the fragment to display for that page
    public Fragment getItem(int position) {

        DynamicFieldsFragment frag = null;
////        frag = registeredFragments.get(position);
////        if (frag != null && position != 0) return frag;
        frag = new DynamicFieldsFragment();
        return frag.newInstance(position + 1, BRETES, MUESTRA, CARAVANA);

    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return NUM_ITEMS;
    }
}