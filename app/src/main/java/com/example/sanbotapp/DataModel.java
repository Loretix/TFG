package com.example.sanbotapp;

import java.io.Serializable;

public class DataModel implements Serializable {
    private String text;
    private String spinnerOption;
    private String imagen;

    public DataModel(String text, String spinnerOption, String imagen){
        this.text = text;
        this.spinnerOption = spinnerOption;
        this.imagen = imagen;
    }

    public String getText() {
        return text;
    }

    public String getSpinnerOption() {
        return spinnerOption;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}

