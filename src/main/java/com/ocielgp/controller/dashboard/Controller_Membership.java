package com.ocielgp.controller.dashboard;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import com.ocielgp.dao.JDBC_Membership;
import com.ocielgp.models.Model_Membership;
import com.ocielgp.utilities.DateTime;
import com.ocielgp.utilities.Input;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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
        });
        this.comboBoxMemberships.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue) {
                this.comboBoxMemberships.show();
            }
        });
        this.comboBoxMemberships.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue != null) {
                this.labelModified.setText(DateTime.getDateWithDayName(newValue.getDateTime()));
                this.labelAdmin.setText(String.valueOf(newValue.getIdAdmin()));
                this.boxHistorical.setVisible(true);
                this.boxMembershipDetail.setVisible(true);
                this.fieldName.setText(newValue.getName());
                this.fieldPrice.setText(newValue.getPrice().toString());
                this.toggleMonthly.setSelected((newValue.getType() == 1));
                this.boxEndButtons.setVisible(true);
            }
        });

        this.scrollPane.getStylesheets().add("styles.css");
        Input.createVisibleAnimation(this.boxMemberships, false);
        Input.createVisibleAnimation(this.boxHistorical, false);
        Input.createVisibleAnimation(this.boxMembershipDetail, false);
        Input.createVisibleAnimation(this.boxEndButtons, false);


        this.buttonCreate.setOnAction(actionEvent -> eventCreate());
        this.buttonEdit.setOnAction(actionEvent -> eventEdit());
    }

    private void eventCreate() {
        restartForm();
        this.boxMembershipDetail.setVisible(true);
        this.boxEndButtons.setVisible(true);
    }

    private void eventEdit() {
        restartForm();
        this.boxMemberships.setVisible(true);
        this.boxMemberships.requestFocus();
    }

    private void restartForm() {
        this.boxMemberships.setVisible(false);
        this.comboBoxMemberships.getSelectionModel().select(-1);
        this.boxHistorical.setVisible(false);
        this.boxMembershipDetail.setVisible(false);
        this.fieldName.setText("");
        this.fieldPrice.setText("");
        this.toggleMonthly.setSelected(true);
        this.boxEndButtons.setVisible(false);
    }
}
