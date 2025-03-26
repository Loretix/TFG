package com.example.sanbotapp.moduloReactivo;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.sanbotapp.robotControl.HeadControl;
import com.example.sanbotapp.robotControl.SpeechControl;
import com.example.sanbotapp.robotControl.WheelControl;
import com.qihancloud.opensdk.function.unit.HardWareManager;
import com.qihancloud.opensdk.function.unit.interfaces.hardware.PIRListener;
import com.qihancloud.opensdk.function.unit.interfaces.hardware.VoiceLocateListener;

public class SanbotResponder {
    private HardWareManager hardWareManager;
    private SpeechControl speechControl;
    private HeadControl headControl;
    private WheelControl wheelControl;

    private final Handler handler = new Handler(Looper.getMainLooper());

    public SanbotResponder(HardWareManager hardWareManager, HeadControl headControl, SpeechControl speechControl, WheelControl wheelControl) {
        this.hardWareManager = hardWareManager;
        this.speechControl = speechControl;
        this.headControl = headControl;
        this.wheelControl = wheelControl;
    }

    /**
     *  Iniciar detección de sonido y movimiento PIR
     */
    public void iniciarDeteccion() {
        //detectarFuenteDeSonido();
        detectarMovimientoPIR();
    }

    /**
     *  Localización de sonido: Girar hacia donde viene el ruido
     */
    private void detectarFuenteDeSonido() {
        hardWareManager.setOnHareWareListener(new VoiceLocateListener() {
            @Override
            public void voiceLocateResult(int angle) {
                // Detectar fuente de sonido
                // todo: funciona bien pero se apagan las orejas en la app por lo que no detecta
                // ¿COMO MANTENERLAS ENCENDIDAS
                if(speechControl.modoEscucha().equals("hola")){

                    // Ángulo detectado según sentido de las agujas del reloj
                    Log.d("SanbotResponder", "Sonido detectado en ángulo: " + angle + "°");
                    int headAngle = convertirAnguloCabeza(angle);

                    //Esperar tiempo
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Log.d("SanbotResponder", "Sonido detectado en ángulo transformado: " + headAngle + "°");
                    if (headAngle != -1) {
                        headControl.girarCabeza(headAngle);
                        speechControl.hablar("¡Escuché algo por aquí!");
                    } else {
                        speechControl.hablar("¡Algo sonó detrás de mí!");
                        // TODO: Girar el cuerpo si el sonido está detrás
                    }

                }

            }
        });
    }

    /**
     *  Detección PIR: Reaccionar si alguien se acerca
     */
    private void detectarMovimientoPIR() {
        hardWareManager.setOnHareWareListener(new PIRListener() {
            @Override
            public void onPIRCheckResult(boolean isChecked, int part) {
                Log.d("SanbotResponder", (part == 1 ? "Front of the body" : "Back of the body") + " PIR has been triggered");

                if (isChecked) {
                    handler.post(() -> {
                        int randomResponse = (int) (Math.random() * (part == 1 ? 2 : 3)) + 1;
                        switch (randomResponse) {
                            case 1:
                                speechControl.hablar("¡Hola! Bienvenido.");
                                break;
                            case 2:
                                speechControl.hablar(part == 1 ?
                                        "¡Hola! Toma asiento y disfruta de la presentación" :
                                        "¿Qué haces ahí? Siéntate y disfruta de la presentación");
                                break;
                            case 3:
                                speechControl.hablar("¡Qué susto! Toma asiento y disfruta de la presentación");
                                break;
                        }
                    });
                }
            }
        });
    }


    /**
     * Desactivar detección de sonido y movimiento PIR - UTILIZAR DESDE LA PRESETACIÓN SI MOLESTA SU USO
     */
    public void desactivarDeteccion() {
        hardWareManager.setOnHareWareListener(null);
    }


    private int convertirAnguloCabeza(int angle) {
        // Angulo cabeza de 0 (izquierda) a 180 (derecha)
        // Movimiento agujas del reloj por lo que 90º seria 180º en absoluto (derecha), y 270º seria 0º en absoluto (izquierda)

        if (angle >= 270) { // 270 = 0º, 90 = 180º/ 270 -270 = 0 / 270 - 90 = 180 / 360 -270 = 90
            return angle - 270;
        } else if (angle <= 90){
            return 90 + angle;
        }else {
            // Si angulo > 90 && angulo < 270
            return -1;
        }

    }

}

