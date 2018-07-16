package com.example.diego.tabbedswipe;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Diego on 24/06/2018.
 */


public class DynamicFieldsFragment extends Fragment {

    protected LinearLayout parentLayout;

    //    private final String _SECTION_NUMBER = "section_number";
//    private final String _BRETES = "0";
//    private final String _MUESTRA = "false";
//    private final String _CARAVANA = "false";
    // debug
    String TAG = "DIEGO";
    Bundle mState;
    private int SECTION_NUMBER = 0;
    private int BRETES = 0;
    private boolean MUESTRA = false;
    private boolean CARAVANA = false;
    private int fieldsAdded = 0;
    private boolean wasDivided = false;

    myCommunicationInterface mCallback;


    // Container Activity must implement this interface
    public interface myCommunicationInterface {
        public void newPage(int pos);
        public void mostrarDialogoLitros();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (myCommunicationInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

/*
If Android decides to recreate your Fragment later, it's going to call the no-argument constructor of your fragment.
So overloading the constructor is not a solution.
 */
    //FIXME que hago con esto ?
    public DynamicFieldsFragment() {
    }

    public static List<View> getViewsByTag(View root, String tag) {
        List<View> result = new LinkedList<>();

        if (root instanceof ViewGroup) {
            final int childCount = ((ViewGroup) root).getChildCount();
            for (int i = 0; i < childCount; i++) {
                result.addAll(getViewsByTag(((ViewGroup) root).getChildAt(i), tag));
            }
        }

        final Object rootTag = root.getTag();
        // handle null tags, code from Guava's Objects.equal
        if (tag == rootTag || (tag != null && tag.equals(rootTag))) {
            result.add(root);
        }

        return result;
    }

    /**
     * Returns a new instance of this fragment for the given section number.
     */
    public DynamicFieldsFragment newInstance(int sectionNumber, int bretes, boolean muestra, boolean caravana) {

//        el moverse de pagina instancia de vuelta
//        addOnPageChangeListener ejecuta antes que esto
        Log.i(TAG, "newInstance() " + sectionNumber);

        SECTION_NUMBER = sectionNumber;
        BRETES = bretes;
        MUESTRA = muestra;
        CARAVANA = caravana;
        String myData = "no data " + SECTION_NUMBER;

        Bundle args = this.getArguments();
        if (args != null) {
            Log.i(TAG, "has data " + sectionNumber);
            myData = args.getString("DATA");
            Log.i(TAG, myData);
        } else {
            Log.i(TAG, "new bundle " + sectionNumber);
            args = new Bundle();
            args.putString("DATA", myData);
            this.setArguments(args);
        }

        return this;
    }

    //region backup plan if setOffscreenPageLimit fails me
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.i(TAG, "onActivityCreated DynamicFieldsFragment " + SECTION_NUMBER);

        if (savedInstanceState != null) {

            String myData = "";
            //Restore the fragment's state here
            savedInstanceState.getInt("SECTION_NUMBER", SECTION_NUMBER);
            savedInstanceState.getInt("BRETES", BRETES + fieldsAdded);
            savedInstanceState.getBoolean("MUESTRA", MUESTRA);
            savedInstanceState.getBoolean("CARAVANA", CARAVANA);
            savedInstanceState.getString("DATA", myData);
        }

    }
    //endregion

//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//
//        Log.i(TAG, "onSaveInstanceState DynamicFieldsFragment " + SECTION_NUMBER);
//
//
//    }

    // If you return to a fragment from the back stack it does not
    // re-create the fragment but re-uses the same instance and starts with onCreateView
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.i(TAG, "onCreateView DynamicFieldsFragment " + SECTION_NUMBER);

        View rootView = inflater.inflate(R.layout.fragment_dynamic_fields, container, false);

        parentLayout = rootView.findViewById(R.id.parent_layout);

        Button addbtn = rootView.findViewById(R.id.add_field_button);
        addbtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addField(1);
            }
        });

        Button nextpage = rootView.findViewById(R.id.next_page);
        nextpage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.newPage(SECTION_NUMBER);
            }
        });



