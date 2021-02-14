package com.ocielgp.controller;

import animatefx.animation.FadeIn;
import com.jfoenix.controls.*;
import com.ocielgp.database.SocioData;
import com.ocielgp.fingerprint.Fingerprint;
import com.ocielgp.model.SociosPlanesModel;
import com.ocielgp.utilities.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class MemberController implements Initializable {
    // Containers
    @FXML
    private VBox addMemberPane;
    @FXML
    private VBox fingerprintPane;
    @FXML
    private VBox planPane;

    // Controls
    // -> Photo section [ph]
    @FXML
    private ImageView ph_imgMemberPhoto;
    @FXML
    private JFXButton ph_buttonDeletePhoto;
    @FXML
    private JFXButton ph_buttonUploadPhoto;

    // -> Personal information section [pi]
    @FXML
    private JFXTextField pi_fieldNames;
    @FXML
    private JFXTextField pi_fieldLastName;
    @FXML
    JFXComboBox<String> pi_comboBoxGender;
    @FXML
    private JFXTextField pi_fieldPhone;
    @FXML
    private JFXTextField pi_fieldEmail;
    @FXML
    private JFXTextField pi_fieldNotes;

    // -> Fingerprint section [fp]
    @FXML
    private VBox fp_boxFingerprint;
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
        this.pi_comboBoxGender.focusedProperty().addListener((observableValue, oldValue, newValue) -> this.sb_datePicker.getStyleClass().remove("red-border-input-line"));
        this.sb_datePicker.focusedProperty().addListener((observableValue, oldValue, newValue) -> this.sb_datePicker.getStyleClass().remove("red-border-input-line"));
        this.sb_comboBoxSubscriptions.focusedProperty().addListener((observableValue, oldValue, newValue) -> this.sb_comboBoxSubscriptions.getStyleClass().remove("red-border-input-line"));
        Fingerprint.setFingerprintUI(this.fingerprintPane, this.fp_boxFingerprint, this.fp_labelFingerprintCounter, this.fp_buttonRestartCapture);

        /* Events Handlers */
        EventHandler<ActionEvent> registerEvent = actionEvent -> {
            LinkedList<InputDetails> nodesRequired = new LinkedList<>();
            nodesRequired.add(new InputDetails(this.pi_fieldNames, this.pi_fieldNames.getText()));
            nodesRequired.add(new InputDetails(this.pi_fieldLastName, this.pi_fieldLastName.getText()));
            nodesRequired.add(new InputDetails(this.pi_comboBoxGender, String.valueOf(this.pi_comboBoxGender.getSelectionModel().getSelectedIndex())));

            if (sb_togglePersonalized.isSelected()) { // Plan Section
                nodesRequired.add(new InputDetails(this.sb_datePicker, (this.sb_datePicker.getValue() == null) ? "" : String.valueOf(sb_datePicker.getValue())));
                nodesRequired.add(new InputDetails(this.sb_fieldPrice, this.sb_fieldPrice.getText()));
                nodesRequired.add(new InputDetails(this.sb_fieldPriceNotes, this.sb_fieldPriceNotes.getText()));
            } else {
                nodesRequired.add(new InputDetails(this.sb_comboBoxSubscriptions, String.valueOf(this.sb_comboBoxSubscriptions.getSelectionModel().getSelectedIndex())));
            }

            if (!pym_togglePayment.isSelected()) { // Payment section
                nodesRequired.add(new InputDetails(this.pym_fieldDebt, this.pym_fieldDebt.getText()));
                nodesRequired.add(new InputDetails(this.pym_fieldDebtNotes, this.pym_fieldDebtNotes.getText()));
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

        EventHandler<ActionEvent> captureEvent = actionEvent -> {
            if (fp_buttonCapture.getText().equals("Iniciar captura")) {
                this.fp_boxFingerprint.requestFocus();
                Fingerprint.StartCapture(this.fp_boxFingerprint, true);
                this.fp_buttonCapture.setText("Detener captura");
                if (Integer.parseInt(this.fp_labelFingerprintCounter.getText()) > 0) {
                    this.fp_buttonRestartCapture.setDisable(false);
                }
            } else {
                Fingerprint.StopCapture();
                this.fp_buttonCapture.setText("Iniciar captura");
            }
        };
        /* End Events Handlers */

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

        // Load data genders
        ObservableList<String> genders = FXCollections.observableArrayList("Hombre", "Mujer");
        this.pi_comboBoxGender.setItems(genders);

        // Load data plans to combobox
        ObservableList<SociosPlanesModel> planes = SocioData.getSociosPlanes();
        if (planes != null && !planes.isEmpty()) {
            sb_comboBoxSubscriptions.setItems(planes);
            sb_comboBoxSubscriptions.setOnAction(actionEvent -> {
                if (sb_comboBoxSubscriptions.getSelectionModel().getSelectedIndex() != -1)
                    System.out.println(sb_comboBoxSubscriptions.getSelectionModel().getSelectedItem().getIdPlan());
            });
        }

        this.fp_buttonCapture.setOnAction(captureEvent);
        this.fp_buttonRestartCapture.setOnAction(actionEvent -> {
            Fingerprint.RestartCapture();
            this.fp_boxFingerprint.requestFocus();
        });
        this.buttonRegister.setOnAction(registerEvent);

        Photos photos = new Photos(this.ph_imgMemberPhoto, this.ph_buttonDeletePhoto);
        this.ph_buttonUploadPhoto.setOnAction(photos.getUploadPhotoEvent());
        this.ph_buttonDeletePhoto.setDisable(true);

        this.ph_buttonDeletePhoto.setOnAction(photos.getDeletePhotoEvent());
    }

    private void subscriptionChanges(boolean visible) {
        // Clear data Inputs
        if (visible) {
            Input.clearInputs(this.sb_comboBoxSubscriptions);
            new FadeIn(this.sb_boxPersonalized).play();
        } else {
            Input.clearInputs(this.sb_datePicker, this.sb_fieldPrice, this.sb_fieldPriceNotes);
            new FadeIn(this.sb_comboBoxSubscriptions).play();
        }

        this.sb_comboBoxSubscriptions.setVisible(!visible);
        this.sb_comboBoxSubscriptions.setManaged(!visible);

        this.sb_boxPersonalized.setVisible(visible);
        this.sb_boxPersonalized.setManaged(visible);

    }

    private void paymentChanges(boolean visible) {
        if (visible) {
            Input.clearInputs(this.pym_fieldDebt, this.pym_fieldDebtNotes);
        } else {
            new FadeIn(this.pym_boxDebt).play();
        }

        this.pym_boxDebt.setVisible(!visible);
        this.pym_boxDebt.setManaged(!visible);
    }


}
