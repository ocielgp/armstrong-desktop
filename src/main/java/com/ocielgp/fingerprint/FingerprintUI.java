package com.ocielgp.fingerprint;

import com.digitalpersona.uareu.Fmd;
import com.jfoenix.controls.JFXButton;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class FingerprintUI {
    private final VBox fingerprintPane;
    private final VBox fingerprintFmd;
    private final Label fingerprintCounter;
    private final JFXButton restartButton;
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
        ((ImageView) this.fingerprintFmd.getChildren().get(0)).setImage(null);
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
        System.out.println(this.fingerprintFmd.getWidth());
        System.out.println(this.fingerprintFmd.getHeight());
        this.fmds.add(fmd);
        this.fingerprintCounter.setText(String.valueOf(fmds.size()));
        this.enableRestart();
    }

}
