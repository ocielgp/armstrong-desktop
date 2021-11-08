package com.ocielgp.controller.dashboard;

import animatefx.animation.FadeInRight;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import com.ocielgp.controller.Popup;
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

    // attributes
    private FormChangeListener formChangeListener;
    private Model_Membership modelMembership = new Model_Membership();

    private void configureForm() {
        createFormChangeListener();
        InputProperties.getScrollEvent(this.scrollPane);

        InputProperties.createVisibleAnimation(this.boxMemberships, false);
        InputProperties.createVisibleAnimation(this.boxHistorical, false);
        InputProperties.createVisibleAnimation(this.boxMembershipDetail, false);
        InputProperties.createVisibleAnimation(this.buttonDelete, false);
        InputProperties.createVisibleAnimation(this.boxEndButtons, false);

        // properties binding
        this.comboBoxMemberships.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue != null) {
                this.modelMembership = newValue;
                fillMembershipData(newValue);
            }
        });
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configureForm();

        JDBC_Membership.ReadMemberships(Model_Membership.ALL).thenAccept(model_memberships -> {
            if (model_memberships.isEmpty()) this.buttonEdit.setDisable(true);
            else this.comboBoxMemberships.setItems(model_memberships);
            Loading.isChildLoaded.set(true);
        });

        // event handlers
        this.buttonCreate.setOnAction(actionEvent -> eventCreate());
        this.buttonEdit.setOnAction(actionEvent -> eventEdit());

        this.buttonDelete.setDisable(true);

        // end buttons
        this.buttonSave.setOnAction(actionEvent -> eventSave());
        this.buttonClear.setOnAction(actionEvent -> clearForm(true));
    }

    private void fillMembershipData(Model_Membership modelMembership) {
        JDBC_Member.ReadMember(modelMembership.getIdAdmin()).thenAccept(model_member -> Platform.runLater(() -> {
            this.labelModified.setText(DateTime.getDateWithDayName(modelMembership.getDateTime()));
            this.labelAdmin.setText(model_member.getName() + " " + model_member.getLastName());
            this.boxHistorical.setVisible(true);

            this.fieldName.setText(modelMembership.getName());
            this.fieldPrice.setText(modelMembership.getPrice().toString());
            this.toggleMonthly.setSelected((modelMembership.getMonthly()));
            this.boxMembershipDetail.setVisible(true);

            this.formChangeListener.setListen(true);

            this.buttonSave.setDisable(true);
            this.boxEndButtons.setVisible(true);
        }));
    }

    private void clearForm(boolean animation) {
        this.formChangeListener.setListen(false);

        this.boxMemberships.setVisible(false);
        this.comboBoxMemberships.setValue(null);
        this.boxHistorical.setVisible(false);

        this.boxMembershipDetail.setVisible(false);
        this.fieldName.setText("");
        this.fieldPrice.setText("");
        this.toggleMonthly.setSelected(true);

        this.buttonDelete.setVisible(false);
        this.buttonSave.setDisable(false);
        this.boxEndButtons.setVisible(false);

        if (animation) {
            new FadeInRight(this.scrollPane).play();
        }
    }

    private void eventCreate() {
        clearForm(false);
        this.boxMembershipDetail.setVisible(true);

        this.boxEndButtons.setVisible(true);
        this.buttonSave.setText("Crear");
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

    private void eventEdit() {
        clearForm(false);
        this.boxMemberships.setVisible(true);
        this.boxMemberships.requestFocus();
        this.buttonDelete.setVisible(true);
        this.buttonSave.setText("Guardar");
    }

    private void eventSave() {
        if (Validator.emptyValidator(this.fieldName, this.fieldPrice) && Validator.moneyValidator(this.fieldPrice, true)) {
            if (this.comboBoxMemberships.getSelectionModel().getSelectedIndex() == -1) { // create membership
                createMembership();
            } else { // save membership
                saveMembership();
            }
        }
    }

    private void eventDelete() {
        Popup popup = new Popup();
        popup.password();
        if (popup.showAndWait()) {
            this.formChangeListener.change("membershipDelete", false);
        }
    }

    private void saveMembership() {
        Loading.show();
        if (this.formChangeListener.isListen()) {
            Model_Membership newModelMembership = new Model_Membership();
            newModelMembership.setIdMembership(this.modelMembership.getIdMembership());
            if (this.formChangeListener.isChanged("membershipDelete")) {
                boolean isOk = JDBC_Membership.DeleteMembership(this.modelMembership.getIdMembership());
                if (isOk) {
                    Loading.closeNow();
                    clearForm(true);
                    Notifications.Warn("Membresías", "Membresía eliminada");
                }
            } else {
                if (this.formChangeListener.isChanged("name")) {
                    newModelMembership.setName(this.fieldName.getText());
                }
                if (this.formChangeListener.isChanged("price")) {
                    newModelMembership.setPrice(new BigDecimal(this.fieldPrice.getText()));
                }
                if (this.formChangeListener.isChanged("monthly")) {
                    newModelMembership.setMonthly(this.toggleMonthly.isSelected());
                }
                boolean isOk = JDBC_Membership.UpdateMembership(newModelMembership);
                if (isOk) {
                    JDBC_Membership.ReadMemberships(Model_Membership.ALL).thenAccept(model_memberships -> {
                        Platform.runLater(() -> {
                            Platform.runLater(() -> this.comboBoxMemberships.setItems(model_memberships));
                            Loading.closeNow();
                            clearForm(true);
                        });
                        Notifications.Success("Membresía", "Nuevos cambios aplicados");
                    });
                }
            }
        }
    }

    private void createFormChangeListener() {
        this.formChangeListener = new FormChangeListener(this.buttonSave);
        this.formChangeListener.add("name");
        this.formChangeListener.add("price");
        this.formChangeListener.add("monthly");
        this.formChangeListener.add("membershipDelete");
        this.fieldName.setOnKeyTyped(keyEvent -> {
            if (this.formChangeListener.isListen()) {
                this.formChangeListener.change(
                        "name",
                        Validator.compare(this.fieldName.getText(), this.modelMembership.getName())
                );
            }
        });
        this.fieldPrice.setOnKeyTyped(keyEvent -> {
            if (this.formChangeListener.isListen()) {
                this.formChangeListener.change(
                        "price",
                        Validator.compare(this.fieldPrice.getText(), this.modelMembership.getPrice().toString())
                );
            }
        });
        this.toggleMonthly.setOnAction(keyEvent -> {
            if (this.formChangeListener.isListen()) {
                this.formChangeListener.change(
                        "monthly",
                        Validator.compare(String.valueOf(this.toggleMonthly.isSelected()), String.valueOf(this.modelMembership.getMonthly()))
                );
            }
        });
    }
}
