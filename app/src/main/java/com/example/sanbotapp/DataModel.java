package com.example.sanbotapp;

import java.io.Serializable;

public class DataModel implements Serializable {
    private String text;
    private String spinnerOption;

    public DataModel(String text, String spinnerOption) {
        this.text = text;
        this.spinnerOption = spinnerOption;
    }

    public String getText() {
        return text;
    }

    public String getSpinnerOption() {
        return spinnerOption;
    }
}

