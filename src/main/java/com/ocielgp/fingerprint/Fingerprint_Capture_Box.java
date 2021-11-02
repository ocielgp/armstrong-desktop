package com.ocielgp.fingerprint;

import com.digitalpersona.uareu.Fmd;
import com.jfoenix.controls.JFXButton;
import com.ocielgp.dao.JDBC_Member_Fingerprint;
import com.ocielgp.utilities.Input;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.ListIterator;

public class Fingerprint_Capture_Box {
    public final VBox boxFingerprintPane;
    private final VBox boxFingerprintView;
    private final Label labelFingerprintCounter;
    private final JFXButton buttonStartCapture;
    private final JFXButton buttonRestartCapture;
    public ArrayList<Fmd> arrayFingerprints = new ArrayList<>();

    public Fingerprint_Capture_Box(VBox boxFingerprintPane, VBox boxFingerprintPreview, Label labelFingerprintCounter, JFXButton buttonStartCapture, JFXButton buttonRestartCapture) {
        this.boxFingerprintPane = boxFingerprintPane;
        this.boxFingerprintView = boxFingerprintPreview;
        this.labelFingerprintCounter = labelFingerprintCounter;
        this.buttonStartCapture = buttonStartCapture;
        this.buttonRestartCapture = buttonRestartCapture;
        Input.createVisibleEvent(this.boxFingerprintPane, true);

        this.buttonStartCapture.setOnAction(actionEvent -> captureEvent());
        this.buttonRestartCapture.setOnAction(actionEvent -> this.restartCaptureEvent());
    }

    public void show() {
        this.boxFingerprintPane.setVisible(true);
    }

    public void hide() {
        this.boxFingerprintPane.setVisible(false);
        this.restartCaptureEvent();
    }

    public void addFingerprint(Fmd fmd) {
        this.arrayFingerprints.add(fmd);
        this.labelFingerprintCounter.setText(String.valueOf(arrayFingerprints.size()));
        this.buttonRestartCapture.setDisable(false);
        this.clearFingerprintPane();
    }

    public void clearFingerprintPane() {
        if (this.boxFingerprintView.getChildren().size() > 0) {
            ((ImageView) this.boxFingerprintView.getChildren().get(0)).setImage(null);
        }
    }

    public ListIterator<Fmd> getFingerprints() {
        return this.arrayFingerprints.listIterator();
    }

    public void loadFingerprints(int idMember) {
        if (this.boxFingerprintPane.isVisible()) {
            JDBC_Member_Fingerprint.ReadFingerprints(idMember).thenAccept(fingerprints -> {
                Platform.runLater(() -> {
                    this.labelFingerprintCounter.setText(fingerprints.getKey().toString());
                    this.arrayFingerprints = fingerprints.getValue();
                    this.buttonRestartCapture.setDisable(!(fingerprints.getKey() > 0));
                });
            });
        }

    }

    // restart
    public void stopReader() {
        Fingerprint_Controller.StopCapture();
        this.buttonStartCapture.setText("Iniciar captura");
    }

    // events
    public void captureEvent() {
        if (this.buttonStartCapture.getText().equals("Iniciar captura")) {
            this.boxFingerprintView.requestFocus();
            Fingerprint_Controller.StartCapture(this.boxFingerprintView);
            this.buttonStartCapture.setText("Detener captura");
        } else {
            Fingerprint_Controller.StartCapture(); // background reader
            this.buttonStartCapture.setText("Iniciar captura");
        }
    }

    public void restartCaptureEvent() {
        this.boxFingerprintPane.requestFocus();
        Fingerprint_Controller.BackgroundReader();
        this.buttonRestartCapture.setText("Iniciar captura");
        this.buttonRestartCapture.setDisable(true);
        this.clearFingerprintPane();
        this.labelFingerprintCounter.setText("0");
        this.arrayFingerprints.clear();
    }

}
