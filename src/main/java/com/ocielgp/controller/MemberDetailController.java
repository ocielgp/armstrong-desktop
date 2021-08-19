package com.ocielgp.controller;

import animatefx.animation.FadeIn;
import animatefx.animation.Shake;
import com.jfoenix.controls.*;
import com.ocielgp.app.GlobalController;
import com.ocielgp.database.payments.DATA_DEBTS;
import com.ocielgp.database.members.DATA_MEMBERS;
import com.ocielgp.database.members.DATA_MEMBERS_FINGERPRINTS;
import com.ocielgp.database.members.DATA_MEMBERS_PHOTOS;
import com.ocielgp.database.members.MODEL_MEMBERS;
import com.ocielgp.database.memeberships.DATA_MEMBERSHIPS;
import com.ocielgp.database.memeberships.MODEL_MEMBERSHIPS;
import com.ocielgp.database.payments.MODEL_DEBTS;
import com.ocielgp.database.payments.DATA_PAYMENTS_MEMBERSHIPS;
import com.ocielgp.database.system.DATA_GYMS;
import com.ocielgp.fingerprint.Fingerprint;
import com.ocielgp.utilities.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MemberDetailController implements Initializable {
    // Containers
    @FXML
    private VBox boxMemberDetails;

    // Controls
    // -> Title section [t]
    @FXML
    private HBox titlePane;
    @FXML
    private Label t_labelTitle;

    // -> Quick View section [qv]
    // TODO: CHANGE SUMMARY VIEW
    @FXML
    private VBox quickViewPane;
    @FXML
    private Label qv_labelRegistrationDate;
    @FXML
    private Label qv_labelLastPayment;
    @FXML
    private Label qv_labelGym;
    @FXML
    private Label qv_staffName;

    // Shortcut pane [sc]
    @FXML
    private VBox shortcutPane;
    @FXML
    private JFXButton sh_buttonOpenDoor;
    @FXML
    private JFXButton sh_buttonAccess;
    @FXML
    private JFXButton sh_buttonPayDebt;

    // -> Photo section [ph]
    @FXML
    private ImageView ph_imgMemberPhoto;
    @FXML
    private JFXButton ph_buttonDeletePhoto;
    @FXML
    private JFXButton ph_buttonUploadPhoto;

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
    private VBox fingerprintPane;
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
    private VBox ms_boxComboBox;
    @FXML
    private JFXComboBox<MODEL_MEMBERSHIPS> ms_comboBoxMemberships;
    @FXML
    private HBox ms_boxEndDate;
    @FXML
    private Label ms_labelEndDate;
    @FXML
    private HBox ms_boxRenewMembership;
    @FXML
    private JFXButton ms_buttonRenewMembership;

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
    private JFXButton buttonAction;
    @FXML
    private JFXButton buttonCancel;

    // Attributes
    private PhotoHandler photoHandler;
    private MembersController rootController;
    private MODEL_MEMBERS modelMembers;
    BooleanUpdater booleanUpdater;


    public MemberDetailController(MembersController rootController) {
        this.rootController = rootController;
    }

    public MemberDetailController(MembersController rootController, int idMember) { // TODO: LOAD MEMBER DATA TO FORM
        this.rootController = rootController;
    }

    public void loadMember(int idMember) {
        MODEL_MEMBERS modelMembers = DATA_MEMBERS.ReadMember(idMember);
        modelMembers.setIdMember(idMember);
        modelMembers.setModelPaymentsMemberships(DATA_PAYMENTS_MEMBERSHIPS.getLastPayment(idMember));
        this.initForm(modelMembers);
    }

    private void initForm(MODEL_MEMBERS modelMembers) {
        if (this.modelMembers != null) {
            this.clearForm(false);
        }
        this.modelMembers = modelMembers;

        // Init form
        this.titlePane.getStyleClass().set(0, Input.styleToColor(DATA_MEMBERS.ReadStyle(this.modelMembers.getIdMember())));
        this.t_labelTitle.setText("[ " + this.modelMembers.getIdMember() + " ] " + this.modelMembers.getName().toUpperCase());

        // Quick view
        this.qv_labelRegistrationDate.setText(
                DateFormatter.getDateWithDayName(
                        LocalDate.parse(this.modelMembers.getRegistrationDate())
                )
        );
        if (this.modelMembers.getModelPaymentsMemberships() != null) {
            this.qv_labelGym.setText(DATA_GYMS.ReadGym(this.modelMembers.getModelPaymentsMemberships().getIdGym()).getName());
            this.qv_labelLastPayment.setText(
                    DateFormatter.getDateWithDayName(
                            LocalDate.parse(this.modelMembers.getModelPaymentsMemberships().getStartDate())
                    )
            );
            MODEL_MEMBERS staffMember = DATA_MEMBERS.ReadMember(this.modelMembers.getModelPaymentsMemberships().getIdStaff());
            this.qv_staffName.setText("[ " + staffMember.getIdMember() + " ] " + staffMember.getName() + " " + staffMember.getLastName());
        } else {
            this.qv_labelLastPayment.setText("N / A");
            this.qv_labelGym.setText(DATA_GYMS.ReadGym(this.modelMembers.getIdGym()).getName());
        }
        this.quickViewPane.setVisible(true);


        // Shortcut
        this.initButtonAccess();
        this.sh_buttonAccess.setOnAction(this.eventHandlerAccess());

        // TODO ADD DIALOG TO THIS
        if (Styles.CREATIVE == Input.colorToStyle(this.titlePane.getStyleClass().get(0))) {
            this.sh_buttonPayDebt.setVisible(true);
        } else {
            this.sh_buttonPayDebt.setVisible(false);
        }
        this.shortcutPane.setVisible(true);

        // Photo
        this.photoHandler.setPhoto(this.modelMembers.getModelMembersPhotos().getPhoto());

        // Personal information
        this.pi_fieldName.setText(this.modelMembers.getName());
        this.pi_fieldLastName.setText(this.modelMembers.getLastName());
        this.pi_comboBoxGender.getSelectionModel().select(this.modelMembers.getGender());
        this.pi_fieldPhone.setText(this.modelMembers.getPhone());
        this.pi_fieldEmail.setText(this.modelMembers.getEmail());
        this.pi_fieldNotes.setText(this.modelMembers.getNotes());

        // Fingerprint
        // TODO: ADD SUPPORT
        Fingerprint.loadFingerprints(this.modelMembers.getIdMember());

        // Membership
        if (this.modelMembers.getModelPaymentsMemberships() != null) {
            this.initRenewMembership();

            this.ms_comboBoxMemberships.setDisable(true);
            this.ms_boxRenewMembership.setVisible(true);

            this.selectPreviousMembership();

            this.ms_labelEndDate.setText(DateFormatter.getDateWithDayName(LocalDate.parse(this.modelMembers.getModelPaymentsMemberships().getEndDate())));
            this.ms_boxEndDate.setVisible(true);
        }

        if (this.ms_comboBoxMemberships.getSelectionModel().getSelectedIndex() == -1) {
            Notifications.warn("Sin membresía", "Última membresía no encontrada.");
        }

        this.pym_boxPayment.setVisible(false);
        this.pym_boxPayment.setDisable(false);

        new FadeIn(this.boxMemberDetails).play();
        this.booleanUpdater.setListener(true);

        // End buttons
        this.buttonAction.setText("Guardar cambios");
        this.buttonAction.setDisable(true);
        this.buttonCancel.setText("Cancelar");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.booleanUpdater = new BooleanUpdater(this.buttonAction);
        booleanUpdater.add("photo");
        booleanUpdater.add("name");
        booleanUpdater.add("lastName");
        booleanUpdater.add("gender");
        booleanUpdater.add("phone");
        booleanUpdater.add("email");
        booleanUpdater.add("notes");
        booleanUpdater.add("fingerprint"); // TODO
        booleanUpdater.add("renewMembership");
        booleanUpdater.add("changeMembership");
        this.createUpdaterListeners();


        // Set max length
        Input.createMaxLengthEvent(this.pi_fieldName, MODEL_MEMBERS.nameLength);
        Input.createMaxLengthEvent(this.pi_fieldLastName, MODEL_MEMBERS.lastNameLength);
        Input.createMaxLengthEvent(this.pi_fieldPhone, MODEL_MEMBERS.phoneLength);
        Input.createMaxLengthEvent(this.pi_fieldEmail, MODEL_MEMBERS.emailLength);
        Input.createMaxLengthEvent(this.pi_fieldNotes, MODEL_MEMBERS.notesLength);

        Input.createMaxLengthEvent(this.pym_fieldPaidOut, MODEL_DEBTS.oweLength);
        // todo: ADD ALL MAX LENGTH RESTANTES

        // Remove red line when input is focused
        this.pi_comboBoxGender.focusedProperty().addListener((observableValue, oldValue, newValue) -> this.pi_comboBoxGender.getStyleClass().remove("red-border-input-line"));
        this.ms_comboBoxMemberships.focusedProperty().addListener((observableValue, oldValue, newValue) -> this.ms_comboBoxMemberships.getStyleClass().remove("red-border-input-line"));

        // Quick view section
        Input.createVisibleProperty(this.quickViewPane);

        // Shorcut section
        Input.createVisibleProperty(this.shortcutPane);
        Input.createVisibleProperty(this.sh_buttonPayDebt);
        this.sh_buttonPayDebt.setOnAction(this.eventHandlerPayDebt()); // TODO

        // Photo section
        this.photoHandler = new PhotoHandler(this.ph_imgMemberPhoto, this.ph_buttonUploadPhoto, this.ph_buttonDeletePhoto);

        // Personal information
        this.pi_comboBoxGender.setItems(FXCollections.observableArrayList("Hombre", "Mujer")); // Genders
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
        Input.createVisibleProperty(this.ms_boxEndDate);
        Input.createVisibleProperty(this.ms_boxRenewMembership);
        this.ms_comboBoxMemberships.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                this.ms_labelEndDate.setText(DateFormatter.getDateWithDayName(DateFormatter.plusDaysToCurrentDate(newValue.getDays())));
                this.ms_boxEndDate.setVisible(true);
                this.pym_boxPayment.setVisible(true);
                this.pym_boxPayment.setDisable(false);

                if (!this.pym_togglePayment.isSelected()) {
                    this.pym_fieldOwe.setText(String.valueOf(newValue.getPrice()));
                }

                if (this.booleanUpdater.isListener()) {
                    if (this.modelMembers != null && this.modelMembers.getModelPaymentsMemberships() != null) {
                        this.booleanUpdater.change("changeMembership", false);
                    } else {
                        this.booleanUpdater.change("renewMembership", false);
                    }
                }
            } else {
                this.pym_boxPayment.setDisable(true);
            }
        });
        this.ms_buttonRenewMembership.setOnAction(this.eventHandlerRenewMembership());

        // TODO: DATEPICKER CODE BACKUP

