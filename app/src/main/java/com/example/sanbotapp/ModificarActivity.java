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
import com.qihancloud.opensdk.beans.FuncConstant;
import com.qihancloud.opensdk.function.unit.HandMotionManager;
import com.qihancloud.opensdk.function.unit.HardWareManager;
import com.qihancloud.opensdk.function.unit.HeadMotionManager;
import com.qihancloud.opensdk.function.unit.SpeechManager;
import com.qihancloud.opensdk.function.unit.SystemManager;
import com.qihancloud.opensdk.function.unit.WheelMotionManager;

import java.util.ArrayList;


public class ModificarActivity extends TopBaseActivity {

    private RecyclerView recyclerView;
    private ArrayList<Long> dataList; // Lista de IDs de los bloques
    private DataAdapterModificarVersion2 adapterV;

    private PresentacionesDbAdapter mDbHelper;
    private Long mRowId;
    private EditText mNombreText;

    private BloqueAccionesDbAdapter mDbHelperBloque;
    private AccionesDbAdapter mDbHelperAccion;
    private final int BLOQUES_LIMIT = 100;
    private int bloquesLimit;

    private SpeechManager speechManager;
    private SystemManager systemManager;
    private HandMotionManager handMotionManager;
    private HeadMotionManager headMotionManager;
    private HardWareManager hardWareManager; //leds //touch sensors //voice locate //gyroscope
    private WheelMotionManager wheelMotionManager;

    private FuncionalidadesActivity funcionalidadesActivity;



    private Button nuevoBloque;
    private Button reproducir;

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

        speechManager = (SpeechManager) getUnitManager(FuncConstant.SPEECH_MANAGER);
        systemManager = (SystemManager) getUnitManager(FuncConstant.SYSTEM_MANAGER);
        handMotionManager = (HandMotionManager) getUnitManager(FuncConstant.HANDMOTION_MANAGER);
        headMotionManager = (HeadMotionManager) getUnitManager(FuncConstant.HEADMOTION_MANAGER);
        hardWareManager = (HardWareManager) getUnitManager(FuncConstant.HARDWARE_MANAGER);
        wheelMotionManager = (WheelMotionManager) getUnitManager(FuncConstant.WHEELMOTION_MANAGER);

        // RecyclerView
        recyclerView = findViewById(R.id.recycler_view);
        reproducir = findViewById(R.id.button_reproducir);

        // Database Connection
        mDbHelper = new PresentacionesDbAdapter( this );
        mDbHelper.open();

        mRowId = (savedInstanceState == null) ? null :
                (Long)savedInstanceState.getSerializable(PresentacionesDbAdapter.KEY_ROWID ) ;
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = (extras != null)?extras.getLong(PresentacionesDbAdapter.KEY_ROWID):null ;
        }

        dataList = new ArrayList<>();

        mDbHelperBloque = new BloqueAccionesDbAdapter(this);
        mDbHelperBloque.open();
        fillData();

        mDbHelperAccion = new AccionesDbAdapter(this);
        mDbHelperAccion.open();

        funcionalidadesActivity = new FuncionalidadesActivity(speechManager, systemManager, handMotionManager,
                headMotionManager, hardWareManager, wheelMotionManager, ModificarActivity.this);

        adapterV = new DataAdapterModificarVersion2(dataList, this, mDbHelperBloque, funcionalidadesActivity, mDbHelperAccion);
        recyclerView.setAdapter(adapterV);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Configura el ItemTouchHelper
        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(adapterV);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        adapterV.setItemTouchHelper(itemTouchHelper);
        itemTouchHelper.attachToRecyclerView(recyclerView);


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
            if (mNombreText.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), "El nombre de la presentación no puede estar vacío", Toast.LENGTH_SHORT).show();
            } else {
                saveState();
                System.out.println("ID CREAAR BLOQUE: " + mRowId);
                Intent intent = new Intent(ModificarActivity.this, EditActivity.class);
                intent.putExtra("PRESENTATION_ID", mRowId);
                startActivity(intent);
            }
        });

        reproducir.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // Recorre los bloques y para cada bloque recorre las acciones y las ejecuta
                for (int i = 0; i < dataList.size(); i++) {

                    ArrayList<DataModel> list = mDbHelperAccion.getAccionesBloque(dataList.get(i));

                    for (int j = 0; j < list.size(); j++) {
                        DataModel data = list.get(j);
                        if (data.getSpinnerOption().equals("Síntesis de voz")) {
                            funcionalidadesActivity.speakOperation(data.getText(), "Normal");

                        } else if (data.getSpinnerOption().equals("Movimiento de brazos")) {
                            funcionalidadesActivity.moveBrazosOperation(data.getText());

                        } else if (data.getSpinnerOption().equals("Movimiento de cabeza")) {
                            funcionalidadesActivity.moveCabezaOperation(data.getText());

                        } else if (data.getSpinnerOption().equals("Movimiento de ruedas")) {
                            funcionalidadesActivity.moveRuedasOperation(data.getText());

                        } else if (data.getSpinnerOption().equals("Encender LEDs")) {
                            funcionalidadesActivity.encenderLedsOperation(data.getText());

                        } else if (data.getSpinnerOption().equals("Cambio de expresión facial")) {
                            funcionalidadesActivity.changeFaceOperation(data.getText());

                        } else if (data.getSpinnerOption().equals("Insertar imagen")) {
                            funcionalidadesActivity.mostrarImagen(data.getText());

                        } else if (data.getSpinnerOption().equals("Insertar vídeo")) {

                        } else if (data.getSpinnerOption().equals("Pregunta verdadero o falso")) {
                            funcionalidadesActivity.trueFalseOperation(data.getText());
                        } else {
                            // No se ha seleccionado ninguna opción
                        }
                    }
                }
            }
        });

    }

    protected void fillData() {
        if(mRowId != null) {
            Cursor notesCursor = mDbHelperBloque.fetchAllBloqueAcciones(mRowId);
            bloquesLimit = notesCursor.getCount();
            System.out.println("Bloques: " + bloquesLimit);

            dataList.clear();
            if(notesCursor.moveToFirst()) {
                do {
                    dataList.add(notesCursor.getLong(notesCursor.getColumnIndexOrThrow(BloqueAccionesDbAdapter.KEY_ROWID)));
                } while (notesCursor.moveToNext());
            }
        }
    }

    private void populateFields () {
        if (mRowId != null) {
            Cursor presentacion = mDbHelper.fetchPresentacion(mRowId);
            startManagingCursor(presentacion);
            mNombreText.setText(presentacion.getString(presentacion.getColumnIndexOrThrow(PresentacionesDbAdapter.KEY_NOMBRE)));

            Cursor notesCursor = mDbHelperBloque.fetchAllBloqueAcciones(mRowId);
            bloquesLimit = notesCursor.getCount();

            dataList.clear();
            if(notesCursor.moveToFirst()) {
                do {
                    dataList.add(notesCursor.getLong(notesCursor.getColumnIndexOrThrow(BloqueAccionesDbAdapter.KEY_ROWID)));
                } while (notesCursor.moveToNext());
            }

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
                mDbHelper.updatePresentacion( mRowId , nombre );
            }
            // Al guardar el estado se actualiza la ordenación de los bloques
            if(dataList.size() > 0) {
                mDbHelperBloque.updateOrdenacionBloques(dataList, mRowId);
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
