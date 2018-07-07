package com.example.diego.tabbedswipe;
//package com.androidtutorialpoint.mycontacts;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Diego on 12/05/2018.
 */

public class DynamicFields extends AppCompatActivity {

    //    create file
    private static final String LOG_TAG_EXTERNAL_STORAGE = "EXTERNAL_STORAGE";
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 1;
    //    debug
    String TAG = "DIEGO";
    //    views
    private LinearLayout parentLayout;
    private int BRETES = 0;
    private boolean CARAVANA = true;
    private boolean MUESTRA = true;
    private int fieldsAdded = 0;
    private boolean wasDivided = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynfields);
        parentLayout = findViewById(R.id.parent_layout);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            //The key argument here must match that used in the other activity
            CARAVANA = extras.getBoolean("CARAVANA");
            MUESTRA = extras.getBoolean("MUESTRA");
            BRETES = extras.getInt("BRETES");
        }

        if (BRETES > 0) {
            addField(BRETES);
        } else
            Toast.makeText(getApplicationContext(), "Si no pones cuantos bretes son va a estar dificil...", Toast.LENGTH_LONG).show();

    }

    public void addField(int n) {
        for (int i = n; i > 0; i--) {
            onAddField(null);
        }
    }

    public void newDivider(int n) {
        removeDivider(n - 1);
        addDivider(n);
        wasDivided = true;
    }

    public void removeDivider(int total) {
        View v = parentLayout.getChildAt(total / 2);
        if (v != null) v.setPadding(0, 0, 0, 0);
    }

    public void addDivider(int total) {
        View v = parentLayout.getChildAt(total / 2);
        if (v != null) v.setPadding(0, 150, 0, 0);
    }

    public void onAddField(View v) {
        Log.i(TAG, "onAddField DynamicFields");

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView;
        if (CARAVANA && MUESTRA) {
            rowView = inflater.inflate(R.layout.field_cm, null);
        } else if (MUESTRA || CARAVANA) {
            if (MUESTRA) {
                rowView = inflater.inflate(R.layout.field_m, null);
            } else {
                rowView = inflater.inflate(R.layout.field_c, null);
            }
        } else {
            rowView = inflater.inflate(R.layout.field, null);
        }

        //#region setear id
//        set id programmaticaly bc equal ids fucks up content?
//        LinearLayout ll = (LinearLayout) rowView;
//        for (int i = 0; i < (int) ll.getChildCount(); i++) {
//            View child = ll.getChildAt(i);
//            if(i == 1){
//                child.setId(R.id.litros);
//            }
//            if(i == 2){
//                child.setId(R.id.caravana);
//            }
//            if(i == 3){
//                child.setId(R.id.muestra);
//            }
//        }
        //   #endregion

        int count = parentLayout.getChildCount();

        TextView myAwesomeTextView = rowView.findViewById(R.id.textview_nro);
        myAwesomeTextView.setText("" + count);

        // Add the new row before the add field_cm button.
        parentLayout.addView(rowView, count - 1);
//        division
        fieldsAdded++;
        if (wasDivided) newDivider(fieldsAdded);
        if (fieldsAdded == BRETES) newDivider(BRETES);
    }

    public void onDelete(View v) {
        int count = parentLayout.getChildCount();
        if (count >= 2) parentLayout.removeView(parentLayout.getChildAt(count - 2));
        else new AlertDialog.Builder(v.getContext()).setMessage("Primero agrega uno!").show();
    }

    public void onExport(View v) {
        Log.i(TAG, "entro");
//        generateNoteOnSD("datos_");
        generateNoteOnSD("");
        Log.i(TAG, "salio");
    }

    public String getData() {
        EditText text;
        String litros = "", caravana = "", muestra = "", brete = "", body = "", lote = "1";
        try {
            if (CARAVANA && MUESTRA) {
                body += "brete,litros,caravana,muestra,lote,\n";
                //            every field_cm group
                int count = parentLayout.getChildCount();
                Log.i(TAG, "count " + count);
                //            skip buttons
                for (int i = 0; i < count - 1; i++) {
                    LinearLayout field = (LinearLayout) parentLayout.getChildAt(i);
                    //                every editText
                    int countfield = field.getChildCount();
                    Log.i(TAG, "countfield " + countfield);
                    for (int j = 0; j < countfield; j++) {
                        View child = field.getChildAt(j);
                        if (j == 0) {
                            brete = "" + (i + 1);
                            Log.i(TAG, "brete " + brete);
                        }
                        if (j == 1) {
                            text = (EditText) child;
                            litros = text.getText().toString();
                            Log.i(TAG, "litros " + litros);
                        }
                        if (j == 2) {
                            text = (EditText) child;
                            caravana = text.getText().toString();
                            Log.i(TAG, "caravana " + caravana);
                        }
                        if (j == 3) {
                            text = (EditText) child;
                            muestra = text.getText().toString();
                            Log.i(TAG, "muestra " + muestra);
                            body += brete + "," + litros + "," + caravana + "," + muestra + "," + lote + ",\n";
                            Log.i(TAG, body);
                        }
                    }
                }
            } else if (MUESTRA || CARAVANA) {
                if (MUESTRA) {

                    body += "brete,litros,muestra,lote,\n";

                    //            every field_cm group
                    int count = parentLayout.getChildCount();
                    Log.i(TAG, "count " + count);
                    //            skip buttons
                    for (int i = 0; i < count - 1; i++) {
                        LinearLayout field = (LinearLayout) parentLayout.getChildAt(i);
                        //                every editText
                        int countfield = field.getChildCount();
                        Log.i(TAG, "countfield " + countfield);
                        for (int j = 0; j < countfield; j++) {
                            View child = field.getChildAt(j);
                            if (j == 0) {
                                brete = "" + (i + 1);
                                Log.i(TAG, "brete " + brete);
                            }
                            if (j == 1) {
                                text = (EditText) child;
                                litros = text.getText().toString();
                                Log.i(TAG, "litros " + litros);
                            }
                            if (j == 2) {
                                text = (EditText) child;
                                muestra = text.getText().toString();
                                Log.i(TAG, "muestra " + muestra);
                                body += brete + "," + litros + "," + muestra + "," + lote + ",\n";
                                Log.i(TAG, body);
                            }
                        }
                    }
                }
                if (CARAVANA) {

                    body += "brete,litros,caravana,lote,\n";

                    //            every field_cm group
                    int count = parentLayout.getChildCount();
                    Log.i(TAG, "count " + count);
                    //            skip buttons
                    for (int i = 0; i < count - 1; i++) {
                        LinearLayout field = (LinearLayout) parentLayout.getChildAt(i);
                        //                every editText
                        int countfield = field.getChildCount();
                        Log.i(TAG, "countfield " + countfield);
                        for (int j = 0; j < countfield; j++) {
                            View child = field.getChildAt(j);
                            if (j == 0) {
                                brete = "" + (i + 1);
                                Log.i(TAG, "brete " + brete);
                            }
                            if (j == 1) {
                                text = (EditText) child;
                                litros = text.getText().toString();
                                Log.i(TAG, "litros " + litros);
                            }
                            if (j == 2) {
                                text = (EditText) child;
                                caravana = text.getText().toString();
                                Log.i(TAG, "caravana " + caravana);
                                body += brete + "," + litros + "," + caravana + "," + lote + ",\n";
                                Log.i(TAG, body);
                            }
                        }
                    }
                }
            } else {
                Log.i(TAG, "No se setearon MUESTRA y CARAVANA");

                body += "brete,litros,lote,\n";

                //            every field_cm group
                int count = parentLayout.getChildCount();
                Log.i(TAG, "count " + count);
                //            skip buttons
                for (int i = 0; i < count - 1; i++) {
                    LinearLayout field = (LinearLayout) parentLayout.getChildAt(i);
                    //                every editText
                    int countfield = field.getChildCount();
                    Log.i(TAG, "countfield " + countfield);
                    for (int j = 0; j < countfield; j++) {
                        View child = field.getChildAt(j);
                        if (j == 0) {
                            brete = "" + (i + 1);
                            Log.i(TAG, "brete " + brete);
                        }
                        if (j == 1) {
                            text = (EditText) child;
                            litros = text.getText().toString();
                            Log.i(TAG, "litros " + litros);
                            body += brete + "," + litros + "," + lote + ",\n";
                            Log.i(TAG, body);
                        }
                    }
                }
            } //END no seteado
        } catch (Exception e) {
            e.printStackTrace();
        }
        return body;
    }

    public void generateNoteOnSD(String filename) {

        String body = getData();

        try {
            if (ExternalStorageUtil.isExternalStorageMounted()) {

                // Check whether this app has write external storage permission or not.
                int writeExternalStoragePermission = ContextCompat.checkSelfPermission(DynamicFields.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                // If do not grant write external storage permission.
                if (writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
                    // Request user to grant write external storage permission.
                    ActivityCompat.requestPermissions(DynamicFields.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
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

}