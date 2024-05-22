package com.example.sanbotapp;


import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qihancloud.opensdk.base.TopBaseActivity;
import com.qihancloud.opensdk.beans.FuncConstant;
import com.qihancloud.opensdk.function.beans.EmotionsType;
import com.qihancloud.opensdk.function.beans.LED;
import com.qihancloud.opensdk.function.beans.SpeakOption;
import com.qihancloud.opensdk.function.beans.handmotion.AbsoluteAngleHandMotion;
import com.qihancloud.opensdk.function.beans.headmotion.AbsoluteAngleHeadMotion;
import com.qihancloud.opensdk.function.beans.headmotion.LocateAbsoluteAngleHeadMotion;
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

import java.util.ArrayList;
import java.util.Objects;


public class FuncionalidadesActivity extends TopBaseActivity {

    private SpeechManager speechManager;
    private SystemManager systemManager;
    private HandMotionManager handMotionManager;
    private HeadMotionManager headMotionManager;
    private HardWareManager hardWareManager; //leds //touch sensors //voice locate //gyroscope
    private WheelMotionManager wheelMotionManager;
    private Context context;


    @Override
    protected void onMainServiceConnected() {

    }

    public FuncionalidadesActivity(SpeechManager speechManager, SystemManager systemManager, HandMotionManager handMotionManager,
                                   HeadMotionManager headMotionManager, HardWareManager hardWareManager, WheelMotionManager wheelMotionManager, Context context) {
        this.speechManager = speechManager;
        this.systemManager = systemManager;
        this.handMotionManager = handMotionManager;
        this.headMotionManager = headMotionManager;
        this.hardWareManager = hardWareManager;
        this.wheelMotionManager = wheelMotionManager;
        this.context = context;
    }

    // Método que dado una cadena y un tipo de voz, reproduce el texto con la voz seleccionada
    public void speakOperation(String texto, String tipo){

        SpeakOption speakOption = new SpeakOption();
        if( tipo == "Normal"){
            speakOption.setSpeed(60);
            speakOption.setIntonation(50);
        }
        speechManager.startSpeak(texto, speakOption);

        // Calculamos el tiempo que tardará en hablar el texto
        int tiempo = texto.length() * 100;
        // Esperamos el tiempo que tardará en hablar el texto
        try {
            Thread.sleep(tiempo);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Método que dado un tipo Insertar imagen en la pantalla, muestra la imagen seleccionada
    public void mostrarImagen(String tipo){
        String[] partes = tipo.split("-");
        String tiempo = partes[0];
        String uri = partes[1];

        // Creamos un intent para iniciar la actividad ImageActivity
        Intent intent = new Intent(context, ImageActivity.class);
        // Pasamos la uri de la imagen como extra del intent
        intent.putExtra("uri", uri);
        intent.putExtra("tiempo", tiempo);
        // Iniciamos la actividad
        context.startActivity(intent);
    }

    public void trueFalseOperation(String tipo){

        String[] partes = tipo.split("-");
        String pregunta = partes[0];
        String respuesta = partes[1];

        SpeakOption speakOption = new SpeakOption();
        speakOption.setSpeed(60);
        speakOption.setIntonation(50);
        speechManager.startSpeak(pregunta, speakOption);

        // Crear un intent para iniciar la actividad TrueFalseActivity
        Intent intent = new Intent(context, TrueFalseActivity.class);
        // Pasar la pregunta y la respuesta como extras del intent
        intent.putExtra("pregunta", pregunta);
        intent.putExtra("respuesta", respuesta);
        // Iniciar la actividad
        context.startActivity(intent);

    }

    // Método que dado un tipo de emoción, cambia la cara del robot a la emoción seleccionada
    public void changeFaceOperation( String tipo){
        if(Objects.equals(tipo, "Arrogancia")){
            systemManager.showEmotion(EmotionsType.ARROGANCE);
        } else if (Objects.equals(tipo, "Sorpresa")){
            systemManager.showEmotion(EmotionsType.SURPRISE);
        } else if (Objects.equals(tipo, "Ojos brillantes")){
            systemManager.showEmotion(EmotionsType.WHISTLE);
        } else if (Objects.equals(tipo, "Enamorado")){
            systemManager.showEmotion(EmotionsType.LAUGHTER);
        } else if (Objects.equals(tipo, "Despedida emotiva")){
            systemManager.showEmotion(EmotionsType.GOODBYE);
        } else if (Objects.equals(tipo, "Tímido")){
            systemManager.showEmotion(EmotionsType.SHY);
        } else if (Objects.equals(tipo, "Normal")){
            systemManager.showEmotion(EmotionsType.NORMAL);
        } else if (Objects.equals(tipo, "Inseguro")){
            systemManager.showEmotion(EmotionsType.SWEAT);
        } else if (Objects.equals(tipo, "Pillo")){
            systemManager.showEmotion(EmotionsType.SNICKER);
        } else if (Objects.equals(tipo, "Culpa")){
            systemManager.showEmotion(EmotionsType.PICKNOSE);
        } else if (Objects.equals(tipo, "Llanto")){
            systemManager.showEmotion(EmotionsType.CRY);
        } else if (Objects.equals(tipo, "Enfado")){
            systemManager.showEmotion(EmotionsType.ABUSE);
        } else if (Objects.equals(tipo, "Super enamorado")){
            systemManager.showEmotion(EmotionsType.KISS);
        } else if (Objects.equals(tipo, "Dormir")){
            systemManager.showEmotion(EmotionsType.SLEEP);
        } else if (Objects.equals(tipo, "Sonrisa")){
            systemManager.showEmotion(EmotionsType.SMILE);
        } else if (Objects.equals(tipo, "Preocupación")){
            systemManager.showEmotion(EmotionsType.GRIEVANCE);
        } else if (Objects.equals(tipo, "Pregunta")){
            systemManager.showEmotion(EmotionsType.QUESTION);
        } else if (Objects.equals(tipo, "Desmayo")){
            systemManager.showEmotion(EmotionsType.FAINT);
        } else if (Objects.equals(tipo, "Elogio")){
            systemManager.showEmotion(EmotionsType.PRISE);
        }
    }

    // Método que dado un tipo de movimiento de brazos, realiza el movimiento seleccionado
    public void moveBrazosOperation(String tipo){
        if(Objects.equals(tipo, "Saludo")){
            AbsoluteAngleHandMotion absoluteAngleHandMotion =
                    new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_RIGHT,5,0);
            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            absoluteAngleHandMotion =
                    new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_RIGHT,5,180);
            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } else if (Objects.equals(tipo, "Señalar")){
            SpeakOption speakOption = new SpeakOption();
            speakOption.setSpeed(60);
            speakOption.setIntonation(50);
            speechManager.startSpeak("Mira", speakOption);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            AbsoluteAngleHandMotion absoluteAngleHandMotion =
                    new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_RIGHT,5,45);
            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);

