package com.example.sanbotapp;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.qihancloud.opensdk.base.TopBaseActivity;

import java.util.ArrayList;


public class MainActivity extends TopBaseActivity {
    private ListView listView;
    private CustomAdapter adapter;
    private ArrayList<String> dataList;
    private Button buttonEdit;

    @Override
    protected void onMainServiceConnected() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        onMainServiceConnected();
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        dataList = new ArrayList<>();

        // Agregar algunos elementos a la lista
        dataList.add("Presentación mujeres ingenieras");
        dataList.add("Elemento 2");
        dataList.add("Elemento 3");

        // Crear el adaptador personalizado
        adapter = new CustomAdapter(this, dataList);

        // Establecer el adaptador en la ListView
        listView.setAdapter(adapter);

        // TODO: Agregar un OnClickListener al botón y arreglar esto para que para cada elemento de la lista se pueda editar

        buttonEdit = findViewById(R.id.button_edit);

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Define la actividad que quieres iniciar cuando se hace clic en el botón
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                startActivity(intent); // Inicia la actividad
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
