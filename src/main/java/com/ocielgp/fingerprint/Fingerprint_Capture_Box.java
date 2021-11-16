package com.ocielgp.fingerprint;

import animatefx.animation.Shake;
import com.digitalpersona.uareu.Engine;
import com.digitalpersona.uareu.Fmd;
import com.digitalpersona.uareu.UareUException;
import com.digitalpersona.uareu.UareUGlobal;
import com.jfoenix.controls.JFXButton;
import com.ocielgp.dao.JDBC_Member_Fingerprint;
import com.ocielgp.utilities.InputProperties;
import com.ocielgp.utilities.Notifications;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.lang.invoke.MethodHandles;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.concurrent.CompletableFuture;

public class Fingerprint_Capture_Box {
    // view
    private final VBox boxFingerprintPane;
    private final VBox boxFingerprintView;
    private final JFXButton buttonRestartCapture;

    // attributes
    private LinkedList<Fmd> fingerprintsList = new LinkedList<>();
    private final StringProperty startCaptureProperty = new SimpleStringProperty("Iniciar captura");
    private final IntegerProperty totalFingerprintsProperty = new SimpleIntegerProperty(0);

    public Fingerprint_Capture_Box(VBox boxFingerprintPane, VBox boxFingerprintPreview, Label labelFingerprintCounter, JFXButton buttonStartCapture, JFXButton buttonRestartCapture) {
        this.boxFingerprintPane = boxFingerprintPane;
        this.boxFingerprintView = boxFingerprintPreview;
        labelFingerprintCounter.textProperty().bind(this.totalFingerprintsProperty.asString());
        this.buttonRestartCapture = buttonRestartCapture;

        buttonStartCapture.setOnAction(actionEvent -> eventCapture());
        buttonStartCapture.textProperty().bind(this.startCaptureProperty);
        this.buttonRestartCapture.setOnAction(actionEvent -> initialStatePane(false));

        InputProperties.createVisibleEvent(this.boxFingerprintPane, true);

        Fingerprint_Controller.setFingerprintCaptureBox(this);
    }

    public void show() {
        this.boxFingerprintPane.setVisible(true);
    }

    public void hide() {
        if (this.boxFingerprintPane.isVisible()) {
            this.boxFingerprintPane.setVisible(false);
            this.initialStatePane(true);
        }
    }

    public void getFingerprints(int idMember) {
        JDBC_Member_Fingerprint.ReadFingerprints(idMember).thenAccept(fingerprints -> Platform.runLater(() -> {
            this.fingerprintsList = fingerprints.getValue();
            this.totalFingerprintsProperty.set(fingerprints.getKey());
            this.buttonRestartCapture.setDisable(!(fingerprints.getKey() > 0));
        }));
    }

    public void addFingerprintToList(Fmd fmd) {
        this.fingerprintsList.add(fmd);
        Platform.runLater(() -> {
            this.totalFingerprintsProperty.set(this.fingerprintsList.size());
            this.buttonRestartCapture.setDisable(false);
            cleanFingerprintPane();
        });
    }

    public ListIterator<Fmd> getFingerprintsListIterator() {
        return fingerprintsList.listIterator();
    }

    public VBox getBoxFingerprintView() {
        return boxFingerprintView;
    }

    public void cleanFingerprintPane() {
        if (this.boxFingerprintView.getChildren().size() > 0) {
            ((ImageView) this.boxFingerprintView.getChildren().get(0)).setImage(null);
        }
    }

    public void initialStateStartCapture() {
        Platform.runLater(() -> this.startCaptureProperty.set("Iniciar captura"));
    }

    public void initialStatePane(boolean isNewThread) {
        if (isNewThread)
            if (Fingerprint_Controller.IsConnected()) Fingerprint_Controller.BackgroundReader();
        this.fingerprintsList.clear();
        Platform.runLater(() -> {
            if (isNewThread) this.startCaptureProperty.set("Iniciar captura");
            this.totalFingerprintsProperty.set(0);
            this.boxFingerprintPane.requestFocus();
            this.buttonRestartCapture.setDisable(true);
            cleanFingerprintPane();
        });
    }

    public void shakeFingerprintView() {
        new Shake(this.boxFingerprintView).play();
        cleanFingerprintPane();
    }

    public CompletableFuture<Boolean> compareLocalFingerprints(Fmd fmdMember) {
        return CompletableFuture.supplyAsync(() -> {
            Engine engine = UareUGlobal.GetEngine();
            ListIterator<Fmd> fingerprints = this.fingerprintsList.listIterator();
            while (fingerprints.hasNext()) {
                try {
                    int falseMatchRate = engine.Compare(fmdMember, 0, fingerprints.next(), 0);
                    int targetFalseMatchRate = Engine.PROBABILITY_ONE / 100000; // target rate is 0.00001
                    if (falseMatchRate < targetFalseMatchRate) {
                        Notifications.Warn("Lector de Huellas", "Esa huella ya ha sido agregada", 2);
                        shakeFingerprintView();
                        return false;
                    }
                } catch (UareUException uareUException) {
                    Notifications.CatchException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], uareUException);
                }
            }
            return true;
        });
    }

    public void eventCapture() {
        if (this.startCaptureProperty.get().equals("Iniciar captura")) {
            Fingerprint_Controller.ViewReader();
            Platform.runLater(() -> {
                this.boxFingerprintView.requestFocus();
                this.startCaptureProperty.set("Detener captura");
            });
        } else {
            Fingerprint_Controller.BackgroundReader();
            Platform.runLater(() -> this.startCaptureProperty.set("Iniciar captura"));
        }
    }

}
