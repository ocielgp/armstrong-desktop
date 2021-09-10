package com.ocielgp.controller;

import animatefx.animation.FadeIn;
import animatefx.animation.FadeInUp;
import animatefx.animation.FadeOutDown;
import animatefx.animation.Shake;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import com.ocielgp.app.GlobalController;
import com.ocielgp.database.members.DATA_MEMBERS;
import com.ocielgp.database.members.DATA_MEMBERS_FINGERPRINTS;
import com.ocielgp.database.members.DATA_MEMBERS_PHOTOS;
import com.ocielgp.database.members.MODEL_MEMBERS;
import com.ocielgp.database.memeberships.DATA_MEMBERSHIPS;
import com.ocielgp.database.memeberships.MODEL_MEMBERSHIPS;
import com.ocielgp.database.payments.DATA_DEBTS;
import com.ocielgp.database.payments.DATA_PAYMENTS_MEMBERSHIPS;
import com.ocielgp.database.payments.MODEL_DEBTS;
import com.ocielgp.database.system.DATA_GYMS;
import com.ocielgp.fingerprint.Fingerprint;
import com.ocielgp.utilities.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class MemberDetailController implements Initializable {
    @FXML
    private VBox boxMemberDetails;
    private final MembersController membersController;

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
    private ScrollPane scrollPane;
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
    @FXML
    private JFXComboBox<String> pi_comboBoxGender;
    private MODEL_MEMBERS modelMembers = null;
    private BooleanUpdater booleanUpdater;

    public MemberDetailController(MembersController membersController) {
        this.membersController = membersController;
    }

    public void loadMember(int idMember) {
        FadeOutDown fadeOutDown = new FadeOutDown(this.scrollPane);
        fadeOutDown.setOnFinished(actionEvent -> {
//            this.scrollPane.setVvalue(0.0);
            if (this.modelMembers != null) {
                this.clearForm(false);
            }
            DATA_MEMBERS.ReadMember(idMember).thenAccept(model_members -> {
                this.modelMembers = model_members;
                this.modelMembers.setIdMember(idMember);
            }).thenRunAsync(() -> DATA_PAYMENTS_MEMBERSHIPS.getLastPayment(idMember).thenAccept(model_payments_memberships -> {
                this.modelMembers.setModelPaymentsMemberships(model_payments_memberships);
                if (model_payments_memberships == null) {
                    DATA_GYMS.ReadGym(this.modelMembers.getIdGym())
                            .thenAccept(model_gyms -> {
                                this.modelMembers.setModelGyms(model_gyms);
                                this.initForm();
                            });
                } else {
                    DATA_MEMBERS.ReadMember(model_payments_memberships.getIdStaff()).thenAccept(model_members -> {
                        DATA_GYMS.ReadGym(model_payments_memberships.getIdGym()).thenAccept(model_gyms -> {
                            this.modelMembers.setModelGyms(model_gyms);
                            Platform.runLater(() -> this.qv_staffName.setText(model_members.getName() + " " + model_members.getLastName()));
                            this.initForm();
                        });
                    });
                }
            }));
        });
        Platform.runLater(fadeOutDown::play);
    }

    private void initForm() {
        if (this.modelMembers != null) {
            Platform.runLater(() -> {
                // Init form
                this.titlePane.getStyleClass().set(0, Input.styleToColor(DATA_MEMBERS.ReadStyle(this.modelMembers.getIdMember())));
                this.t_labelTitle.setText("[ " + this.modelMembers.getIdMember() + " ] " + this.modelMembers.getName().toUpperCase());

                // quick view
                this.qv_labelRegistrationDate.setText(
                        DateFormatter.getDateWithDayName(
                                LocalDate.parse(this.modelMembers.getRegistrationDate())
                        )
                );
                if (this.modelMembers.getModelPaymentsMemberships() != null) {
                    this.qv_labelLastPayment.setText(
                            DateFormatter.getDateWithDayName(
                                    LocalDate.parse(this.modelMembers.getModelPaymentsMemberships().getStartDate())
                            )
                    );
                } else {
                    this.qv_labelLastPayment.setText("N / A");
                }
                this.qv_labelGym.setText(this.modelMembers.getModelGyms().getName());
                this.quickViewPane.setVisible(true);


                // Shortcut
                this.initButtonAccess();
                this.sh_buttonAccess.setOnAction(this.eventHandlerAccess());

                this.sh_buttonPayDebt.setVisible(Styles.CREATIVE == Input.colorToStyle(this.titlePane.getStyleClass().get(0)));
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
                    this.ms_labelEndDate.setText(
                            DateFormatter.getDateWithDayName(LocalDate.parse(this.modelMembers.getModelPaymentsMemberships().getEndDate()
                            ))
                    );
                    this.ms_boxEndDate.setVisible(true);
                }

                if (this.ms_comboBoxMemberships.getSelectionModel().getSelectedIndex() == -1) {
                    Notifications.warn("Sin membresía", "Última membresía no encontrada.");
                }

                this.pym_boxPayment.setVisible(false);
                this.pym_boxPayment.setDisable(false);

                Loading.close();
                this.membersController.enableTable();
                new FadeInUp(this.scrollPane).play();
                this.boxMemberDetails.requestFocus();

                // End buttons
                this.buttonAction.setText("Guardar cambios");
                this.buttonAction.setDisable(true);
                this.buttonCancel.setText("Cancelar");

                this.booleanUpdater.setListener(true);
            });
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Input.getScrollEvent(this.scrollPane);
        this.scrollPane.opacityProperty().addListener(((observableValue, oldValue, newValue) -> {
            if (this.modelMembers != null) {
                if (newValue.doubleValue() > 0) {
                    this.scrollPane.setVvalue(1d);
                }
            } else {
                this.scrollPane.setVvalue(0d);
            }
        }));
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
        this.photoHandler = new PhotoHandler(this.booleanUpdater, this.ph_imgMemberPhoto, this.ph_buttonUploadPhoto, this.ph_buttonDeletePhoto);

        // Personal information

        DATA_MEMBERS.ReadGenders().thenAccept(genders -> {
            this.pi_comboBoxGender.setItems(genders);
        });
        this.pi_comboBoxGender.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.H) {
                this.pi_comboBoxGender.getSelectionModel().select(0);
            } else if (keyEvent.getCode() == KeyCode.M) {
                this.pi_comboBoxGender.getSelectionModel().select(1);
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

        DATA_MEMBERSHIPS.ReadMemberships().thenAccept(model_memberships -> {
            this.ms_comboBoxMemberships.setItems(model_memberships);
        });

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
        this.buttonAction.setOnAction(actionEvent -> CompletableFuture.runAsync(this::eventHandlerRegister));
        this.buttonCancel.setOnAction((actionEvent) -> {
            this.clearForm(true);
            this.membersController.unselectTable();
        });

        Platform.runLater(() -> new FadeIn(this.membersController.boxMembersPane));
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
        Platform.runLater(() -> {
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
                new FadeInUp(this.scrollPane).play();
            }
        });
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
    private void eventHandlerRegister() {
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
                    MODEL_DEBTS finalModelDebts = modelDebts;
                    if (this.booleanUpdater.isListener()) {
                        if (this.booleanUpdater.isChanged("photo")) {
                            DATA_MEMBERS_PHOTOS.UpdatePhoto(this.modelMembers.getIdMember(), this.photoHandler.getPhoto());
                        }
                        if (this.booleanUpdater.isChanged("name")) {
                            DATA_MEMBERS.UpdateName(this.modelMembers.getIdMember(), memberModel.getName());
                        }

                        if (this.booleanUpdater.isChanged("lastName")) {
                            DATA_MEMBERS.UpdateLastName(this.modelMembers.getIdMember(), memberModel.getLastName());
                        }
                        if (this.booleanUpdater.isChanged("gender")) {
                            DATA_MEMBERS.UpdateGender(this.modelMembers.getIdMember(), memberModel.getGender());
                        }
                        if (this.booleanUpdater.isChanged("phone")) {
                            DATA_MEMBERS.UpdatePhone(this.modelMembers.getIdMember(), memberModel.getPhone());
                        }
                        if (this.booleanUpdater.isChanged("email")) {
                            DATA_MEMBERS.UpdateEmail(this.modelMembers.getIdMember(), memberModel.getEmail());
                        }
                        if (this.booleanUpdater.isChanged("notes")) {
                            DATA_MEMBERS.UpdateNotes(this.modelMembers.getIdMember(), memberModel.getNotes());
                        }
                        if (this.booleanUpdater.isChanged("fingerprint")) {
//                                flag =
                        }
                        if (this.booleanUpdater.isChanged("renewMembership")) {
                            DATA_PAYMENTS_MEMBERSHIPS.CreatePaymentMembership(this.modelMembers.getIdMember(), modelMemberships).thenAccept(idPaymentMembership -> {
                                if (finalModelDebts != null && idPaymentMembership > 0) {
                                    DATA_DEBTS.CreateDebt(finalModelDebts, this.modelMembers.getIdMember(), 1);
                                }
                            });
                        } else if (this.booleanUpdater.isChanged("changeMembership") && this.modelMembers.getModelPaymentsMemberships() != null) {
                            DATA_MEMBERSHIPS.UpdateMembership(this.modelMembers.getIdMember(), modelMemberships);
                            if (modelDebts != null) {
                                DATA_DEBTS.CreateDebt(modelDebts, this.modelMembers.getIdMember(), 1);
                            }
                        }
                        Notifications.success("Información actualizada", "[ " + this.modelMembers.getIdMember() + " ] " + memberModel.getName() + " información actualizada.");
                    } else {
                        MODEL_MEMBERSHIPS finalModelMemberships = modelMemberships;
                        MODEL_MEMBERS finalMemberModel = memberModel;
                        DATA_MEMBERS.CreateMember(memberModel).thenAccept(idMember -> {
                            if (idMember > 0) {
                                DATA_MEMBERS_PHOTOS.CreatePhoto(idMember, photoHandler.getPhoto());
                                DATA_MEMBERS_FINGERPRINTS.InsertFingerprints(idMember, Fingerprint.getFingerprints());
                                DATA_PAYMENTS_MEMBERSHIPS.CreatePaymentMembership(idMember, finalModelMemberships).thenAccept(idLastMembership -> {
                                    if (finalModelDebts != null && idLastMembership > 0) {
                                        DATA_DEBTS.CreateDebt(finalModelDebts, idMember, 1);
                                    }
                                });

                                Notifications.success("Nuevo socio", "[ " + idMember + " ] " + finalMemberModel.getName() + " ha sido registrado.");
                            }
                        });
                    }

                    FadeOutDown fadeOutDown = new FadeOutDown(this.scrollPane);
                    fadeOutDown.setOnFinished(actionEvent -> {
                        this.clearForm(true);
                        this.membersController.refreshTable();
                    });
                    this.membersController.disableTable();
                    fadeOutDown.play();

                    // Clear memory data
                    nodesRequired = null;
                    formValid = null;
                    memberModel = null;
                    modelMemberships = null;
                    modelDebts = null;
                }
            }
        }
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
                    DATA_MEMBERS.UpdateAccess(this.modelMembers.getIdMember(), this.modelMembers.isAccess()).thenAccept(bool_access -> {
                        if (bool_access) {
                            this.modelMembers.setAccess(!this.modelMembers.isAccess());
                            this.sh_buttonOpenDoor.setDisable(true);
                            this.sh_buttonAccess.getStyleClass().set(3, Input.styleToColor(Styles.SUCCESS));
                            this.sh_buttonAccess.setText("Desbloquear acceso");
                            Notifications.danger("Acceso bloqueado", this.modelMembers.getName() + " ha perdido el acceso a los gimnasios.");
                            // TODO: ABONAR MENSUALIDADES
                        }
                    });
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
                    DATA_MEMBERS.UpdateAccess(this.modelMembers.getIdMember(), this.modelMembers.isAccess()).thenAccept(bool_access -> {
                        this.modelMembers.setAccess(!this.modelMembers.isAccess());
                        this.sh_buttonOpenDoor.setDisable(false);
                        this.sh_buttonAccess.getStyleClass().set(3, Input.styleToColor(Styles.DANGER));
                        this.sh_buttonAccess.setText("Bloquear acceso");
                        Notifications.success("Acceso desbloqueado", this.modelMembers.getName() + " puede entrar a los gimnasios nuevamente.");
                    });
                }
            }
        };
    }

    private EventHandler<ActionEvent> eventHandlerPayDebt() {
        return actionEvent -> {
            DATA_DEBTS.ReadDebts(this.modelMembers.getIdMember()).thenAccept(model_debts -> {
                Dialog dialog = new Dialog(
                        Styles.CREATIVE,
                        "Abonar mensualidad",
                        model_debts,
                        Dialog.OK, Dialog.CANCEL
                );
                if (dialog.show()) {

                }
            });
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
