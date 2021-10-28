package com.ocielgp.utilities;

import com.jfoenix.controls.JFXButton;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class FormChangeListener {
    private final HashMap<String, Boolean> formListeners = new HashMap<>();
    private boolean isListen = false;
    private final JFXButton buttonChange;

    public FormChangeListener(JFXButton buttonUpdater) {
        this.buttonChange = buttonUpdater;
    }

    public boolean isListen() {
        return isListen;
    }

    public void setListen(boolean listen) {
        CompletableFuture.runAsync(() -> {
            this.isListen = listen;
            if (!listen) {
                restartValues();
            }
        });
    }

    private void restartValues() {
        CompletableFuture.runAsync(() -> this.formListeners.replaceAll((key, value) -> value = true));
    }

    public void add(String codeName) {
        CompletableFuture.runAsync(() -> this.formListeners.put(codeName, true));
    }

    public void change(String codeName, boolean value) {
        CompletableFuture.runAsync(() -> {
            this.formListeners.replace(codeName, value);
            System.out.println("[" + codeName + "]: - " + value);

            boolean flag = false;
            for (Boolean aBoolean : this.formListeners.values()) {
                if (!aBoolean) {
                    flag = true;
                    break;
                }
            }
            this.buttonChange.setDisable(!flag);
        });
    }

    public boolean isChanged(String codeName) {
        return !this.formListeners.get(codeName);
    }
}
