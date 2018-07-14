package com.example.diego.tabbedswipe;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class SwypeActivity extends AppCompatActivity implements DynamicFieldsFragment.myCommunicationInterface {

    //    create file
    private static final String LOG_TAG_EXTERNAL_STORAGE = "EXTERNAL_STORAGE";
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 1;

    //    debug
    String TAG = "DIEGO";

    //    views
    private int BRETES = 0;
    private boolean CARAVANA = false;
    private boolean MUESTRA = false;
    private int VACAS = 0;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    public void newPage(int pos) {

        Log.i(TAG, "newPage "+pos);

        // The user selected the headline of an article from the HeadlinesFragment
        // Do something here to display that article
//        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), null);
        mSectionsPagerAdapter.setN(pos + 1);
        mSectionsPagerAdapter.notifyDataSetChanged();
        mViewPager.setCurrentItem(pos + 1);
    }

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
//            VACAS = extras.getInt("VACAS");
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

        int pages = 1;
        if (BRETES != 0 && VACAS / BRETES > 0)
            pages = VACAS / BRETES;

        //Saves the state of N fragments
        //TODO independizarse de esto para guardar datos
        mViewPager.setOffscreenPageLimit(50);

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

        if (id == R.id.action_export) {
            onExport();
        }

        return super.onOptionsItemSelected(item);
    }

    public void onExport() {

        Log.i(TAG, "entro");

        String data = "";
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {

            data += getFragmentData(i);

        }

        Log.i(TAG, data);

        generateNoteOnSD("", data);

        Log.i(TAG, "salio");

    }

    //hacky way to get fragment
//    private String getFragmentTag(int viewPagerId, int fragmentPosition) {
//        return "android:switcher:" + viewPagerId + ":" + fragmentPosition;
//    }

    //TODO guardar en carpeta propia TambosCSV
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

                    //Dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    String mensaje = "Ubicacion: Almacenamiento local > Dipositivo > DCIM > "+ txtname;
                    builder.setMessage(mensaje)
                            .setTitle("Datos exportados!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //Cerrar app
                                    finish();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            }

        } catch (Exception ex) {
            Log.e(LOG_TAG_EXTERNAL_STORAGE, ex.getMessage(), ex);
            Toast.makeText(getApplicationContext(), "No se pudo guardar el archivo... ", Toast.LENGTH_LONG).show();
        }
    }

    public void onBackPressed() {
        // do something here and don't write super.onBackPressed()
        if (mViewPager.getCurrentItem() != 0) {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1,false);
        }else{
            Toast.makeText(getApplicationContext(), "Si habilito este boton vas a perder tu progreso...", Toast.LENGTH_LONG).show();
//            finish();
        }
    }

    private class PageSelectedListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int position) {
            Log.i(TAG, "addOnPageChangeListener");

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

//    public String getFragmentData(int i){

//    String data = "";
//        Fragment f;
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        String tag = getFragmentTag(mViewPager.getId(), i);
//        f = getSupportFragmentManager().findFragmentByTag(tag);

//        if (f == null) {
//            //creates new instance
//            f = mSectionsPagerAdapter.getItem(i);
//        }

//        return (DynamicFieldsFragment) f;

//    }

    public String getFragmentData(int i){

        String data = "";
        DynamicFieldsFragment f = (DynamicFieldsFragment) mSectionsPagerAdapter.getRegisteredFragment(i);

        if(f != null){

            data += f.recoverData();
            boolean endswithcomma = Objects.equals(data.substring(data.length() - 1), ",");
            if (data.length() != 0 && endswithcomma)
                data += "\n";
            else data += ",\n";

        }
        else {
            Log.i(TAG, "ALGO MALIO SAL " + i);
        }

        return data;

    }

}
