package com.ocielgp.controller;

import animatefx.animation.FadeIn;
import animatefx.animation.Flash;
import animatefx.animation.Tada;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.ocielgp.app.Application;
import com.ocielgp.app.UserPreferences;
import com.ocielgp.utilities.Hash;
import com.ocielgp.utilities.Input;
import com.ocielgp.utilities.Loader;
import com.ocielgp.utilities.Notifications;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller_Popup implements Initializable {
    public static final String POPUP_NOTIFY = "NOTIFY";
    public static final String POPUP_CONFIRM = "CONFIRM";
    public static final String POPUP_SECURE_MODE = "SECURE_MODE";

    @FXML
    private VBox popup;
    @FXML
    private AnchorPane boxTitle;
    @FXML
    private Label labelTitle;
    @FXML
    private FontIcon fontIconClose;
    @FXML
    private Label labelContent;
    @FXML
    private AnchorPane boxPassword;
    @FXML
    private JFXTextField fieldPassword;
    @FXML
    private HBox boxButtons;

    private boolean boolAnswer;
    private final Stage stage = new Stage(StageStyle.TRANSPARENT);

    public Controller_Popup() {
        this.popup = (VBox) Loader.Load("popup.fxml", "Controller_Popup", true, this);
        Scene scene = new Scene(this.popup);
        scene.setFill(Color.TRANSPARENT);
        this.stage.setScene(scene);
        this.stage.initOwner(Application.getPrimaryStage());
        this.stage.initModality(Modality.APPLICATION_MODAL);
        this.stage.toFront();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.popup.getStylesheets().add("styles.css");
        this.popup.getStyleClass().add(UserPreferences.getPreferenceString("THEME"));
    }

    public void fillView(String style, String title, String content, String popupType) {
        this.boxTitle.getStyleClass().add(style);
        this.labelTitle.setText(title);
        this.labelContent.setText(content);

        JFXButton buttonPrimary = new JFXButton("Aceptar");
        buttonPrimary.getStyleClass().addAll("btn-colorful", style);
//        popup.getStyleClass().forEach(System.out::println);
        this.boxButtons.getChildren().setAll(buttonPrimary);
        if (!popupType.equals(POPUP_NOTIFY)) {
            JFXButton buttonSecondary = new JFXButton("Cancelar");
            buttonSecondary.getStyleClass().addAll("btn-secondary");
            buttonSecondary.setOnAction(actionEvent -> this.eventCancel());
            this.boxButtons.getChildren().add(buttonSecondary);

            if (popupType.equals(POPUP_SECURE_MODE)) {
                Platform.runLater(this.fieldPassword::requestFocus);
                this.fieldPassword.setOnKeyPressed(keyEvent -> {
                    if (keyEvent.getCode() == KeyCode.ENTER) eventSecureMode();
                });
                buttonPrimary.setOnAction(actionEvent -> this.eventSecureMode());
            } else {
                buttonPrimary.setOnAction(actionEvent -> this.eventConfirm());
                Input.createVisibleProperty(this.boxPassword, false);
                Platform.runLater(buttonPrimary::requestFocus);
            }
        }
        this.fontIconClose.setOnMouseClicked(mouseEvent -> this.eventCancel());
    }

    private void closeStage() {
        Platform.runLater(this.stage::close);
    }

    public boolean showAndWait() {
        this.stage.showingProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {

                System.out.println(this.popup.getHeight());
                new FadeIn(this.popup).play();
            }
        });
        this.stage.showAndWait();
        return this.boolAnswer;
    }

    private void eventConfirm() {
        this.boolAnswer = true;
        closeStage();
    }

    private void eventCancel() {
        this.boolAnswer = false;
        closeStage();
    }

    private int attempts = 0;

    private void eventSecureMode() { // compare input password to admin stored password
        if (Hash.generateHash(this.fieldPassword.getText()).equals(Application.getStaffUserModel().getModelStaffMember().getPassword())) {
            Application.setSecureMode(false);
            this.boolAnswer = true;
            closeStage();
        } else {
            this.boolAnswer = false;
            attempts++;
            this.fieldPassword.requestFocus();
            if (attempts == 3) {
                Notifications.danger("Contraseña incorrecta (" + attempts + " / 3)", "Aplicación bloqueada");
                this.popup.setDisable(true);
                Flash applicationBlockedAnimation = new Flash(this.popup);
                applicationBlockedAnimation.setCycleCount(Timeline.INDEFINITE);
                applicationBlockedAnimation.play();
            } else {
                Notifications.danger("Contraseña incorrecta (" + attempts + " / 3)", "Vuelve a intentarlo");
                Platform.runLater(this.fieldPassword::requestFocus);
            }
        }
    }
}
