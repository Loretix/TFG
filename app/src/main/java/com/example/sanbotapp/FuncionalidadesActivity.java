package com.example.sanbotapp;


import android.media.AudioManager;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.qihancloud.opensdk.function.beans.SpeakOption;
import com.qihancloud.opensdk.function.beans.handmotion.AbsoluteAngleHandMotion;
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
    private MediaManager mediaManager;
    private ProjectorManager projectorManager;
    private AudioManager audioManager;


    @Override
    protected void onMainServiceConnected() {

    }

    public FuncionalidadesActivity( SpeechManager speechManager, SystemManager systemManager, HandMotionManager handMotionManager) {
        this.speechManager = speechManager;
        this.systemManager = systemManager;
        this.handMotionManager = handMotionManager;
    }

    // Método que dado una cadena y un tipo de voz, reproduce el texto con la voz seleccionada
    public void speakOperation(String texto, String tipo){

        SpeakOption speakOption = new SpeakOption();
        if( tipo == "Normal"){
            speakOption.setSpeed(60);
            speakOption.setIntonation(50);
        }
        speechManager.startSpeak(texto, speakOption);
    }

    // Método que dado un tipo de emoción, cambia la cara del robot a la emoción seleccionada
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

        } else if (Objects.equals(tipo, "Hip, hip, hurra!")){
        }
    }

    // Método que dado un tipo de movimiento de cabeza, realiza el movimiento seleccionado
    public void moveCabezaOperation(String tipo){
        if(Objects.equals(tipo, "Asentir")){

        } else if (Objects.equals(tipo, "Negar")){

        } else if (Objects.equals(tipo, "Mirar a la derecha")){

        } else if (Objects.equals(tipo, "Mirar a la izquierda")){

        } else if (Objects.equals(tipo, "Mirar arriba")){

        } else if (Objects.equals(tipo, "Mirar abajo")){

        } else if (Objects.equals(tipo, "Lo siento")){

        } else if (Objects.equals(tipo, "Resignación")){

        }
    }

    // Método que dado un tipo de movimiento de ruedas, realiza el movimiento seleccionado
    public void moveRuedasOperation(String tipo){
        if(Objects.equals(tipo, "Avanzar")){

        } else if (Objects.equals(tipo, "Retroceder")){

        } else if (Objects.equals(tipo, "Girar a la derecha")){

        } else if (Objects.equals(tipo, "Girar a la izquierda")){

        } else if (Objects.equals(tipo, "Girar sobre si mismo")){

        }
    }

    // Método que dado un tipo de luces que se encienden, realiza el movimiento seleccionado
    public void encenderLedsOperation(String tipo){
        String[] partes = tipo.split("-");
        String partEsp = partes[0];
        String modeEsp = partes[1];
        String part = "";
        String mode = "";

        if (Objects.equals(partEsp, "Todo")){

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