//        Button delbtn = rootView.findViewById(R.id.delete_button);
//        delbtn.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onDelete(v);
//            }
//        });

//        Button expbtn = (Button) rootView.findViewById(R.id.export_button);
//        expbtn.setOnClickListener(new OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                String data = getData();
//                Log.i(TAG, data);
//
//            }
//        });

        //To prevent ViewPager to recreate the fragment
//        setRetainInstance(true);

        String data = "";
        if (savedInstanceState != null) {
            savedInstanceState.getString("DATA", data);
        }
        setData(data);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.i(TAG, "onViewCreated DynamicFieldsFragment " + SECTION_NUMBER);

        int b = BRETES;
        if (b > 0) {
            addField(b);
        }

        List<View> fields = getViewsByTag(getView(), "field");
        for (View field : fields) {

            final EditText txtEdit = (EditText) field;

            // Validar litros
            CharSequence test = txtEdit.getHint();
            if(test.equals("Litros")){
                txtEdit.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        String value = txtEdit.getText().toString();
                        float litros = 0;
                        if(!value.isEmpty()){
                            try{
                                litros = Float.parseFloat(value);
                            }catch(Exception e){
                                Log.i(TAG, e.getMessage());
                            }
                            if(litros >= 40){

                                mCallback.mostrarDialogoLitros();

                            }
                        }
                    }
                });
            }

            // Save data on input focus
            txtEdit.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) { // when EditText loses focus

                        Log.i(TAG, v.getId() + " lost focus");

                        // Get a handler that can be used to post to the main thread
                        Handler mainHandler = new Handler(getContext().getMainLooper());
                        Runnable myRunnable = new Runnable() {
                            @Override
                            public void run() {

                                saveData();

                            }
                        };
                        mainHandler.post(myRunnable);

                    }
                }
            });
        }

    }