//        this.ms_datePicker.getEditor().setOnMousePressed(mouseEvent -> {
//            this.ms_datePicker.show();
//        });
//        this.ms_datePicker.getEditor().textProperty().addListener((observableValue, oldDate, newDate) -> {
//            if (newDate != null && !newDate.equals("") && !oldDate.equals(newDate)) {
//                this.ms_datePicker.getEditor().setText(DateFormatter.getDateWithDayName(this.ms_datePicker.getValue()));
//            }
//        });

//        this.ms_datePicker.setDayCellFactory(new Callback<>() {
//            @Override
//            public DateCell call(DatePicker datePicker) {
//                return new DateCell() {
//                    @Override
//                    public void updateItem(LocalDate item, boolean empty) {
//                        super.updateItem(item, empty);
//                        setDisable(item.isBefore(LocalDate.now()) || item.isEqual(LocalDate.now()));
//                    }
//                };
//            }
//        });

        ObservableList<MODEL_MEMBERSHIPS> memberships = DATA_MEMBERSHIPS.ReadMemberships(); // Load data plans to combobox
        if (memberships != null) {
            this.ms_comboBoxMemberships.setItems(memberships);
        }

        // Payment section
        Input.createVisibleProperty(this.pym_boxPayment);
        this.pym_boxPayment.setVisible(true);
        this.pym_boxPayment.disabledProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                this.pym_togglePayment.setSelected(true);
            }
        });
        this.pym_boxPayment.setDisable(true);
        this.pym_togglePayment.selectedProperty().addListener((observable, oldValue, newValue) -> {
            paymentChanges(newValue); // Have a debt

            if (this.booleanUpdater.isListener()) {
                if (this.modelMembers != null && this.modelMembers.getModelPaymentsMemberships() != null) {
                    this.booleanUpdater.change("changeMembership", false);
                } else {
                    this.booleanUpdater.change("renewMembership", false);
                }
            }
        });
        this.paymentChanges(this.pym_togglePayment.isSelected());
        this.pym_fieldPaidOut.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!this.pym_togglePayment.isSelected()) { // !false
                if (Validator.moneyValidator(new InputDetails(this.pym_fieldPaidOut, this.pym_fieldPaidOut.getText()), false)) {
                    this.pym_fieldOwe.setText(String.valueOf(this.getMembershipPrice() - Double.parseDouble(newValue)));
                } else {
                    this.pym_fieldOwe.setText(String.valueOf(this.getMembershipPrice()));
                }
            }
        });

        // End buttons section
        this.buttonAction.setOnAction(this.eventHandlerRegister());
        this.buttonCancel.setOnAction((actionEvent) -> {
            this.clearForm(true);
            this.rootController.unselectTable();
        });

        Platform.runLater(() -> new FadeIn(this.rootController.boxMembersPane));
    }

    private void paymentChanges(boolean visible) {
        if (visible) {
            Input.clearInputs(this.pym_fieldPaidOut, this.pym_fieldOwe);
        } else {
            new FadeIn(this.pym_boxOwe).play();
            this.pym_fieldOwe.setText(String.valueOf(this.ms_comboBoxMemberships.getSelectionModel().getSelectedItem().getPrice()));
        }

        this.pym_boxOwe.setVisible(!visible);
        this.pym_boxOwe.setManaged(!visible);
    }

    private Double getMembershipPrice() {
        return this.ms_comboBoxMemberships.getSelectionModel().getSelectedItem().getPrice();
    }

    private void clearForm(boolean animation) {
        this.modelMembers = null;
        this.booleanUpdater.setListener(false);
        this.titlePane.getStyleClass().set(0, "default-style");
        this.t_labelTitle.setText("NUEVO SOCIO");

        this.quickViewPane.setVisible(false);
        this.shortcutPane.setVisible(false);

        this.photoHandler.resetHandler();

        Fingerprint.ResetFingerprintUI();

        Input.clearInputs(
                this.pi_fieldName,
                this.pi_fieldLastName,
                this.pi_comboBoxGender,
                (this.pi_fieldPhone.getText().isEmpty()) ? null : this.pi_fieldPhone,
                (this.pi_fieldEmail.getText().isEmpty()) ? null : this.pi_fieldEmail,
                (this.pi_fieldNotes.getText().isEmpty()) ? null : this.pi_fieldNotes,
                this.ms_comboBoxMemberships
        );

        this.ms_comboBoxMemberships.setDisable(false);
        this.ms_boxEndDate.setVisible(false);
        this.restartRenewMembership(true);

        this.buttonAction.setText("Registrar");
        this.buttonAction.setDisable(false);
        this.buttonCancel.setText("Limpiar");

        if (animation) {
            new FadeIn(this.boxMemberDetails).play();
        }


//        this.pym_boxPayment.setVisible(true);
//        this.pym_togglePayment.setSelected(true);
    }

    // Init shortcut pane
    private void initButtonAccess() {
        if (this.modelMembers.isAccess()) {
            this.sh_buttonAccess.getStyleClass().set(3, Input.styleToColor(Styles.DANGER));
            this.sh_buttonAccess.setText("Bloquear acceso");
            this.sh_buttonOpenDoor.setDisable(false);
        } else {
            this.sh_buttonAccess.getStyleClass().set(3, Input.styleToColor(Styles.SUCCESS));
            this.sh_buttonAccess.setText("Desbloquear acceso");
            this.sh_buttonOpenDoor.setDisable(true);
        }
    }

    // Event Handlers
    private EventHandler<ActionEvent> eventHandlerRegister() {
        return actionEvent -> {
            ArrayList<InputDetails> nodesRequired = new ArrayList<>();
            // Personal information section
            nodesRequired.add(new InputDetails(this.pi_fieldName, this.pi_fieldName.getText()));
            nodesRequired.add(new InputDetails(this.pi_fieldLastName, this.pi_fieldLastName.getText()));
            nodesRequired.add(new InputDetails(this.pi_comboBoxGender, String.valueOf(this.pi_comboBoxGender.getSelectionModel().getSelectedIndex())));
            nodesRequired.add(new InputDetails(GlobalController.getCurrentGymNode(), String.valueOf(GlobalController.getCurrentGymNode().getSelectionModel().getSelectedIndex())));

            // Plan section
            nodesRequired.add(new InputDetails(this.ms_comboBoxMemberships, String.valueOf(this.ms_comboBoxMemberships.getSelectionModel().getSelectedIndex())));

            // Payment section
            if (!pym_togglePayment.isSelected()) {
                nodesRequired.add(new InputDetails(this.pym_fieldPaidOut, this.pym_fieldPaidOut.getText()));
            }

            Boolean formValid = Validator.emptyValidator(nodesRequired.listIterator());
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
                    if (!this.pym_togglePayment.isSelected()) {
                        nodesRequired.add(new InputDetails(this.pym_fieldPaidOut, this.pym_fieldPaidOut.getText()));
                    }
                    formValid = Validator.moneyValidator(nodesRequired.listIterator());
                }

                if (formValid) { // Form 100% valid

                    MODEL_MEMBERS memberModel = new MODEL_MEMBERS();
                    memberModel.setName(Input.capitalizeFirstLetterPerWord(this.pi_fieldName.getText()));
                    memberModel.setLastName(Input.capitalizeFirstLetterPerWord(this.pi_fieldLastName.getText()));
                    memberModel.setGender(this.pi_comboBoxGender.getSelectionModel().getSelectedItem());

                    memberModel.setPhone(Input.spaceRemover(this.pi_fieldPhone.getText()));
                    memberModel.setEmail(Input.spaceRemover(this.pi_fieldEmail.getText()));
                    memberModel.setNotes(Input.capitalizeFirstLetter(this.pi_fieldNotes.getText()));
                    memberModel.setIdGym(GlobalController.getCurrentGym().getIdGym());

                    // Membership
                    MODEL_MEMBERSHIPS modelMemberships;
                    modelMemberships = this.ms_comboBoxMemberships.getSelectionModel().getSelectedItem();

                    // Payment
                    MODEL_DEBTS modelDebts = null;
                    if (!this.pym_togglePayment.isSelected()) { // !false
                        double paidOut = Double.parseDouble(this.pym_fieldPaidOut.getText());
                        double owe = Double.parseDouble(this.pym_fieldOwe.getText());

                        if (owe == 0) {
                            formValid = false;
                            Notifications.danger("Error", "Se trata de un pago completo, no hay deuda.");
                            Validator.shakeInput(this.pym_fieldPaidOut);
                        } else if (owe < 0) {
                            formValid = false;
                            Notifications.danger("Error", "La deuda es mayor al total a pagar.");
                            Validator.shakeInput(this.pym_fieldPaidOut);
                        } else {
                            modelDebts = new MODEL_DEBTS();
                            modelDebts.setPaidOut(paidOut);
                            modelDebts.setOwe(owe);
                            modelDebts.setAmount(1);
                            modelDebts.setDescription(modelMemberships.getDescription() + " (" + modelMemberships.getDays() + ") días");
                        }
                    }

                    if (formValid) { // Save into data server
                        if (this.booleanUpdater.isListener()) {
                            boolean flag = true;
                            if (!this.booleanUpdater.getBool("photo")) {
                                flag = DATA_MEMBERS_PHOTOS.UpdatePhoto(this.modelMembers.getIdMember(), this.photoHandler.getPhoto());
                            }
                            if (flag && !this.booleanUpdater.getBool("name")) {
                                flag = DATA_MEMBERS.UpdateName(this.modelMembers.getIdMember(), memberModel.getName());
                            }
                            if (flag && this.booleanUpdater.getBool("lastName")) {
                                flag = DATA_MEMBERS.UpdateLastName(this.modelMembers.getIdMember(), memberModel.getLastName());
                            }
                            if (flag && !this.booleanUpdater.getBool("gender")) {
                                flag = DATA_MEMBERS.UpdateGender(this.modelMembers.getIdMember(), memberModel.getGender());
                            }
                            if (flag && !this.booleanUpdater.getBool("phone")) {
                                flag = DATA_MEMBERS.UpdatePhone(this.modelMembers.getIdMember(), memberModel.getPhone());
                            }
                            if (flag && !this.booleanUpdater.getBool("email")) {
                                flag = DATA_MEMBERS.UpdateEmail(this.modelMembers.getIdMember(), memberModel.getEmail());
                            }
                            if (flag && !this.booleanUpdater.getBool("notes")) {
                                flag = DATA_MEMBERS.UpdateNotes(this.modelMembers.getIdMember(), memberModel.getNotes());
                            }
                            if (flag && !this.booleanUpdater.getBool("fingerprint")) {
//                                flag =
                            }
                            if (flag && !this.booleanUpdater.getBool("renewMembership")) {
                                int idLastMembership = DATA_PAYMENTS_MEMBERSHIPS.CreatePaymentMembership(this.modelMembers.getIdMember(), modelMemberships);
                                if (modelDebts != null && idLastMembership > 0) {
                                    DATA_DEBTS.CreateDebt(modelDebts, this.modelMembers.getIdMember(), 1);
                                }
                            } else if (flag && !this.booleanUpdater.getBool("changeMembership") && this.modelMembers.getModelPaymentsMemberships() != null) {
                                DATA_MEMBERSHIPS.UpdateMembership(this.modelMembers.getIdMember(), modelMemberships);
                                if (modelDebts != null) {
                                    DATA_DEBTS.CreateDebt(modelDebts, this.modelMembers.getIdMember(), 1);
                                }
                            }
                            if (flag) {
                                Notifications.success("Guardar cambios", "La información de " + memberModel.getName() + " ha sido actualizada.");
                            } else {
                                Notifications.danger("Guardar cambios", "Parte de la información no se pudo guardar.");
                            }
                        } else {
                            System.out.println("All is good");
                            int idMember = DATA_MEMBERS.CreateMember(memberModel);
                            if (idMember > 0) {
                                // TODO AFTER MEMBER IS CREATED, DO THIS
                                DATA_MEMBERS_PHOTOS.CreatePhoto(idMember, photoHandler.getPhoto());
                                DATA_MEMBERS_FINGERPRINTS.InsertFingerprints(idMember, Fingerprint.getFingerprints());

                                int idLastMembership = DATA_PAYMENTS_MEMBERSHIPS.CreatePaymentMembership(idMember, modelMemberships);
                                // TODO: CREATE PENDING PAYMENTS
                                if (modelDebts != null && idLastMembership > 0) {
                                    DATA_DEBTS.CreateDebt(modelDebts, idMember, 1);
                                }

                                Notifications.success("Nuevo miembro", memberModel.getName() + " ha sido registrado.");
                            }
                        }

                        this.rootController.refreshTable();
                        this.clearForm(true);

                        // Clear memory data
                        nodesRequired = null;
                        formValid = null;
                        memberModel = null;
                        modelMemberships = null;
                        modelDebts = null;
                    }
                }
            }
        };
    }

    private EventHandler<ActionEvent> eventHandlerAccess() {
        return actionEvent -> {
            if (Styles.DANGER == Input.colorToStyle(this.sh_buttonAccess.getStyleClass().get(3))) {
                Dialog dialog = new Dialog(
                        Styles.DANGER,
                        "Bloquear acceso",
                        "Se le bloqueara el acceso a todos los gimnasios a " + this.modelMembers.getName() + " " + this.modelMembers.getLastName() + ", ¿quieres continuar?",
                        DialogTypes.MESSAGE,
                        Dialog.YES, Dialog.CANCEL
                );
                if (dialog.show()) {
                    if (DATA_MEMBERS.UpdateAccess(this.modelMembers.getIdMember(), this.modelMembers.isAccess())) {
                        this.modelMembers.setAccess(!this.modelMembers.isAccess());
                        this.sh_buttonOpenDoor.setDisable(true);
                        this.sh_buttonAccess.getStyleClass().set(3, Input.styleToColor(Styles.SUCCESS));
                        this.sh_buttonAccess.setText("Desbloquear acceso");
                        Notifications.danger("Acceso bloqueado", this.modelMembers.getName() + " ha perdido el acceso a los gimnasios.");
                        // TODO: ABONAR MENSUALIDADES
                    }
                }
            } else {
                Dialog dialog = new Dialog(
                        Styles.SUCCESS,
                        "Desbloquear acceso",
                        "Se desbloqueara el acceso a " + this.modelMembers.getName() + " " + this.modelMembers.getLastName() + ", ¿quieres continuar?",
                        DialogTypes.MESSAGE,
                        Dialog.YES, Dialog.CANCEL
                );
                if (dialog.show()) {
                    if (DATA_MEMBERS.UpdateAccess(this.modelMembers.getIdMember(), this.modelMembers.isAccess())) {
                        this.modelMembers.setAccess(!this.modelMembers.isAccess());
                        this.sh_buttonOpenDoor.setDisable(false);
                        this.sh_buttonAccess.getStyleClass().set(3, Input.styleToColor(Styles.DANGER));
                        this.sh_buttonAccess.setText("Bloquear acceso");
                        Notifications.success("Acceso desbloqueado", this.modelMembers.getName() + " puede entrar a los gimnasios nuevamente.");
                    }
                }
            }
        };
    }

    private EventHandler<ActionEvent> eventHandlerPayDebt() {
        return actionEvent -> {
            ArrayList<MODEL_DEBTS> debtsList = DATA_DEBTS.ReadDebts(this.modelMembers.getIdMember());
            Dialog dialog = new Dialog(
                    Styles.CREATIVE,
                    "Abonar mensualidad",
                    debtsList,
                    Dialog.OK, Dialog.CANCEL
            );
            if (dialog.show()) {

            }
        };
    }

    private EventHandler<ActionEvent> eventHandlerChangeMembership() {
        return actionEvent -> {
            if (Styles.SUCCESS == Input.colorToStyle(this.sh_buttonAccess.getStyleClass().get(3))) {
                new Shake(this.ms_buttonRenewMembership).play();
                Notifications.danger("Acceso bloqueado", "Para realizar esta acción, el usuario no debe tener bloqueos.");
            } else {
                if (Input.colorToStyle(this.ms_buttonRenewMembership.getStyleClass().get(3)) == Styles.WARN) {
                    Dialog dialog = new Dialog(
                            Styles.WARN,
                            "Cambiar membresía",
                            "Solo podrás cambiar la membresía el mismo día de pago, ¿deseas continuar?",
                            DialogTypes.MESSAGE,
                            Dialog.YES, Dialog.NO
                    );
                    if (dialog.show()) {
                        this.enableComboMemberships(true);
                        this.ms_buttonRenewMembership.getStyleClass().set(3, Input.styleToColor(Styles.DANGER));
                        this.ms_buttonRenewMembership.setText("Cancelar");
                    }
                } else {
                    this.restartRenewMembership(false);
                }
            }
        };
    }

    private EventHandler<ActionEvent> eventHandlerRenewMembership() {
        return actionEvent -> {
            if (Styles.SUCCESS == Input.colorToStyle(this.sh_buttonAccess.getStyleClass().get(3))) {
                new Shake(this.ms_buttonRenewMembership).play();
                Notifications.danger("Acceso bloqueado", "Para realizar esta acción, el usuario no debe tener bloqueos.");
            } else if (this.sh_buttonPayDebt.isVisible()) {
                new Shake(this.ms_buttonRenewMembership).play();
                Notifications.danger("Error", "Para renovar una mensualidad el usuario no debe tener adeudos.");
            } else {
                if (Input.colorToStyle(this.ms_buttonRenewMembership.getStyleClass().get(3)) == Styles.EPIC) {
                    if (this.ms_comboBoxMemberships.getSelectionModel().getSelectedIndex() != -1) {
                        Dialog dialog = new Dialog(Styles.EPIC, "Renovar membresía", "¿La membresía es la misma?", DialogTypes.MESSAGE, Dialog.YES, Dialog.NO);
                        if (dialog.show()) {
                            this.ms_labelEndDate.setText(
                                    DateFormatter.getDateWithDayName(
                                            DateFormatter.plusDaysToCurrentDate(
                                                    this.ms_comboBoxMemberships.getSelectionModel().getSelectedItem().getDays()
                                            )
                                    )
                            );
                            new Shake(this.ms_labelEndDate).play();
                            this.booleanUpdater.change("renewMembership", false);
                        }
                    } else {
                        Notifications.buildNotification("gmi-event", "Renovar membresía", "Selecciona la nueva membresía.");
                    }
                    this.enableComboMemberships(true);

                    this.ms_buttonRenewMembership.getStyleClass().set(3, Input.styleToColor(Styles.DANGER));
                    this.ms_buttonRenewMembership.setText("Cancelar");
                } else {
                    this.restartRenewMembership(false);
                }
            }
        };
    }

    // Utilities
    private void selectPreviousMembership() {
        if (this.modelMembers != null && this.modelMembers.getModelPaymentsMemberships() != null) {
            for (MODEL_MEMBERSHIPS modelMemberships : this.ms_comboBoxMemberships.getItems()) {
                if (modelMemberships.getIdMembership() == this.modelMembers.getModelPaymentsMemberships().getIdMembership()) {
                    this.ms_comboBoxMemberships.getSelectionModel().select(modelMemberships);
                }
            }
        }
    }

    private void enableComboMemberships(boolean enable) {
        if (enable) {
            new Shake(this.ms_comboBoxMemberships).play();
        } else {
            this.selectPreviousMembership();
        }

        if (this.modelMembers != null && this.modelMembers.getModelPaymentsMemberships() != null) {
            this.ms_comboBoxMemberships.setDisable(!enable);
            this.ms_boxEndDate.setVisible(true);
        } else {
            this.ms_comboBoxMemberships.setDisable(false);
            this.ms_boxEndDate.setVisible(false);
        }

        this.pym_boxPayment.setVisible(enable);
        this.pym_boxPayment.setDisable(this.ms_comboBoxMemberships.getSelectionModel().getSelectedIndex() == -1);
    }

    private void initRenewMembership() {
        if (this.modelMembers != null && this.modelMembers.getModelPaymentsMemberships() != null) {
            if (DateFormatter.daysDifferenceToday(LocalDate.parse(this.modelMembers.getModelPaymentsMemberships().getStartDate())) == 0) {
                this.ms_buttonRenewMembership.setText("Cambiar");
                this.ms_buttonRenewMembership.getStyleClass().set(3, Input.styleToColor(Styles.WARN));
                this.ms_buttonRenewMembership.setOnAction(this.eventHandlerChangeMembership());
            } else {
                this.ms_buttonRenewMembership.setText("Renovar");
                this.ms_buttonRenewMembership.getStyleClass().set(3, Input.styleToColor(Styles.EPIC));
                this.ms_buttonRenewMembership.setOnAction(this.eventHandlerRenewMembership());
            }
        }
    }

    private void restartRenewMembership(boolean hide) {
        this.enableComboMemberships(false);
        this.booleanUpdater.change("renewMembership", true);
        this.booleanUpdater.change("changeMembership", true);

        this.ms_boxRenewMembership.setVisible(!hide);

        if (this.modelMembers != null && this.modelMembers.getModelPaymentsMemberships() != null) {
            this.ms_labelEndDate.setText(
                    DateFormatter.getDateWithDayName(LocalDate.parse(this.modelMembers.getModelPaymentsMemberships().getEndDate()))
            );
        }

        this.initRenewMembership();
    }

    // Update listener
    private void createUpdaterListeners() {
        this.ph_imgMemberPhoto.imageProperty().addListener((observable, oldValue, newValue) -> {
            if (this.booleanUpdater.isListener()) {
                this.booleanUpdater.change(
                        "photo",
                        false
                );
            }
        });
        this.pi_fieldName.setOnKeyTyped(keyEvent -> {
            if (this.booleanUpdater.isListener()) {
                this.booleanUpdater.change(
                        "name",
                        Validator.compare(this.pi_fieldName.getText(), this.modelMembers.getName())
                );
            }
        });
        this.pi_fieldLastName.setOnKeyTyped(keyEvent -> {
            if (this.booleanUpdater.isListener()) {
                this.booleanUpdater.change(
                        "lastName",
                        Validator.compare(this.pi_fieldLastName.getText(), this.modelMembers.getLastName())
                );
            }
        });
        this.pi_comboBoxGender.setOnAction(keyEvent -> {
            if (this.booleanUpdater.isListener()) {
                this.booleanUpdater.change(
                        "gender",
                        Validator.compare(this.pi_comboBoxGender.getSelectionModel().getSelectedItem(), this.modelMembers.getGender())
                );
            }
        });
        this.pi_fieldPhone.setOnKeyTyped(keyEvent -> {
            if (this.booleanUpdater.isListener()) {
                this.booleanUpdater.change(
                        "phone",
                        Validator.compare(this.pi_fieldPhone.getText(), this.modelMembers.getPhone())
                );
            }
        });
        this.pi_fieldEmail.setOnKeyTyped(keyEvent -> {
            if (this.booleanUpdater.isListener()) {
                this.booleanUpdater.change(
                        "email",
                        Validator.compare(this.pi_fieldEmail.getText(), this.modelMembers.getEmail())
                );
            }
        });
        this.pi_fieldNotes.setOnKeyTyped(keyEvent -> {
            if (this.booleanUpdater.isListener()) {
                this.booleanUpdater.change(
                        "notes",
                        Validator.compare(this.pi_fieldNotes.getText(), this.modelMembers.getNotes())
                );
            }
        });
    }
}
