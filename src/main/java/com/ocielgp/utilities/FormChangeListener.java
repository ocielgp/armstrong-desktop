package com.ocielgp.utilities;

import com.jfoenix.controls.JFXButton;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class FormChangeListener {
    private final HashMap<String, Boolean> formListeners = new HashMap<>();
    private boolean isListen = false;
    private final JFXButton buttonAction;

    public FormChangeListener(JFXButton buttonUpdater) {
        this.buttonAction = buttonUpdater;
    }

    public boolean isListen() {
        return isListen;
    }

    public void setListen(boolean listen) {
        if (this.isListen != listen) {
            CompletableFuture.runAsync(() -> {
                this.isListen = listen;
                if (!listen) {
                    restartValues();
                }
            });
        }
    }

    public void add(String codeName) {
        this.formListeners.put(codeName, true);
    }

    synchronized public void change(String codeName, boolean value) {
        CompletableFuture.runAsync(() -> {
            this.formListeners.replace(codeName, value);
//            System.out.println("[" + codeName + "]: - " + value);

            boolean newChanges = false;
            for (Boolean flag : this.formListeners.values()) {
                if (!flag) {
                    newChanges = true;
                    break;
                }
            }
            this.buttonAction.setDisable(!newChanges);
        });
    }

    public boolean isChanged(String codeName) {
        return !this.formListeners.get(codeName);
    }

    private void restartValues() {
        CompletableFuture.runAsync(() -> this.formListeners.replaceAll((key, value) -> value = true));
    }
}
