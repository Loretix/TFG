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
import com.qihancloud.opensdk.function.unit.SpeechManager;
import com.qihancloud.opensdk.function.unit.SystemManager;

import java.util.ArrayList;
import java.util.Objects;


public class EditActivity extends TopBaseActivity {

    private EditText editTextTitle;
    private Spinner spinnerOptions;
    private Button buttonSave, buttonAdd, buttonAddExpresionFacial;
    private ArrayList<DataModel> dataList;
    private TextView textViewOptions;
    private LinearLayout layoutTextView;

    private SpeechManager speechManager;
    private SystemManager systemManager;

    private RecyclerView recyclerView;
    private DataAdapter adapterV;


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


        // Obtener referencias a los elementos de UI
        editTextTitle = findViewById(R.id.editTextTitleBloque);
        spinnerOptions = findViewById(R.id.spinnerOptions);
        buttonSave = findViewById(R.id.button_save);
        buttonAdd = findViewById(R.id.buttonAdd);
        recyclerView = findViewById(R.id.recycler_view);

        // Expresion facial
        LinearLayout layoutEditText = findViewById(R.id.layoutEditText);
        EditText editTextOption = findViewById(R.id.editTextOption);
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




        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOptions.setAdapter(adapter);

        // Crear un ArrayList para contener los datos
        dataList = new ArrayList<>();

        // Crear un nuevo adaptador y establecerlo en el RecyclerView
        adapterV = new DataAdapter(dataList, this);
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
                    editTextOption.setHint("Introduce el discurso");
                } else if (selectedItem.equals("Movimiento de brazos")) {
                    layoutEditText.setVisibility(View.GONE);
                    layoutExpresionFacial.setVisibility(View.GONE);

                } else if (selectedItem.equals("Movimiento de cabeza")) {
                    layoutEditText.setVisibility(View.GONE);
                    layoutExpresionFacial.setVisibility(View.GONE);

                } else if (selectedItem.equals("Movimiento de ruedas")) {
                    layoutEditText.setVisibility(View.GONE);
                    layoutExpresionFacial.setVisibility(View.GONE);

                } else if (selectedItem.equals("Encender LEDs")) {
                    layoutEditText.setVisibility(View.GONE);
                    layoutExpresionFacial.setVisibility(View.GONE);

                } else if (selectedItem.equals("Cambio de expresión facial")) {
                    layoutEditText.setVisibility(View.GONE);
                    layoutExpresionFacial.setVisibility(View.VISIBLE);
                    spinnerFacial.setEnabled(true);

                } else if (selectedItem.equals("Insertar imagen")) {
                    layoutEditText.setVisibility(View.GONE);
                    layoutExpresionFacial.setVisibility(View.GONE);

                } else if (selectedItem.equals("Insertar vídeo")) {
                    layoutEditText.setVisibility(View.GONE);
                    layoutExpresionFacial.setVisibility(View.GONE);

                } else {
                    layoutEditText.setVisibility(View.GONE);
                    layoutExpresionFacial.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hacer nada si no hay selección
            }
        });

        spinnerFacial.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

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
                } else {
                    // Mostrar un mensaje de error si el texto está vacío
                    Toast.makeText(EditActivity.this, "Introduce un valor", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    //  FUNCIONALIDADES ROBOT
    public void speakOperation(String texto, String tipo){

        SpeakOption speakOption = new SpeakOption();
        if( tipo == "Normal"){
            speakOption.setSpeed(60);
            speakOption.setIntonation(50);
        }
        speechManager.startSpeak(texto, speakOption);
    }

    public void changeFaceOperation( String tipo){
        if(Objects.equals(tipo, "Arrogancia")){
            systemManager.showEmotion(EmotionsType.ARROGANCE);
        } else if (Objects.equals(tipo, "Sorpresa")){
            systemManager.showEmotion(EmotionsType.SURPRISE);
        } else if (Objects.equals(tipo, "Silbido")){
            systemManager.showEmotion(EmotionsType.WHISTLE);
        } else if (Objects.equals(tipo, "Enamorado")){
            systemManager.showEmotion(EmotionsType.LAUGHTER);
        } else if (Objects.equals(tipo, "Adiós")){
            systemManager.showEmotion(EmotionsType.GOODBYE);
        } else if (Objects.equals(tipo, "Tímido")){
            systemManager.showEmotion(EmotionsType.SHY);
        } else if (Objects.equals(tipo, "Normal")){
            systemManager.showEmotion(EmotionsType.NORMAL);
        } else if (Objects.equals(tipo, "Sudor")){
            systemManager.showEmotion(EmotionsType.SWEAT);
        } else if (Objects.equals(tipo, "Risa contenida")){
            systemManager.showEmotion(EmotionsType.SNICKER);
        } else if (Objects.equals(tipo, "Recoger nariz")){
            systemManager.showEmotion(EmotionsType.PICKNOSE);
        } else if (Objects.equals(tipo, "Llanto")){
            systemManager.showEmotion(EmotionsType.CRY);
        } else if (Objects.equals(tipo, "Abuso")){
            systemManager.showEmotion(EmotionsType.ABUSE);
        } else if (Objects.equals(tipo, "Beso")){
            systemManager.showEmotion(EmotionsType.KISS);
        } else if (Objects.equals(tipo, "Dormir")){
            systemManager.showEmotion(EmotionsType.SLEEP);
        } else if (Objects.equals(tipo, "Sonrisa")){
            systemManager.showEmotion(EmotionsType.SMILE);
        } else if (Objects.equals(tipo, "Queja")){
            systemManager.showEmotion(EmotionsType.GRIEVANCE);
        } else if (Objects.equals(tipo, "Pregunta")){
            systemManager.showEmotion(EmotionsType.QUESTION);
        } else if (Objects.equals(tipo, "Desmayo")){
            systemManager.showEmotion(EmotionsType.FAINT);
        } else if (Objects.equals(tipo, "Elogio")){
            systemManager.showEmotion(EmotionsType.PRISE);
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
