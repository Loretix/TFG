package com.example.sanbotapp;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.VideoView;

import com.google.gson.Gson;
import com.qihancloud.opensdk.base.TopBaseActivity;
import com.qihancloud.opensdk.beans.FuncConstant;
import com.qihancloud.opensdk.function.beans.EmotionsType;
import com.qihancloud.opensdk.function.beans.FaceRecognizeBean;
import com.qihancloud.opensdk.function.beans.LED;
import com.qihancloud.opensdk.function.beans.SpeakOption;
import com.qihancloud.opensdk.function.beans.handmotion.AbsoluteAngleHandMotion;
import com.qihancloud.opensdk.function.beans.handmotion.NoAngleHandMotion;
import com.qihancloud.opensdk.function.beans.headmotion.AbsoluteAngleHeadMotion;
import com.qihancloud.opensdk.function.beans.headmotion.RelativeAngleHeadMotion;
import com.qihancloud.opensdk.function.beans.speech.Grammar;
import com.qihancloud.opensdk.function.beans.wheelmotion.DistanceWheelMotion;
import com.qihancloud.opensdk.function.beans.wheelmotion.RelativeAngleWheelMotion;
import com.qihancloud.opensdk.function.unit.HandMotionManager;
import com.qihancloud.opensdk.function.unit.HardWareManager;
import com.qihancloud.opensdk.function.unit.HeadMotionManager;
import com.qihancloud.opensdk.function.unit.MediaManager;
import com.qihancloud.opensdk.function.unit.ProjectorManager;
import com.qihancloud.opensdk.function.unit.SpeechManager;
import com.qihancloud.opensdk.function.unit.SystemManager;
import com.qihancloud.opensdk.function.unit.WheelMotionManager;
import com.qihancloud.opensdk.function.unit.interfaces.hardware.PIRListener;
import com.qihancloud.opensdk.function.unit.interfaces.hardware.TouchSensorListener;
import com.qihancloud.opensdk.function.unit.interfaces.media.FaceRecognizeListener;
import com.qihancloud.opensdk.function.unit.interfaces.speech.RecognizeListener;


import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;


public class PresentacionActivity extends TopBaseActivity {

    EmotionsType currentEmotion, emotions[];

    RelativeAngleHeadMotion relativeAngleHeadMotion;
    AbsoluteAngleHeadMotion absoluteAngleHeadMotion;

    private SpeechManager speechManager; //voice, speechRec
    private HeadMotionManager headMotionManager;    //head movements
    private HandMotionManager handMotionManager;    //hands movements
    private SystemManager systemManager; //emotions
    private HardWareManager hardWareManager; //leds //touch sensors //voice locate //gyroscope
    private WheelMotionManager wheelMotionManager;
    private MediaManager mediaManager;
    private ProjectorManager projectorManager;
    private AudioManager audioManager;

    private MainActivity mainActivity;

    private Button btnpresentacion;
    private Button btnreconocimientofacial;

    private Button btnPresentacionAffectiveLab;

    private Button btnDialogoPlanetario;

    private Button btnPruebaEmociones;

    public Boolean reconocimientoFacial = false;

    private volatile long tiempoRestante;
    private volatile int distanciaRestante = 0;
    private final AtomicBoolean movimientoDetectado = new AtomicBoolean(false);

    private boolean pirdetection = false;

    // array con las preguntas que el robot tiene que reconocer, las respuestas que tiene que dar,
    // el tiempo aproximado de espera

    private String[][] pregunta_respuesta =
            {
                    {"hola quien eres tu", "Me llamo Arturito y estoy buscando a Drako, habíamos quedado en vernos en un planeta" +
                            "del sistema solar pero no recuerdo en cual habiamos quedado", "7"},
                    {"mala memoria", "Chicos y chicas, ¿ podéis ayudarme a encontrar el planeta en el que está Drako ?", "5"},
                    {"si", "Genial, muchas gracias", "3"},
                    {"planeta mas cercano al sol", "¿ Sabéis cuál es ?", "3"},
                    {"mercurio", "¿ Y cómo es Mercurio ? ¿Es el planeta más, ?", "3"},
                    {"pequeno", "¿Y es el planeta más, ?", "3"},
                    {"rapido", "muy bien", "3"},
                    {"el planeta en el que has quedado con draco", "No, no me suena, además en Mercurio no hay atmósfera y hace mucho calor", "5"},
                    {"bueno sigamos buscando", "¿ sabéis qué planeta viene después ?", "3"},
                    {"venus", "¿ Venus ? No creo, no me suena, tengo entendido que hace mucho calor", "5"},
                    {"tienes razon", "Entonces, ¿ qué planeta viene luego ?", "3"},
                    {"marte", "¿ y qué color tiene ?", "3"},
                    {"rojo", "Pues tampoco me suena, no recuerdo que hubiéramos hablado nada de color rojo", "5"},
                    {"planetas gaseosos", "¿ gaseosos ? ¿ qué quieres decir ?", "3"},
                    {"globos gigantes", "y ¿ cuál es el primero de los gaseosos ?", "3"},
                    {"jupiter", "¿ y qué característica tiene ?", "3"},
                    {"mas grande", "quizá, podría ser, ¿ hay más planetas gaseosos ?", "3"},
                    {"claro", "niños, ¿ cuál es el siguiente ?", "3"},
                    {"saturno", "¿ Saturno ? Me suena, tiene algo especial pero no recuerdo qué es", "4"},
                    {"los anillos", "Pues sí, recuerdo haber hablado con Drako algo sobre planetas con anillos pero Saturno no me suena, ¿ cuál es el siguiente ?", "7"},
                    {"urano", "Urano, es cierto, pero le pasa lo contrario que a Venus, ¿ que es el planeta más, ?", "5"},
                    {"frio", "No, no creo que Drako esté allí, sólo falta uno, ¿ cuál es ?", "4"},
                    {"neptuno", "Sí, es cierto, es el último, pero está muy lejos, ¿ y allí acaba el sistema solar ?", "5"},
                    {"profes estudiaron otro como planeta", "¿ alguien sabe cuál es ?", "3"},
                    {"pluton", "Ay, sí, es un planeta enano o planetoide", "3"},
                    {"fin conversacion"}
            };



    MediaPlayer mp1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        //register(PresentacionActivity.class);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presentacion);

