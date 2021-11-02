package com.ocielgp.controller;

import animatefx.animation.FadeIn;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.ocielgp.app.Application;
import com.ocielgp.dao.JDBC_Membership;
import com.ocielgp.dao.JDBC_Payment_Visit;
import com.ocielgp.models.Model_Membership;
import com.ocielgp.utilities.InputDetails;
import com.ocielgp.utilities.Notifications;
import com.ocielgp.utilities.Validator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller_Dashboard_Visit implements Initializable {

    @FXML
    private VBox boxVisit;
    @FXML
    private FontIcon fontIconClose;
    @FXML
    private JFXComboBox<Model_Membership> comboBoxVisitors;
    @FXML
    private JFXButton buttonRegister;

    private final Stage stage = new Stage(StageStyle.TRANSPARENT);

    public Controller_Dashboard_Visit() {
        System.out.println("constructor");
    }

    private void configStage() {
        Scene scene = new Scene(this.boxVisit);
        scene.getStylesheets().add("styles.css");
        scene.setFill(Color.TRANSPARENT);
        this.stage.setScene(scene);
        this.stage.showingProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                this.stage.setX(
                        Screen.getPrimary().getVisualBounds().getWidth() / 2 - stage.getWidth() / 2
                );
                this.stage.setY(
                        Screen.getPrimary().getVisualBounds().getHeight() / 2 - stage.getHeight() / 2
                );
                new FadeIn(this.boxVisit).play();
                this.stage.toFront();
            }
        });
        this.stage.initOwner(Application.STAGE_PRIMARY);
        this.stage.initModality(Modality.APPLICATION_MODAL);
        Application.STAGE_SECONDARY = stage;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configStage();
        this.comboBoxVisitors.itemsProperty().addListener((observableValue, oldValue, newValue) -> {
            Platform.runLater(() -> this.comboBoxVisitors.show());
        });
        JDBC_Membership.ReadMemberships(Model_Membership.VISIT).thenAccept(model_visits -> this.comboBoxVisitors.setItems(model_visits));
        this.fontIconClose.setOnMouseClicked(mouseEvent -> closeStage());

        this.boxVisit.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                closeStage();
            }
        });

        this.buttonRegister.setOnAction(actionEvent -> {
            if (Validator.emptyValidator(new InputDetails(this.comboBoxVisitors, String.valueOf(this.comboBoxVisitors.getSelectionModel().getSelectedIndex())))) {
                this.boxVisit.setDisable(true);
                JDBC_Payment_Visit.CreatePaymentVisit(this.comboBoxVisitors.getSelectionModel().getSelectedItem());
                Notifications.Success("Visita", "Visita de $" + this.comboBoxVisitors.getSelectionModel().getSelectedItem().getPrice() + " registrada");
                closeStage();
            }
        });
        Platform.runLater(this.stage::showAndWait);
    }

    private void closeStage() {
        Platform.runLater(this.stage::close);
    }
}
