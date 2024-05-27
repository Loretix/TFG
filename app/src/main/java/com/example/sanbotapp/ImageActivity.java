package com.example.sanbotapp;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.ActionBar;

import com.bumptech.glide.Glide;
import com.qihancloud.opensdk.base.TopBaseActivity;
import com.qihancloud.opensdk.beans.FuncConstant;
import com.qihancloud.opensdk.function.beans.SpeakOption;
import com.qihancloud.opensdk.function.unit.HandMotionManager;
import com.qihancloud.opensdk.function.unit.HardWareManager;
import com.qihancloud.opensdk.function.unit.HeadMotionManager;
import com.qihancloud.opensdk.function.unit.SpeechManager;
import com.qihancloud.opensdk.function.unit.SystemManager;
import com.qihancloud.opensdk.function.unit.WheelMotionManager;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;


public class ImageActivity extends TopBaseActivity {

    private Semaphore imageUpdateSemaphore = new Semaphore(1);

    private ImageView imageView;
    private FuncionalidadesActivity funcionalidadesActivity;
    private ArrayList<DataModel> dataList;
    private int currentIndex = 0;

    private SpeechManager speechManager;
    private SystemManager systemManager;
    private HandMotionManager handMotionManager;
    private HeadMotionManager headMotionManager;
    private HardWareManager hardWareManager; //leds //touch sensors //voice locate //gyroscope
    private WheelMotionManager wheelMotionManager;

    private Button btnComenzar, btnFinalizar, btnPausar;
    private TextView txtNuevo;
    private ImageView gifImagen;
    private TextView txtBienvenida;
    private ImageView imagenSaanbot;
    private LinearLayout linearLayout;

    private Boolean reproduciendose = true;



    @Override
    protected void onMainServiceConnected() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Mantener la pantalla encendida
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Ocultar la barra de navegación
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        super.onCreate(savedInstanceState);
        onMainServiceConnected();
        setContentView(R.layout.imagen);

        speechManager = (SpeechManager) getUnitManager(FuncConstant.SPEECH_MANAGER);
        systemManager = (SystemManager) getUnitManager(FuncConstant.SYSTEM_MANAGER);
        handMotionManager = (HandMotionManager) getUnitManager(FuncConstant.HANDMOTION_MANAGER);
        headMotionManager = (HeadMotionManager) getUnitManager(FuncConstant.HEADMOTION_MANAGER);
        hardWareManager = (HardWareManager) getUnitManager(FuncConstant.HARDWARE_MANAGER);
        wheelMotionManager = (WheelMotionManager) getUnitManager(FuncConstant.WHEELMOTION_MANAGER);

        funcionalidadesActivity = new FuncionalidadesActivity(speechManager, systemManager, handMotionManager,
                headMotionManager, hardWareManager, wheelMotionManager, ImageActivity.this);

        imageView = findViewById(R.id.imagenView);
        btnComenzar = findViewById(R.id.btnComenzar);
        txtNuevo = findViewById(R.id.txtNuevo);
        gifImagen = findViewById(R.id.gifImagen);
        txtBienvenida = findViewById(R.id.txtBienvenida);
        imagenSaanbot = findViewById(R.id.imagenSaanbot);
        linearLayout = findViewById(R.id.linearLayout_botones);
        btnFinalizar = findViewById(R.id.button_finalizar);
        btnPausar = findViewById(R.id.button_pausar);


        // Recibir el intent con la URL de la imagen si se proporciona
        Intent intent = getIntent();
        if (intent != null ) {
            dataList = (ArrayList<DataModel>) getIntent().getSerializableExtra("dataList");
        }

        btnComenzar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtBienvenida.setVisibility(View.GONE);
                btnComenzar.setVisibility(View.GONE);
                imagenSaanbot.setVisibility(View.GONE);

                txtNuevo.setVisibility(View.VISIBLE);
                gifImagen.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.VISIBLE);

                Glide.with(ImageActivity.this)
                        .asGif()
                        .load(R.drawable.robot_reproduccion)  // Reemplaza con tu archivo GIF
                        .into(gifImagen);

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        reproducirAcciones();
                    }
                }, 1000);
            }
        });

        btnFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnPausar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cambiar texto del botón
                if (btnPausar.getText().equals("Pausar")) {
                    btnPausar.setText("Reanudar");
                    reproduciendose = false;
                } else {
                    btnPausar.setText("Pausar");
                    reproduciendose = true;
                    continueActions();
                }
            }
        });

    }

    public void updateImage(String imageUri) {
        try {
            System.out.println("INTENTO ADQUIRIR EL SEMAFORO " + imageUpdateSemaphore);
            // Intentar adquirir el semáforo
            imageUpdateSemaphore.acquire();

            System.out.println("TENGO EL SEMAFORO " + imageUri);
            String[] partes = imageUri.split("-");
            String tiempo = partes[0];
            String uri = partes[1];

            runOnUiThread(() -> {
                txtNuevo.setVisibility(View.GONE);
                gifImagen.setVisibility(View.GONE);

                if (uri.startsWith("http")) {
                    Glide.with(this).load(uri).into(imageView);
                } else {
                    imageView.setImageURI(Uri.parse(uri));
                }
            });

            // Esperar el tiempo especificado antes de liberar el semáforo
            Thread.sleep(Integer.parseInt(tiempo) * 1000L);

            // Quitar imagen de fondo
            runOnUiThread(() -> {
                imageView.setImageDrawable(null);
                txtNuevo.setVisibility(View.VISIBLE);
                gifImagen.setVisibility(View.VISIBLE);
            });



        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // Liberar el semáforo en el bloque finally para asegurarse de que se libere incluso si ocurre una excepción
            imageUpdateSemaphore.release();
        }
    }


    public void reproducirAcciones(){
        // Recorre el dataList y ejecuta las acciones
        for (int i = 0; i < dataList.size() && reproduciendose; i++) {
            currentIndex = i;
            DataModel data = dataList.get(i);
            System.out.println("Opción: " + data.getSpinnerOption());

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
                // Actualizar la imagen en un hilo separado
                int finalI = i;
                new Thread(() -> {
                    updateImage(data.getText());
                    currentIndex = finalI;
                    // Después de actualizar la imagen, continuar con la siguiente acción
                    continueActions();
                }).start();
                // Salir del bucle principal para esperar a que la imagen se actualice
                break;

            } else if (data.getSpinnerOption().equals("Insertar vídeo")) {

            } else if (data.getSpinnerOption().equals("Pregunta verdadero o falso")) {
                funcionalidadesActivity.trueFalseOperation(data.getText());
            } else {
                // No se ha seleccionado ninguna opción
            }
        }
    }

    public void continueActions() {
        // Continuar con las acciones restantes
        for (int i = currentIndex + 1; i < dataList.size() && reproduciendose; i++) {
            currentIndex = i;
            DataModel data = dataList.get(i);
            // Ejecutar la acción correspondiente según la opción
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
                // Actualizar la imagen en un hilo separado
                int finalI = i;
                new Thread(() -> {
                    updateImage(data.getText());
                    currentIndex = finalI;
                    // Después de actualizar la imagen, continuar con la siguiente acción
                    continueActions();
                }).start();
                // Salir del bucle principal para esperar a que la imagen se actualice
                break;

            } else if (data.getSpinnerOption().equals("Insertar vídeo")) {

            } else if (data.getSpinnerOption().equals("Pregunta verdadero o falso")) {
                funcionalidadesActivity.trueFalseOperation(data.getText());
            } else {
                // No se ha seleccionado ninguna opción
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
