package com.ocielgp.fingerprint;

import com.digitalpersona.uareu.Fmd;
import com.jfoenix.controls.JFXButton;
import com.ocielgp.database.members.DATA_MEMBERS_FINGERPRINTS;
import com.ocielgp.utilities.Input;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.concurrent.CompletableFuture;

public class FingerprintUI {
    private final VBox fingerprintPane;
    private VBox fingerprintFmd;
    private final Label fingerprintCounter;
    private JFXButton startCaptureButton;
    private final JFXButton restartCaptureButton;
    public ArrayList<Fmd> fmds;

    private final EventHandler<ActionEvent> captureEvent = actionEvent -> {
        if (this.startCaptureButton.getText().equals("Iniciar captura")) {
            this.fingerprintFmd.requestFocus();
            Fingerprint.StartCapture(this.fingerprintFmd);
            this.startCaptureButton.setText("Detener captura");
        } else {
            Fingerprint.StartCapture(); // Background reader
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
        Input.createVisibleProperty(this.fingerprintPane, true);

        this.startCaptureButton.setOnAction(captureEvent);
        this.restartCaptureButton.setOnAction(actionEvent -> {
            if (this.fingerprintFmd.getChildren().size() > 0) {
                ((ImageView) this.fingerprintFmd.getChildren().get(0)).setImage(null);
            }
            this.clearFingerprints();
            this.restartCaptureButton.setDisable(true);
            this.fingerprintFmd.requestFocus();
        });
    }

    public ArrayList<Fmd> getFmds() {
        return fmds;
    }

    public void clearFingerprints() {
        Platform.runLater(() -> {
            this.fmds.clear();
            this.fingerprintCounter.setText("0");
        });
    }

    private void enableRestartButton() {
        Platform.runLater(() -> {
            this.restartCaptureButton.setDisable(false);
        });
    }

    public void resetUI() {
        Platform.runLater(() -> {
            if (this.fingerprintFmd.getChildren().size() > 0) {
                ((ImageView) this.fingerprintFmd.getChildren().get(0)).setImage(null);
            }
            if (this.fmds.size() == 0) {
                this.restartCaptureButton.setDisable(true);
            }
            this.startCaptureButton.setText("Iniciar captura");
            this.startCaptureButton.setOnAction(captureEvent);
        });
    }

    public void restartUI() {
        Platform.runLater(() -> {
            if (this.fingerprintFmd.getChildren().size() > 0) {
                ((ImageView) this.fingerprintFmd.getChildren().get(0)).setImage(null);
            }
            this.clearFingerprints();
            this.restartCaptureButton.setDisable(true);
            this.startCaptureButton.setText("Iniciar captura");
            this.startCaptureButton.setOnAction(captureEvent);
        });
    }

    public void show() {
        Platform.runLater(() -> {
            this.fingerprintPane.setVisible(true);
        });
    }

    public void hide() {
        Platform.runLater(() -> {
            this.fingerprintPane.setVisible(false);
        });
    }

    public void add(Fmd fmd) {
        CompletableFuture.runAsync(() -> {
            this.fmds.add(fmd);
            Platform.runLater(() -> {
                this.fingerprintCounter.setText(String.valueOf(fmds.size()));
                this.enableRestartButton();
            });
        });
    }

    public ListIterator<Fmd> getFingerprints() {
        return (this.fmds.size() == 0) ? null : this.fmds.listIterator();
    }

    public void loadFingerprints(int idMember) {
        CompletableFuture.runAsync(() -> {
            if (this.fingerprintPane.isVisible()) {
                DATA_MEMBERS_FINGERPRINTS.ReadFingerprints(idMember).thenAccept(fingerprints -> {
                    Platform.runLater(() -> {
                        this.fingerprintCounter.setText(fingerprints.getKey().toString());
                        this.fmds = fingerprints.getValue();
                        this.restartCaptureButton.setDisable(!(fingerprints.getKey() > 0));
                    });
                });
            }
        });
    }

}
