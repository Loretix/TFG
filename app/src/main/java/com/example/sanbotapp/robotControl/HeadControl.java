package com.example.sanbotapp.robotControl;

import com.qihancloud.opensdk.function.beans.headmotion.AbsoluteAngleHeadMotion;
import com.qihancloud.opensdk.function.beans.headmotion.RelativeAngleHeadMotion;
import com.qihancloud.opensdk.function.unit.HeadMotionManager;

public class HeadControl {
    private HeadMotionManager headMotionManager;

    // Constructor
    public HeadControl(HeadMotionManager headMotionManager){
        this.headMotionManager = headMotionManager;
    }

    // Enum utilizado para definir las acciones de cabeza, en este caso: derecha, izquierda, arriba, abajo y centro
    public enum AccionesCabeza {
        DERECHA,
        IZQUIERDA,
        ARRIBA,
        ABAJO,
        CENTRO;
    }

    // Función utilizada para indicar la acción que se quiere realizar
    // con la cabeza
    public boolean controlBasicoCabeza(AccionesCabeza accion) {
        RelativeAngleHeadMotion relativeAngleHeadMotion;
        AbsoluteAngleHeadMotion absoluteAngleHeadMotion;
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

    // Función para poner la cabeza en su posición original, en este caso: en el centro
    public boolean reiniciar(){
        controlBasicoCabeza(AccionesCabeza.CENTRO);
        return true;
    }
}