        speechManager = (SpeechManager) getUnitManager(FuncConstant.SPEECH_MANAGER);
        headMotionManager = (HeadMotionManager) getUnitManager(FuncConstant.HEADMOTION_MANAGER);
        handMotionManager = (HandMotionManager) getUnitManager(FuncConstant.HANDMOTION_MANAGER);
        hardWareManager = (HardWareManager) getUnitManager(FuncConstant.HARDWARE_MANAGER);
        systemManager = (SystemManager) getUnitManager(FuncConstant.SYSTEM_MANAGER);
        wheelMotionManager = (WheelMotionManager) getUnitManager(FuncConstant.WHEELMOTION_MANAGER);
        mediaManager = (MediaManager) getUnitManager(FuncConstant.MEDIA_MANAGER);
        projectorManager = (ProjectorManager) getUnitManager(FuncConstant.PROJECTOR_MANAGER);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        emotions = new EmotionsType[]{EmotionsType.ARROGANCE, EmotionsType.SURPRISE, EmotionsType.WHISTLE, EmotionsType.LAUGHTER, EmotionsType.GOODBYE,
                EmotionsType.SHY, EmotionsType.SWEAT, EmotionsType.SNICKER, EmotionsType.PICKNOSE, EmotionsType.CRY, EmotionsType.ABUSE,
                EmotionsType.ANGRY, EmotionsType.KISS, EmotionsType.SLEEP, EmotionsType.SMILE, EmotionsType.GRIEVANCE, EmotionsType.QUESTION,
                EmotionsType.FAINT, EmotionsType.PRISE, EmotionsType.NORMAL};

