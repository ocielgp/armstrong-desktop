package com.ocielgp.controller.dashboard;

import animatefx.animation.FadeInRight;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import com.ocielgp.app.Application;
import com.ocielgp.dao.JDBC_Member;
import com.ocielgp.dao.JDBC_Membership;
import com.ocielgp.models.Model_Membership;
import com.ocielgp.utilities.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller_Membership implements Initializable {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox boxRoot;

    @FXML
    private VBox boxMemberships;
    @FXML
    private JFXComboBox<Model_Membership> comboBoxMemberships;

    @FXML
    private VBox boxHistorical;
    @FXML
    private Label labelModified;
    @FXML
    private Label labelAdmin;

    @FXML
    private VBox boxMembershipDetail;
    @FXML
    private JFXTextField fieldName;
    @FXML
    private JFXTextField fieldPrice;
    @FXML
    private JFXToggleButton toggleMonthly;
    @FXML
    private JFXButton buttonDelete;

    @FXML
    private HBox boxEndButtons;
    @FXML
    private JFXButton buttonSave;
    @FXML
    private JFXButton buttonClear;

    @FXML
    private JFXButton buttonCreate;
    @FXML
    private JFXButton buttonEdit;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Input.getScrollEvent(this.scrollPane);
        JDBC_Membership.ReadMemberships(Model_Membership.ALL).thenAccept(model_memberships -> {
            if (model_memberships.isEmpty()) this.buttonEdit.setDisable(true);
            else this.comboBoxMemberships.setItems(model_memberships);
            Application.isChildLoaded = true;
            Loading.close();
        });
        this.comboBoxMemberships.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue && !this.comboBoxMemberships.isShowing()) {
                this.comboBoxMemberships.show();
            }
        });
        this.comboBoxMemberships.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue != null) fillData(newValue);
        });

        Input.createVisibleAnimation(this.boxMemberships, false);
        Input.createVisibleAnimation(this.boxHistorical, false);
        Input.createVisibleAnimation(this.boxMembershipDetail, false);
        Input.createVisibleAnimation(this.buttonDelete, false);
        Input.createVisibleAnimation(this.boxEndButtons, false);


        this.buttonCreate.setOnAction(actionEvent -> eventCreate());
        this.buttonEdit.setOnAction(actionEvent -> eventEdit());
        this.buttonSave.setOnAction(actionEvent -> eventSave());
        this.buttonClear.setOnAction(actionEvent -> clearForm(true));
    }

    private void fillData(Model_Membership modelMembership) {
        Loading.show();
        JDBC_Member.ReadMember(modelMembership.getIdAdmin()).thenAccept(model_member -> {
            Platform.runLater(() -> {
                this.labelModified.setText(DateTime.getDateWithDayName(modelMembership.getDateTime()));
                this.labelAdmin.setText(model_member.getName() + " " + model_member.getLastName());
                this.boxHistorical.setVisible(true);
                this.boxMembershipDetail.setVisible(true);
                this.fieldName.setText(modelMembership.getName());
                this.fieldPrice.setText(modelMembership.getPrice().toString());
                this.toggleMonthly.setSelected((modelMembership.isMonthly()));
                this.boxEndButtons.setVisible(true);
            });
            Loading.closeNow();
        });
    }

    private void clearForm(boolean animation) {
        this.boxMemberships.setVisible(false);
        this.comboBoxMemberships.setValue(null);
        this.boxHistorical.setVisible(false);
        this.boxMembershipDetail.setVisible(false);
        this.fieldName.setText("");
        this.fieldPrice.setText("");
        this.toggleMonthly.setSelected(true);
        this.buttonDelete.setVisible(false);
        this.boxEndButtons.setVisible(false);

        if (animation) {
            new FadeInRight(this.scrollPane).play();
        }
    }

    private void eventSave() {
        if (this.comboBoxMemberships.getSelectionModel().getSelectedIndex() == -1) { // create membership
            if (Validator.emptyValidator(this.fieldName, this.fieldPrice) && Validator.moneyValidator(this.fieldPrice, true)) {
                createMembership();
            }
        } else { // save membership

        }
    }

    private void createMembership() {
        Model_Membership modelMembership = new Model_Membership();
        modelMembership.setName(this.fieldName.getText());
        modelMembership.setPrice(new BigDecimal(this.fieldPrice.getText()));
        modelMembership.setMonthly(this.toggleMonthly.isSelected());
        int idMembership = JDBC_Membership.CreateMembership(modelMembership);
        if (idMembership > 0) {
            Notifications.Success("Membresía", "La membresía ha sido creada");
            clearForm(true);
        }
    }

    private void eventCreate() {
        clearForm(false);
        this.boxMembershipDetail.setVisible(true);
        this.boxEndButtons.setVisible(true);
    }

    private void eventEdit() {
        clearForm(false);
        this.boxMemberships.setVisible(true);
        this.boxMemberships.requestFocus();
        this.buttonDelete.setVisible(true);
    }
}
