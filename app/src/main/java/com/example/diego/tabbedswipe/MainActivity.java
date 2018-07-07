package com.example.diego.tabbedswipe;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    //    debug
    String TAG = "DIEGO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean CARAVANA = ((CheckBox) findViewById(R.id.checkBox_caravanas)).isChecked();
                boolean MUESTRA = ((CheckBox) findViewById(R.id.checkBox_muestras)).isChecked();
                EditText b = findViewById(R.id.edittext_bretes);
                String brete = b.getText().toString();
                int BRETES = 0;
                if (!brete.isEmpty())
                    BRETES = Integer.parseInt(brete);
                EditText v = findViewById(R.id.edittext_vacas);
                String vacas = v.getText().toString();
                int VACAS = 20;
                if (!vacas.isEmpty())
                    VACAS = Integer.parseInt(vacas);

                // Dynamic fields
                // Intent i = new Intent(MainActivity.this, DynamicFields.class);

                // Swipe y dynamic fields juntos
                Intent i = new Intent(MainActivity.this, SwypeActivity.class);

//                Log.i(TAG,"BRETES "+BRETES);
//                Log.i(TAG,"MUESTRA "+MUESTRA);
//                Log.i(TAG,"CARAVANA "+CARAVANA);
//                Log.i(TAG,"VACAS "+VACAS);

                i.putExtra("BRETES", BRETES);
                i.putExtra("MUESTRA", MUESTRA);
                i.putExtra("CARAVANA", CARAVANA);
                i.putExtra("VACAS", VACAS);
                startActivity(i);
            }
        });

    }

}
