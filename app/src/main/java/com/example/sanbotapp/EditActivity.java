package com.example.sanbotapp;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.qihancloud.opensdk.function.beans.SpeakOption;
import com.qihancloud.opensdk.function.unit.SpeechManager;

import java.util.ArrayList;


public class EditActivity extends TopBaseActivity {

    private EditText editTextTitle;
    private Spinner spinnerOptions;
    private Button buttonSave, buttonAdd;
    private ArrayList<DataModel> dataList;
    private TextView textViewOptions;
    private LinearLayout layoutTextView;

    private SpeechManager speechManager;

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

        // Obtener referencias a los elementos de UI
        editTextTitle = findViewById(R.id.editTextTitleBloque);
        spinnerOptions = findViewById(R.id.spinnerOptions);
        buttonSave = findViewById(R.id.button_save);
        buttonAdd = findViewById(R.id.buttonAdd);
        recyclerView = findViewById(R.id.recycler_view);

        //layoutTextView = findViewById(R.id.layoutTextView);
        LinearLayout layoutEditText = findViewById(R.id.layoutEditText);
        EditText editTextOption = findViewById(R.id.editTextOption);


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


    }

   /*

   FUNCION VIEJA SIN BOTON ACCION

    private void actualizarInterfaz() {
        StringBuilder stringBuilder = new StringBuilder();
        for (DataModel data : dataList) {
            stringBuilder.append("Spinner Option: ").append(data.getSpinnerOption()).append("\n");
            stringBuilder.append("Text: ").append(data.getText()).append("\n\n");
        }
        textViewOptions.setText(stringBuilder.toString());
    }*/

    @SuppressLint("SetTextI18n")

    private void actualizarInterfaz() {
        // Obtener el último elemento agregado
        DataModel ultimoElemento = dataList.get(dataList.size() - 1);

        // Crear un nuevo LinearLayout para cada par de TextView y Button
        LinearLayout nuevoLinearLayout = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        // Agregar márgenes en la parte inferior
        params.bottomMargin = 20;
        nuevoLinearLayout.setLayoutParams(params);

        // Agregar color de fondo;
        nuevoLinearLayout.setBackground(getResources().getDrawable(R.drawable.action_shape));

        // Crear un nuevo TextView para mostrar el texto del último elemento
        TextView nuevoTextView = new TextView(this);
        nuevoTextView.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1
        ));
        nuevoTextView.setText("Spinner Option: " + ultimoElemento.getSpinnerOption() + "\n"
                + "Text: " + ultimoElemento.getText() + "\n\n");

        // Crear un nuevo botón para acciones relacionadas con el nuevo texto
        Button nuevoBoton = new Button(this);
        nuevoBoton.setText("Acción");
        nuevoBoton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        Button eliminarBoton = new Button(this);
        eliminarBoton.setText("Eliminar");
        eliminarBoton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        // Crear un nuevo LinearLayout para contener el TextView y el botón
        LinearLayout contenidoLayout = new LinearLayout(this);
        contenidoLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        contenidoLayout.setOrientation(LinearLayout.HORIZONTAL);
        contenidoLayout.setGravity(Gravity.CENTER_VERTICAL);

        // Agregar el TextView y el botón al LinearLayout de contenido
        contenidoLayout.addView(nuevoTextView);
        contenidoLayout.addView(nuevoBoton);
        contenidoLayout.addView(eliminarBoton);

        // Agregar el LinearLayout de contenido al nuevo LinearLayout principal
        nuevoLinearLayout.addView(contenidoLayout);

        // Agregar el nuevo LinearLayout al layout principal
        layoutTextView.addView(nuevoLinearLayout);

        // Establecer un OnClickListener para el botón
        nuevoBoton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EditActivity.this, ultimoElemento.getText() , Toast.LENGTH_SHORT).show();
                //SPEECH, velocidad y tono del dialogo
                SpeakOption speakOption = new SpeakOption();
                speakOption.setSpeed(60);
                speakOption.setIntonation(50);

                speechManager.startSpeak(ultimoElemento.getText(), speakOption);
            }
        });

        eliminarBoton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataList.remove(ultimoElemento);
                layoutTextView.removeView(nuevoLinearLayout);
            }
        });
    }

    public void speakOperation(String texto, String tipo){

        SpeakOption speakOption = new SpeakOption();
        if( tipo == "Normal"){
            speakOption.setSpeed(60);
            speakOption.setIntonation(50);
        }
        speechManager.startSpeak(texto, speakOption);

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
