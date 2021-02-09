package com.ocielgp.controller;

import com.jfoenix.controls.*;
import com.ocielgp.database.SocioData;
import com.ocielgp.model.SociosPlanesModel;
import com.ocielgp.utilities.Input;
import com.ocielgp.utilities.InputDetails;
import com.ocielgp.utilities.NotificationHandler;
import com.ocielgp.utilities.Validator;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class AddMemberController implements Initializable {
    // Containers
    @FXML
    private VBox addMemberPane;
    @FXML
    private VBox fingerprintPane;

    // Controls
    // -> Photo section [ph]
    @FXML
    private ImageView ph_imgMemberPhoto;
    @FXML
    private JFXButton ph_buttonTakePhoto;
    @FXML
    private JFXButton ph_buttonUploadPhoto;

    // -> Personal information section [pi]
    @FXML
    private JFXTextField pi_fieldNames;
    @FXML
    private JFXTextField pi_fieldLastName;
    @FXML
    private JFXTextField pi_fieldPhone;
    @FXML
    private JFXTextField pi_fieldEmail;
    @FXML
    private JFXTextField pi_fieldNotes;

    // -> Fingerprint section [fp]
    @FXML
    private ImageView fp_imgFingerprint;
    @FXML
    private Label fp_labelFingerprintCounter;
    @FXML
    private JFXButton fp_buttonCapture;
    @FXML
    private JFXButton fp_buttonRestartCapture;

    // -> Subscription section [sb]
    @FXML
    private JFXToggleButton sb_togglePersonalized;
    @FXML
    private JFXComboBox<SociosPlanesModel> sb_comboBoxSubscriptions;
    @FXML
    private JFXDatePicker sb_datePicker;
    @FXML
    private JFXTextField sb_fieldPrice;
    @FXML
    private JFXTextField sb_fieldPriceNotes;
    @FXML
    private VBox sb_boxPersonalized;

    // -> Payment section [pym]
    @FXML
    private JFXToggleButton pym_togglePayment;
    @FXML
    private VBox pym_boxDebt;
    @FXML
    private JFXTextField pym_fieldDebt;
    @FXML
    private JFXTextField pym_fieldDebtNotes;

    // -> End buttons
    @FXML
    private JFXButton buttonRegister;
    @FXML
    private JFXButton buttonCancel;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.sb_datePicker.focusedProperty().addListener((observableValue, oldValue, newValue) -> this.sb_datePicker.getStyleClass().remove("red-border-input-line"));
        this.sb_comboBoxSubscriptions.focusedProperty().addListener((observableValue, oldValue, newValue) -> this.sb_comboBoxSubscriptions.getStyleClass().remove("red-border-input-line"));

        EventHandler<ActionEvent> registerEvent = actionEvent -> {
            LinkedList<InputDetails> nodesRequired = new LinkedList<>();
            nodesRequired.add(new InputDetails(pi_fieldNames, pi_fieldNames.getText()));
            nodesRequired.add(new InputDetails(pi_fieldLastName, pi_fieldLastName.getText()));

            if (sb_togglePersonalized.isSelected()) { // Plan Section
                nodesRequired.add(new InputDetails(sb_datePicker, (sb_datePicker.getValue() == null) ? "" : String.valueOf(sb_datePicker.getValue())));
                nodesRequired.add(new InputDetails(sb_fieldPrice, sb_fieldPrice.getText()));
                nodesRequired.add(new InputDetails(sb_fieldPriceNotes, sb_fieldPriceNotes.getText()));
            } else {
                nodesRequired.add(new InputDetails(sb_comboBoxSubscriptions, String.valueOf(sb_comboBoxSubscriptions.getSelectionModel().getSelectedIndex())));
            }

            if (!pym_togglePayment.isSelected()) { // Payment section
                nodesRequired.add(new InputDetails(pym_fieldDebt, pym_fieldDebt.getText()));
                nodesRequired.add(new InputDetails(pym_fieldDebtNotes, pym_fieldDebtNotes.getText()));
            }

            boolean formValidated = true;
            for (InputDetails node : nodesRequired) {
                boolean inputValidated = Validator.emptyValidator(node);
                if (!inputValidated && formValidated) {
                    formValidated = false;
                }
            }

            if (formValidated) {
                HashMap<String, InputDetails> inputsOnlyText = new HashMap<>();
                inputsOnlyText.put(pi_fieldNames.getText(), new InputDetails(pi_fieldNames, "Nombres"));
                inputsOnlyText.put(pi_fieldLastName.getText(), new InputDetails(pi_fieldLastName, "Apellidos"));
                boolean onlyTextValidator = Validator.onlyTextValidator(inputsOnlyText);
            } else {
                NotificationHandler.danger("Error", "Los campos en rojo no pueden estar vacios.", 2);
            }
        };
        this.sb_togglePersonalized.setOnAction(actionEvent -> {
            // Plan personalized
            subscriptionChanges(sb_togglePersonalized.isSelected());
        });

        this.pym_togglePayment.setOnAction(actionEvent -> {
            // Have a debt
            paymentChanges(pym_togglePayment.isSelected());
        });

        // Initialize form
        this.subscriptionChanges(sb_togglePersonalized.isSelected());
        this.paymentChanges(pym_togglePayment.isSelected());

        // Load data plans to combobox
        ObservableList<SociosPlanesModel> planes = SocioData.getSociosPlanes();
        if (planes != null && !planes.isEmpty()) {
            sb_comboBoxSubscriptions.setItems(planes);
            sb_comboBoxSubscriptions.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    if (sb_comboBoxSubscriptions.getSelectionModel().getSelectedIndex() != -1)
                        System.out.println(sb_comboBoxSubscriptions.getSelectionModel().getSelectedItem().getIdPlan());
                }
            });
        }

        buttonRegister.addEventFilter(ActionEvent.ACTION, registerEvent);
    }

    private void subscriptionChanges(boolean visible) {
        this.sb_comboBoxSubscriptions.setVisible(!visible);
        this.sb_comboBoxSubscriptions.setManaged(!visible);

        this.sb_boxPersonalized.setVisible(visible);
        this.sb_boxPersonalized.setManaged(visible);

        // Clear data Inputs
        if (visible) {
            Input.clearInputs(this.sb_comboBoxSubscriptions);
        } else {
            Input.clearInputs(this.sb_datePicker, this.sb_fieldPrice, this.sb_fieldPriceNotes);
        }
    }

    private void paymentChanges(boolean visible) {
        this.pym_boxDebt.setVisible(!visible);
        this.pym_boxDebt.setManaged(!visible);
        if (visible) {
            Input.clearInputs(this.pym_fieldDebt, this.pym_fieldDebtNotes);
        }
    }


}
