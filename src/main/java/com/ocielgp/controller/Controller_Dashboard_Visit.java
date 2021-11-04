package com.ocielgp.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.ocielgp.app.Application;
import com.ocielgp.app.UserPreferences;
import com.ocielgp.dao.JDBC_Membership;
import com.ocielgp.dao.JDBC_Payment_Visit;
import com.ocielgp.models.Model_Membership;
import com.ocielgp.utilities.Loading;
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
    private VBox boxRoot;
    @FXML
    private FontIcon fontIconClose;
    @FXML
    private JFXComboBox<Model_Membership> comboBoxVisitors;
    @FXML
    private JFXButton buttonRegister;

    private final Stage stage = new Stage(StageStyle.TRANSPARENT);

    public Controller_Dashboard_Visit() {
        this.stage.showingProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                this.stage.setX(
                        Screen.getPrimary().getVisualBounds().getWidth() / 2 - stage.getWidth() / 2
                );
                this.stage.setY(
                        Screen.getPrimary().getVisualBounds().getHeight() / 2 - stage.getHeight() / 2
                );
                this.stage.toFront();
            }
        });
        this.stage.initOwner(Application.STAGE_PRIMARY);
        this.stage.initModality(Modality.APPLICATION_MODAL);
        Application.STAGE_SECONDARY = stage;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Scene scene = new Scene(this.boxRoot);
        scene.getStylesheets().add("styles.css");
        scene.setFill(Color.TRANSPARENT);
        this.boxRoot.getStyleClass().add(UserPreferences.getPreferenceString("THEME"));
        this.stage.setScene(scene);
        this.comboBoxVisitors.itemsProperty().addListener((observableValue, oldValue, newValue) -> {
            Platform.runLater(() -> this.comboBoxVisitors.show());
        });
        JDBC_Membership.ReadMemberships(Model_Membership.VISIT).thenAccept(model_visits -> {
            this.comboBoxVisitors.setItems(model_visits);
            Platform.runLater(() -> this.comboBoxVisitors.requestFocus());
        });
        this.fontIconClose.setOnMouseClicked(mouseEvent -> closeStage());

        this.boxRoot.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                closeStage();
            }
        });

        this.buttonRegister.setOnAction(actionEvent -> {
            if (Validator.emptyValidator(this.comboBoxVisitors)) {
                this.boxRoot.setDisable(true);
                JDBC_Payment_Visit.CreatePaymentVisit(this.comboBoxVisitors.getSelectionModel().getSelectedItem());
                Notifications.Success("Visita", "Visita de $" + this.comboBoxVisitors.getSelectionModel().getSelectedItem().getPrice() + " registrada");
                closeStage();
            }
        });
        Platform.runLater(() -> {
            Application.isAnimationFinished = true;
            Application.isChildLoaded = true;
            Loading.close();
            this.stage.showAndWait();
        });
    }

    private void closeStage() {
        Application.STAGE_PRIMARY = null;
        Platform.runLater(this.stage::close);
    }
}
