package com.example.sanbotapp;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;
import java.util.Objects;


public class EditActivity extends TopBaseActivity {

    private EditText editTextTitle;
    private Spinner spinnerOptions;
    private Button buttonSave, buttonAdd, buttonAddExpresionFacial, buttonAddMovBrazos;
    private Button buttonAddMovCabeza, buttonAddMovRuedas, buttonAddLED, buttonAddTrueFalse;
    private ArrayList<DataModel> dataList;
    private TextView textViewOptions;
    private LinearLayout layoutTextView;

    private SpeechManager speechManager;
    private SystemManager systemManager;
    private HandMotionManager handMotionManager;
    private HeadMotionManager headMotionManager;
    private HardWareManager hardWareManager; //leds //touch sensors //voice locate //gyroscope
    private WheelMotionManager wheelMotionManager;

    private RecyclerView recyclerView;
    private DataAdapter adapterV;

    private FuncionalidadesActivity funcionalidadesActivity;


    @Override
    protected void onMainServiceConnected() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        onMainServiceConnected();
        setContentView(R.layout.activity_edit);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar_title_conf);

        speechManager = (SpeechManager) getUnitManager(FuncConstant.SPEECH_MANAGER);
        systemManager = (SystemManager) getUnitManager(FuncConstant.SYSTEM_MANAGER);
        handMotionManager = (HandMotionManager) getUnitManager(FuncConstant.HANDMOTION_MANAGER);
        headMotionManager = (HeadMotionManager) getUnitManager(FuncConstant.HEADMOTION_MANAGER);
        hardWareManager = (HardWareManager) getUnitManager(FuncConstant.HARDWARE_MANAGER);
        wheelMotionManager = (WheelMotionManager) getUnitManager(FuncConstant.WHEELMOTION_MANAGER);


        // Obtener referencias a los elementos de UI
        editTextTitle = findViewById(R.id.editTextTitleBloque);
        spinnerOptions = findViewById(R.id.spinnerOptions);
        buttonSave = findViewById(R.id.button_save);
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
        adapterV = new DataAdapter(dataList, funcionalidadesActivity);
        recyclerView.setAdapter(adapterV);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Configura el ItemTouchHelper
        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(adapterV);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        adapterV.setItemTouchHelper(itemTouchHelper);
        itemTouchHelper.attachToRecyclerView(recyclerView);


        // TODO: Agregar más opciones si es necesario

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
                } else if (selectedItem.equals("Movimiento de brazos")) {
                    layoutEditText.setVisibility(View.GONE);
                    layoutExpresionFacial.setVisibility(View.GONE);
                    layoutMovBrazos.setVisibility(View.VISIBLE);
                    layoutLED.setVisibility(View.GONE);
                    layoutMovCabeza.setVisibility(View.GONE);
                    layoutMovRuedas.setVisibility(View.GONE);
                    layoutTrueFalse.setVisibility(View.GONE);


                } else if (selectedItem.equals("Movimiento de cabeza")) {
                    layoutEditText.setVisibility(View.GONE);
                    layoutExpresionFacial.setVisibility(View.GONE);
                    layoutMovBrazos.setVisibility(View.GONE);
                    layoutLED.setVisibility(View.GONE);
                    layoutMovCabeza.setVisibility(View.VISIBLE);
                    layoutMovRuedas.setVisibility(View.GONE);
                    layoutTrueFalse.setVisibility(View.GONE);


                } else if (selectedItem.equals("Movimiento de ruedas")) {
                    layoutEditText.setVisibility(View.GONE);
                    layoutExpresionFacial.setVisibility(View.GONE);
                    layoutMovBrazos.setVisibility(View.GONE);
                    layoutLED.setVisibility(View.GONE);
                    layoutMovCabeza.setVisibility(View.GONE);
                    layoutMovRuedas.setVisibility(View.VISIBLE);
                    layoutTrueFalse.setVisibility(View.GONE);


                } else if (selectedItem.equals("Encender LEDs")) {
                    layoutEditText.setVisibility(View.GONE);
                    layoutExpresionFacial.setVisibility(View.GONE);
                    layoutMovBrazos.setVisibility(View.GONE);
                    layoutLED.setVisibility(View.VISIBLE);
                    layoutMovCabeza.setVisibility(View.GONE);
                    layoutMovRuedas.setVisibility(View.GONE);
                    layoutTrueFalse.setVisibility(View.GONE);

                } else if (selectedItem.equals("Cambio de expresión facial")) {
                    layoutEditText.setVisibility(View.GONE);
                    layoutMovBrazos.setVisibility(View.GONE);
                    layoutExpresionFacial.setVisibility(View.VISIBLE);
                    spinnerFacial.setEnabled(true);
                    layoutLED.setVisibility(View.GONE);
                    layoutMovCabeza.setVisibility(View.GONE);
                    layoutMovRuedas.setVisibility(View.GONE);
                    layoutTrueFalse.setVisibility(View.GONE);


                } else if (selectedItem.equals("Insertar imagen")) {
                    layoutEditText.setVisibility(View.GONE);
                    layoutExpresionFacial.setVisibility(View.GONE);
                    layoutMovBrazos.setVisibility(View.GONE);
                    layoutLED.setVisibility(View.GONE);
                    layoutMovCabeza.setVisibility(View.GONE);
                    layoutMovRuedas.setVisibility(View.GONE);
                    layoutTrueFalse.setVisibility(View.GONE);

                } else if (selectedItem.equals("Insertar vídeo")) {
                    layoutEditText.setVisibility(View.GONE);
                    layoutExpresionFacial.setVisibility(View.GONE);
                    layoutMovBrazos.setVisibility(View.GONE);
                    layoutLED.setVisibility(View.GONE);
                    layoutMovCabeza.setVisibility(View.GONE);
                    layoutMovRuedas.setVisibility(View.GONE);
                    layoutTrueFalse.setVisibility(View.GONE);

                }  else if (selectedItem.equals("Pregunta verdadero o falso")) {
                    layoutEditText.setVisibility(View.GONE);
                    layoutExpresionFacial.setVisibility(View.GONE);
                    layoutMovBrazos.setVisibility(View.GONE);
                    layoutLED.setVisibility(View.GONE);
                    layoutMovCabeza.setVisibility(View.GONE);
                    layoutMovRuedas.setVisibility(View.GONE);
                    layoutTrueFalse.setVisibility(View.VISIBLE);

                } else {
                    layoutEditText.setVisibility(View.GONE);
                    layoutExpresionFacial.setVisibility(View.GONE);
                    layoutMovBrazos.setVisibility(View.GONE);
                    layoutLED.setVisibility(View.GONE);
                    layoutMovCabeza.setVisibility(View.GONE);
                    layoutMovRuedas.setVisibility(View.GONE);
                    layoutTrueFalse.setVisibility(View.GONE);

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
                    DataModel dataModel = new DataModel(text, spinnerOption);
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
                    DataModel dataModel = new DataModel(text, spinnerOption);
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
                    DataModel dataModel = new DataModel(text, spinnerOption);
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
                    DataModel dataModel = new DataModel(text, spinnerOption);
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
                    DataModel dataModel = new DataModel(text, spinnerOption);
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
                    DataModel dataModel = new DataModel(text, spinnerOption);
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
                    DataModel dataModel = new DataModel(text, spinnerOption);
                    dataList.add(dataModel);
                    adapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(dataList.size() - 1);
                } else {
                    // Mostrar un mensaje de error si el texto está vacío
                    Toast.makeText(EditActivity.this, "Introduce un valor", Toast.LENGTH_SHORT).show();
                }
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
