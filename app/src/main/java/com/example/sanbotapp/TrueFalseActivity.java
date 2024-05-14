package com.example.sanbotapp;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;

import com.qihancloud.opensdk.base.TopBaseActivity;
import com.qihancloud.opensdk.beans.FuncConstant;
import com.qihancloud.opensdk.function.beans.SpeakOption;
import com.qihancloud.opensdk.function.unit.SpeechManager;


public class TrueFalseActivity extends TopBaseActivity {

    private TextView preguntaTextView;
    private SpeechManager speechManager;

    @Override
    protected void onMainServiceConnected() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        onMainServiceConnected();
        setContentView(R.layout.activity_truefalse);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar_title_preguntas);

        speechManager = (SpeechManager) getUnitManager(FuncConstant.SPEECH_MANAGER);
        preguntaTextView = findViewById(R.id.textViewPregunta);

        SpeakOption speakOption = new SpeakOption();
        speakOption.setSpeed(60);
        speakOption.setIntonation(50);

        // Recibe la pregunta y la respuesta como extras del intent
        Intent intent = getIntent();
        if (intent != null) {
            String pregunta = intent.getStringExtra("pregunta");
            preguntaTextView.setText(pregunta);
        }

        // Configura el listener para el botón de "Verdadero"
        Button verdaderoButton = findViewById(R.id.buttonVerdadero);
        verdaderoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Comprueba si la respuesta es verdadera
                // Aquí deberías comparar la respuesta con la respuesta correcta
                // Por ejemplo:
                String respuesta = intent.getStringExtra("respuesta");
                if ("True".equalsIgnoreCase(respuesta)) {
                    speechManager.startSpeak("¡Correcto! ¡Enhorabuena!", speakOption);
                } else {
                    speechManager.startSpeak("¡Incorrecto! ¡Inténtalo de nuevo!", speakOption);
                }
            }
        });

        // Configura el listener para el botón de "Falso"
        Button falsoButton = findViewById(R.id.buttonFalso);
        falsoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Comprueba si la respuesta es falsa
                // Aquí deberías comparar la respuesta con la respuesta correcta
                // Por ejemplo:
                String respuesta = intent.getStringExtra("respuesta");
                if ("False".equalsIgnoreCase(respuesta)) {
                    speechManager.startSpeak("¡Correcto! ¡Enhorabuena!", speakOption);
                } else {
                    speechManager.startSpeak("¡Incorrecto! ¡Inténtalo de nuevo!", speakOption);
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
