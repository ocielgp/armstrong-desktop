package com.ocielgp.controller;

import animatefx.animation.FadeIn;
import animatefx.animation.Shake;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.ocielgp.app.Application;
import com.ocielgp.app.UserPreferences;
import com.ocielgp.utilities.Hash;
import com.ocielgp.utilities.InputProperties;
import com.ocielgp.utilities.Loader;
import com.ocielgp.utilities.Styles;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
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

public class Popup implements Initializable {
    private static final String POPUP_ALERT = "ALERT";
    private static final String POPUP_CONFIRM = "CONFIRM";
    private static final String POPUP_PASSWORD = "PASSWORD";

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
    private String style;
    private String title;
    private String body;
    private String popupType;

    private final Stage stage = new Stage(StageStyle.TRANSPARENT);
    private boolean boolAnswer = false;

    public Popup() {
        this.stage.showingProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                this.stage.setX(
                        Screen.getPrimary().getVisualBounds().getWidth() / 2 - stage.getWidth() / 2
                );
                this.stage.setY(
                        Screen.getPrimary().getVisualBounds().getHeight() / 2 - stage.getHeight() / 2
                );
                new FadeIn(this.boxPopup).play();
                this.stage.toFront();
            }
        });
        this.stage.initOwner(Application.STAGE_PRIMARY);
        this.stage.initModality(Modality.APPLICATION_MODAL);
        Application.STAGE_POPUP = stage;
    }

    private void createPopup(String style, String title, String body, String popupType) {
        this.style = style;
        this.title = title.toUpperCase();
        this.body = body;
        this.popupType = popupType;

        this.boxPopup = (VBox) Loader.Load("popup.fxml", "Popup", true, this);
        Scene scene = new Scene(this.boxPopup);
        scene.getStylesheets().add("styles.css");
        scene.setFill(Color.TRANSPARENT);
        this.stage.setScene(scene);
    }

    public void alert(String style, String title, String content) {
        createPopup(style, title, content, POPUP_ALERT);
    }

    public void confirm(String style, String title, String content) {
        createPopup(style, title, content, POPUP_CONFIRM);
    }

    public void password() {
        createPopup(Styles.WARN, "Contraseña", "Esta acción necesita tu contraseña", POPUP_PASSWORD);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.boxPopup.getStyleClass().addAll(UserPreferences.GetPreferenceString("THEME"));
        this.boxTitle.getStyleClass().add(this.style);
        this.labelTitle.textProperty().bind(new SimpleStringProperty(title));
        this.labelContent.textProperty().bind(new SimpleStringProperty(body));

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
        InputProperties.createVisibleEvent(this.boxPassword, false);
        if (this.popupType.equals(POPUP_ALERT)) {
            buttonPrimary.setOnAction(actionEvent -> eventConfirm());
        } else {
            JFXButton buttonSecondary = new JFXButton("Cancelar");
            buttonSecondary.getStyleClass().addAll("btn-secondary");
            buttonSecondary.setOnAction(actionEvent -> this.eventCancel());
            this.boxButtons.getChildren().add(buttonSecondary);

            if (this.popupType.equals(POPUP_CONFIRM)) {
                buttonPrimary.setOnAction(actionEvent -> eventConfirm());
                Platform.runLater(buttonPrimary::requestFocus);
            } else if (this.popupType.equals(POPUP_PASSWORD)) {
                popupPassword();
                buttonPrimary.setOnAction(actionEvent -> eventPassword());
            }
        }
    }

    private void popupPassword() {
        this.boxPassword.setVisible(true);
        this.fieldPassword.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) eventPassword();
        });
        Platform.runLater(this.fieldPassword::requestFocus);
    }

    public boolean showAndWait() {
        this.stage.showAndWait();
        return this.boolAnswer;
    }

    private void eventConfirm() {
        this.boolAnswer = true;
        closeStage();
    }

    private void eventPassword() { // compare input password to admin stored password
        if (Hash.generateHash(this.fieldPassword.getText()).equals(Application.GetModelAdmin().getPassword())) {
            this.boolAnswer = true;
            closeStage();
        } else {
            this.boolAnswer = false;
            this.fieldPassword.requestFocus();
            Platform.runLater(() -> new Shake(this.fieldPassword).play());
        }
    }

    private void eventCancel() {
        this.boolAnswer = false;
        closeStage();
    }

    private void closeStage() {
        Platform.runLater(this.stage::close);
    }
}
