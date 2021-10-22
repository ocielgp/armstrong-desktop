package com.ocielgp.controller;

import animatefx.animation.FadeIn;
import animatefx.animation.Flash;
import animatefx.animation.Shake;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.ocielgp.app.Application;
import com.ocielgp.app.UserPreferences;
import com.ocielgp.utilities.Hash;
import com.ocielgp.utilities.Input;
import com.ocielgp.utilities.Loader;
import com.ocielgp.utilities.Notifications;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
import javafx.stage.Screen;
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
    private VBox boxPopup;
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
    private JFXPasswordField fieldPassword;
    @FXML
    private HBox boxButtons;

    // attributes
    private final String style;
    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty content = new SimpleStringProperty();
    private final String popupType;

    private final Stage stage = new Stage(StageStyle.TRANSPARENT);
    private boolean boolAnswer = false;
    private int secureModeAttempts = 0;

    public Controller_Popup(String style, String title, String content, String popupType) {
        this.style = style;
        this.title.set(title);
        this.content.set(content);
        this.popupType = popupType;

        this.boxPopup = (VBox) Loader.Load("popup.fxml", "Controller_Popup", true, this);
        Scene scene = new Scene(this.boxPopup);
        scene.getStylesheets().add("styles.css");
        scene.setFill(Color.TRANSPARENT);
        this.stage.setScene(scene);
        this.stage.initOwner(Application.STAGE_PRIMARY);
        this.stage.initModality(Modality.APPLICATION_MODAL);
        this.stage.toFront();
        Application.STAGE_POPUP = stage;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.boxPopup.getStyleClass().addAll(UserPreferences.getPreferenceString("THEME"));
        this.boxTitle.getStyleClass().add(this.style);
        this.labelTitle.textProperty().bind(this.title);
        this.labelContent.textProperty().bind(this.content);

        JFXButton buttonPrimary = new JFXButton("Aceptar");
        buttonPrimary.getStyleClass().addAll("btn-colorful", style);
        this.boxPopup.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                this.boolAnswer = false;
                closeStage();
            }
        });
        this.fontIconClose.setOnMouseClicked(mouseEvent -> this.eventCancel());
        this.boxButtons.getChildren().setAll(buttonPrimary);
        if (popupType.equals(POPUP_CONFIRM)) {
            popupConfirm();
            buttonPrimary.setOnAction(actionEvent -> eventConfirm());
            Platform.runLater(buttonPrimary::requestFocus);
        } else if (popupType.equals(POPUP_SECURE_MODE)) {
            popUpSecureMode();
            buttonPrimary.setOnAction(actionEvent -> eventSecureMode());
        }
    }

    private void popupConfirm() {
        JFXButton buttonSecondary = new JFXButton("Cancelar");
        buttonSecondary.getStyleClass().addAll("btn-secondary");
        buttonSecondary.setOnAction(actionEvent -> this.eventCancel());
        this.boxButtons.getChildren().add(buttonSecondary);
        Input.createVisibleProperty(this.boxPassword, false);
    }

    private void popUpSecureMode() {
        this.boxPopup.setOnKeyPressed(null);
        this.fieldPassword.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) eventSecureMode();
        });
        Input.createVisibleProperty(this.fontIconClose, false);
        Platform.runLater(this.fieldPassword::requestFocus);
    }

    public boolean showAndWait() {
        this.stage.showingProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                stage.setX(
                        Screen.getPrimary().getVisualBounds().getWidth() / 2 - stage.getWidth() / 2
                );
                stage.setY(
                        Screen.getPrimary().getVisualBounds().getHeight() / 2 - stage.getHeight() / 2
                );
                new FadeIn(this.boxPopup).play();
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

    private void eventSecureMode() { // compare input password to admin stored password
        if (Hash.generateHash(this.fieldPassword.getText()).equals(Application.getModelAdmin().getModelStaffMember().getPassword())) {
            this.boolAnswer = true;
            closeStage();
        } else {
            this.boolAnswer = false;
            secureModeAttempts++;
            this.fieldPassword.requestFocus();
            if (secureModeAttempts == 3) {
                Notifications.Danger("Modo Seguro", "Intentos excedidos", 60);
                this.boxPopup.setDisable(true);
                Flash applicationBlockedAnimation = new Flash(this.boxPopup);
                applicationBlockedAnimation.setCycleCount(Timeline.INDEFINITE);
                applicationBlockedAnimation.play();
            } else {
                Platform.runLater(() -> new Shake(this.fieldPassword).play());
                Platform.runLater(this.fieldPassword::requestFocus);
            }
        }
    }

    private void closeStage() {
        Platform.runLater(this.stage::close);
    }
}
