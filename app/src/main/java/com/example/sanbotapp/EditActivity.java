package com.example.sanbotapp;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.qihancloud.opensdk.base.TopBaseActivity;
import com.qihancloud.opensdk.beans.FuncConstant;
import com.qihancloud.opensdk.function.beans.EmotionsType;
import com.qihancloud.opensdk.function.beans.SpeakOption;
import com.qihancloud.opensdk.function.unit.HandMotionManager;
import com.qihancloud.opensdk.function.unit.HardWareManager;
import com.qihancloud.opensdk.function.unit.HeadMotionManager;
import com.qihancloud.opensdk.function.unit.SpeechManager;
import com.qihancloud.opensdk.function.unit.SystemManager;
import com.qihancloud.opensdk.function.unit.WheelMotionManager;

import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;


public class EditActivity extends TopBaseActivity {

    private Spinner spinnerOptions;
    private Button buttonSave, buttonAdd, buttonAddExpresionFacial, buttonAddMovBrazos;
    private Button buttonAddMovCabeza, buttonAddMovRuedas, buttonAddLED, buttonAddTrueFalse;
    private Button buttonReproducir, BSelectImage, BSelectVideo;
    private ArrayList<DataModel> dataList;
    private TextView textViewOptions;
    private LinearLayout layoutTextView;
    private AlertDialog dialog;
    private ImageView IVPreviewImage;
    private VideoView IVPreviewVideo;
    private Uri selectedImageUri;
    private Uri selectedVideoUri;

    private SpeechManager speechManager;
    private SystemManager systemManager;
    private HandMotionManager handMotionManager;
    private HeadMotionManager headMotionManager;
    private HardWareManager hardWareManager; //leds //touch sensors //voice locate //gyroscope
    private WheelMotionManager wheelMotionManager;

    private RecyclerView recyclerView;
    private DataAdapter adapterV;

    private FuncionalidadesActivity funcionalidadesActivity;

    private Long mRowId;
    private Long mRowIdPresentacion;
    private EditText mNombreText;
    private BloqueAccionesDbAdapter mDbHelperBloque;
    private AccionesDbAdapter mDbHelperAcciones;

    int SELECT_PICTURE = 200;
    int SELECT_VIDEO = 1;