            try {
                Thread.sleep(7000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            absoluteAngleHandMotion =
                    new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_RIGHT,5,180);
            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } else if (Objects.equals(tipo, "Tengo una duda")){
            SpeakOption speakOption = new SpeakOption();
            speakOption.setSpeed(60);
            speakOption.setIntonation(50);

            AbsoluteAngleHandMotion absoluteAngleHandMotion =
                    new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_RIGHT,5,0);
            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            speechManager.startSpeak("Tengo una duda", speakOption);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            absoluteAngleHandMotion =
                    new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_RIGHT,5,180);
            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } else if (Objects.equals(tipo, "¿Me das un abrazo?")){
            SpeakOption speakOption = new SpeakOption();
            speakOption.setSpeed(60);
            speakOption.setIntonation(50);
            speechManager.startSpeak("¿Me das un abrazo?", speakOption);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            AbsoluteAngleHandMotion absoluteAngleHandMotion =
                    new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_BOTH,5,90);
            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            absoluteAngleHandMotion =
                    new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_BOTH,5,180);
            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } else if (Objects.equals(tipo, "Arriba las manos policía")){
            SpeakOption speakOption = new SpeakOption();
            speakOption.setSpeed(60);
            speakOption.setIntonation(50);
            speechManager.startSpeak("¡Oh no!, ¡La pasma!", speakOption);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            AbsoluteAngleHandMotion absoluteAngleHandMotion =
                    new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_BOTH,5,0);
            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            absoluteAngleHandMotion =
                    new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_BOTH,5,180);
            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } else if (Objects.equals(tipo, "Hagamos una OLA")){
            SpeakOption speakOption = new SpeakOption();
            speakOption.setSpeed(60);
            speakOption.setIntonation(50);
            speechManager.startSpeak("¡Hagamos una OLA!", speakOption);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            speechManager.startSpeak("¡ Tres, dos, uno ... UEE !", speakOption);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            AbsoluteAngleHandMotion absoluteAngleHandMotion =
                    new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_BOTH,8,0);
            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);

            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            absoluteAngleHandMotion =
                    new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_BOTH,5,180);
            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } else if (Objects.equals(tipo, "El limbo")){
            AbsoluteAngleHandMotion absoluteAngleHandMotion =
                    new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_BOTH,5,90);
            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            absoluteAngleHandMotion =
                    new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_BOTH,5,180);
            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } else if (Objects.equals(tipo, "El robot")){
            AbsoluteAngleHandMotion absoluteAngleHandMotion =
                    new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_LEFT,8,90);
            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            absoluteAngleHandMotion =
                    new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_RIGHT,8,45);
            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            absoluteAngleHandMotion =
                    new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_LEFT,8,145);
            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            absoluteAngleHandMotion =
                    new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_RIGHT,8,200);
            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            absoluteAngleHandMotion =
                    new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_LEFT,8,90);
            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            absoluteAngleHandMotion =
                    new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_BOTH,8,180);
            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    // Método que dado un tipo de movimiento de cabeza, realiza el movimiento seleccionado
    public void moveCabezaOperation(String tipo){
        if(Objects.equals(tipo, "Asentir")){
            AbsoluteAngleHeadMotion absoluteAngleHeadMotion =
                    new AbsoluteAngleHeadMotion(AbsoluteAngleHeadMotion.ACTION_VERTICAL,30);
            headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            absoluteAngleHeadMotion =
                    new AbsoluteAngleHeadMotion(AbsoluteAngleHeadMotion.ACTION_VERTICAL,7);
            headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            absoluteAngleHeadMotion =
                    new AbsoluteAngleHeadMotion(AbsoluteAngleHeadMotion.ACTION_VERTICAL,30);
            headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            LocateAbsoluteAngleHeadMotion locateAbsoluteAngleHeadMotion = new LocateAbsoluteAngleHeadMotion(LocateAbsoluteAngleHeadMotion.ACTION_BOTH_LOCK, 90, 15);
            headMotionManager.doAbsoluteLocateMotion(locateAbsoluteAngleHeadMotion);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } else if (Objects.equals(tipo, "Negar")){
            AbsoluteAngleHeadMotion absoluteAngleHeadMotion =
                    new AbsoluteAngleHeadMotion(AbsoluteAngleHeadMotion.ACTION_HORIZONTAL,0);
            headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion);

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            absoluteAngleHeadMotion =
                    new AbsoluteAngleHeadMotion(AbsoluteAngleHeadMotion.ACTION_HORIZONTAL,180);
            headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion);

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            absoluteAngleHeadMotion =
                    new AbsoluteAngleHeadMotion(AbsoluteAngleHeadMotion.ACTION_HORIZONTAL,0);
            headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion);

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            LocateAbsoluteAngleHeadMotion locateAbsoluteAngleHeadMotion = new LocateAbsoluteAngleHeadMotion(LocateAbsoluteAngleHeadMotion.ACTION_BOTH_LOCK, 90, 15);
            headMotionManager.doAbsoluteLocateMotion(locateAbsoluteAngleHeadMotion);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } else if (Objects.equals(tipo, "Mirar a la derecha")){
            AbsoluteAngleHeadMotion absoluteAngleHeadMotion =
                    new AbsoluteAngleHeadMotion(AbsoluteAngleHeadMotion.ACTION_HORIZONTAL,180);
            headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion);

            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            LocateAbsoluteAngleHeadMotion locateAbsoluteAngleHeadMotion = new LocateAbsoluteAngleHeadMotion(LocateAbsoluteAngleHeadMotion.ACTION_BOTH_LOCK, 90, 15);
            headMotionManager.doAbsoluteLocateMotion(locateAbsoluteAngleHeadMotion);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } else if (Objects.equals(tipo, "Mirar a la izquierda")){
            AbsoluteAngleHeadMotion absoluteAngleHeadMotion =
                    new AbsoluteAngleHeadMotion(AbsoluteAngleHeadMotion.ACTION_HORIZONTAL,0);
            headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion);

            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            LocateAbsoluteAngleHeadMotion locateAbsoluteAngleHeadMotion = new LocateAbsoluteAngleHeadMotion(LocateAbsoluteAngleHeadMotion.ACTION_BOTH_LOCK, 90, 15);
            headMotionManager.doAbsoluteLocateMotion(locateAbsoluteAngleHeadMotion);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } else if (Objects.equals(tipo, "Mirar arriba")){
            AbsoluteAngleHeadMotion absoluteAngleHeadMotion =
                    new AbsoluteAngleHeadMotion(AbsoluteAngleHeadMotion.ACTION_VERTICAL,30);
            headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion);

            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            LocateAbsoluteAngleHeadMotion locateAbsoluteAngleHeadMotion = new LocateAbsoluteAngleHeadMotion(LocateAbsoluteAngleHeadMotion.ACTION_BOTH_LOCK, 90, 15);
            headMotionManager.doAbsoluteLocateMotion(locateAbsoluteAngleHeadMotion);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } else if (Objects.equals(tipo, "Mirar abajo")){
            AbsoluteAngleHeadMotion absoluteAngleHeadMotion =
                    new AbsoluteAngleHeadMotion(AbsoluteAngleHeadMotion.ACTION_VERTICAL,7);
            headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion);

            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            LocateAbsoluteAngleHeadMotion locateAbsoluteAngleHeadMotion = new LocateAbsoluteAngleHeadMotion(LocateAbsoluteAngleHeadMotion.ACTION_BOTH_LOCK, 90, 15);
            headMotionManager.doAbsoluteLocateMotion(locateAbsoluteAngleHeadMotion);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } else if (Objects.equals(tipo, "Lo siento")){

            SpeakOption speakOption = new SpeakOption();
            speakOption.setSpeed(60);
            speakOption.setIntonation(50);
            speechManager.startSpeak("Lo siento", speakOption);

            AbsoluteAngleHeadMotion absoluteAngleHeadMotion =
                    new AbsoluteAngleHeadMotion(AbsoluteAngleHeadMotion.ACTION_VERTICAL,7);
            headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion);

            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            LocateAbsoluteAngleHeadMotion locateAbsoluteAngleHeadMotion = new LocateAbsoluteAngleHeadMotion(LocateAbsoluteAngleHeadMotion.ACTION_BOTH_LOCK, 90, 15);
            headMotionManager.doAbsoluteLocateMotion(locateAbsoluteAngleHeadMotion);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Método que dado un tipo de movimiento de ruedas, realiza el movimiento seleccionado
    public void moveRuedasOperation(String tipo){
        String[] partes = tipo.split("-");
        String movimiento = partes[0];
        int distancia = Integer.parseInt(partes[1]);

        if(Objects.equals(movimiento, "Avanzar")){

            System.out.println("Movimiento de ruedas" +"Avanzar " + distancia + " cm");
            DistanceWheelMotion distanceWheelMotion = new DistanceWheelMotion(DistanceWheelMotion.ACTION_FORWARD_RUN, 5, distancia);
            wheelMotionManager.doDistanceMotion(distanceWheelMotion);

            long tiempoEspera = (long) (5000 * (distancia / 100.0));
            try {
                Thread.sleep(tiempoEspera);
            } catch (InterruptedException e) {
                System.out.println("Error al avanzar: " + e.getMessage());
                e.printStackTrace();
            }

        } else if (Objects.equals(movimiento, "Retroceder")){

            RelativeAngleWheelMotion movimientoRuedas = new RelativeAngleWheelMotion(RelativeAngleWheelMotion.TURN_RIGHT, 5, 180);
            wheelMotionManager.doRelativeAngleMotion(movimientoRuedas);

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            DistanceWheelMotion distanceWheelMotion = new DistanceWheelMotion(DistanceWheelMotion.ACTION_FORWARD_RUN, 5, distancia);
            wheelMotionManager.doDistanceMotion(distanceWheelMotion);

            long tiempoEspera = (long) (5000 * (distancia / 100.0));
            try {
                Thread.sleep(tiempoEspera);
            } catch (InterruptedException e) {
                System.out.println("Error al avanzar: " + e.getMessage());
                e.printStackTrace();
            }

            movimientoRuedas = new RelativeAngleWheelMotion(RelativeAngleWheelMotion.TURN_RIGHT, 5, 180);
            wheelMotionManager.doRelativeAngleMotion(movimientoRuedas);

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } else if (Objects.equals(tipo, "Girar a la derecha")){
            RelativeAngleWheelMotion movimientoRuedas = new RelativeAngleWheelMotion(RelativeAngleWheelMotion.TURN_RIGHT, 5, 90);
            wheelMotionManager.doRelativeAngleMotion(movimientoRuedas);

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } else if (Objects.equals(tipo, "Girar a la izquierda")){
            RelativeAngleWheelMotion movimientoRuedas = new RelativeAngleWheelMotion(RelativeAngleWheelMotion.TURN_LEFT, 5, 90);
            wheelMotionManager.doRelativeAngleMotion(movimientoRuedas);

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } else if (Objects.equals(tipo, "Girar sobre si mismo")){
            RelativeAngleWheelMotion movimientoRuedas = new RelativeAngleWheelMotion(RelativeAngleWheelMotion.TURN_LEFT, 5, 360);
            wheelMotionManager.doRelativeAngleMotion(movimientoRuedas);
        }
    }

    // Método que dado un tipo de luces que se encienden, realiza el movimiento seleccionado
    public void encenderLedsOperation(String tipo){
        String[] partes = tipo.split("-");
        String partEsp = partes[0];
        String modeEsp = partes[1];

        if (Objects.equals(partEsp, "Todo")){
            if (Objects.equals(modeEsp, "Blanco")){
                hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_WHITE));
            } else if (Objects.equals(modeEsp, "Rojo")){
                hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_RED));
            } else if (Objects.equals(modeEsp, "Verde")){
                hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_GREEN));
            }  else if (Objects.equals(modeEsp, "Rosa")){
                hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_PINK));
            }  else if (Objects.equals(modeEsp, "Morado")){
                hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_PURPLE));
            }  else if (Objects.equals(modeEsp, "Azul")){
                hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_BLUE));
            }  else if (Objects.equals(modeEsp, "Amarillo")){
                hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_YELLOW));
            }  else if (Objects.equals(modeEsp, "Parpadeo blanco")){
                hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_FLICKER_WHITE));
            }  else if (Objects.equals(modeEsp, "Parpadeo rojo")){
                hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_FLICKER_RED));
            }  else if (Objects.equals(modeEsp, "Parpadeo verde")){
                hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_FLICKER_GREEN));
            }  else if (Objects.equals(modeEsp, "Parpadeo rosa")){
                hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_FLICKER_PINK));
            }  else if (Objects.equals(modeEsp, "Parpadeo morado")){
                hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_FLICKER_PURPLE));
            }  else if (Objects.equals(modeEsp, "Parpadeo azul")){
                hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_FLICKER_BLUE));
            }  else if (Objects.equals(modeEsp, "Parpadeo amarillo")){
                hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_FLICKER_YELLOW));
            }  else if (Objects.equals(modeEsp, "Parpadeo aleatorio")){
                hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_FLICKER_RANDOM));
            }
        } else if (Objects.equals(partEsp, "Ruedas")){
            if (Objects.equals(modeEsp, "Blanco")){
                hardWareManager.setLED(new LED(LED.PART_WHEEL, LED.MODE_WHITE));
            } else if (Objects.equals(modeEsp, "Rojo")){
                hardWareManager.setLED(new LED(LED.PART_WHEEL, LED.MODE_RED));
            } else if (Objects.equals(modeEsp, "Verde")){
                hardWareManager.setLED(new LED(LED.PART_WHEEL, LED.MODE_GREEN));
            }  else if (Objects.equals(modeEsp, "Rosa")){
                hardWareManager.setLED(new LED(LED.PART_WHEEL, LED.MODE_PINK));
            }  else if (Objects.equals(modeEsp, "Morado")){
                hardWareManager.setLED(new LED(LED.PART_WHEEL, LED.MODE_PURPLE));
            }  else if (Objects.equals(modeEsp, "Azul")){
                hardWareManager.setLED(new LED(LED.PART_WHEEL, LED.MODE_BLUE));
            }  else if (Objects.equals(modeEsp, "Amarillo")){
                hardWareManager.setLED(new LED(LED.PART_WHEEL, LED.MODE_YELLOW));
            }  else if (Objects.equals(modeEsp, "Parpadeo blanco")){
                hardWareManager.setLED(new LED(LED.PART_WHEEL, LED.MODE_FLICKER_WHITE));
            }  else if (Objects.equals(modeEsp, "Parpadeo rojo")){
                hardWareManager.setLED(new LED(LED.PART_WHEEL, LED.MODE_FLICKER_RED));
            }  else if (Objects.equals(modeEsp, "Parpadeo verde")){
                hardWareManager.setLED(new LED(LED.PART_WHEEL, LED.MODE_FLICKER_GREEN));
            }  else if (Objects.equals(modeEsp, "Parpadeo rosa")){
                hardWareManager.setLED(new LED(LED.PART_WHEEL, LED.MODE_FLICKER_PINK));
            }  else if (Objects.equals(modeEsp, "Parpadeo morado")){
                hardWareManager.setLED(new LED(LED.PART_WHEEL, LED.MODE_FLICKER_PURPLE));
            }  else if (Objects.equals(modeEsp, "Parpadeo azul")){
                hardWareManager.setLED(new LED(LED.PART_WHEEL, LED.MODE_FLICKER_BLUE));
            }  else if (Objects.equals(modeEsp, "Parpadeo amarillo")){
                hardWareManager.setLED(new LED(LED.PART_WHEEL, LED.MODE_FLICKER_YELLOW));
            }  else if (Objects.equals(modeEsp, "Parpadeo aleatorio")){
                hardWareManager.setLED(new LED(LED.PART_WHEEL, LED.MODE_FLICKER_RANDOM));
            }
        }  else if (Objects.equals(partEsp, "Brazo izquierdo")){
            if (Objects.equals(modeEsp, "Blanco")){
                hardWareManager.setLED(new LED(LED.PART_LEFT_HAND, LED.MODE_WHITE));
            } else if (Objects.equals(modeEsp, "Rojo")){
                hardWareManager.setLED(new LED(LED.PART_LEFT_HAND, LED.MODE_RED));
            } else if (Objects.equals(modeEsp, "Verde")){
                hardWareManager.setLED(new LED(LED.PART_LEFT_HAND, LED.MODE_GREEN));
            }  else if (Objects.equals(modeEsp, "Rosa")){
                hardWareManager.setLED(new LED(LED.PART_LEFT_HAND, LED.MODE_PINK));
            }  else if (Objects.equals(modeEsp, "Morado")){
                hardWareManager.setLED(new LED(LED.PART_LEFT_HAND, LED.MODE_PURPLE));
            }  else if (Objects.equals(modeEsp, "Azul")){
                hardWareManager.setLED(new LED(LED.PART_LEFT_HAND, LED.MODE_BLUE));
            }  else if (Objects.equals(modeEsp, "Amarillo")){
                hardWareManager.setLED(new LED(LED.PART_LEFT_HAND, LED.MODE_YELLOW));
            }  else if (Objects.equals(modeEsp, "Parpadeo blanco")){
                hardWareManager.setLED(new LED(LED.PART_LEFT_HAND, LED.MODE_FLICKER_WHITE));
            }  else if (Objects.equals(modeEsp, "Parpadeo rojo")){
                hardWareManager.setLED(new LED(LED.PART_LEFT_HAND, LED.MODE_FLICKER_RED));
            }  else if (Objects.equals(modeEsp, "Parpadeo verde")){
                hardWareManager.setLED(new LED(LED.PART_LEFT_HAND, LED.MODE_FLICKER_GREEN));
            }  else if (Objects.equals(modeEsp, "Parpadeo rosa")){
                hardWareManager.setLED(new LED(LED.PART_LEFT_HAND, LED.MODE_FLICKER_PINK));
            }  else if (Objects.equals(modeEsp, "Parpadeo morado")){
                hardWareManager.setLED(new LED(LED.PART_LEFT_HAND, LED.MODE_FLICKER_PURPLE));
            }  else if (Objects.equals(modeEsp, "Parpadeo azul")){
                hardWareManager.setLED(new LED(LED.PART_LEFT_HAND, LED.MODE_FLICKER_BLUE));
            }  else if (Objects.equals(modeEsp, "Parpadeo amarillo")){
                hardWareManager.setLED(new LED(LED.PART_LEFT_HAND, LED.MODE_FLICKER_YELLOW));
            }  else if (Objects.equals(modeEsp, "Parpadeo aleatorio")){
                hardWareManager.setLED(new LED(LED.PART_LEFT_HAND, LED.MODE_FLICKER_RANDOM));
            }
        } else if (Objects.equals(partEsp, "Brazo derecho")){
            if (Objects.equals(modeEsp, "Blanco")){
                hardWareManager.setLED(new LED(LED.PART_RIGHT_HAND, LED.MODE_WHITE));
            } else if (Objects.equals(modeEsp, "Rojo")){
                hardWareManager.setLED(new LED(LED.PART_RIGHT_HAND, LED.MODE_RED));
            } else if (Objects.equals(modeEsp, "Verde")){
                hardWareManager.setLED(new LED(LED.PART_RIGHT_HAND, LED.MODE_GREEN));
            }  else if (Objects.equals(modeEsp, "Rosa")){
                hardWareManager.setLED(new LED(LED.PART_RIGHT_HAND, LED.MODE_PINK));
            }  else if (Objects.equals(modeEsp, "Morado")){
                hardWareManager.setLED(new LED(LED.PART_RIGHT_HAND, LED.MODE_PURPLE));
            }  else if (Objects.equals(modeEsp, "Azul")){
                hardWareManager.setLED(new LED(LED.PART_RIGHT_HAND, LED.MODE_BLUE));
            }  else if (Objects.equals(modeEsp, "Amarillo")){
                hardWareManager.setLED(new LED(LED.PART_RIGHT_HAND, LED.MODE_YELLOW));
            }  else if (Objects.equals(modeEsp, "Parpadeo blanco")){
                hardWareManager.setLED(new LED(LED.PART_RIGHT_HAND, LED.MODE_FLICKER_WHITE));
            }  else if (Objects.equals(modeEsp, "Parpadeo rojo")){
                hardWareManager.setLED(new LED(LED.PART_RIGHT_HAND, LED.MODE_FLICKER_RED));
            }  else if (Objects.equals(modeEsp, "Parpadeo verde")){
                hardWareManager.setLED(new LED(LED.PART_RIGHT_HAND, LED.MODE_FLICKER_GREEN));
            }  else if (Objects.equals(modeEsp, "Parpadeo rosa")){
                hardWareManager.setLED(new LED(LED.PART_RIGHT_HAND, LED.MODE_FLICKER_PINK));
            }  else if (Objects.equals(modeEsp, "Parpadeo morado")){
                hardWareManager.setLED(new LED(LED.PART_RIGHT_HAND, LED.MODE_FLICKER_PURPLE));
            }  else if (Objects.equals(modeEsp, "Parpadeo azul")){
                hardWareManager.setLED(new LED(LED.PART_RIGHT_HAND, LED.MODE_FLICKER_BLUE));
            }  else if (Objects.equals(modeEsp, "Parpadeo amarillo")){
                hardWareManager.setLED(new LED(LED.PART_RIGHT_HAND, LED.MODE_FLICKER_YELLOW));
            }  else if (Objects.equals(modeEsp, "Parpadeo aleatorio")){
                hardWareManager.setLED(new LED(LED.PART_RIGHT_HAND, LED.MODE_FLICKER_RANDOM));
            }
        } else if (Objects.equals(partEsp, "Oreja izquierda")){
            if (Objects.equals(modeEsp, "Blanco")){
                hardWareManager.setLED(new LED(LED.PART_LEFT_HEAD, LED.MODE_WHITE));
            } else if (Objects.equals(modeEsp, "Rojo")){
                hardWareManager.setLED(new LED(LED.PART_LEFT_HEAD, LED.MODE_RED));
            } else if (Objects.equals(modeEsp, "Verde")){
                hardWareManager.setLED(new LED(LED.PART_LEFT_HEAD, LED.MODE_GREEN));
            }  else if (Objects.equals(modeEsp, "Rosa")){
                hardWareManager.setLED(new LED(LED.PART_LEFT_HEAD, LED.MODE_PINK));
            }  else if (Objects.equals(modeEsp, "Morado")){
                hardWareManager.setLED(new LED(LED.PART_LEFT_HEAD, LED.MODE_PURPLE));
            }  else if (Objects.equals(modeEsp, "Azul")){
                hardWareManager.setLED(new LED(LED.PART_LEFT_HEAD, LED.MODE_BLUE));
            }  else if (Objects.equals(modeEsp, "Amarillo")){
                hardWareManager.setLED(new LED(LED.PART_LEFT_HEAD, LED.MODE_YELLOW));
            }  else if (Objects.equals(modeEsp, "Parpadeo blanco")){
                hardWareManager.setLED(new LED(LED.PART_LEFT_HEAD, LED.MODE_FLICKER_WHITE, (byte) 10, (byte) 3));
            }  else if (Objects.equals(modeEsp, "Parpadeo rojo")){
                hardWareManager.setLED(new LED(LED.PART_LEFT_HEAD, LED.MODE_FLICKER_RED, (byte) 10, (byte) 3));
            }  else if (Objects.equals(modeEsp, "Parpadeo verde")){
                hardWareManager.setLED(new LED(LED.PART_LEFT_HEAD, LED.MODE_FLICKER_GREEN, (byte) 10, (byte) 3));
            }  else if (Objects.equals(modeEsp, "Parpadeo rosa")){
                hardWareManager.setLED(new LED(LED.PART_LEFT_HEAD, LED.MODE_FLICKER_PINK, (byte) 10, (byte) 3));
            }  else if (Objects.equals(modeEsp, "Parpadeo morado")){
                hardWareManager.setLED(new LED(LED.PART_LEFT_HEAD, LED.MODE_FLICKER_PURPLE, (byte) 10, (byte) 3));
            }  else if (Objects.equals(modeEsp, "Parpadeo azul")){
                hardWareManager.setLED(new LED(LED.PART_LEFT_HEAD, LED.MODE_FLICKER_BLUE, (byte) 10, (byte) 3));
            }  else if (Objects.equals(modeEsp, "Parpadeo amarillo")){
                hardWareManager.setLED(new LED(LED.PART_LEFT_HEAD, LED.MODE_FLICKER_YELLOW, (byte) 10, (byte) 3));
            }  else if (Objects.equals(modeEsp, "Parpadeo aleatorio")){
                hardWareManager.setLED(new LED(LED.PART_LEFT_HEAD, LED.MODE_FLICKER_RANDOM, (byte) 10, (byte) 3));
            }

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            hardWareManager.setLED(new LED(LED.PART_LEFT_HEAD, LED.MODE_CLOSE));

        } else if (Objects.equals(partEsp, "Oreja derecha")){
            if (Objects.equals(modeEsp, "Blanco")){
                hardWareManager.setLED(new LED(LED.PART_RIGHT_HEAD, LED.MODE_WHITE));
            } else if (Objects.equals(modeEsp, "Rojo")){
                hardWareManager.setLED(new LED(LED.PART_RIGHT_HEAD, LED.MODE_RED));
            } else if (Objects.equals(modeEsp, "Verde")){
                hardWareManager.setLED(new LED(LED.PART_RIGHT_HEAD, LED.MODE_GREEN));
            }  else if (Objects.equals(modeEsp, "Rosa")){
                hardWareManager.setLED(new LED(LED.PART_RIGHT_HEAD, LED.MODE_PINK));
            }  else if (Objects.equals(modeEsp, "Morado")){
                hardWareManager.setLED(new LED(LED.PART_RIGHT_HEAD, LED.MODE_PURPLE));
            }  else if (Objects.equals(modeEsp, "Azul")){
                hardWareManager.setLED(new LED(LED.PART_RIGHT_HEAD, LED.MODE_BLUE));
            }  else if (Objects.equals(modeEsp, "Amarillo")){
                hardWareManager.setLED(new LED(LED.PART_RIGHT_HEAD, LED.MODE_YELLOW));
            }  else if (Objects.equals(modeEsp, "Parpadeo blanco")){
                hardWareManager.setLED(new LED(LED.PART_RIGHT_HEAD, LED.MODE_FLICKER_WHITE, (byte) 10, (byte) 3));
            }  else if (Objects.equals(modeEsp, "Parpadeo rojo")){
                hardWareManager.setLED(new LED(LED.PART_RIGHT_HEAD, LED.MODE_FLICKER_RED, (byte) 10, (byte) 3));
            }  else if (Objects.equals(modeEsp, "Parpadeo verde")){
                hardWareManager.setLED(new LED(LED.PART_RIGHT_HEAD, LED.MODE_FLICKER_GREEN, (byte) 10, (byte) 3));
            }  else if (Objects.equals(modeEsp, "Parpadeo rosa")){
                hardWareManager.setLED(new LED(LED.PART_RIGHT_HEAD, LED.MODE_FLICKER_PINK, (byte) 10, (byte) 3));
            }  else if (Objects.equals(modeEsp, "Parpadeo morado")){
                hardWareManager.setLED(new LED(LED.PART_RIGHT_HEAD, LED.MODE_FLICKER_PURPLE, (byte) 10, (byte) 3));
            }  else if (Objects.equals(modeEsp, "Parpadeo azul")){
                hardWareManager.setLED(new LED(LED.PART_RIGHT_HEAD, LED.MODE_FLICKER_BLUE, (byte) 10, (byte) 3));
            }  else if (Objects.equals(modeEsp, "Parpadeo amarillo")){
                hardWareManager.setLED(new LED(LED.PART_RIGHT_HEAD, LED.MODE_FLICKER_YELLOW, (byte) 10, (byte) 3));
            }  else if (Objects.equals(modeEsp, "Parpadeo aleatorio")){
                hardWareManager.setLED(new LED(LED.PART_RIGHT_HEAD, LED.MODE_FLICKER_RANDOM, (byte) 10, (byte) 3));
            }

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            hardWareManager.setLED(new LED(LED.PART_RIGHT_HEAD, LED.MODE_CLOSE));
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
