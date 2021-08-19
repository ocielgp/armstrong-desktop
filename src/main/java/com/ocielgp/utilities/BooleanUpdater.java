package com.ocielgp.utilities;

import com.jfoenix.controls.JFXButton;

import java.util.HashMap;
import java.util.Iterator;

public class BooleanUpdater {
    private final HashMap<String, Boolean> booleanUpdaters = new HashMap<>();
    private boolean listener = false;
    private JFXButton buttonUpdater;

    public BooleanUpdater(JFXButton buttonUpdater) {
        this.buttonUpdater = buttonUpdater;
    }

    public void add(String codeName) {
        this.booleanUpdaters.put(
                codeName,
                true
        );
    }

    public boolean getBool(String codeName) {
        return this.booleanUpdaters.get(codeName);
    }

    public void change(String codeName, boolean value) {
        this.booleanUpdaters.replace(codeName, value);

        boolean flag = false;
        for (Boolean aBoolean : this.booleanUpdaters.values()) {
            if (!aBoolean) {
                this.buttonUpdater.setDisable(false);
                flag = true;
                break;
            }
        }
        this.buttonUpdater.setDisable(!flag);
        System.out.println(codeName + "- " + value);
    }

    private void resetAll() {
        this.booleanUpdaters.forEach((s, aBoolean) -> aBoolean = false);
    }

    public boolean isListener() {
        return listener;
    }

    public void setListener(boolean listener) {
        this.listener = listener;
        this.resetAll();
    }
}
