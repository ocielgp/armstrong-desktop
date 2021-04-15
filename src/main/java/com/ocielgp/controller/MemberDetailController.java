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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MemberDetailController implements Initializable {
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
    private VBox ms_boxComboBox;
    @FXML
    private JFXComboBox<MembershipsModel> ms_comboBoxMemberships;
    @FXML
    private HBox ms_boxEndDate;
    @FXML
    private Label ms_labelEndDate;
    @FXML
    private VBox ms_boxPersonalized;
    @FXML
    private JFXDatePicker ms_datePicker;
    @FXML
    private JFXTextField ms_fieldPrice;
    @FXML
    private JFXTextField ms_fieldDescription;

    // -> Payment section [pym]
    @FXML
    private VBox pym_boxPayment;
    @FXML
    private JFXToggleButton pym_togglePayment;
    @FXML
    private VBox pym_boxOwe;
    @FXML
    private JFXTextField pym_fieldPaidOut;
    @FXML
    private JFXTextField pym_fieldOwe;

    // -> End buttons
    @FXML
    private JFXButton buttonRegister;
    @FXML
    private JFXButton buttonCancel;

    // Attributes
    Pagination pagination;

    public MemberDetailController(Pagination pagination) {
        this.pagination = pagination;
    }

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

        Input.createMaxLengthEvent(this.pym_fieldPaidOut, PaymentDebtsModel.oweLength);
        // todo: ADD ALL MAX LENGTH RESTANTES

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
        this.pi_comboBoxGender.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.H) {
                this.pi_comboBoxGender.getSelectionModel().select("Hombre");
            } else if (keyEvent.getCode() == KeyCode.M) {
                this.pi_comboBoxGender.getSelectionModel().select("Mujer");
            }
        });

        // Fingerprint section
        Fingerprint.setFingerprintBox(this.fingerprintPane, this.fp_boxFingerprint, this.fp_labelFingerprintCounter, this.fp_buttonCapture, this.fp_buttonRestartCapture);

        // Membership section

        this.ms_boxEndDate.setVisible(false); // Hide end date
        this.ms_comboBoxMemberships.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                this.ms_labelEndDate.setText(DateFormatter.getDateWithDayName(DateFormatter.plusDaysToCurrentDate(newValue.getDays())));
                this.ms_boxEndDate.setVisible(true);
                this.pym_boxPayment.setDisable(false);

                if (!this.pym_togglePayment.isSelected()) {
                    this.pym_fieldOwe.setText(String.valueOf(newValue.getPrice()));
                }
            }
        });

        this.ms_datePicker.getEditor().setOnMousePressed(mouseEvent -> {
            this.ms_datePicker.show();
        });
        this.ms_datePicker.getEditor().textProperty().addListener((observableValue, oldDate, newDate) -> {
            if (newDate != null && !newDate.equals("") && !oldDate.equals(newDate)) {
                this.ms_datePicker.getEditor().setText(DateFormatter.getDateWithDayName(this.ms_datePicker.getValue()));
            }
        });

        this.ms_fieldPrice.textProperty().addListener((observable, oldValue, newValue) -> {
            if (Validator.moneyValidator(false, new InputDetails(this.ms_fieldPrice, this.ms_fieldPrice.getText()))) {
                this.pym_boxPayment.setDisable(false);

                if (!this.pym_togglePayment.isSelected()) {
                    this.pym_fieldOwe.setText(String.valueOf(this.ms_fieldPrice.getText()));
                }
            } else {
                this.pym_boxPayment.setDisable(true);
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
            membershipChanges(this.ms_togglePersonalized.isSelected()); // Plan personalized
        });
        ObservableList<MembershipsModel> memberships = MembershipsData.getMemberships(); // Load data plans to combobox
        if (memberships != null && !memberships.isEmpty()) {
            this.ms_comboBoxMemberships.setItems(memberships);
        }
        this.membershipChanges(this.ms_togglePersonalized.isSelected());

        // Payment section
        this.pym_boxPayment.disabledProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                this.pym_togglePayment.setSelected(true);
            }
        });
        this.pym_boxPayment.setDisable(true);
        this.pym_togglePayment.selectedProperty().addListener((observable, oldValue, newValue) -> {
            paymentChanges(newValue); // Have a debt
        });
        this.paymentChanges(this.pym_togglePayment.isSelected());
        this.pym_fieldPaidOut.textProperty().addListener((observable, oldValue, newValue) -> {
            if (Validator.moneyValidator(false, new InputDetails(this.pym_fieldPaidOut, this.pym_fieldPaidOut.getText()))) {
                this.pym_fieldOwe.setText(String.valueOf(this.getMembershipPrice() - Double.parseDouble(newValue)));
            } else {
                this.pym_fieldOwe.setText(String.valueOf(this.getMembershipPrice()));
            }
        });

        // End buttons section
        EventHandler<ActionEvent> registerEvent = actionEvent -> {
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
                nodesRequired.add(new InputDetails(this.pym_fieldPaidOut, this.pym_fieldPaidOut.getText()));
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
                        nodesRequired.add(new InputDetails(this.pym_fieldPaidOut, this.pym_fieldPaidOut.getText()));
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
                    if (!this.pym_togglePayment.isSelected()) { // !false
                        double paidOut = Double.parseDouble(this.pym_fieldPaidOut.getText());
                        double owe = Double.parseDouble(this.pym_fieldOwe.getText());

                        pendingPaymentModel = new PaymentDebtsModel();
                        if (owe == 0) {
                            formValid = false;
                            NotificationHandler.danger("Error", "Se trata de un pago completo, no hay deuda.", 2);
                            Validator.shakeInput(this.pym_fieldPaidOut);
                        } else if (owe < 0) {
                            formValid = false;
                            NotificationHandler.danger("Error", "La deuda es mayor al total a pagar.", 2);
                            Validator.shakeInput(this.pym_fieldPaidOut);
                        } else {
                            pendingPaymentModel.setPaidOut(paidOut);
                            pendingPaymentModel.setOwe(owe);
                        }

                        if (this.ms_togglePersonalized.isSelected()) {
                            pendingPaymentModel.setNotes(Input.capitalizeFirstLetter(this.ms_fieldDescription.getText()));
                        } else {
                            pendingPaymentModel.setNotes(membershipModel.getDescription());
                        }
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

                            this.pagination.loadData(1); // Refresh table
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
            Input.clearInputs(this.ms_comboBoxMemberships, this.ms_labelEndDate);
            new FadeIn(this.ms_boxPersonalized).play();
        } else {
            Input.clearInputs(this.ms_datePicker, this.ms_fieldPrice, this.ms_fieldDescription);
            new FadeIn(this.ms_boxComboBox).play();
        }

        this.ms_boxComboBox.setVisible(!visible);
        this.ms_boxComboBox.setManaged(!visible);
        this.ms_boxEndDate.setVisible(false);

        this.ms_boxPersonalized.setVisible(visible);
        this.ms_boxPersonalized.setManaged(visible);

        this.pym_boxPayment.setDisable(true);
    }

    private void paymentChanges(boolean visible) {
        if (visible) {
            Input.clearInputs(this.pym_fieldPaidOut, this.pym_fieldOwe);
        } else {
            new FadeIn(this.pym_boxOwe).play();

            if (this.ms_togglePersonalized.isSelected()) {
                this.pym_fieldOwe.setText(this.ms_fieldPrice.getText());
            } else {
                this.pym_fieldOwe.setText(String.valueOf(this.ms_comboBoxMemberships.getSelectionModel().getSelectedItem().getPrice()));
            }
        }

        this.pym_boxOwe.setVisible(!visible);
        this.pym_boxOwe.setManaged(!visible);
    }

    private Double getMembershipPrice() {
        if (this.ms_togglePersonalized.isSelected()) {
            return Double.parseDouble(this.ms_fieldPrice.getText());
        } else {
            return this.ms_comboBoxMemberships.getSelectionModel().getSelectedItem().getPrice();
        }
    }

}
