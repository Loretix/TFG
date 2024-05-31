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
    private TextView txtPausa;
    private TextView txtFinal;
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
        txtPausa = findViewById(R.id.txtPausa);
        txtFinal = findViewById(R.id.txtFinal);



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
                    txtNuevo.setVisibility(View.GONE);
                    gifImagen.setVisibility(View.GONE);
                    txtPausa.setVisibility(View.VISIBLE);
                    imagenSaanbot.setVisibility(View.VISIBLE);
                    reproduciendose = false;
                } else {
                    btnPausar.setText("Pausar");
                    reproduciendose = true;
                    txtPausa.setVisibility(View.GONE);
                    txtNuevo.setVisibility(View.VISIBLE);
                    gifImagen.setVisibility(View.VISIBLE);
                    imagenSaanbot.setVisibility(View.GONE);
                    continueActions();
                }
            }
        });

    }

    public void reproducirAcciones() {

        DataModel dataadd = new DataModel("Finalizado", "Finalizado", "");
        dataList.add(dataadd);

        // Recorre el dataList y ejecuta las acciones
        for (int i = 0; i < dataList.size() && reproduciendose; i++) {
            currentIndex = i;
            DataModel data = dataList.get(i);
            System.out.println("Opción: " + data.getSpinnerOption());

            // Si data.getImagen() no está vacío, mostrar la imagen de fondo

            if (data.getSpinnerOption().equals("Síntesis de voz")) {
                if(!data.getImagen().isEmpty()){
                    int finalI = i;
                    new Thread(() -> {
                        putImage(data.getImagen(),data.getText(), "voz");
                        currentIndex = finalI;
                        // Después de actualizar la imagen, continuar con la siguiente acción
                        continueActions();
                    }).start();
                    // Salir del bucle principal para esperar a que la imagen se actualice
                    break;
                } else {
                    funcionalidadesActivity.speakOperation(data.getText(), "Normal");
                }

            } else if (data.getSpinnerOption().equals("Movimiento de brazos")) {
                if(!data.getImagen().isEmpty()){
                    int finalI = i;
                    new Thread(() -> {
                        putImage(data.getImagen(),data.getText(), "brazos");
                        currentIndex = finalI;
                        // Después de actualizar la imagen, continuar con la siguiente acción
                        continueActions();
                    }).start();
                    // Salir del bucle principal para esperar a que la imagen se actualice
                    break;
                } else {
                    funcionalidadesActivity.moveBrazosOperation(data.getText());
                }

            } else if (data.getSpinnerOption().equals("Movimiento de cabeza")) {
                if(!data.getImagen().isEmpty()){
                    int finalI = i;
                    new Thread(() -> {
                        putImage(data.getImagen(),data.getText(), "cabeza");
                        currentIndex = finalI;
                        // Después de actualizar la imagen, continuar con la siguiente acción
                        continueActions();
                    }).start();
                    // Salir del bucle principal para esperar a que la imagen se actualice
                    break;
                } else {
                    funcionalidadesActivity.moveCabezaOperation(data.getText());
                }

            } else if (data.getSpinnerOption().equals("Movimiento de ruedas")) {
                if(!data.getImagen().isEmpty()){
                    int finalI = i;
                    new Thread(() -> {
                        putImage(data.getImagen(),data.getText(), "ruedas");
                        currentIndex = finalI;
                        // Después de actualizar la imagen, continuar con la siguiente acción
                        continueActions();
                    }).start();
                    // Salir del bucle principal para esperar a que la imagen se actualice
                    break;
                } else {
                    funcionalidadesActivity.moveRuedasOperation(data.getText());
                }

            } else if (data.getSpinnerOption().equals("Encender LEDs")) {
                if(!data.getImagen().isEmpty()){
                    int finalI = i;
                    new Thread(() -> {
                        putImage(data.getImagen(),data.getText(), "leds");
                        currentIndex = finalI;
                        // Después de actualizar la imagen, continuar con la siguiente acción
                        continueActions();
                    }).start();
                    // Salir del bucle principal para esperar a que la imagen se actualice
                    break;
                } else {
                    funcionalidadesActivity.encenderLedsOperation(data.getText());
                }

            } else if (data.getSpinnerOption().equals("Cambio de expresión facial")) {
                if(!data.getImagen().isEmpty()){
                    int finalI = i;
                    new Thread(() -> {
                        putImage(data.getImagen(),data.getText(), "facial");
                        currentIndex = finalI;
                        // Después de actualizar la imagen, continuar con la siguiente acción
                        continueActions();
                    }).start();
                    // Salir del bucle principal para esperar a que la imagen se actualice
                    break;
                } else {
                    funcionalidadesActivity.changeFaceOperation(data.getText());
                }

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
                // Parar presentación para reproducir el video
                reproduciendose = false;
                btnPausar.setText("Reanudar");
                txtNuevo.setVisibility(View.GONE);
                gifImagen.setVisibility(View.GONE);
                txtPausa.setVisibility(View.VISIBLE);
                imagenSaanbot.setVisibility(View.VISIBLE);
                funcionalidadesActivity.mostrarVideo(data.getText());

            } else if (data.getSpinnerOption().equals("Pregunta verdadero o falso")) {
                reproduciendose = false;
                String[] partes = data.getText().split("-");
                String pregunta = partes[0];
                String respuesta = partes[1];

                SpeakOption speakOption = new SpeakOption();
                speakOption.setSpeed(60);
                speakOption.setIntonation(50);
                speechManager.startSpeak(pregunta, speakOption);

                // Crear un intent para iniciar la actividad TrueFalseActivity
                Intent intent = new Intent(this, TrueFalseActivity.class);
                // Pasar la pregunta y la respuesta como extras del intent
                intent.putExtra("pregunta", pregunta);
                intent.putExtra("respuesta", respuesta);

                startActivityForResult(intent, 200);
                return;
            } else {
                // No se ha seleccionado ninguna opción
            }
        }
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
                txtPausa.setVisibility(View.GONE);
                imagenSaanbot.setVisibility(View.GONE);

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

    public void putImage(String imageUri, String texto, String tipo) {
        try {
            System.out.println("INTENTO ADQUIRIR EL SEMAFORO " + imageUpdateSemaphore);
            // Intentar adquirir el semáforo
            imageUpdateSemaphore.acquire();

            runOnUiThread(() -> {
                txtNuevo.setVisibility(View.GONE);
                gifImagen.setVisibility(View.GONE);
                txtPausa.setVisibility(View.GONE);
                imagenSaanbot.setVisibility(View.GONE);

                if (imageUri.startsWith("http")) {
                    Glide.with(this).load(imageUri).into(imageView);
                } else {
                    imageView.setImageURI(Uri.parse(imageUri));
                }
            });

            // Esperar a que se cargue la imagen
            Thread.sleep(1000);

            // Hablar el texto
            if(tipo.equals("voz")){
                funcionalidadesActivity.speakOperation(texto, "Normal");
            } else if (tipo.equals("brazos")){
                funcionalidadesActivity.moveBrazosOperation(texto);
            } else if (tipo.equals("cabeza")){
                funcionalidadesActivity.moveCabezaOperation(texto);
            } else if (tipo.equals("facial")){
                funcionalidadesActivity.changeFaceOperation(texto);
            } else if (tipo.equals("leds")){
                funcionalidadesActivity.encenderLedsOperation(texto);
            } else if (tipo.equals("ruedas")){
                funcionalidadesActivity.moveRuedasOperation(texto);
            }


            // Esperar el tiempo especificado antes de liberar el semáforo
            Thread.sleep(1000);


        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // Liberar el semáforo en el bloque finally para asegurarse de que se libere incluso si ocurre una excepción
            imageUpdateSemaphore.release();
            runOnUiThread(() -> {
                imageView.setImageDrawable(null);
                txtNuevo.setVisibility(View.VISIBLE);
                gifImagen.setVisibility(View.VISIBLE);
            });
        }
    }

    public void continueActions() {
        // Continuar con las acciones restantes


        for (int i = currentIndex + 1; i < dataList.size() && reproduciendose; i++) {
            currentIndex = i;
            DataModel data = dataList.get(i);
            System.out.println("Opción: " + data.getSpinnerOption());
            // Ejecutar la acción correspondiente según la opción
            if (data.getSpinnerOption().equals("Síntesis de voz")) {
                if(!data.getImagen().isEmpty()){
                    int finalI = i;
                    new Thread(() -> {
                        putImage(data.getImagen(),data.getText(), "voz");
                        currentIndex = finalI;
                        // Después de actualizar la imagen, continuar con la siguiente acción
                        continueActions();
                    }).start();
                    // Salir del bucle principal para esperar a que la imagen se actualice
                    break;
                } else {
                    funcionalidadesActivity.speakOperation(data.getText(), "Normal");
                }

            } else if (data.getSpinnerOption().equals("Movimiento de brazos")) {
                if(!data.getImagen().isEmpty()){
                    int finalI = i;
                    new Thread(() -> {
                        putImage(data.getImagen(),data.getText(), "brazos");
                        currentIndex = finalI;
                        // Después de actualizar la imagen, continuar con la siguiente acción
                        continueActions();
                    }).start();
                    // Salir del bucle principal para esperar a que la imagen se actualice
                    break;
                } else {
                    funcionalidadesActivity.moveBrazosOperation(data.getText());
                }

            } else if (data.getSpinnerOption().equals("Movimiento de cabeza")) {
                if(!data.getImagen().isEmpty()){
                    int finalI = i;
                    new Thread(() -> {
                        putImage(data.getImagen(),data.getText(), "cabeza");
                        currentIndex = finalI;
                        // Después de actualizar la imagen, continuar con la siguiente acción
                        continueActions();
                    }).start();
                    // Salir del bucle principal para esperar a que la imagen se actualice
                    break;
                } else {
                    funcionalidadesActivity.moveCabezaOperation(data.getText());
                }

            } else if (data.getSpinnerOption().equals("Movimiento de ruedas")) {
                if(!data.getImagen().isEmpty()){
                    int finalI = i;
                    new Thread(() -> {
                        putImage(data.getImagen(),data.getText(), "ruedas");
                        currentIndex = finalI;
                        // Después de actualizar la imagen, continuar con la siguiente acción
                        continueActions();
                    }).start();
                    // Salir del bucle principal para esperar a que la imagen se actualice
                    break;
                } else {
                    funcionalidadesActivity.moveRuedasOperation(data.getText());
                }

            } else if (data.getSpinnerOption().equals("Encender LEDs")) {
                if(!data.getImagen().isEmpty()){
                    int finalI = i;
                    new Thread(() -> {
                        putImage(data.getImagen(),data.getText(), "leds");
                        currentIndex = finalI;
                        // Después de actualizar la imagen, continuar con la siguiente acción
                        continueActions();
                    }).start();
                    // Salir del bucle principal para esperar a que la imagen se actualice
                    break;
                } else {
                    funcionalidadesActivity.encenderLedsOperation(data.getText());
                }

            } else if (data.getSpinnerOption().equals("Cambio de expresión facial")) {
                if(!data.getImagen().isEmpty()){
                    int finalI = i;
                    new Thread(() -> {
                        putImage(data.getImagen(),data.getText(), "facial");
                        currentIndex = finalI;
                        // Después de actualizar la imagen, continuar con la siguiente acción
                        continueActions();
                    }).start();
                    // Salir del bucle principal para esperar a que la imagen se actualice
                    break;
                } else {
                    funcionalidadesActivity.changeFaceOperation(data.getText());
                }

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
                // Parar presentación para reproducir el video
                reproduciendose = false;
                runOnUiThread(() -> {
                    btnPausar.setText("Reanudar");
                    txtNuevo.setVisibility(View.GONE);
                    gifImagen.setVisibility(View.GONE);
                    txtPausa.setVisibility(View.VISIBLE);
                    imagenSaanbot.setVisibility(View.VISIBLE);
                    funcionalidadesActivity.mostrarVideo(data.getText());
                });

            } else if (data.getSpinnerOption().equals("Pregunta verdadero o falso")) {
                // Esper el semáforo para mostrar la pregunta
                reproduciendose = false;
                String[] partes = data.getText().split("-");
                String pregunta = partes[0];
                String respuesta = partes[1];

                SpeakOption speakOption = new SpeakOption();
                speakOption.setSpeed(60);
                speakOption.setIntonation(50);
                speechManager.startSpeak(pregunta, speakOption);

                // Crear un intent para iniciar la actividad TrueFalseActivity
                Intent intent = new Intent(this, TrueFalseActivity.class);
                // Pasar la pregunta y la respuesta como extras del intent
                intent.putExtra("pregunta", pregunta);
                intent.putExtra("respuesta", respuesta);

                startActivityForResult(intent, 200);
                return;
                
            } else if (data.getSpinnerOption().equals("Finalizado")) {

                // Esperar el semáforo para mostrar el mensaje de finalización
                try {
                    imageUpdateSemaphore.acquire();
                    runOnUiThread(() -> {
                        txtNuevo.setVisibility(View.GONE);
                        gifImagen.setVisibility(View.GONE);
                        txtFinal.setVisibility(View.VISIBLE);
                        imagenSaanbot.setVisibility(View.VISIBLE);
                        btnPausar.setVisibility(View.GONE);
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            } else {
                // No se ha seleccionado ninguna opción
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == RESULT_OK) {
            System.out.println("RESULTADO TRUEEEEEEEEE: " + currentIndex );
            // Continuar con las acciones restantes
            new Handler().postDelayed(() -> {
                reproduciendose = true;
                continueActions();
            }, 1000);
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
