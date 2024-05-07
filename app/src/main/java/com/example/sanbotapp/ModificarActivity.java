package com.example.sanbotapp;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qihancloud.opensdk.base.TopBaseActivity;

import java.util.ArrayList;


public class ModificarActivity extends TopBaseActivity {

    private ArrayList<String> dataList;
    private RecyclerView recyclerView;
    private DataAdapterModificar adapterV;

    private Button nuevoBloque;

    @Override
    protected void onMainServiceConnected() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        onMainServiceConnected();
        setContentView(R.layout.activity_modificar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar_title_conf);

        nuevoBloque = findViewById(R.id.button_new_block);

        // RecyclerView
        recyclerView = findViewById(R.id.recycler_view);

        // LISTA DE BLOQUES DE LA PRESENTACION
        dataList = new ArrayList<>();
        dataList.add("Bloque 1");
        dataList.add("Bloque 2");
        dataList.add("Bloque 3");


        adapterV = new DataAdapterModificar(dataList, this);
        recyclerView.setAdapter(adapterV);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Configura el ItemTouchHelper
        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(adapterV);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        adapterV.setItemTouchHelper(itemTouchHelper);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        // Onclick nuevo bloque voy a la pantalla de editar
        nuevoBloque.setOnClickListener(v -> {
            Intent intent = new Intent(ModificarActivity.this, EditActivity.class);
            startActivity(intent);
        });

        /* ESTO LO GUARDO PORQUE ES PARA AÑADIR UN NUEVO ITEM A LA LISTA

        nuevoBloque.setOnClickListener(v -> {
            dataList.add("Bloque " + (dataList.size() + 1));
            adapterV.notifyItemInserted(dataList.size());
        });*/



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