        btnpresentacion = findViewById(R.id.btnpresentacion);
        btnpresentacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    presentacionNinos();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        btnreconocimientofacial = findViewById(R.id.btn_opcion1);
        btnreconocimientofacial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reconocimientoFacial = true;
                setReconocimientoFacial();
            }
        });

        // boton para narrar la presentación de AffectiveLab

        btnPresentacionAffectiveLab = findViewById(R.id.btn_opcion2);

        btnPresentacionAffectiveLab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



        // boton para realizar el diálogo preparado del planetario
        btnDialogoPlanetario = findViewById(R.id.btn_opcion3);
        btnDialogoPlanetario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogoPlanetario();
            }
        });

        // boton de prueba de emociones para la demostración
        btnPruebaEmociones = findViewById(R.id.btn_opcion4);
        btnPruebaEmociones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    pruebaEmociones();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        mp1 = MediaPlayer.create(PresentacionActivity.this,R.raw.musica);
        mp1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mp1.release();
            }
        }    );

        mediaManager.setMediaListener(new FaceRecognizeListener() {
            @Override
            public void recognizeResult(List<FaceRecognizeBean> list) {
                StringBuilder sb = new StringBuilder();
                for (FaceRecognizeBean bean : list) {
                    sb.append(new Gson().toJson(bean));
                    sb.append("\n");


                    // Acceder al valor de la propiedad "user"
                    String user = bean.getUser();
                    // Hacer algo con el valor de "user"
                    System.out.println("Usuario reconocido: " + user);

                    if(user != "" && reconocimientoFacial){
                        if(user == "Adri"){
                            speechManager.startSpeak("hola " + user + " ¿cómo estás?");
                        } else {
                            speechManager.startSpeak("Tu no eres Adri, eres " + user + " , sabes donde está Adrián?");
                        }
                    }

                }
                System.out.println("Persona reconocida????：" + sb.toString());


            }
        });

    }

    // Método para avanzar
    public void avanzarConEsperas(int velocidad, int distancia) {
        DistanceWheelMotion distanceWheelMotion = new DistanceWheelMotion(DistanceWheelMotion.ACTION_FORWARD_RUN, velocidad, distancia);
        wheelMotionManager.doDistanceMotion(distanceWheelMotion);

        // Calcular el tiempo de espera necesario
        long tiempoEspera = (long) (5000 * (distancia / 100.0)); // Convertir la distancia a segundos
        tiempoRestante = tiempoEspera;
        distanciaRestante = (int) (tiempoRestante * 100 / 5000);

        // Crear y ejecutar un hilo para el bucle
        Thread bucleThread = new Thread(() -> {
            try {
                long tiempoInicio = System.currentTimeMillis();
                System.out.println("Tiempo de espera: " + tiempoInicio);

                while (distanciaRestante > 0) {
                    if (movimientoDetectado.get()) {
                        System.out.println("Detenido por detección de movimiento");
                        break; // Salir del bucle
                    }

                    System.out.println("Tiempo restante: " + tiempoRestante + " distancia recorrida: " + distanciaRestante + " tiempo de espera: " + tiempoEspera + " tiempo inicio: " + tiempoInicio + " distancia: " + distancia);
                    tiempoRestante = tiempoEspera - (System.currentTimeMillis() - tiempoInicio);
                    distanciaRestante = (int) (tiempoRestante * 100 / 5000);
                    Thread.sleep(100); // Actualizar cada 100 milisegundos
                }

                System.out.println("FINAAL Tiempo restante: " + tiempoRestante + " distancia recorrida: " + distanciaRestante + " pirdetection: " + tiempoEspera + " tiempo inicio: " + tiempoInicio + " distancia: " + distancia);

                /*if (distanciaRestante > 0) {
                    System.out.println("Distancia restante: " + distanciaRestante);
                    speechManager.startSpeak("No puedo avanzar, hay un obstáculo en el camino");

                    avanzar(5, distanciaRestante);
                }*/

            } catch (InterruptedException e) {
                System.out.println("Error al avanzar: " + e.getMessage());
                e.printStackTrace();
            }
        });

        if (movimientoDetectado.get()) {
            bucleThread.interrupt();
            if (distanciaRestante > 0) {
                System.out.println("Distancia restante: " + distanciaRestante);
                speechManager.startSpeak("No puedo avanzar, hay un obstáculo en el camino");
                movimientoDetectado.set(false);
                avanzarConEsperas(5, distanciaRestante);
            }
        } else {
            // Iniciar el hilo del bucle
            bucleThread.start();
        }

        // Configurar el listener del hardware
        hardWareManager.setOnHareWareListener(new PIRListener() {
            @Override
            public void onPIRCheckResult(boolean isCheck, int part) {
                System.out.print((part == 1 ? "delante del cuerpo" : "Detras del cuerpo") + " detectado");

                // Si se detecta movimiento delante del robot
                if (part == 1) {
                    movimientoDetectado.set(true);
                    System.out.println("FINAAL PIR Tiempo restante: " + tiempoRestante + "distancia restante: " + distanciaRestante);

                } else {
                    System.out.println("FINAAL PIR Tiempo restante: " + tiempoRestante + "distancia recorrida: " + distanciaRestante);
                }
            }
        });

    }




    public void reproducirVideo(){
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) / 2, 0);

        // Cambiamos el layout de la actividad por el layout del video
        setContentView(R.layout.video);

        // Obtener la referencia al VideoView en tu layout XML
        VideoView videoView = findViewById(R.id.videoView);

        // Establecer la URI del archivo de video ubicado en res/raw
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.emocionesingles);

        // Establecer la URI del video en el VideoView
        videoView.setVideoURI(videoUri);

        // Comenzar la reproducción del video
        videoView.start();

        // Establecer un Listener para detectar cuando el video haya terminado de reproducirse
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // Puedes realizar alguna acción aquí si lo necesitas
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) , 0);
                //setContentView(R.layout.activity_presentacion);

                /*
                projectorManager.switchProjector(false);
                girarDerecha(5, 180);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) , 0);

                 */


            }
        });
    }

    public void startProyector(){
        projectorManager.switchProjector(true);

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        projectorManager.setMode(ProjectorManager.MODE_WALL);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        projectorManager.setBright(31);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        projectorManager.setTrapezoidV(30);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }


    // Método para avanzar
    public void avanzar(int velocidad, int distancia) {
        DistanceWheelMotion distanceWheelMotion = new DistanceWheelMotion(DistanceWheelMotion.ACTION_FORWARD_RUN, velocidad, distancia);
        wheelMotionManager.doDistanceMotion(distanceWheelMotion);

        long tiempoEspera = (long) (5000 * (distancia / 100.0));
        try {
            Thread.sleep(tiempoEspera);
        } catch (InterruptedException e) {
            System.out.println("Error al avanzar: " + e.getMessage());
            e.printStackTrace();
        }

    }


    // Método para girar a la izquierda
    public void  girarIzquierda(int velocidad, int angulo) {
        RelativeAngleWheelMotion relativeAngleWheelMotion = new RelativeAngleWheelMotion(RelativeAngleWheelMotion.TURN_LEFT, velocidad, angulo);
        wheelMotionManager.doRelativeAngleMotion(relativeAngleWheelMotion);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    // Método para girar a la derecha
    public void girarDerecha(int velocidad, int angulo) {
        RelativeAngleWheelMotion relativeAngleWheelMotion = new RelativeAngleWheelMotion(RelativeAngleWheelMotion.TURN_RIGHT, velocidad, angulo);
        wheelMotionManager.doRelativeAngleMotion(relativeAngleWheelMotion);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Hacer un cuadrado
    public void cuadrado(int tamano) {
        avanzar(5, tamano);
        girarDerecha(5, 90);
        avanzar(5, tamano);
        girarDerecha(5, 90);
        avanzar(5, tamano);
        girarDerecha(5, 90);
        avanzar(5, tamano);
        girarDerecha(5, 90);
    }

    private void startPresentation() {

        //SPEECH, velocidad y tono del dialogo
        SpeakOption speakOption = new SpeakOption();
        speakOption.setSpeed(60);
        speakOption.setIntonation(50);

        // INTRO --------------------------------------------------------------------------------------------------------------------------
        speechManager.startSpeak("Hola, soy SanBot, un robot de integración sensorial y robótica. Me encanta interactuar con las personas y ayudarlas en lo que necesiten. ", speakOption);
        try {
            Thread.sleep(9000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // LEDS --------------------------------------------------------------------------------------------------------------------------
        speechManager.startSpeak(" No solo puedo hablar, ¿ sabes que también puedo cambiar el color de mi cuerpo ?", speakOption);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //flicker led
        hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_FLICKER_RANDOM));

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // HEAD MOVEMENT --------------------------------------------------------------------------------------------------------------------------
        speechManager.startSpeak("También puedo mover mi cabeza, ", speakOption);
        //head movement
        AbsoluteAngleHeadMotion absoluteAngleHeadMotion1 = new AbsoluteAngleHeadMotion(
                AbsoluteAngleHeadMotion.ACTION_HORIZONTAL,130
        );

        headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion1);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //head movement
        absoluteAngleHeadMotion1 = new AbsoluteAngleHeadMotion(
                AbsoluteAngleHeadMotion.ACTION_HORIZONTAL,90
        );
        headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion1);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // HAND MOVEMENT --------------------------------------------------------------------------------------------------------------------------
        speechManager.startSpeak("mis brazos...", speakOption);
        //hand up
        AbsoluteAngleHandMotion absoluteAngleWingMotion3 = new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_LEFT, 5, 70);
        handMotionManager.doAbsoluteAngleMotion(absoluteAngleWingMotion3);
        absoluteAngleWingMotion3 = new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_RIGHT, 5, 70);
        handMotionManager.doAbsoluteAngleMotion(absoluteAngleWingMotion3);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //hands down (reset position)
        handMotionManager.doNoAngleMotion(new NoAngleHandMotion(NoAngleHandMotion.PART_BOTH, 5,NoAngleHandMotion.ACTION_RESET));

        // DANCE  --------------------------------------------------------------------------------------------------------------------------
        speechManager.startSpeak("y por su puesto ¡ Puedo bailar !", speakOption);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // PLAY MUSIC
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) / 2, 0);

        mp1.start();

        systemManager.showEmotion(EmotionsType.SMILE);
        hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_FLICKER_RANDOM_THREE_GROUP));
        AbsoluteAngleHandMotion absoluteAngleWingMotion = new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_BOTH, 5, 0);
        handMotionManager.doAbsoluteAngleMotion(absoluteAngleWingMotion);
        RelativeAngleWheelMotion relativeAngleWheelMotion = new RelativeAngleWheelMotion(
                RelativeAngleWheelMotion.TURN_LEFT, 3,360
        );
        wheelMotionManager.doRelativeAngleMotion(relativeAngleWheelMotion);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //hands down (reset position)
        handMotionManager.doNoAngleMotion(new NoAngleHandMotion(NoAngleHandMotion.PART_BOTH, 5,NoAngleHandMotion.ACTION_RESET));
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mp1.stop();

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) , 0);


        // EMOTIONS --------------------------------------------------------------------------------------------------------------------------
        speechManager.startSpeak("A pesar de ser un robot, también puedo mostrar emociones", speakOption);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // ENAMORADO
        speakOption.setSpeed(70);
        hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_PINK));
        systemManager.showEmotion(EmotionsType.KISS);
        AbsoluteAngleHandMotion absoluteAngleWingMotion1 = new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_BOTH, 5, 70);
        handMotionManager.doAbsoluteAngleMotion(absoluteAngleWingMotion1);
        speechManager.startSpeak("Cuando estoy enamorada no se como ocultarlo", speakOption);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // TRISTE
        speakOption.setSpeed(50);
        hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_BLUE));
        systemManager.showEmotion(EmotionsType.CRY);
        handMotionManager.doNoAngleMotion(new NoAngleHandMotion(NoAngleHandMotion.PART_BOTH, 5,NoAngleHandMotion.ACTION_RESET));
        AbsoluteAngleHeadMotion absoluteAngleHeadMotion = new AbsoluteAngleHeadMotion(AbsoluteAngleHeadMotion.ACTION_VERTICAL, 7);
        headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion);
        speechManager.startSpeak("O cuando estoy triste no puedo evitar llorar ", speakOption);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        speakOption.setSpeed(60);
        hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_CLOSE));
        systemManager.showEmotion(EmotionsType.SMILE);
        AbsoluteAngleHeadMotion absoluteAngleHeadMotion2 = new AbsoluteAngleHeadMotion(AbsoluteAngleHeadMotion.ACTION_VERTICAL, 30);
        headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion2);
        speechManager.startSpeak("Pero hoy estoy muy contenta de poder enseñaros todo lo que se hacer", speakOption);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // NAVEGACION --------------------------------------------------------------------------------------------------------------------------

        girarDerecha(3, 30);
        avanzar(5, 300);
        girarDerecha(3, 180);

        systemManager.showEmotion(EmotionsType.SMILE);
        AbsoluteAngleHandMotion absoluteAngleWingMotionlab = new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_BOTH, 5, 70);
        handMotionManager.doAbsoluteAngleMotion(absoluteAngleWingMotionlab);
        speechManager.startSpeak("El laboratorio 2 0 7 es mi casa, y estoy muy contenta de poder enseñárosla hoy", speakOption);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        handMotionManager.doNoAngleMotion(new NoAngleHandMotion(NoAngleHandMotion.PART_BOTH, 5,NoAngleHandMotion.ACTION_RESET));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        avanzar(5, 300);
        girarDerecha(3, 90);


        // PROYECTOR  --------------------------------------------------------------------------------------------------------------------------
        speechManager.startSpeak(" ¿ Os gustaría ver un vídeo sobre algunas cosas que hacemos aquí en el Affectif lab ?", speakOption);
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        speechManager.startSpeak(" Vamos, os voy a proyectar un vídeo", speakOption);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // NAVEGAR
        avanzar(5, 100);
        // PROYECTOR
        startProyector();
        reproducirVideo();

    }

    public void setReconocimientoFacial(){
        SpeakOption speakOption = new SpeakOption();
        speakOption.setSpeed(60);
        speakOption.setIntonation(50);
        // RECONOCIMIENTO FACIAL  --------------------------------------------------------------------------------------------------------------------------
        speechManager.startSpeak(" ¿ sabeis que también dispongo de un módulo de reconocimiento facial ? ¿ Adrián, puedes acercarte a mi ? ", speakOption);
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        reconocimientoFacial = true;
    }

    // PRESENTACIÓN AFFECTIVE LAB --------------------------------------------------------------------------------------------------------------------------
    public void presentacionAffectiveLab() throws InterruptedException {
        // definimos la velocidad y entonación para la presentación
        SpeakOption speakOption = new SpeakOption();
        speakOption.setSpeed(55);
        speakOption.setIntonation(50);

        // cambio la emoción a sonreír
        changeEmotion(EmotionsType.SMILE);

        controlBrazos(TipoBrazo.DERECHO, 8, 70);
        speechManager.startSpeak("Hola, buenos días, mi nombre es Sanbot y os voy a presentar a nuestro grupo Affectif Lab", speakOption);
        try {
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        controlBasicoBrazos(AccionesBrazos.BAJAR_BRAZO,TipoBrazo.DERECHO);

        speechManager.startSpeak("Nuestro equipo es reconocido como un grupo de referencia por el Gobierno de Aragón", speakOption);
        try {
            Thread.sleep(5500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        speechManager.startSpeak("Además forma parte del I3A", speakOption);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        speechManager.startSpeak("Affectif Lab es un grupo multidisciplinar", speakOption);
        try {
            Thread.sleep(3500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        speechManager.startSpeak("En él hay investigadores procedentes de áreas como la ingeniería, la psicología, la sociología, la educación, y la geriatría", speakOption);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        speechManager.startSpeak("Nuestra área de investigación es la interacción y el desarrollo de interfaces", speakOption);
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        speechManager.startSpeak("Hemos trabajado con interfaces multimodales mediante personajes 3D virtuales", speakOption);
        try {
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        speechManager.startSpeak("También con interfaces tangibles, como nuestras mesas interactivas", speakOption);
        try {
            Thread.sleep(6500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        speechManager.startSpeak("En éstas interfaces los periféricos habituales como el ratón o el teclado" +
                "son sustituidos por objetos de uso común", speakOption);
        try {
            Thread.sleep(9500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        speechManager.startSpeak("Y no olvidemos las interfaces afectivas basadas en el reconocimiento" +
                "del estado emocional del usuario", speakOption);
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        speechManager.startSpeak("Realizándolo por medio de reconocimiento facial o medidas fisiológicas", speakOption);
        try {
            Thread.sleep(7500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        speechManager.startSpeak("En cuanto accesibilidad nuestro objetivo es favorecer la inclusión" +
                "de usuarios con necesidades especiales", speakOption);
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        speechManager.startSpeak("Con ello conseguimos explorar los beneficios terapéuticos, educativos, y sociales de los nuevos" +
                "modos de interacción digital", speakOption);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        speechManager.startSpeak("Actualmente nuestro grupo lidera un proyecto nacional coordinado por la universidad de Zaragoza llamado PLEISAR", speakOption);
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        speechManager.startSpeak("En él, participan investigadores que pertenecen a la Universidad de las Islas Baleares, a la Universidad de Granada," +
                "y a la universidad de La Laguna", speakOption);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        speechManager.startSpeak("En éste proyecto se ha comenzado a trabajar con asistentes por voz como Alexa y con robots sociales como...", speakOption);
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // el robot "se ve" en pantalla
        speechManager.startSpeak("¡Espera!");
        // cambia su emoción a sorpresa
        changeEmotion(EmotionsType.PRISE);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // avisa al publico
        speechManager.startSpeak("¡Mirad eso!");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // se gira CUANTO EXACTAMENTE (??)
        controlTronco(TipoDireccion.DERECHA, 5, 90);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // levanta el brazo
        controlBrazos(TipoBrazo.DERECHO, 8, 70);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        speechManager.startSpeak("¡Ese soy yo!", speakOption);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // recupera su posición CUANTOS GRADOS EXACTAMENTE (??)
        controlBasicoTronco(AccionesTronco.GIRO_90, TipoDireccion.IZQUIERDA);
        controlBasicoBrazos(AccionesBrazos.BAJAR_BRAZO, TipoBrazo.DERECHO);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // cambia su emoción
        changeEmotion(EmotionsType.SNICKER);
        speechManager.startSpeak("Ah, y tambien tengo a uno de mis primos en Teruel, trabajando con los Amantes", speakOption);
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        speechManager.startSpeak("Y bueno, eso es todo", speakOption);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // cambia su emoción
        changeEmotion(EmotionsType.SLEEP);
        // baja la cabeza
        controlBasicoCabeza(AccionesCabeza.ABAJO);
        // pone los brazos atrás
        controlBrazos(TipoBrazo.AMBOS, 7, 230);
        // se despide del publico
        speechManager.startSpeak("Muchas gracias por vuestra atención", speakOption);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // recupera su posición
        controlBasicoCabeza(AccionesCabeza.CENTRO);
        controlBasicoBrazos(AccionesBrazos.BAJAR_BRAZO, TipoBrazo.AMBOS);
        changeEmotion(EmotionsType.SMILE);
    }

    // DIALOGO ----------------------------------------------------------------------------------------------------------------------------------
    public boolean reconocerRespuesta(SpeakOption so){
        speechManager.setOnSpeechListener(new RecognizeListener() {
            // variable (no utilizada)
            boolean preguntaEncontrada = false;
            @Override
            public boolean onRecognizeResult(Grammar grammar) {

                // paso la gramática reconocida a String
                String cadenaReconocida = grammar.getText();
                // paso la cadena a minúsculas
                cadenaReconocida = cadenaReconocida.toLowerCase();
                // NO NECESARIO. ME CAMBIA Ñ POR N
                cadenaReconocida = Normalizer.normalize(cadenaReconocida, Normalizer.Form.NFD);
                // elimino las tildes de la cadena reconocida para poder compararlo cómodamente con cadenas concretas
                cadenaReconocida = cadenaReconocida.replaceAll("[^\\p{ASCII}]", "");
                // saco por pantalla la cadena reconocida
                System.out.println("La cadena reconocida es " + cadenaReconocida);

                // recorro el vector para ver si la pregunta está dentro de la conversación
                for(int i=0; i<pregunta_respuesta.length; i++){
                    preguntaEncontrada = false;
                    // expresión regular para ver si la cadena a comparar está dentro de la cadena reconocida
                    String regex = "\\b" + pregunta_respuesta[i][0] + "\\b";
                    Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
                    if(pattern.matcher(cadenaReconocida).find()){
                        preguntaEncontrada = true;
                        // si la cadena reconocida es "fin conversación" el robot para
                        if(cadenaReconocida.equals("fin conversacion")){
                            speechManager.doSleep();
                        }
                        // en caso contrario responde la respuesta correspondiente y el tiempo de espera
                        // correspondiente
                        else{
                            System.out.println("Encontré la pregunta");
                            //if(!pregunta_respuesta[i][1].equals("")){
                            speechManager.startSpeak(pregunta_respuesta[i][1], so);
                            System.out.println("He respondido a la pregunta");
                            try {
                                Thread.sleep(Integer.parseInt(pregunta_respuesta[i][2])*1000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            // se vuelve a despertar para continuar la conversación
                            speechManager.doWakeUp();
                        }
                    }
                }
                return true;
            }

            @Override
            public void onRecognizeVolume(int i) {
            }

        });
        return true;
    }

    public void dialogoPlanetario() {
        // definimos la velocidad y entonación de la conversación
        SpeakOption speakOption = new SpeakOption();
        speakOption.setSpeed(60);
        speakOption.setIntonation(50);
        // el robot dice el dialogo inciial
        speechManager.startSpeak("Hola, Estoy buscando a Drako, ¿ Le habéis visto ? ", speakOption);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // empieza el reconocimiento de voz
        speechManager.doWakeUp();
        reconocerRespuesta(speakOption);
    }

    // REACCIONES ----
    public void saludo() {
        // cambiar emoción a contento
        changeEmotion(EmotionsType.LAUGHTER);
        // saludar con el brazo derecho
        speechManager.startSpeak("Hola, soy Sanbot");
        controlBrazos(TipoBrazo.DERECHO, 8, 70);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        controlBasicoBrazos(AccionesBrazos.BAJAR_BRAZO, TipoBrazo.DERECHO);
    }

    public void hacerOla() throws InterruptedException {
        // bajar los brazos en caso de que no los tenga ya bajados
        controlBasicoBrazos(AccionesBrazos.BAJAR_BRAZO, TipoBrazo.AMBOS);
        // cambiar emoción a feliz
        changeEmotion(EmotionsType.LAUGHTER);
        // respuesta para notificar que va a hacer la ola
        speechManager.startSpeak("Hagamos una ola");
        Thread.sleep(2000);
        narrarCuentaAtras(5);
        Thread.sleep(2000);
        // subir los brazos para simular la ola
        controlBasicoBrazos(AccionesBrazos.LEVANTAR_BRAZO, TipoBrazo.AMBOS);
        hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_FLICKER_RANDOM));
        Thread.sleep(2000);
        // bajar los brazos para simular la ola
        controlBasicoBrazos(AccionesBrazos.BAJAR_BRAZO, TipoBrazo.AMBOS);
    }

    public void reaccionChocarCinco() throws InterruptedException {
        // cambiar emoción a sorprendido
        changeEmotion(EmotionsType.PRISE);
        // respuesta para notificar que choque los cinco
        speechManager.startSpeak("Genial. Choca esos cinco");
        // sube uno de los brazos
        controlBrazos(TipoBrazo.DERECHO, 10, 70);
        hardWareManager.setOnHareWareListener(new TouchSensorListener() {
            @Override
            public void onTouch(int part) {
                if (part == 10) {
                    controlBasicoBrazos(AccionesBrazos.BAJAR_BRAZO, TipoBrazo.DERECHO);
                }
            }
        });
    }

    public void reaccionTriste(){
        // cambiar emoción a triste
        changeEmotion(EmotionsType.GOODBYE);
        // bajar los brazos para simular tristeza
        controlBasicoBrazos(AccionesBrazos.BAJAR_BRAZO, TipoBrazo.AMBOS);
    }

    public void reaccionAlegre() throws InterruptedException {
        controlBasicoBrazos(AccionesBrazos.LEVANTAR_BRAZO, TipoBrazo.AMBOS);
        // cambiar cara
        changeEmotion(EmotionsType.PRISE);
        // poner luces
        hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_FLICKER_RANDOM));
        // dar vueltas
        controlBasicoTronco(AccionesTronco.GIRO_360, TipoDireccion.IZQUIERDA);
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        controlBasicoBrazos(AccionesBrazos.BAJAR_BRAZO, TipoBrazo.AMBOS);
    }
    public void reaccionRespuestaCorrecta() {
        // cambiar emoción a feliz
        changeEmotion(EmotionsType.SMILE);
        hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_GREEN));
        // respuesta para notificar que la respuesta es correcta
        speechManager.startSpeak("Muy bien, respuesta correcta");
        // subir los brazos para simular alegría
        controlBasicoBrazos(AccionesBrazos.LEVANTAR_BRAZO, TipoBrazo.AMBOS);
    }

    public void reaccionRespuestaIncorrecta() {
        // cambiar emoción a triste
        changeEmotion(EmotionsType.GOODBYE);
        hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_BLUE));
        // respuesta para notificar que la respuesta es incorrecta
        speechManager.startSpeak("La respuesta es incorrecta");
        // bajar los brazos para simular tristeza
        controlBasicoBrazos(AccionesBrazos.BAJAR_BRAZO, TipoBrazo.AMBOS);
    }

    public void pruebaEmociones() throws InterruptedException {
        SpeakOption speakOption = new SpeakOption();
        speakOption.setSpeed(60);
        speakOption.setIntonation(50);

        speechManager.startSpeak("Cuando quiero saludar hago así", speakOption);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        saludo();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        speechManager.startSpeak("Cuando quiero felicitar a los niños hago así", speakOption);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        reaccionChocarCinco();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        speechManager.startSpeak("Cuando aciertan la pregunta hago así", speakOption);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        reaccionRespuestaCorrecta();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        speechManager.startSpeak("Cuando no aciertan la pregunta hago así", speakOption);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        reaccionRespuestaIncorrecta();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        speechManager.startSpeak("Cuando estoy muy contento hago así", speakOption);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        reaccionAlegre();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        speechManager.startSpeak("Puedo seleccionar un equipo aleatorio para responder mi pregunta", speakOption);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String[] equipos = new String[] {"AZUL", "AMARILLO", "ROJO", "VERDE"};
        elegirEquipo(Arrays.asList(equipos));
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        speechManager.startSpeak("Tambiém puedo seleccionar un alumno aleatorio para responder mi pregunta", speakOption);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String[] nombres = new String[] {"MARÍA", "FELIPE", "CARLOS", "ANDREA", "DAVID"};
        elegirAlumno(Arrays.asList(nombres));
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        speechManager.startSpeak("Cuando quiero participar con los niños hago así", speakOption);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        hacerOla();
    }

    // EXTRA ----
    // Robot narra una cuenta atrás del tiempo que se pase como parametro.
    // IDEA: Utilizarlo como cronómetro para acciones como tiempos de respuesta a preguntas, juegos, etc.
    public void narrarCuentaAtras(int segundos) throws InterruptedException {
        while (segundos > 0) {
            speechManager.startSpeak(String.valueOf(segundos));
            Thread.sleep(1000);
            segundos--;
        }
    }

    public enum AccionesBrazos {
        LEVANTAR_BRAZO,
        BAJAR_BRAZO,
    }

    public enum AccionesTronco {
        GIRO_90,
        GIRO_180,
        GIRO_360;
    }

    public enum AccionesCabeza {
        DERECHA,
        IZQUIERDA,
        ARRIBA,
        ABAJO,
        CENTRO;
    }

    public enum TipoBrazo {
        DERECHO,
        IZQUIERDO,
        AMBOS;
    }

    public enum TipoDireccion {
        DERECHA,
        IZQUIERDA;
    }

    public void elegirEquipo(List<String> listaEquipos) throws InterruptedException {
        speechManager.startSpeak("Le toca responder al equipo");
        Random rand = new Random();
        String randomElement = listaEquipos.get(rand.nextInt(listaEquipos.size()));
        Thread.sleep(2000);
        speechManager.startSpeak(randomElement);
    }

    public void elegirAlumno(List<String> listaNombres) throws InterruptedException {
        speechManager.startSpeak("Le toca responder a");
        Random rand = new Random();
        String randomElement = listaNombres.get(rand.nextInt(listaNombres.size()));
        Thread.sleep(2000);
        speechManager.startSpeak(randomElement);
    }

    public boolean controlBasicoBrazos(AccionesBrazos accion, TipoBrazo brazo) {
        byte[] absolutePart = new byte[]{AbsoluteAngleHandMotion.PART_LEFT, AbsoluteAngleHandMotion.PART_RIGHT, AbsoluteAngleHandMotion.PART_BOTH};
        AbsoluteAngleHandMotion absoluteAngleHandMotion = new AbsoluteAngleHandMotion(absolutePart[0], 10, 0);
        switch(accion) {
            case LEVANTAR_BRAZO:
                switch (brazo) {
                    case IZQUIERDO:
                        absoluteAngleHandMotion = new AbsoluteAngleHandMotion(absolutePart[0], 10, 10);
                        handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
                        break;
                    case DERECHO:
                        absoluteAngleHandMotion = new AbsoluteAngleHandMotion(absolutePart[1], 10, 10);
                        handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
                        break;
                    case AMBOS:
                        absoluteAngleHandMotion = new AbsoluteAngleHandMotion(absolutePart[2], 10, 10);
                        handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
                        break;
                }
                break;
            case BAJAR_BRAZO:
                switch (brazo) {
                    case IZQUIERDO:
                        absoluteAngleHandMotion = new AbsoluteAngleHandMotion(absolutePart[0], 10, 170);
                        handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
                        break;
                    case DERECHO:
                        absoluteAngleHandMotion = new AbsoluteAngleHandMotion(absolutePart[1], 10, 170);
                        handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
                        break;
                    case AMBOS:
                        absoluteAngleHandMotion = new AbsoluteAngleHandMotion(absolutePart[2], 10, 170);
                        handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
                        break;
                }
                break;
        }
        return true;
    }

    public boolean controlBrazos(TipoBrazo brazo, int velocidad, int angulo) {
        byte[] absolutePart = new byte[]{AbsoluteAngleHandMotion.PART_LEFT, AbsoluteAngleHandMotion.PART_RIGHT, AbsoluteAngleHandMotion.PART_BOTH};
        if (brazo.equals(TipoBrazo.DERECHO)) {
            AbsoluteAngleHandMotion absoluteAngleHandMotion = new AbsoluteAngleHandMotion(absolutePart[1], velocidad, angulo);
            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
        } else if (brazo.equals(TipoBrazo.IZQUIERDO)) {
            AbsoluteAngleHandMotion absoluteAngleHandMotion = new AbsoluteAngleHandMotion(absolutePart[0], velocidad, angulo);
            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
        } else if (brazo.equals(TipoBrazo.AMBOS)) {
            AbsoluteAngleHandMotion absoluteAngleHandMotion = new AbsoluteAngleHandMotion(absolutePart[2], velocidad, angulo);
            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
        }
        return true;
    }

    public boolean controlBasicoTronco(AccionesTronco accion, TipoDireccion direccion){
        RelativeAngleWheelMotion movimientoRuedas = new RelativeAngleWheelMotion(RelativeAngleWheelMotion.TURN_LEFT, 5, 360);
        switch (accion){
            case GIRO_90:
                switch (direccion){
                    case DERECHA:
                        movimientoRuedas = new RelativeAngleWheelMotion(RelativeAngleWheelMotion.TURN_RIGHT, 5, 90);
                        wheelMotionManager.doRelativeAngleMotion(movimientoRuedas);
                        break;
                    case IZQUIERDA:
                        movimientoRuedas = new RelativeAngleWheelMotion(RelativeAngleWheelMotion.TURN_LEFT, 5, 90);
                        wheelMotionManager.doRelativeAngleMotion(movimientoRuedas);
                        break;
                }
                break;
            case GIRO_180:
                switch (direccion){
                    case DERECHA:
                        movimientoRuedas = new RelativeAngleWheelMotion(RelativeAngleWheelMotion.TURN_RIGHT, 5, 180);
                        wheelMotionManager.doRelativeAngleMotion(movimientoRuedas);
                        break;
                    case IZQUIERDA:
                        movimientoRuedas = new RelativeAngleWheelMotion(RelativeAngleWheelMotion.TURN_LEFT, 5, 180);
                        wheelMotionManager.doRelativeAngleMotion(movimientoRuedas);
                        break;
                }
                break;
            case GIRO_360:
                switch (direccion){
                    case DERECHA:
                        movimientoRuedas = new RelativeAngleWheelMotion(RelativeAngleWheelMotion.TURN_RIGHT, 5, 360);
                        wheelMotionManager.doRelativeAngleMotion(movimientoRuedas);
                        break;
                    case IZQUIERDA:
                        movimientoRuedas = new RelativeAngleWheelMotion(RelativeAngleWheelMotion.TURN_LEFT, 5, 360);
                        wheelMotionManager.doRelativeAngleMotion(movimientoRuedas);
                        break;
                }
                break;
        }
        return true;
    }

    public boolean controlTronco(TipoDireccion direccion, int velocidad, int angulo) {
        RelativeAngleWheelMotion movimientoRuedas = new RelativeAngleWheelMotion(RelativeAngleWheelMotion.TURN_LEFT, 5, 360);
        if (direccion.equals(TipoDireccion.DERECHA)) {
            movimientoRuedas = new RelativeAngleWheelMotion(RelativeAngleWheelMotion.TURN_RIGHT, velocidad, angulo);
            wheelMotionManager.doRelativeAngleMotion(movimientoRuedas);
        } else{
            movimientoRuedas = new RelativeAngleWheelMotion(RelativeAngleWheelMotion.TURN_LEFT, velocidad, angulo);
            wheelMotionManager.doRelativeAngleMotion(movimientoRuedas);
        }
        return true;
    }

    public boolean controlBasicoCabeza(AccionesCabeza accion){
        switch (accion) {
            case IZQUIERDA:
                relativeAngleHeadMotion = new RelativeAngleHeadMotion(RelativeAngleHeadMotion.ACTION_LEFT, 180);
                headMotionManager.doRelativeAngleMotion(relativeAngleHeadMotion);
                break;
            case DERECHA:
                relativeAngleHeadMotion = new RelativeAngleHeadMotion(RelativeAngleHeadMotion.ACTION_RIGHT, 180);
                headMotionManager.doRelativeAngleMotion(relativeAngleHeadMotion);
                break;
            case ARRIBA:
                relativeAngleHeadMotion = new RelativeAngleHeadMotion(RelativeAngleHeadMotion.ACTION_UP, 30);
                headMotionManager.doRelativeAngleMotion(relativeAngleHeadMotion);
                break;
            case ABAJO:
                relativeAngleHeadMotion = new RelativeAngleHeadMotion(RelativeAngleHeadMotion.ACTION_DOWN, 30);
                headMotionManager.doRelativeAngleMotion(relativeAngleHeadMotion);
                break;
            case CENTRO:
                absoluteAngleHeadMotion = new AbsoluteAngleHeadMotion(AbsoluteAngleHeadMotion.ACTION_HORIZONTAL,90);
                headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion);
                break;
        }
        return true;
    }

    // funcion que muestra la emoción pasada por parametro
    public void changeEmotion(EmotionsType emotion) {
        currentEmotion = emotion;
        systemManager.showEmotion(currentEmotion);
    }

    public void presentacionNinos() throws InterruptedException {
        //SPEECH, velocidad y tono del dialogo
        SpeakOption speakOption = new SpeakOption();
        speakOption.setSpeed(60);
        speakOption.setIntonation(50);

        // INTRO --------------------------------------------------------------------------------------------------------------------------
        speechManager.startSpeak("Hola, soy SanBot, un robot de integración sensorial y robótica. Me encanta interactuar con las personas y ayudarlas en lo que necesiten. ", speakOption);
        try {
            Thread.sleep(9000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // LEDS --------------------------------------------------------------------------------------------------------------------------
        speechManager.startSpeak(" No solo puedo hablar, ¿ sabes que también puedo cambiar el color de mi cuerpo ?", speakOption);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //flicker led
        hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_FLICKER_RANDOM));

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // HEAD MOVEMENT --------------------------------------------------------------------------------------------------------------------------
        speechManager.startSpeak("También puedo mover mi cabeza, ", speakOption);
        //head movement
        AbsoluteAngleHeadMotion absoluteAngleHeadMotion1 = new AbsoluteAngleHeadMotion(
                AbsoluteAngleHeadMotion.ACTION_HORIZONTAL,130
        );

        headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion1);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //head movement
        absoluteAngleHeadMotion1 = new AbsoluteAngleHeadMotion(
                AbsoluteAngleHeadMotion.ACTION_HORIZONTAL,90
        );
        headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion1);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // HAND MOVEMENT --------------------------------------------------------------------------------------------------------------------------
        speechManager.startSpeak("mis brazos...", speakOption);
        //hand up
        AbsoluteAngleHandMotion absoluteAngleWingMotion3 = new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_LEFT, 5, 70);
        handMotionManager.doAbsoluteAngleMotion(absoluteAngleWingMotion3);
        absoluteAngleWingMotion3 = new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_RIGHT, 5, 70);
        handMotionManager.doAbsoluteAngleMotion(absoluteAngleWingMotion3);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //hands down (reset position)
        handMotionManager.doNoAngleMotion(new NoAngleHandMotion(NoAngleHandMotion.PART_BOTH, 5,NoAngleHandMotion.ACTION_RESET));

        // DANCE  --------------------------------------------------------------------------------------------------------------------------
        speechManager.startSpeak("y por su puesto ¡ Puedo bailar !", speakOption);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // PLAY MUSIC
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) / 2, 0);

        mp1.start();

        systemManager.showEmotion(EmotionsType.SMILE);
        hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_FLICKER_RANDOM_THREE_GROUP));
        AbsoluteAngleHandMotion absoluteAngleWingMotion = new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_BOTH, 5, 0);
        handMotionManager.doAbsoluteAngleMotion(absoluteAngleWingMotion);
        RelativeAngleWheelMotion relativeAngleWheelMotion = new RelativeAngleWheelMotion(
                RelativeAngleWheelMotion.TURN_LEFT, 3,360
        );
        wheelMotionManager.doRelativeAngleMotion(relativeAngleWheelMotion);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //hands down (reset position)
        handMotionManager.doNoAngleMotion(new NoAngleHandMotion(NoAngleHandMotion.PART_BOTH, 5,NoAngleHandMotion.ACTION_RESET));
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mp1.stop();

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) , 0);


        // EMOTIONS --------------------------------------------------------------------------------------------------------------------------
        speechManager.startSpeak("A pesar de ser un robot, también puedo mostrar emociones", speakOption);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // ENAMORADO
        speakOption.setSpeed(70);
        hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_PINK));
        systemManager.showEmotion(EmotionsType.KISS);
        AbsoluteAngleHandMotion absoluteAngleWingMotion1 = new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_BOTH, 5, 70);
        handMotionManager.doAbsoluteAngleMotion(absoluteAngleWingMotion1);
        speechManager.startSpeak("Cuando estoy enamorada no se como ocultarlo", speakOption);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // TRISTE
        speakOption.setSpeed(50);
        hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_BLUE));
        systemManager.showEmotion(EmotionsType.CRY);
        handMotionManager.doNoAngleMotion(new NoAngleHandMotion(NoAngleHandMotion.PART_BOTH, 5,NoAngleHandMotion.ACTION_RESET));
        AbsoluteAngleHeadMotion absoluteAngleHeadMotion = new AbsoluteAngleHeadMotion(AbsoluteAngleHeadMotion.ACTION_VERTICAL, 7);
        headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion);
        speechManager.startSpeak("O cuando estoy triste no puedo evitar llorar ", speakOption);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        speakOption.setSpeed(60);
        hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_CLOSE));
        systemManager.showEmotion(EmotionsType.SMILE);
        AbsoluteAngleHeadMotion absoluteAngleHeadMotion2 = new AbsoluteAngleHeadMotion(AbsoluteAngleHeadMotion.ACTION_VERTICAL, 30);
        headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion2);
        speechManager.startSpeak("Pero hoy estoy muy contenta de poder enseñaros todo lo que se hacer", speakOption);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // NAVEGACION

        avanzar(5, 50);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        controlBasicoTronco(AccionesTronco.GIRO_180, TipoDireccion.IZQUIERDA);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        avanzar(5, 50);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        controlBasicoTronco(AccionesTronco.GIRO_180, TipoDireccion.IZQUIERDA);

        systemManager.showEmotion(EmotionsType.SMILE);
        AbsoluteAngleHandMotion absoluteAngleWingMotionlab = new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_BOTH, 5, 70);
        handMotionManager.doAbsoluteAngleMotion(absoluteAngleWingMotionlab);
        speechManager.startSpeak("El laboratorio 2 0 7 es mi casa, y estoy muy contenta de poder enseñárosla hoy", speakOption);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        handMotionManager.doNoAngleMotion(new NoAngleHandMotion(NoAngleHandMotion.PART_BOTH, 5,NoAngleHandMotion.ACTION_RESET));

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        speechManager.startSpeak("Cuando respondéis bien una pregunta, hago esto", speakOption);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        reaccionChocarCinco();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        speechManager.startSpeak("Cuando estoy muy contenta hago así", speakOption);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        reaccionAlegre();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        speechManager.startSpeak("Cuando quiero participar con los niños hago así", speakOption);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        hacerOla();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        speechManager.startSpeak("Y bueno, eso es todo", speakOption);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // cambia su emoción
        changeEmotion(EmotionsType.SLEEP);
        // baja la cabeza
        controlBasicoCabeza(AccionesCabeza.ABAJO);
        // pone los brazos atrás
        controlBrazos(TipoBrazo.AMBOS, 7, 230);
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // se despide del publico
        speechManager.startSpeak("Muchas gracias por vuestra atención, ahora os dejo con un vídeo" +
                "ya que tuve la oportunidad de trabajar con Emilia y Khea en su último videoclip", speakOption);
        try {
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // recupera su posición
        controlBasicoCabeza(AccionesCabeza.CENTRO);
        controlBasicoBrazos(AccionesBrazos.BAJAR_BRAZO, TipoBrazo.AMBOS);
        changeEmotion(EmotionsType.SMILE);

        // PROYECTOR
        //startProyector();
        reproducirVideo();
    }


    @Override
    protected void onMainServiceConnected() {

    }

}
