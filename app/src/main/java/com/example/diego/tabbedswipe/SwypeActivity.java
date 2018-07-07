package com.example.diego.tabbedswipe;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class SwypeActivity extends AppCompatActivity {

    //    create file
    private static final String LOG_TAG_EXTERNAL_STORAGE = "EXTERNAL_STORAGE";
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 1;
    //    debug
    String TAG = "DIEGO";
    //    views
    private LinearLayout parentLayout;
    private int BRETES = 0;
    private boolean CARAVANA = false;
    private boolean MUESTRA = false;
    private int VACAS = 20;

    //  The {@link android.support.v4.view.PagerAdapter} that will provide
    //  fragments for each of the sections. We use a
    //  {@link FragmentPagerAdapter} derivative, which will keep every
    //  loaded fragment in memory. If this becomes too memory intensive, it
    //  may be best to switch to a
    //  {@link android.support.v4.app.FragmentStatePagerAdapter}.
    private SectionsPagerAdapter mSectionsPagerAdapter;
    //  The {@link ViewPager} that will host the section contents.
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate SwypeActivity");

        setContentView(R.layout.activity_swype);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            //The key argument here must match that used in the other activity
            BRETES = extras.getInt("BRETES");
            MUESTRA = extras.getBoolean("MUESTRA");
            CARAVANA = extras.getBoolean("CARAVANA");
            VACAS = extras.getInt("VACAS");
        }

//        Log.i(TAG,"BRETES "+BRETES);
//        Log.i(TAG,"MUESTRA "+MUESTRA);
//        Log.i(TAG,"CARAVANA "+CARAVANA);
//        Log.i(TAG,"VACAS "+VACAS);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), extras);

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        int pages = 6;
        if (VACAS / BRETES > 0)
            pages = (VACAS / BRETES) + 1;

        //Saves the state of N fragments
        mViewPager.setOffscreenPageLimit(pages);

        mViewPager.addOnPageChangeListener(new PageSelectedListener());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_swype, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_export) {
            onExport();
        }

        return super.onOptionsItemSelected(item);
    }

    public void onExport() {

        Log.i(TAG, "entro");

        String data = "";
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {

            Fragment f;
            FragmentManager fragmentManager = getSupportFragmentManager();
            String tag = getFragmentTag(mViewPager.getId(), i);
            f = getSupportFragmentManager().findFragmentByTag(tag);
            if (f == null) {
                //creates new instance
                f = mSectionsPagerAdapter.getItem(i);
            }

            DynamicFieldsFragment dff = (DynamicFieldsFragment) f;
            data += dff.recoverData();
            if (data.length() != 0 && Objects.equals(data.substring(data.length() - 1), ","))
                data += "\n";
            else data += ",\n";
        }

        Log.i(TAG, data);


        generateNoteOnSD("", data);


        Log.i(TAG, "salio");

    }

    private String getFragmentTag(int viewPagerId, int fragmentPosition) {
        return "android:switcher:" + viewPagerId + ":" + fragmentPosition;
    }

    public void generateNoteOnSD(String filename, String body) {

        try {
            if (ExternalStorageUtil.isExternalStorageMounted()) {

                // Check whether this app has write external storage permission or not.
                int writeExternalStoragePermission = ContextCompat.checkSelfPermission(SwypeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                // If do not grant write external storage permission.
                if (writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
                    // Request user to grant write external storage permission.
                    ActivityCompat.requestPermissions(SwypeActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
                } else {
                    // Save filename.txt file to /storage/emulated/0/DCIM folder
                    String publicDcimDirPath = ExternalStorageUtil.getPublicExternalStorageBaseDir(Environment.DIRECTORY_DCIM);

//                    current date
                    Date currentTime = Calendar.getInstance().getTime();
                    String date = android.text.format.DateFormat.format("d\\M\\yyyy_h:mm", currentTime).toString();

//                    TXT
                    String txtname = filename + date + ".txt";
                    File newFile = new File(publicDcimDirPath, txtname);
                    FileWriter fw = new FileWriter(newFile);
                    fw.write(body);
                    fw.flush();
                    fw.close();

//                    CSV
//                    String csvname = filename+"_"+date+".csv";
//                    File newCsv = new File(publicDcimDirPath, csvname);
//                    FileWriter csv = new FileWriter(newCsv);
//                    csv.write(body);
//                    csv.flush();
//                    csv.close();

                    Toast.makeText(getApplicationContext(), "Datos exportados! Ubicacion: " + newFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                }
            }

        } catch (Exception ex) {
            Log.e(LOG_TAG_EXTERNAL_STORAGE, ex.getMessage(), ex);
            Toast.makeText(getApplicationContext(), "No se pudo guardar el archivo... ", Toast.LENGTH_LONG).show();
        }
    }

    public void onBackPressed() {
        // do something here and don't write super.onBackPressed()
        Toast.makeText(getApplicationContext(), "Si habilito este boton vas a perder tu progreso...", Toast.LENGTH_LONG).show();
    }

    private class PageSelectedListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int position) {
            Log.i(TAG, "addOnPageChangeListener");

            //TODO save the DynamicFieldsFragment state
            int index = mViewPager.getCurrentItem();
            Log.i(TAG, "index " + index);
            Fragment f = mSectionsPagerAdapter.getItem(index);
            DynamicFieldsFragment dff = (DynamicFieldsFragment) f;
//            String test = dff.getData();
//            Log.i(TAG,"test "+test);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }


}
