package com.ocielgp.controller;

import animatefx.animation.FadeIn;
import animatefx.animation.Shake;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.ocielgp.app.Application;
import com.ocielgp.app.UserPreferences;
import com.ocielgp.dao.JDBC_Debt;
import com.ocielgp.models.Model_Debt;
import com.ocielgp.utilities.*;
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

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

public class Popup implements Initializable {
    private static final String POPUP_ALERT = "ALERT";
    private static final String POPUP_CONFIRM = "CONFIRM";
    private static final String POPUP_DEBT = "DEBT";
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
    private AnchorPane boxField;
    @FXML
    private JFXTextField fieldText;
    @FXML
    private HBox boxButtons;

    // attributes
    private String style;
    private String title;
    private String body;
    private String popupType;


    private final Stage stage = new Stage(StageStyle.TRANSPARENT);
    private boolean boolAnswer = false;
    private Model_Debt modelDebt;

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

    public void debt(int idMember) {
        this.modelDebt = JDBC_Debt.ReadDebt(idMember);
        createPopup(Styles.CREATIVE, "Abonar mensualidad", "Adeudo: " + this.modelDebt.getOwe().toString(), POPUP_DEBT);
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
        InputProperties.createVisibleEvent(this.boxField, false);
        if (this.popupType.equals(POPUP_ALERT)) {
            buttonPrimary.setOnAction(actionEvent -> eventConfirm());
        } else {
            JFXButton buttonSecondary = new JFXButton("Cancelar");
            buttonSecondary.getStyleClass().addAll("btn-secondary");
            buttonSecondary.setOnAction(actionEvent -> this.eventCancel());
            this.boxButtons.getChildren().add(buttonSecondary);

            switch (this.popupType) {
                case POPUP_CONFIRM -> {
                    buttonPrimary.setOnAction(actionEvent -> eventConfirm());
                    Platform.runLater(buttonPrimary::requestFocus);
                }
                case POPUP_DEBT -> {
                    popupDebt();
                    buttonPrimary.setOnAction(actionEvent -> eventDebt());
                }
                case POPUP_PASSWORD -> {
                    popupPassword();
                    buttonPrimary.setOnAction(actionEvent -> eventPassword());
                }
            }
        }
    }

    private void popupDebt() {
        this.boxField.setVisible(true);
        this.fieldText.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) eventDebt();
        });
        Platform.runLater(this.fieldText::requestFocus);
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

    private void eventDebt() {
        if (Validator.moneyValidator(this.fieldText, true)) {
            BigDecimal paidOut = new BigDecimal(this.fieldText.getText());
            BigDecimal newOwe = this.modelDebt.getOwe().subtract(paidOut);
            if (paidOut.compareTo(BigDecimal.ZERO) >= 0) {
                this.modelDebt.setOwe(newOwe);
                this.modelDebt.setPaidOut(paidOut);
                this.boolAnswer = JDBC_Debt.UpdateDebt(this.modelDebt);

                if (newOwe.compareTo(BigDecimal.ZERO) == 0) {
                    Notifications.BuildNotification("gmi-payment", "Deuda", "Deuda liquidada", 3, Styles.SUCCESS);
                } else {
                    Notifications.BuildNotification("gmi-payment", "Deuda", "Deuda pendiente de $ " + newOwe, 3, Styles.CREATIVE);
                }
                closeStage();
            } else {
                new Shake(this.fieldText).play();
            }
        }
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
