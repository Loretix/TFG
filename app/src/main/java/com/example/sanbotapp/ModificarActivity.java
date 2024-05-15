package com.example.sanbotapp;


import android.content.Intent;
import android.content.SharedPreferences;
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

    private RecyclerView recyclerView;

    private PresentacionesDbAdapter mDbHelper;
    private Long mRowId;
    private EditText mNombreText;

    private BloqueAccionesDbAdapter mDbHelperBloque;
    private final int BLOQUES_LIMIT = 100;
    private int bloquesLimit;


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

        // RecyclerView
        recyclerView = findViewById(R.id.recycler_view);

        // Database Connection
        mDbHelper = new PresentacionesDbAdapter( this );
        mDbHelper.open();

        mRowId = (savedInstanceState == null) ? null :
                (Long)savedInstanceState.getSerializable(PresentacionesDbAdapter.KEY_ROWID ) ;
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = (extras != null)?extras.getLong(PresentacionesDbAdapter.KEY_ROWID):null ;
        }

        mDbHelperBloque = new BloqueAccionesDbAdapter(this);
        mDbHelperBloque.open();
        fillData();

        nuevoBloque = findViewById(R.id.button_new_block);
        mNombreText = findViewById(R.id.editTextTitlePresentacion);
        Button guardar = findViewById(R.id.button_save);

        guardar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });

        // Onclick nuevo bloque voy a la pantalla de editar
        nuevoBloque.setOnClickListener(v -> {
            Intent intent = new Intent(ModificarActivity.this, EditActivity.class);
            intent.putExtra("PRESENTATION_ID", mRowId);
            startActivity(intent);
        });

    }

    private void fillData() {
        if(mRowId != null) {
            Cursor notesCursor = mDbHelperBloque.fetchAllBloqueAcciones(mRowId);
            bloquesLimit = notesCursor.getCount();
            System.out.println("Bloques: " + bloquesLimit);
            DataAdapterModificar adapter = new DataAdapterModificar(notesCursor, this);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            // Configura el ItemTouchHelper
            ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(adapter);
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
            adapter.setItemTouchHelper(itemTouchHelper);
            itemTouchHelper.attachToRecyclerView(recyclerView);
        }
    }

    private void populateFields () {
        if (mRowId != null) {
            Cursor presentacion = mDbHelper.fetchPresentacion(mRowId);
            startManagingCursor(presentacion);
            mNombreText.setText(presentacion.getString(presentacion.getColumnIndexOrThrow(PresentacionesDbAdapter.KEY_NOMBRE)));

            Cursor notesCursor = mDbHelperBloque.fetchAllBloqueAcciones(mRowId);
            bloquesLimit = notesCursor.getCount();
            System.out.println("Bloques: " + bloquesLimit);
            DataAdapterModificar adapter = new DataAdapterModificar(notesCursor, this);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            // Configura el ItemTouchHelper
            ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(adapter);
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
            adapter.setItemTouchHelper(itemTouchHelper);
            itemTouchHelper.attachToRecyclerView(recyclerView);

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
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
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
