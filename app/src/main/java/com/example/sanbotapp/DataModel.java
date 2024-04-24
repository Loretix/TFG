package com.example.sanbotapp;

public class DataModel {
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

