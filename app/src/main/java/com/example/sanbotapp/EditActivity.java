package com.example.sanbotapp;


import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

import com.qihancloud.opensdk.base.TopBaseActivity;

import java.util.ArrayList;


public class EditActivity extends TopBaseActivity {

    private EditText editTextTitle;

    private Spinner spinnerOptions;

    private Button buttonSave;

    @Override
    protected void onMainServiceConnected() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        onMainServiceConnected();
        setContentView(R.layout.activity_edit);
        setTitle("Configuración");

        // Obtener referencias a los elementos de UI
        editTextTitle = findViewById(R.id.editTextTitle);
        spinnerOptions = findViewById(R.id.spinnerOptions);
        buttonSave = findViewById(R.id.button_save);
        LinearLayout layoutEditText = findViewById(R.id.layoutEditText);
        EditText editTextOption = findViewById(R.id.editTextOption);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOptions.setAdapter(adapter);



        spinnerOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if (selectedItem.equals("Discurso")) {
                    layoutEditText.setVisibility(View.VISIBLE);
                    editTextOption.setHint("Introduce el discurso");
                } else if (selectedItem.equals("Otra opción")) {
                    // Aquí puedes manejar otras opciones y mostrar diferentes elementos si es necesario
                } else {
                    layoutEditText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hacer nada si no hay selección
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
