package com.ocielgp.fingerprint;

import com.digitalpersona.uareu.Fmd;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.ListIterator;

public class FingerprintUI {
    private final VBox fingerprintPane;
    private VBox fingerprintFmd;
    private Label fingerprintCounter;
    private JFXButton startCaptureButton;
    private JFXButton restartCaptureButton;
    public ArrayList<Fmd> fmds;

    private final EventHandler<ActionEvent> captureEvent = actionEvent -> {
        if (this.startCaptureButton.getText().equals("Iniciar captura")) {
            this.fingerprintFmd.requestFocus();
            System.out.println("inicio captura");
            Fingerprint.StartCapture(this.fingerprintFmd, true);
            this.startCaptureButton.setText("Detener captura");
            if (Integer.parseInt(this.fingerprintCounter.getText()) > 0) {
                this.restartCaptureButton.setDisable(false);
            }
        } else {
            Fingerprint.StopCapture();
            this.startCaptureButton.setText("Iniciar captura");
        }
    };

    public FingerprintUI(VBox fingerprintPane, VBox fingerprintFmdPane, Label fingerprintCounter, JFXButton startCaptureButton, JFXButton restartCaptureButton) {
        this.fingerprintPane = fingerprintPane;
        this.fingerprintFmd = fingerprintFmdPane;
        this.fingerprintCounter = fingerprintCounter;
        this.startCaptureButton = startCaptureButton;
        this.restartCaptureButton = restartCaptureButton;
        this.fmds = new ArrayList<>();

        this.startCaptureButton.setOnAction(captureEvent);
        this.restartCaptureButton.setOnAction(actionEvent -> {
            Fingerprint.RestartCapture();
            this.fingerprintFmd.requestFocus();
        });
    }

    public ArrayList<Fmd> getFmds() {
        return fmds;
    }

    public void clearFmd() {
//        this.fingerprintFmd.getChildren().clear();
    }

    public void clearFingerprints() {
        this.fmds.clear();
    }

    private void enableRestartButton() {
        this.restartCaptureButton.setDisable(false);
    }

    public void restartCapture() {
        ((ImageView) this.fingerprintFmd.getChildren().get(0)).setImage(null);
        this.clearFingerprints();
        this.restartCaptureButton.setDisable(true);
        this.fingerprintCounter.setText("0");
        this.startCaptureButton.setText("Iniciar captura");
        this.startCaptureButton.setOnAction(captureEvent);
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
        this.enableRestartButton();
    }

    public ListIterator<Fmd> getFingerprints() {
        if (this.fmds.size() == 0) {
            return null;
        } else {
            return this.fmds.listIterator();
        }
    }

}
