package com.ocielgp.controller;

import animatefx.animation.BounceIn;
import animatefx.animation.FadeIn;
import animatefx.animation.FadeInUp;
import animatefx.animation.Pulse;
import com.jfoenix.controls.JFXButton;
import com.ocielgp.app.Application;
import com.ocielgp.app.UserPreferences;
import com.ocielgp.utilities.Loader;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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
import java.util.concurrent.CompletableFuture;

public class Controller_Popup implements Initializable {
    public static final String POPUP_NOTIFY = "NOTIFY";
    public static final String POPUP_CONFIRM = "CONFIRM";

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
    private HBox boxButtons;

    private boolean boolAnswer;
    private final Stage stage = new Stage(StageStyle.UNDECORATED);

    public Controller_Popup() {
        this.popup = (VBox) Loader.Load("popup.fxml", "Controller_Popup", true, this);
        Scene scene = new Scene(this.popup);
        scene.setFill(Color.TRANSPARENT);
        this.stage.setScene(scene);
        this.stage.initOwner(Application.getPrimaryStage());
        this.stage.initModality(Modality.APPLICATION_MODAL);
        this.stage.setAlwaysOnTop(true);
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
        this.boxButtons.getChildren().setAll(buttonPrimary);
        buttonPrimary.setOnAction(actionEvent -> this.eventConfirm());
        if (popupType.equals(POPUP_CONFIRM)) {
            JFXButton buttonSecondary = new JFXButton("Cancelar");
            buttonSecondary.getStyleClass().addAll("btn-secondary");
            buttonSecondary.setOnAction(actionEvent -> this.eventCancel());
            this.boxButtons.getChildren().add(buttonSecondary);
        }
        this.fontIconClose.setOnMouseClicked(mouseEvent -> this.eventCancel());
    }

    public boolean showAndWait() {
        this.stage.showingProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                System.out.println(this.popup.getWidth());
                new FadeIn(this.popup).play();
            }
        });
        this.stage.showAndWait();
        return this.boolAnswer;
    }

    private void eventConfirm() {
        this.boolAnswer = true;
        Platform.runLater(this.stage::close);
    }

    private void eventCancel() {
        this.boolAnswer = false;
        Platform.runLater(this.stage::close);
    }
}
