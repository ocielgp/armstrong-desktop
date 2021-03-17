package com.ocielgp.controller;

import animatefx.animation.FadeIn;
import com.jfoenix.controls.*;
import com.ocielgp.app.AppController;
import com.ocielgp.database.MembersData;
import com.ocielgp.database.MembershipsData;
import com.ocielgp.fingerprint.Fingerprint;
import com.ocielgp.model.MembersModel;
import com.ocielgp.model.MembershipsModel;
import com.ocielgp.model.PaymentDebtsModel;
import com.ocielgp.utilities.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
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
    private PhotoHandler photoHandler;

    // -> Personal information section [pi]
    @FXML
    private JFXTextField pi_fieldName;
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

    // -> Memberships section [ms]
    @FXML
    private JFXToggleButton ms_togglePersonalized;
    @FXML
    private JFXComboBox<MembershipsModel> ms_comboBoxMemberships;
    @FXML
    private JFXDatePicker ms_datePicker;
    @FXML
    private JFXTextField ms_fieldPrice;
    @FXML
    private JFXTextField ms_fieldDescription;
    @FXML
    private VBox ms_boxPersonalized;

    // -> Payment section [pym]
    @FXML
    private JFXToggleButton pym_togglePayment;
    @FXML
    private VBox pym_boxOwe;
    @FXML
    private JFXTextField pym_fieldOwe;
    @FXML
    private JFXTextField pym_fieldOweNotes;

    // -> End buttons
    @FXML
    private JFXButton buttonRegister;
    @FXML
    private JFXButton buttonCancel;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set max length
        Input.createMaxLengthEvent(this.pi_fieldName, MembersModel.nameLength);
        Input.createMaxLengthEvent(this.pi_fieldLastName, MembersModel.lastNameLength);
        Input.createMaxLengthEvent(this.pi_fieldPhone, MembersModel.phoneLength);
        Input.createMaxLengthEvent(this.pi_fieldEmail, MembersModel.emailLength);
        Input.createMaxLengthEvent(this.pi_fieldNotes, MembersModel.notesLength);

        Input.createMaxLengthEvent(this.ms_fieldPrice, MembershipsModel.priceLength);
        Input.createMaxLengthEvent(this.ms_fieldDescription, MembershipsModel.descriptionLength);

        Input.createMaxLengthEvent(this.pym_fieldOwe, PaymentDebtsModel.oweLength);
        Input.createMaxLengthEvent(this.pym_fieldOweNotes, PaymentDebtsModel.notesLength);
        // todo: ADD ALL MAX LENGTH LEFT

        // Remove red line when input is focused
        this.pi_comboBoxGender.focusedProperty().addListener((observableValue, oldValue, newValue) -> this.pi_comboBoxGender.getStyleClass().remove("red-border-input-line"));
        this.ms_datePicker.focusedProperty().addListener((observableValue, oldValue, newValue) -> this.ms_datePicker.getStyleClass().remove("red-border-input-line"));
        this.ms_comboBoxMemberships.focusedProperty().addListener((observableValue, oldValue, newValue) -> this.ms_comboBoxMemberships.getStyleClass().remove("red-border-input-line"));

        // Photo section
        this.photoHandler = new PhotoHandler(this.ph_imgMemberPhoto, this.ph_buttonUploadPhoto, this.ph_buttonDeletePhoto);
        this.ph_buttonDeletePhoto.setDisable(true);

        // Personal information
        ObservableList<String> genders = FXCollections.observableArrayList("Hombre", "Mujer"); // Genders
        this.pi_comboBoxGender.setItems(genders);

        // Fingerprint section
        Fingerprint.setFingerprintBox(this.fingerprintPane, this.fp_boxFingerprint, this.fp_labelFingerprintCounter, this.fp_buttonCapture, this.fp_buttonRestartCapture);

        // Membership section
        this.ms_datePicker.getEditor().textProperty().addListener((observableValue, oldDate, newDate) -> {
            if (newDate != null && !oldDate.equals(newDate)) {
                this.ms_datePicker.getEditor().setText(DateFormatter.getDateWithDayName(this.ms_datePicker.getValue()));
            }
        });
        this.ms_datePicker.setDayCellFactory(new Callback<>() {
            @Override
            public DateCell call(DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        setDisable(item.isBefore(LocalDate.now()) || item.isEqual(LocalDate.now()));
                    }
                };
            }
        });
        this.ms_togglePersonalized.setOnAction(actionEvent -> {
            membershipChanges(ms_togglePersonalized.isSelected()); // Plan personalized
        });
        ObservableList<MembershipsModel> memberships = MembershipsData.getMemberships(); // Load data plans to combobox
        if (memberships != null && !memberships.isEmpty()) {
            this.ms_comboBoxMemberships.setItems(memberships);
        }
        this.membershipChanges(this.ms_togglePersonalized.isSelected());

        // Payment section
        this.pym_togglePayment.setOnAction(actionEvent -> {
            paymentChanges(this.pym_togglePayment.isSelected()); // Have a debt
        });
        this.paymentChanges(this.pym_togglePayment.isSelected());

        // End buttons section
        EventHandler<ActionEvent> registerEvent = actionEvent -> { // TODO: Register event
            ArrayList<InputDetails> nodesRequired = new ArrayList<>();
            // Personal information section
            nodesRequired.add(new InputDetails(this.pi_fieldName, this.pi_fieldName.getText()));
            nodesRequired.add(new InputDetails(this.pi_fieldLastName, this.pi_fieldLastName.getText()));
            nodesRequired.add(new InputDetails(this.pi_comboBoxGender, String.valueOf(this.pi_comboBoxGender.getSelectionModel().getSelectedIndex())));
            nodesRequired.add(new InputDetails(AppController.getCurrentGymNode(), String.valueOf(AppController.getCurrentGymNode().getSelectionModel().getSelectedIndex())));

            // Plan section
            if (ms_togglePersonalized.isSelected()) {
                nodesRequired.add(new InputDetails(this.ms_datePicker, this.ms_datePicker.getEditor().getText()));
                nodesRequired.add(new InputDetails(this.ms_fieldPrice, this.ms_fieldPrice.getText()));
                nodesRequired.add(new InputDetails(this.ms_fieldDescription, this.ms_fieldDescription.getText()));
            } else {
                nodesRequired.add(new InputDetails(this.ms_comboBoxMemberships, String.valueOf(this.ms_comboBoxMemberships.getSelectionModel().getSelectedIndex())));
            }

            // Payment section
            if (!pym_togglePayment.isSelected()) {
                nodesRequired.add(new InputDetails(this.pym_fieldOwe, this.pym_fieldOwe.getText()));
                nodesRequired.add(new InputDetails(this.pym_fieldOweNotes, this.pym_fieldOweNotes.getText()));
            }

            boolean formValid = Validator.emptyValidator(nodesRequired.listIterator());
            if (formValid) { // Text validator
                nodesRequired.clear();
                nodesRequired.add(new InputDetails(this.pi_fieldName, this.pi_fieldName.getText()));
                nodesRequired.add(new InputDetails(this.pi_fieldLastName, this.pi_fieldLastName.getText()));
                formValid = Validator.textValidator(nodesRequired.listIterator());

                if (formValid) { // Number validator
                    if (this.pi_fieldPhone.getText().length() != 0) { // Phone validator
                        formValid = Validator.phoneValidator(new InputDetails(this.pi_fieldPhone, this.pi_fieldPhone.getText()));
                    }
                }

                if (formValid) {
                    if (this.pi_fieldEmail.getText().length() != 0) { // Email validator
                        formValid = Validator.emailValidator(new InputDetails(this.pi_fieldEmail, this.pi_fieldEmail.getText()));
                    }
                }

                if (formValid) { // Money validator
                    nodesRequired.clear();
                    if (this.ms_togglePersonalized.isSelected()) {
                        nodesRequired.add(new InputDetails(this.ms_fieldPrice, this.ms_fieldPrice.getText()));
                    }
                    if (!this.pym_togglePayment.isSelected()) {
                        nodesRequired.add(new InputDetails(this.pym_fieldOwe, this.pym_fieldOwe.getText()));
                    }
                    formValid = Validator.moneyValidator(nodesRequired.listIterator());
                }

                if (formValid) { // Form 100% valid
                    MembersModel memberModel = new MembersModel();
                    memberModel.setName(Input.capitalizeFirstLetterPerWord(this.pi_fieldName.getText()));
                    memberModel.setLastName(Input.capitalizeFirstLetterPerWord(this.pi_fieldLastName.getText()));
                    memberModel.setGender(Character.toString(this.pi_comboBoxGender.getSelectionModel().getSelectedItem().charAt(0)));

                    memberModel.setPhone(Input.spaceRemover(this.pi_fieldPhone.getText()));
                    memberModel.setEmail(Input.spaceRemover(this.pi_fieldEmail.getText()));
                    memberModel.setNotes(Input.capitalizeFirstLetter(this.pi_fieldNotes.getText()));

                    // Membership
                    MembershipsModel membershipModel;
                    if (this.ms_togglePersonalized.isSelected()) {
                        membershipModel = new MembershipsModel();
                        membershipModel.setDays(DateFormatter.differenceBetweenDays(LocalDate.now(), this.ms_datePicker.getValue()));
                        membershipModel.setDescription(this.ms_fieldDescription.getText());
                        membershipModel.setPrice(Double.parseDouble(this.ms_fieldPrice.getText()));
                    } else {
                        membershipModel = this.ms_comboBoxMemberships.getSelectionModel().getSelectedItem();
                    }

                    // Payment
                    PaymentDebtsModel pendingPaymentModel = null;
                    if (!this.pym_togglePayment.isSelected()) {
                        double oweAmount = Double.parseDouble(pym_fieldOwe.getText());
                        double membershipPrice;
                        if (this.ms_togglePersonalized.isSelected()) {
                            membershipPrice = Double.parseDouble(this.ms_fieldPrice.getText());
                        } else {
                            membershipPrice = this.ms_comboBoxMemberships.getSelectionModel().getSelectedItem().getPrice();
                        }

                        pendingPaymentModel = new PaymentDebtsModel();
                        if (oweAmount > membershipPrice) {
                            formValid = false;
                            NotificationHandler.danger("Error", "La deuda es mayor al total a pagar.", 2);
                            Validator.shakeInput(this.pym_fieldOwe);
                        } else if (oweAmount == 0) {
                            formValid = false;
                            NotificationHandler.danger("Error", "Se trata de un pago completo, no hay deuda.", 2);
                            Validator.shakeInput(this.pym_fieldOwe);
                        } else {
                            pendingPaymentModel.setPaidOut(membershipPrice - oweAmount);
                            pendingPaymentModel.setOwe(oweAmount);
                        }

                        pendingPaymentModel.setNotes(Input.capitalizeFirstLetter(this.pym_fieldOweNotes.getText()));
                    }

                    if (formValid) { // Save into data server
                        System.out.println(memberModel.getIdMember());
                        System.out.println(memberModel.getEmail().equals(""));
                        System.out.println("All is good");
                        int idMember = MembersData.addMember(memberModel);
                        if (idMember > 0) {
                            // TODO AFTER MEMBER IS CREATED, DO THIS
                            MembersData.uploadPhoto(idMember, photoHandler.getPhoto());
                            MembersData.uploadFingerprints(idMember, Fingerprint.getFingerprints());

                            MembersData.uploadMembership(idMember, membershipModel, Input.capitalizeFirstLetter(this.ms_fieldDescription.getText()));
                            // TODO: CREATE PENDING PAYMENTS
                            if (pendingPaymentModel != null) {
                                MembersData.createDebt(idMember, pendingPaymentModel);
                            }
                        }
                    }
                }
            }
            nodesRequired = null;
        };
        this.buttonRegister.setOnAction(registerEvent);
    }

    private void membershipChanges(boolean visible) {
        // Clear data Inputs
        if (visible) {
            Input.clearInputs(this.ms_comboBoxMemberships);
            new FadeIn(this.ms_boxPersonalized).play();
        } else {
            Input.clearInputs(this.ms_datePicker, this.ms_fieldPrice, this.ms_fieldDescription);
            new FadeIn(this.ms_comboBoxMemberships).play();
        }

        this.ms_comboBoxMemberships.setVisible(!visible);
        this.ms_comboBoxMemberships.setManaged(!visible);

        this.ms_boxPersonalized.setVisible(visible);
        this.ms_boxPersonalized.setManaged(visible);

    }

    private void paymentChanges(boolean visible) {
        if (visible) {
            Input.clearInputs(this.pym_fieldOwe, this.pym_fieldOweNotes);
        } else {
            new FadeIn(this.pym_boxOwe).play();
        }

        this.pym_boxOwe.setVisible(!visible);
        this.pym_boxOwe.setManaged(!visible);
    }


}
