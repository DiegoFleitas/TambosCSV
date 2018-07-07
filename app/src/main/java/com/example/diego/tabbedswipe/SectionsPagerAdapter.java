package com.example.diego.tabbedswipe;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;


/**
 * Created by Diego on 24/06/2018.
 */

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    //    debug
    private String TAG = "DIEGO";

    private int BRETES = 0;
    private boolean MUESTRA = false;
    private boolean CARAVANA = false;
    private int VACAS = 0;

    private int pages = 6;

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
                pages = (VACAS / BRETES) + 1;

        }

//        Log.i(TAG,"BRETES "+BRETES);
//        Log.i(TAG,"MUESTRA "+MUESTRA);
//        Log.i(TAG,"CARAVANA "+CARAVANA);
//        Log.i(TAG,"VACAS "+VACAS);
    }

    protected void onCreate(Bundle savedInstanceState) {

    }

    @Override
    public Fragment getItem(int position) {

        // getItem is called to instantiate the fragment for the given page.
        // Return a DynamicFieldsFragment
        Integer var = position;
        DynamicFieldsFragment frag = new DynamicFieldsFragment();
        return frag.newInstance(position + 1, BRETES, MUESTRA, CARAVANA);
    }

    @Override
    public int getCount() {
        return pages;
    }
}