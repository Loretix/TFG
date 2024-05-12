package com.example.sanbotapp;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

    private PresentacionesDbAdapter mDbHelper;
    private Long mRowId;
    private EditText mNombreText;


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

        // Database Connection
        mDbHelper = new PresentacionesDbAdapter( this );
        mDbHelper.open();

        mRowId = (savedInstanceState == null) ? null :
                (Long)savedInstanceState.getSerializable(PresentacionesDbAdapter.KEY_ROWID ) ;
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = (extras != null)?extras.getLong(PresentacionesDbAdapter.KEY_ROWID):null ;
        }

        nuevoBloque = findViewById(R.id.button_new_block);
        mNombreText = findViewById(R.id.editTextTitlePresentacion);
        Button guardar = findViewById(R.id.button_save);

        guardar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });

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

    private void populateFields () {
        if (mRowId != null) {
            Cursor presentacion = mDbHelper.fetchPresentacion(mRowId);
            startManagingCursor(presentacion);
            mNombreText.setText(presentacion.getString(presentacion.getColumnIndexOrThrow(PresentacionesDbAdapter.KEY_NOMBRE)));
        }
    }

    @Override
    protected void onSaveInstanceState ( Bundle outState ) {
        super.onSaveInstanceState( outState ) ;
        saveState();
        outState.putSerializable (PresentacionesDbAdapter.KEY_ROWID , mRowId ) ;
    }

    @Override
    public void onPause () {
        super.onPause();
        saveState();
    }

    @Override
    public void onResume () {
        super.onResume();
        populateFields();
    }

    private void saveState () {
        String nombre = mNombreText.getText().toString();
        if (nombre == null || nombre.equals("") ) {
            Toast.makeText(getApplicationContext(),"Presentación no creada/modificada, campos inválidos", Toast.LENGTH_SHORT).show();
        } else{
            if ( mRowId == null ) {
                long id = mDbHelper.createPresentacion( nombre );
                if (id > 0) { mRowId = id; }
            } else {
                mDbHelper.updateHabitacion( mRowId , nombre );
            }
        }

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
