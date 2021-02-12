package com.ocielgp.fingerprint;

import com.digitalpersona.uareu.Fmd;
import com.jfoenix.controls.JFXButton;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.LinkedList;

public class FingerprintUI {
    private VBox fingerprintPane;
    private VBox fingerprintFmd;
    private Label fingerprintCounter;
    private JFXButton restartButton;
    public ArrayList<Fmd> fmds;

    public FingerprintUI(VBox fingerprintPane, VBox fingerprintFmd, Label fingerprintCounter, JFXButton restartButton) {
        this.fingerprintPane = fingerprintPane;
        this.fingerprintFmd = fingerprintFmd;
        this.fingerprintCounter = fingerprintCounter;
        this.restartButton = restartButton;
        this.fmds = new ArrayList<>();
    }

    public ArrayList<Fmd> getFmds() {
        return fmds;
    }

    public void clearFmd() {
        this.fingerprintFmd.getChildren().clear();
    }

    public void clearFingerprints() {
        this.fmds.clear();
    }

    public void enableRestart() {
        this.restartButton.setDisable(false);
    }

    public void restartCapture() {
        this.clearFingerprints();
        this.restartButton.setDisable(true);
        this.fingerprintCounter.setText("0");
    }

    public void show() {
        this.fingerprintPane.setVisible(true);
        this.fingerprintPane.setManaged(true);
    }

    public void hide() {
        this.fingerprintPane.setVisible(false);
        this.fingerprintPane.setManaged(false);
    }

    public void add(Fmd fmd) {
        this.fmds.add(fmd);
        this.fingerprintCounter.setText(String.valueOf(fmds.size()));
        this.enableRestart();
    }

}