//    public void onDelete(View v) {
//        int count = parentLayout.getChildCount();
//        if (count >= 2) parentLayout.removeView(parentLayout.getChildAt(count - 2));
//        else new AlertDialog.Builder(v.getContext()).setMessage("Primero agrega uno!").show();
//    }



    public void addField(int n) {
        for (int i = n; i > 0; i--) {
            onAddField();
        }
    }

    public void onAddField() {

//        Log.i(TAG, "onAddField DynamicFieldsFragment " + SECTION_NUMBER);

        if (this.isAdded()) {

            LayoutInflater inflater = getActivity().getLayoutInflater();
            View rowView;

            int BRETES = this.BRETES;
            boolean MUESTRA = this.MUESTRA;
            boolean CARAVANA = this.CARAVANA;

//            Log.i(TAG,"BRETES "+BRETES);
//            Log.i(TAG,"MUESTRA "+MUESTRA);
//            Log.i(TAG,"CARAVANA "+CARAVANA);

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

            int count = parentLayout.getChildCount();

            if(MUESTRA){
                TextView m = rowView.findViewById(R.id.number_edit_textmuestra);
                m.setText("" + count);
            }

            // Add the new row before the add field_cm button.
            parentLayout.addView(rowView, count - 1);

            fieldsAdded++;

//            // division de bretes
//            if (wasDivided) newDivider(fieldsAdded);
//            if (fieldsAdded == BRETES) newDivider(BRETES);

//            newDivider(BRETES);

        }

    }

    //TODO recover state from Bundle (and stop abusing setOffscreenPageLimit)
    public void setData(String data) {
        Log.i(TAG, "setData() "+SECTION_NUMBER );

        if(data == null || data.equals("") ){
            Log.i(TAG, "setData() FAILED");
            return;
        }

        String caravana = "", litros = "", muestra = "", bretada = "";
        try {
            String[] lines = data.split("\\n");
            int count = 0;
            if (CARAVANA && MUESTRA) {
//              caravana,litros,muestra,bretada,
                if (parentLayout != null)
                    count = parentLayout.getChildCount();
                else {
                    View v = this.getView();
                    if (v != null) {
                        parentLayout = v.findViewById(R.id.parent_layout);
                        count = parentLayout.getChildCount();
                    } else {
                        Log.i(TAG, "View es null");
                        return;
                    }
                }

//                Log.i(TAG,"count "+count);
                for(int h = 0; h < lines.length; h++){

                    String[] values = lines[h].split(",");

                    for (int i = 0; i < values.length; i++) {

                        String text = values[i];
                        Log.i(TAG, "text "+ text);


                    }
                }

            } else if (MUESTRA || CARAVANA) {
                if (MUESTRA) {

//                    "litros,muestra,bretada,

                }
                if (CARAVANA) {

//                    body += "caravana,litros,bretada,\n";

                }
            } else {
                Log.i(TAG, "No se setearon MUESTRA y CARAVANA");

//                body += "litros,bretada,\n";

            } //END no seteado
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getData() {
        EditText text;
        String caravana = "", litros = "", muestra = "", bretada = "", body = "";

        //Una bretada son 2 paginas
//        SECTION_NUMBER 1 => bretada 1
//        SECTION_NUMBER 2 => bretada 1

//        SECTION_NUMBER 3 => bretada 2
//        SECTION_NUMBER 4 => bretada 2

//        SECTION_NUMBER 5 => bretada 3
//        SECTION_NUMBER 6 => bretada 3

//        SECTION_NUMBER 7 => bretada 4
//        SECTION_NUMBER 8 => bretada 4

        int n = 1;
        if(SECTION_NUMBER != 1 && SECTION_NUMBER != 2){
            if(SECTION_NUMBER % 2 == 0) n = SECTION_NUMBER / 2;
            else n = (SECTION_NUMBER + 1) / 2;
        }
        bretada = String.valueOf(n);


        try {
            if (CARAVANA && MUESTRA) {
//                body += "caravana,litros,muestra,bretada,\n";
                //            every field_cm group
                int count = 0;
                if (parentLayout != null)
                    count = parentLayout.getChildCount();
                else {
                    View v = this.getView();
                    if (v != null) {
                        parentLayout = v.findViewById(R.id.parent_layout);
                        count = parentLayout.getChildCount();
                    } else {
                        Log.i(TAG, "View es null");
                        return body;
                    }
                }

//                Log.i(TAG,"count "+count);
                //            skip buttons
                for (int i = 0; i < count - 1; i++) {
                    LinearLayout field = (LinearLayout) parentLayout.getChildAt(i);
                    //                every editText
                    int countfield = field.getChildCount();
//                    Log.i(TAG,"countfield "+countfield);
                    for (int j = 0; j < countfield; j++) {
                        View child = field.getChildAt(j);
                        switch(j){
                            case 0 :
                                text = (EditText) child;
                                caravana = text.getText().toString();
                                caravana = byeCommas(caravana);

//                                Log.i(TAG,"caravana "+caravana);
                                break;
                            case 1 :
                                text = (EditText) child;
                                litros = text.getText().toString();
//                                Log.i(TAG,"litros "+litros);
                                break;
                            case 2 :
                                text = (EditText) child;
                                muestra = text.getText().toString();
                                muestra = byeCommas(muestra);

//                                Log.i(TAG,"muestra "+muestra);
                                body += caravana + "," + litros + "," + muestra + "," + bretada + "\n";
                                break;
                        }
                    }
                }

            } else if (MUESTRA || CARAVANA) {
                if (MUESTRA) {

//                    body += "litros,muestra,bretada,\n";

                    //            every field_cm group
                    int count = parentLayout.getChildCount();
//                    Log.i(TAG,"count "+count);
                    //            skip buttons
                    for (int i = 0; i < count - 1; i++) {
                        LinearLayout field = (LinearLayout) parentLayout.getChildAt(i);
                        //                every editText
                        int countfield = field.getChildCount();
//                        Log.i(TAG,"countfield "+countfield);
                        for (int j = 0; j < countfield; j++) {
                            View child = field.getChildAt(j);
                            switch(j){
                                case 0 :
                                    text = (EditText) child;
                                    litros = text.getText().toString();

//                                    Log.i(TAG,"litros "+litros);
                                    break;
                                case 1 :
                                    text = (EditText) child;
                                    muestra = text.getText().toString();
                                    muestra = byeCommas(muestra);

//                                    Log.i(TAG,"muestra "+muestra);
                                    body += ","+ litros + "," + muestra + "," + bretada + "\n";
                                    break;
                            }
                        }
                    }
                }
                if (CARAVANA) {

//                    body += "caravana,litros,bretada,\n";

                    //            every field_cm group
                    int count = parentLayout.getChildCount();
//                    Log.i(TAG,"count "+count);
                    //            skip buttons
                    for (int i = 0; i < count - 1; i++) {
                        LinearLayout field = (LinearLayout) parentLayout.getChildAt(i);
                        //                every editText
                        int countfield = field.getChildCount();
//                        Log.i(TAG,"countfield "+countfield);
                        for (int j = 0; j < countfield; j++) {
                            View child = field.getChildAt(j);
                            switch(j){
                                case 0 :
                                    text = (EditText) child;
                                    caravana = text.getText().toString();
                                    caravana = byeCommas(caravana);

//                                    Log.i(TAG,"caravana "+caravana);
                                    break;
                                case 1 :
                                    text = (EditText) child;
                                    litros = text.getText().toString();

//                                    Log.i(TAG,"litros "+litros);
                                    body += caravana + "," + litros + "," +  "," + bretada + "\n";
                                    break;
                            }
                        }
                    }
                }
            } else {
                Log.i(TAG, "No se setearon MUESTRA y CARAVANA");

//                body += "litros,bretada,\n";

                //            every field_cm group
                int count = parentLayout.getChildCount();
//                Log.i(TAG,"count "+count);
                //            skip buttons
                for (int i = 0; i < count - 1; i++) {
                    LinearLayout field = (LinearLayout) parentLayout.getChildAt(i);
                    //                every editText
                    int countfield = field.getChildCount();
//                    Log.i(TAG,"countfield "+countfield);
                    for (int j = 0; j < countfield; j++) {
                        View child = field.getChildAt(j);
//                        switch(j){
//                            case 0 :
                                text = (EditText) child;
                                litros = text.getText().toString();
//                                Log.i(TAG,"litros "+litros);
                                body += "," + litros + "," + "," +  bretada + "\n";
//                                break;
//                        }
                    }
                }
            } //END no seteado
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, body);
        return body;
    }

    public String recoverData() {

        Log.i(TAG, "recoverData " + SECTION_NUMBER);

        String myData = "";
        Bundle args = this.getArguments();
        if (args != null){
            // Restore the fragment's state here
            myData = args.getString("DATA");
        }
        else Log.i(TAG, "Bundle was null " + SECTION_NUMBER);

        return myData;
    }

    public void saveData() {
        Log.i(TAG, "saveData " + SECTION_NUMBER);

        Bundle args = this.getArguments();
        String data = getData();
        Log.i(TAG, "data " + SECTION_NUMBER);
        Log.i(TAG, data);
        args.clear();
        //Save the fragment's state here
        args.putString("DATA", data);
        this.setArguments(args);

    }

    // deletes commas that would ruin the csv format
    public String byeCommas(String text){
        return text.replace(",","");
    }

    //    region division de bretes
//    public void newDivider(int n) {
//        removeDivider(n - 1);
//        addDivider(n);
//        wasDivided = true;
//    }
//
//    public void removeDivider(int total) {
//        View v = parentLayout.getChildAt(total / 2);
//        if (v != null) v.setPadding(0, 0, 0, 0);
//    }
//
//    public void addDivider(int total) {
//        View v = parentLayout.getChildAt(total / 2);
//        if (v != null) v.setPadding(0, 150, 0, 0);
//    }
//    endregion

}