    @Override
    protected void onMainServiceConnected() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        onMainServiceConnected();
        checkPermissions();
        setContentView(R.layout.activity_edit);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar_title_conf);

        speechManager = (SpeechManager) getUnitManager(FuncConstant.SPEECH_MANAGER);
        systemManager = (SystemManager) getUnitManager(FuncConstant.SYSTEM_MANAGER);
        handMotionManager = (HandMotionManager) getUnitManager(FuncConstant.HANDMOTION_MANAGER);
        headMotionManager = (HeadMotionManager) getUnitManager(FuncConstant.HEADMOTION_MANAGER);
        hardWareManager = (HardWareManager) getUnitManager(FuncConstant.HARDWARE_MANAGER);
        wheelMotionManager = (WheelMotionManager) getUnitManager(FuncConstant.WHEELMOTION_MANAGER);


        // Database Connection

        mDbHelperBloque = new BloqueAccionesDbAdapter( this );
        mDbHelperBloque.open();

        // Extraer el ID del bloque y el ID de la presentación del Intent
        mRowIdPresentacion = (Long) getIntent().getLongExtra("PRESENTATION_ID", -1);
        mRowId = (Long) getIntent().getLongExtra("BLOCK_ID", -1);

        if (mRowId == -1) {
            // Se está editando un bloque existente
            mRowId = null;
        }

        mDbHelperAcciones = new AccionesDbAdapter( this );
        mDbHelperAcciones.open();

        // Obtener referencias a los elementos de UI
        mNombreText = findViewById(R.id.editTextTitleBloque);
        spinnerOptions = findViewById(R.id.spinnerOptions);
        buttonSave = findViewById(R.id.button_save);
        buttonReproducir = findViewById(R.id.button_reproducir);
        recyclerView = findViewById(R.id.recycler_view);

        // Sistesis de voz
        buttonAdd = findViewById(R.id.buttonAdd);
        LinearLayout layoutEditText = findViewById(R.id.layoutEditText);
        EditText editTextOption = findViewById(R.id.editTextOption);

        // Expresion facial
        LinearLayout layoutExpresionFacial = findViewById(R.id.layoutExpresionfacial);
        buttonAddExpresionFacial = findViewById(R.id.buttonAddExpresionFacial);
        Spinner spinnerFacial = (Spinner) findViewById(R.id.spinnerOptionsExpresionFacial);

                // Create an ArrayAdapter using the string array and a default spinner layout.
                ArrayAdapter<CharSequence> adapterFacial = ArrayAdapter.createFromResource(
                        this,
                        R.array.expresionesFaciales,
                        android.R.layout.simple_spinner_item
                );
                // Specify the layout to use when the list of choices appears.
        adapterFacial.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Apply the adapter to the spinner.
        spinnerFacial.setAdapter(adapterFacial);

        // Movimiento de brazos
        buttonAddMovBrazos = findViewById(R.id.buttonAddMovBrazos);
        Spinner spinnerMovBrazos = findViewById(R.id.spinnerOptionsMovBrazos);
        LinearLayout layoutMovBrazos = findViewById(R.id.layoutMovBrazos);

        ArrayAdapter<CharSequence> adapterMovBrazos = ArrayAdapter.createFromResource(
                this,
                R.array.movimientoBrazos,
                android.R.layout.simple_spinner_item
        );
        adapterMovBrazos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMovBrazos.setAdapter(adapterMovBrazos);

        // Movimiento de cabeza
        buttonAddMovCabeza = findViewById(R.id.buttonAddMovCabeza);
        Spinner spinnerMovCabeza = findViewById(R.id.spinnerOptionsMovCabeza);
        LinearLayout layoutMovCabeza = findViewById(R.id.layoutMovCabeza);

        ArrayAdapter<CharSequence> adapterMovCabeza = ArrayAdapter.createFromResource(
                this,
                R.array.movimientoCabeza,
                android.R.layout.simple_spinner_item
        );
        adapterMovCabeza.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMovCabeza.setAdapter(adapterMovCabeza);

        // Movimiento de ruedas
        buttonAddMovRuedas = findViewById(R.id.buttonAddMovRuedas);
        EditText editTextMovRuedas = findViewById(R.id.editTextOptionMovRuedas);
        Spinner spinnerMovRuedas = findViewById(R.id.spinnerOptionsMovRuedas);
        LinearLayout layoutMovRuedas = findViewById(R.id.layoutMovRuedas);

        ArrayAdapter<CharSequence> adapterMovRuedas = ArrayAdapter.createFromResource(
                this,
                R.array.movimientoRuedas,
                android.R.layout.simple_spinner_item
        );
        adapterMovRuedas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMovRuedas.setAdapter(adapterMovRuedas);

        // Encender LEDS
        buttonAddLED = findViewById(R.id.buttonAddLED);
        Spinner spinnerLED = findViewById(R.id.spinnerOptionsLEDPart);
        Spinner spinnerLEDColor = findViewById(R.id.spinnerOptionsLEDMode);
        LinearLayout layoutLED = findViewById(R.id.layoutLED);

        ArrayAdapter<CharSequence> adapterLED = ArrayAdapter.createFromResource(
                this,
                R.array.fled_part,
                android.R.layout.simple_spinner_item
        );
        adapterLED.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLED.setAdapter(adapterLED);

        ArrayAdapter<CharSequence> adapterLEDColor = ArrayAdapter.createFromResource(
                this,
                R.array.fled_mode,
                android.R.layout.simple_spinner_item
        );
        adapterLEDColor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLEDColor.setAdapter(adapterLEDColor);

        // Añadir imagen -------------------------------------------------------------------------------------
        BSelectImage = findViewById(R.id.BSelectImage);
        LinearLayout layoutImage = findViewById(R.id.layoutImage);

        // Añadir video -------------------------------------------------------------------------------------
        BSelectVideo = findViewById(R.id.BSelectVideo);
        LinearLayout layoutVideo = findViewById(R.id.layoutVideo);

        // Pregunta verdadero o false
        buttonAddTrueFalse = findViewById(R.id.buttonAddTrueFalse);
        Spinner spinnerTrueFalse = findViewById(R.id.spinnerOptionsTrueFalse);
        LinearLayout layoutTrueFalse = findViewById(R.id.layoutTrueFalse);
        EditText editTextTrueFalse = findViewById(R.id.editTextOptionTrueFalse);

        ArrayAdapter<CharSequence> adapterTrueFalse = ArrayAdapter.createFromResource(
                this,
                R.array.pregunta_true_false,
                android.R.layout.simple_spinner_item
        );
        adapterTrueFalse.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTrueFalse.setAdapter(adapterTrueFalse);


        // Configurar el Spinner Opciones de funcionalidades
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOptions.setAdapter(adapter);

        // Crear un ArrayList para contener los datos
        dataList = new ArrayList<>();

        // Crear un nuevo adaptador y establecerlo en el RecyclerView

        funcionalidadesActivity = new FuncionalidadesActivity(speechManager, systemManager, handMotionManager,
                headMotionManager, hardWareManager, wheelMotionManager, EditActivity.this);
        adapterV = new DataAdapter(dataList, funcionalidadesActivity, this);
        recyclerView.setAdapter(adapterV);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Configura el ItemTouchHelper
        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(adapterV);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        adapterV.setItemTouchHelper(itemTouchHelper);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        spinnerOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if (selectedItem.equals("Síntesis de voz")) {
                    layoutEditText.setVisibility(View.VISIBLE);
                    layoutExpresionFacial.setVisibility(View.GONE);
                    layoutMovBrazos.setVisibility(View.GONE);
                    layoutLED.setVisibility(View.GONE);
                    layoutMovCabeza.setVisibility(View.GONE);
                    layoutMovRuedas.setVisibility(View.GONE);
                    editTextOption.setHint("Introduce el discurso");
                    layoutTrueFalse.setVisibility(View.GONE);
                    layoutImage.setVisibility(View.GONE);
                    layoutVideo.setVisibility(View.GONE);
                } else if (selectedItem.equals("Movimiento de brazos")) {
                    layoutEditText.setVisibility(View.GONE);
                    layoutExpresionFacial.setVisibility(View.GONE);
                    layoutMovBrazos.setVisibility(View.VISIBLE);
                    layoutLED.setVisibility(View.GONE);
                    layoutMovCabeza.setVisibility(View.GONE);
                    layoutMovRuedas.setVisibility(View.GONE);
                    layoutTrueFalse.setVisibility(View.GONE);
                    layoutImage.setVisibility(View.GONE);
                    layoutVideo.setVisibility(View.GONE);
                } else if (selectedItem.equals("Movimiento de cabeza")) {
                    layoutEditText.setVisibility(View.GONE);
                    layoutExpresionFacial.setVisibility(View.GONE);
                    layoutMovBrazos.setVisibility(View.GONE);
                    layoutLED.setVisibility(View.GONE);
                    layoutMovCabeza.setVisibility(View.VISIBLE);
                    layoutMovRuedas.setVisibility(View.GONE);
                    layoutTrueFalse.setVisibility(View.GONE);
                    layoutImage.setVisibility(View.GONE);
                    layoutVideo.setVisibility(View.GONE);

                } else if (selectedItem.equals("Movimiento de ruedas")) {
                    layoutEditText.setVisibility(View.GONE);
                    layoutExpresionFacial.setVisibility(View.GONE);
                    layoutMovBrazos.setVisibility(View.GONE);
                    layoutLED.setVisibility(View.GONE);
                    layoutMovCabeza.setVisibility(View.GONE);
                    layoutMovRuedas.setVisibility(View.VISIBLE);
                    layoutTrueFalse.setVisibility(View.GONE);
                    layoutImage.setVisibility(View.GONE);
                    layoutVideo.setVisibility(View.GONE);

                } else if (selectedItem.equals("Encender LEDs")) {
                    layoutEditText.setVisibility(View.GONE);
                    layoutExpresionFacial.setVisibility(View.GONE);
                    layoutMovBrazos.setVisibility(View.GONE);
                    layoutLED.setVisibility(View.VISIBLE);
                    layoutMovCabeza.setVisibility(View.GONE);
                    layoutMovRuedas.setVisibility(View.GONE);
                    layoutTrueFalse.setVisibility(View.GONE);
                    layoutImage.setVisibility(View.GONE);
                    layoutVideo.setVisibility(View.GONE);
                } else if (selectedItem.equals("Cambio de expresión facial")) {
                    layoutEditText.setVisibility(View.GONE);
                    layoutMovBrazos.setVisibility(View.GONE);
                    layoutExpresionFacial.setVisibility(View.VISIBLE);
                    spinnerFacial.setEnabled(true);
                    layoutLED.setVisibility(View.GONE);
                    layoutMovCabeza.setVisibility(View.GONE);
                    layoutMovRuedas.setVisibility(View.GONE);
                    layoutTrueFalse.setVisibility(View.GONE);
                    layoutImage.setVisibility(View.GONE);
                    layoutVideo.setVisibility(View.GONE);

                } else if (selectedItem.equals("Insertar imagen")) {
                    layoutEditText.setVisibility(View.GONE);
                    layoutExpresionFacial.setVisibility(View.GONE);
                    layoutMovBrazos.setVisibility(View.GONE);
                    layoutLED.setVisibility(View.GONE);
                    layoutMovCabeza.setVisibility(View.GONE);
                    layoutMovRuedas.setVisibility(View.GONE);
                    layoutTrueFalse.setVisibility(View.GONE);
                    layoutImage.setVisibility(View.VISIBLE);
                    layoutVideo.setVisibility(View.GONE);

                } else if (selectedItem.equals("Insertar vídeo")) {
                    layoutEditText.setVisibility(View.GONE);
                    layoutExpresionFacial.setVisibility(View.GONE);
                    layoutMovBrazos.setVisibility(View.GONE);
                    layoutLED.setVisibility(View.GONE);
                    layoutMovCabeza.setVisibility(View.GONE);
                    layoutMovRuedas.setVisibility(View.GONE);
                    layoutTrueFalse.setVisibility(View.GONE);
                    layoutImage.setVisibility(View.GONE);
                    layoutVideo.setVisibility(View.VISIBLE);

                }  else if (selectedItem.equals("Pregunta verdadero o falso")) {
                    layoutEditText.setVisibility(View.GONE);
                    layoutExpresionFacial.setVisibility(View.GONE);
                    layoutMovBrazos.setVisibility(View.GONE);
                    layoutLED.setVisibility(View.GONE);
                    layoutMovCabeza.setVisibility(View.GONE);
                    layoutMovRuedas.setVisibility(View.GONE);
                    layoutTrueFalse.setVisibility(View.VISIBLE);
                    layoutImage.setVisibility(View.GONE);
                    layoutVideo.setVisibility(View.GONE);

                } else {
                    layoutEditText.setVisibility(View.GONE);
                    layoutExpresionFacial.setVisibility(View.GONE);
                    layoutMovBrazos.setVisibility(View.GONE);
                    layoutLED.setVisibility(View.GONE);
                    layoutMovCabeza.setVisibility(View.GONE);
                    layoutMovRuedas.setVisibility(View.GONE);
                    layoutTrueFalse.setVisibility(View.GONE);
                    layoutImage.setVisibility(View.GONE);
                    layoutVideo.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hacer nada si no hay selección
            }
        });

        spinnerMovRuedas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               String selectedItem = parent.getItemAtPosition(position).toString();
               if (selectedItem.equals("Avanzar") || selectedItem.equals("Retroceder")) {
                   editTextMovRuedas.setVisibility(View.VISIBLE);
               } else {
                   editTextMovRuedas.setVisibility(View.GONE);
               }
           }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hacer nada si no hay selección
            }
       });


        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el texto y la opción del EditText y el Spinner
                String text = editTextOption.getText().toString().trim();
                String spinnerOption = spinnerOptions.getSelectedItem().toString();

                // Verificar si el texto y la opción no están vacíos
                if (!TextUtils.isEmpty(text)) {
                    // Crear un nuevo objeto DataModel y agregarlo a la lista
                    DataModel dataModel = new DataModel(text, spinnerOption, "");
                    dataList.add(dataModel);

                    // Limpiar el EditText después de agregar el elemento
                    editTextOption.setText("");

                    // Notificar al adaptador que se ha agregado un nuevo elemento
                    adapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(dataList.size() - 1);
                } else {
                    // Mostrar un mensaje de error si el texto está vacío
                    Toast.makeText(EditActivity.this, "Introduce un valor", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonAddExpresionFacial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el texto y la opción del EditText y el Spinner
                String text = spinnerFacial.getSelectedItem().toString();
                String spinnerOption = spinnerOptions.getSelectedItem().toString();

                // Verificar si el texto y la opción no están vacíos
                if (!TextUtils.isEmpty(text)) {
                    // Crear un nuevo objeto DataModel y agregarlo a la lista
                    DataModel dataModel = new DataModel(text, spinnerOption, "");
                    dataList.add(dataModel);
                    adapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(dataList.size() - 1);
                } else {
                    // Mostrar un mensaje de error si el texto está vacío
                    Toast.makeText(EditActivity.this, "Introduce un valor", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonAddMovBrazos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el texto y la opción del EditText y el Spinner
                String text = spinnerMovBrazos.getSelectedItem().toString();
                String spinnerOption = spinnerOptions.getSelectedItem().toString();

                // Verificar si el texto y la opción no están vacíos
                if (!TextUtils.isEmpty(text)) {
                    // Crear un nuevo objeto DataModel y agregarlo a la lista
                    DataModel dataModel = new DataModel(text, spinnerOption, "");
                    dataList.add(dataModel);
                    adapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(dataList.size() - 1);
                } else {
                    // Mostrar un mensaje de error si el texto está vacío
                    Toast.makeText(EditActivity.this, "Introduce un valor", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonAddMovCabeza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el texto y la opción del EditText y el Spinner
                String text = spinnerMovCabeza.getSelectedItem().toString();
                String spinnerOption = spinnerOptions.getSelectedItem().toString();

                // Verificar si el texto y la opción no están vacíos
                if (!TextUtils.isEmpty(text)) {
                    // Crear un nuevo objeto DataModel y agregarlo a la lista
                    DataModel dataModel = new DataModel(text, spinnerOption, "");
                    dataList.add(dataModel);
                    adapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(dataList.size() - 1);
                } else {
                    // Mostrar un mensaje de error si el texto está vacío
                    Toast.makeText(EditActivity.this, "Introduce un valor", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonAddMovRuedas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el texto y la opción del EditText y el Spinner
                String text = spinnerMovRuedas.getSelectedItem().toString();
                String spinnerOption = spinnerOptions.getSelectedItem().toString();

                if (text.equals("Avanzar") || text.equals("Retroceder")) {
                    text = text + "-" + editTextMovRuedas.getText().toString();
                }

                // Verificar si el texto y la opción no están vacíos
                if (!TextUtils.isEmpty(text)) {
                    // Crear un nuevo objeto DataModel y agregarlo a la lista
                    DataModel dataModel = new DataModel(text, spinnerOption, "");
                    dataList.add(dataModel);
                    adapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(dataList.size() - 1);
                } else {
                    // Mostrar un mensaje de error si el texto está vacío
                    Toast.makeText(EditActivity.this, "Introduce un valor", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonAddLED.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gardamos la opción de los dos selectores separada por un guión
                String text = spinnerLED.getSelectedItem().toString() + "-" + spinnerLEDColor.getSelectedItem().toString();
                String spinnerOption = spinnerOptions.getSelectedItem().toString();

                // Verificar si el texto y la opción no están vacíos
                if (!TextUtils.isEmpty(text)) {
                    // Crear un nuevo objeto DataModel y agregarlo a la lista
                    DataModel dataModel = new DataModel(text, spinnerOption, "");
                    dataList.add(dataModel);
                    adapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(dataList.size() - 1);
                } else {
                    // Mostrar un mensaje de error si el texto está vacío
                    Toast.makeText(EditActivity.this, "Introduce un valor", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonAddTrueFalse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el texto y la opción del EditText y el Spinner
                String text = editTextTrueFalse.getText().toString()+ "-" + spinnerTrueFalse.getSelectedItem().toString();
                String spinnerOption = spinnerOptions.getSelectedItem().toString();

                // Verificar si el texto y la opción no están vacíos
                if (!TextUtils.isEmpty(text)) {
                    // Crear un nuevo objeto DataModel y agregarlo a la lista
                    DataModel dataModel = new DataModel(text, spinnerOption, "");
                    dataList.add(dataModel);
                    adapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(dataList.size() - 1);
                } else {
                    // Mostrar un mensaje de error si el texto está vacío
                    Toast.makeText(EditActivity.this, "Introduce un valor", Toast.LENGTH_SHORT).show();
                }
            }
        });

        BSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un intent para seleccionar una imagen de la galería
                mostrarDialogoSubirImagen();
            }
        });

        BSelectVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un intent para seleccionar un video de la galería
                mostrarDialogoSubirVideo();
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // Guardar el estado
                System.out.println("dataList: " + dataList + "BOTON GUARDAR");
                setResult(RESULT_OK);
                finish();
            }
        });

        buttonReproducir.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // Iniciar actividad imagen
                Intent intent = new Intent(EditActivity.this, ImageActivity.class);
                intent.putExtra("dataList", dataList);
                startActivity(intent);
            }
        });

    }


    private void populateFields () {
        if (mRowId != null) {
            Cursor bloqueAcciones = mDbHelperBloque.fetchBloqueAcciones(mRowId);
            startManagingCursor(bloqueAcciones);
            mNombreText.setText(bloqueAcciones.getString(bloqueAcciones.getColumnIndexOrThrow(BloqueAccionesDbAdapter.KEY_NOMBRE)));

            System.out.println("BloqueAccionesDbAdapter.KEY_NOMBRE: " + bloqueAcciones.getString(bloqueAcciones.getColumnIndexOrThrow(BloqueAccionesDbAdapter.KEY_NOMBRE)));
            // Cargar los datos de la base de datos en el RecyclerView
            Cursor acciones = mDbHelperAcciones.fetchAllAcciones(mRowId);

            // Gurdar del data list los datos de la base de datos
            dataList.clear();
            if (acciones.moveToFirst()) {
                do {
                    String accion = acciones.getString(acciones.getColumnIndexOrThrow(AccionesDbAdapter.KEY_CONFIGURACION));
                    String tipo = acciones.getString(acciones.getColumnIndexOrThrow(AccionesDbAdapter.KEY_FUNCIONALIDAD));
                    String imagen = acciones.getString(acciones.getColumnIndexOrThrow(AccionesDbAdapter.KEY_IMAGEN));
                    System.out.println("POPULATE FIELDS accion: " + accion + " tipo: " + tipo + " imagen: " + imagen);
                    DataModel dataModel = new DataModel(accion, tipo, imagen);
                    dataList.add(dataModel);
                } while (acciones.moveToNext());
            }
            //adapterV.notifyDataSetChanged();
        }
    }

    @Override
    protected void onSaveInstanceState ( Bundle outState ) {
        super.onSaveInstanceState( outState ) ;
        saveState();
        outState.putSerializable (BloqueAccionesDbAdapter.KEY_ROWID , mRowId ) ;
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

    private void saveState() {
        System.out.println("dataList: " + dataList + "SAVE STATE");
        String nombre = mNombreText.getText().toString();
        if ( nombre.equals("")) {
            Toast.makeText(getApplicationContext(), "Bloque de acciones no creado/modificado, campos inválidos", Toast.LENGTH_SHORT).show();
        } else {
            if (mRowId == null) {
                System.out.println("SAVE STATE mRowId NULO: " + mRowId);
                if (mRowIdPresentacion == -1) {
                    // No se encontró el ID de la presentación, muestra un mensaje de error
                    Toast.makeText(getApplicationContext(), "No se pudo encontrar el ID de la presentación", Toast.LENGTH_SHORT).show();
                } else {
                    // Se encontró el ID de la presentación, crea un nuevo bloque de acciones
                    long id = mDbHelperBloque.createBloqueAcciones(nombre, mRowIdPresentacion);
                    if (id > 0) {
                        mRowId = id;
                        // Para cada elemento del datalist, guardar en la base de datos
                        if(dataList.size() > 0) {
                            for (DataModel data : dataList) {
                                System.out.println("SAVE STATE accion: " + data.getText() + " tipo: " + data.getSpinnerOption() + " imagen: " + data.getImagen());
                                mDbHelperAcciones.createAcciones(data.getSpinnerOption(), data.getText(), mRowId, data.getImagen());
                            }
                        }

                    }
                }
            } else {
                System.out.println("SAVE STATE mRowId NO NULO: " + mRowId);
                mDbHelperBloque.updateBloqueAcciones(mRowId, nombre);
                // Guardar las acciones en la base de datos
                // Borrar todas las acciones del bloque y las vuelve a crear con los datos del datalist
                if (dataList.size() > 0){
                    mDbHelperAcciones.deleteAllAcciones(mRowId);
                    // Para cada elemento del datalist, guardar en la base de datos
                    for (DataModel data : dataList) {
                        System.out.println("SAVE STATE accion: " + data.getText() + " tipo: " + data.getSpinnerOption() + " imagen: " + data.getImagen());
                        mDbHelperAcciones.createAcciones(data.getSpinnerOption(), data.getText(), mRowId, data.getImagen());
                    }
                } else {
                    dataList.size();
                    mDbHelperAcciones.deleteAllAcciones(mRowId);
                }
            }


        }
    }

    @SuppressLint("SetTextI18n")
    private void mostrarDialogoSubirImagen() {
        // Inflar el layout del diálogo personalizado
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.popup_selectimage, null);

        // Obtener referencias a los botones del layout
        Button btnSelectImage = dialogView.findViewById(R.id.btn_select_image);
        Button btnSelectUrl = dialogView.findViewById(R.id.btn_select_url);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnAceptar = dialogView.findViewById(R.id.btn_aceptar);
        EditText ETTexto = dialogView.findViewById(R.id.ETTiempo);

        // Obtener referencias a los elementos del layout url
        LinearLayout LLUrl = dialogView.findViewById(R.id.layoutEditText);
        EditText ETUrl = dialogView.findViewById(R.id.editTextOption);
        Button btnAnadir = dialogView.findViewById(R.id.buttonAdd);
        // One Preview Image
        IVPreviewImage = dialogView.findViewById(R.id.IVPreviewImage);


        // Configurar el comportamiento del botón "Cancelar"
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Si el usuario elige "Cancelar", simplemente cierra el diálogo
                dialog.dismiss();
            }
        });

        // Configurar el comportamiento del botón "Seleccionar imagen"
        btnSelectUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LLUrl.setVisibility(View.VISIBLE);
                IVPreviewImage.setVisibility(View.GONE);
            }
        });

        // Configurar el comportamiento del botón "Eliminar"
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedImageUri != null) {
                    String text = ETTexto.getText().toString() + "-" + selectedImageUri.toString();
                    String spinnerOption = spinnerOptions.getSelectedItem().toString();

                    // Verificar si el texto y la opción no están vacíos
                    if (!TextUtils.isEmpty(text)) {
                        // Crear un nuevo objeto DataModel y agregarlo a la lista
                        DataModel dataModel = new DataModel(text, spinnerOption, "");
                        dataList.add(dataModel);
                        //adapterV.notifyDataSetChanged();
                        recyclerView.scrollToPosition(dataList.size() - 1);
                    } else {
                        // Mostrar un mensaje de error si el texto está vacío
                        Toast.makeText(EditActivity.this, "Introduce un valor", Toast.LENGTH_SHORT).show();
                    }
                } else if ( !ETUrl.toString().isEmpty() ) {
                    String text = ETTexto.getText().toString() + "-" + ETUrl.getText().toString();
                    String spinnerOption = spinnerOptions.getSelectedItem().toString();

                    // Verificar si el texto y la opción no están vacíos
                    if (!TextUtils.isEmpty(text)) {
                        // Crear un nuevo objeto DataModel y agregarlo a la lista
                        DataModel dataModel = new DataModel(text, spinnerOption, "");
                        dataList.add(dataModel);
                        //adapterV.notifyDataSetChanged();
                        recyclerView.scrollToPosition(dataList.size() - 1);
                    } else {
                        // Mostrar un mensaje de error si el texto está vacío
                        Toast.makeText(EditActivity.this, "Introduce un valor", Toast.LENGTH_SHORT).show();
                    }
                }


                // Si no hay selected image
                dialog.dismiss();
            }
        });

        // Configurar el comportamiento del botón "Seleccionar imagen"
        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissions();
                IVPreviewImage.setVisibility(View.VISIBLE);
                LLUrl.setVisibility(View.GONE);
                // Si el usuario elige "Seleccionar imagen", se abre la galería
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, SELECT_PICTURE);
            }
        });

        btnAnadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Coger url del edit y mostrar la imagen en el layout de imagen
                if( ETUrl.getText().toString().isEmpty()){
                    Toast.makeText(EditActivity.this, "Introduce una URL", Toast.LENGTH_SHORT).show();
                }else {
                    IVPreviewImage.setVisibility(View.VISIBLE);
                    String url = ETUrl.getText().toString();

                    Glide.with(EditActivity.this)
                            .load(url)
                            .into(IVPreviewImage);


                }

            }
        });

        // Configurar AlertDialog con el layout personalizado y el contexto adecuado
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        // Crear y mostrar el diálogo
        dialog = builder.create();
        dialog.show();
    }

    // Dialogo para seleccionar video
    private void mostrarDialogoSubirVideo() {
        // Inflar el layout del diálogo personalizado
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.popup_selectvideo, null);

        // Obtener referencias a los botones del layout
        Button btnSelectVideo = dialogView.findViewById(R.id.btn_select_video);
        Button btnSelectUrl = dialogView.findViewById(R.id.btn_select_url);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnAceptar = dialogView.findViewById(R.id.btn_aceptar);

        // Obtener referencias a los elementos del layout url
        LinearLayout LLUrl = dialogView.findViewById(R.id.layoutEditText);
        EditText ETUrl = dialogView.findViewById(R.id.editTextOption);
        // One Preview Image
        IVPreviewVideo = dialogView.findViewById(R.id.IVPreviewVideo);

        // Configurar el comportamiento del botón "Cancelar"
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Si el usuario elige "Cancelar", simplemente cierra el diálogo
                dialog.dismiss();
            }
        });

        // Configurar el comportamiento del botón "Seleccionar video"
        btnSelectUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LLUrl.setVisibility(View.VISIBLE);
                IVPreviewVideo.setVisibility(View.GONE);
            }
        });

        // Configurar el comportamiento del botón "Eliminar"
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedVideoUri != null) {
                    String text =  selectedVideoUri.toString();
                    String spinnerOption = spinnerOptions.getSelectedItem().toString();

                    // Verificar si el texto y la opción no están vacíos
                    if (!TextUtils.isEmpty(text)) {
                        // Crear un nuevo objeto DataModel y agregarlo a la lista
                        DataModel dataModel = new DataModel(text, spinnerOption, "");
                        dataList.add(dataModel);
                        //adapterV.notifyDataSetChanged();
                        recyclerView.scrollToPosition(dataList.size() - 1);
                    } else {
                        // Mostrar un mensaje de error si el texto está vacío
                        Toast.makeText(EditActivity.this, "Introduce un valor", Toast.LENGTH_SHORT).show();
                    }
                } else if ( !ETUrl.toString().isEmpty() ) {
                    String text = ETUrl.getText().toString();
                    String spinnerOption = spinnerOptions.getSelectedItem().toString();

                    // Verificar si el texto y la opción no están vacíos
                    if (!TextUtils.isEmpty(text)) {
                        // Crear un nuevo objeto DataModel y agregarlo a la lista
                        DataModel dataModel = new DataModel(text, spinnerOption, "");
                        dataList.add(dataModel);
                        //adapterV.notifyDataSetChanged();
                        recyclerView.scrollToPosition(dataList.size() - 1);
                    } else {
                        // Mostrar un mensaje de error si el texto está vacío
                        Toast.makeText(EditActivity.this, "Introduce un valor", Toast.LENGTH_SHORT).show();
                    }
                }


                // Si no hay selected image
                dialog.dismiss();
            }
        });

        // Configurar el comportamiento del botón "Seleccionar imagen"
        btnSelectVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissions();
                IVPreviewVideo.setVisibility(View.VISIBLE);
                LLUrl.setVisibility(View.GONE);

                // Si el usuario elige "Seleccionar un video", se abre la galería

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                try {
                    startActivityForResult(Intent.createChooser(intent, "Select a Video"), SELECT_VIDEO);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    // Handle the error
                    Toast.makeText(EditActivity.this, "No application found to pick video", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Configurar AlertDialog con el layout personalizado y el contexto adecuado
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        // Crear y mostrar el diálogo
        dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    IVPreviewImage.setImageURI(selectedImageUri);
                }
            }

            //TODO: VERRRR COMO RENDERIZAR LA IMAGEN
            if (requestCode == 202) {
                // Get the url of the image from data
                Uri uri = data.getData();
                if (null != uri) {
                    System.out.println("uri activity result = " + uri);
                    // Aquí puedes guardar la URI seleccionada y actualizar la vista previa de la imagen
                    adapterV.updateImagePreview(uri);

                }
            }

            if (requestCode == SELECT_VIDEO) {
                // Get the url of the image from data
                selectedVideoUri = data.getData();
                if (null != selectedVideoUri) {
                    System.out.println("selectedVideoUri = " + selectedVideoUri);
                    // update the preview image in the layout
                    IVPreviewVideo.setVideoURI(selectedVideoUri);
                    IVPreviewVideo.start();
                }
            }
        }
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
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
