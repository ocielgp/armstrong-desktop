package com.ocielgp.controller;

import animatefx.animation.FadeIn;
import animatefx.animation.FadeInUp;
import animatefx.animation.FadeOutDown;
import animatefx.animation.Shake;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import com.ocielgp.app.Application;
import com.ocielgp.dao.*;
import com.ocielgp.fingerprint.Fingerprint_Controller;
import com.ocielgp.models.Model_Debt;
import com.ocielgp.models.Model_Member;
import com.ocielgp.models.Model_Membership;
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

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class Controller_Member implements Initializable {
    @FXML
    private VBox boxMemberDetails;
    // attributes
    private final Controller_Members controllerMembers;
    @FXML
    private ScrollPane boxScrollPane;
    @FXML
    private Label t_labelTitle;
    // -> title [t]
    @FXML
    private HBox titlePane;
    @FXML
    private Label qv_labelRegistrationDate;
    @FXML
    private Label qv_labelLastPayment;
    @FXML
    private Label qv_labelGym;
    @FXML
    private Label qv_staffName;
    // -> quick view [qv]
    @FXML
    private VBox boxQuickView;
    // shortcut [s]
    @FXML
    private VBox boxShortcut;
    @FXML
    private JFXButton s_buttonOpenDoor;
    @FXML
    private JFXButton s_buttonAccess;
    @FXML
    private JFXButton s_buttonPayDebt;
    @FXML
    private JFXButton ph_buttonDeletePhoto;
    @FXML
    private JFXButton ph_buttonUploadPhoto;
    // -> photo [ph]
    @FXML
    private ImageView ph_imgMemberPhoto;
    @FXML
    private JFXTextField pi_fieldLastName;
    // -> personal information [pi]
    @FXML
    private JFXTextField pi_fieldName;
    @FXML
    private JFXTextField pi_fieldPhone;
    @FXML
    private JFXTextField pi_fieldEmail;
    @FXML
    private JFXTextField pi_fieldNotes;
    @FXML
    private JFXComboBox<String> pi_comboBoxGender;
    @FXML
    private VBox fp_boxFingerprint;
    @FXML
    private Label fp_labelFingerprintCounter;
    @FXML
    private JFXButton fp_buttonCapture;
    @FXML
    private JFXButton fp_buttonRestartCapture;
    // -> fingerprint [fp]
    @FXML
    private VBox boxFingerprint;
    @FXML
    private HBox ms_boxEndDate;
    @FXML
    private Label ms_labelEndDate;
    @FXML
    private HBox ms_boxRenewMembership;
    @FXML
    private JFXButton ms_buttonRenewMembership;
    // -> memberships [ms]
    @FXML
    private JFXComboBox<Model_Membership> ms_comboBoxMemberships;
    @FXML
    private JFXToggleButton pym_togglePayment;
    @FXML
    private VBox pym_boxOwe;
    @FXML
    private JFXTextField pym_fieldPaidOut;
    @FXML
    private JFXTextField pym_fieldOwe;
    // -> payment [pym]
    @FXML
    private VBox boxPayment;
    @FXML
    private JFXButton buttonClear;
    // -> end buttons
    @FXML
    private JFXButton buttonAction;
    private BooleanUpdater booleanUpdater;
    private PhotoHandler photoHandler;
    private Model_Member modelMember = null;

    public Controller_Member(Controller_Members controllerMembers) {
        this.controllerMembers = controllerMembers;
    }

    public void loadMember(int idMember) {
        FadeOutDown fadeOutDown = new FadeOutDown(this.boxScrollPane);
        fadeOutDown.setOnFinished(actionEvent -> {
//            this.boxScrollPane.setVvalue(0.0);
            if (this.modelMember != null) {
                this.clearForm(false);
            }
            JDBC_Member.ReadMember(idMember).thenAccept(model_members -> {
                        this.modelMember = model_members;
                        this.modelMember.setIdMember(idMember);
                    }).thenRunAsync(() -> Fingerprint_Controller.loadFingerprints(idMember))
                    .thenRunAsync(() -> JDBC_Payment_Membership.ReadLastPayment(idMember).thenAccept(model_payments_memberships -> {
                        this.modelMember.setModelPaymentMembership(model_payments_memberships);
                        if (model_payments_memberships == null) {
                            JDBC_Gym.ReadGym(this.modelMember.getIdGym())
                                    .thenAccept(model_gyms -> {
                                        this.modelMember.setModelGyms(model_gyms);
                                        this.initForm();
                                    });
                        } else {
                            JDBC_Member.ReadMember(model_payments_memberships.getIdStaff()).thenAccept(model_members -> {
                                JDBC_Gym.ReadGym(model_payments_memberships.getIdGym()).thenAccept(model_gyms -> {
                                    this.modelMember.setModelGyms(model_gyms);
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
        if (this.modelMember != null) {
            Platform.runLater(() -> {
                // init form
                this.titlePane.getStyleClass().set(0, Input.styleToColor(JDBC_Member.ReadStyle(this.modelMember.getIdMember())));
                this.t_labelTitle.setText("[ " + this.modelMember.getIdMember() + " ] " + this.modelMember.getName().toUpperCase());

                // quick view
                this.qv_labelRegistrationDate.setText(
                        DateFormatter.getDateWithDayName(
                                LocalDate.parse(this.modelMember.getRegistrationDate())
                        )
                );
                if (this.modelMember.getModelPaymentMembership() != null) {
                    this.qv_labelLastPayment.setText(
                            DateFormatter.getDateWithDayName(
                                    LocalDate.parse(this.modelMember.getModelPaymentMembership().getStartDate())
                            )
                    );
                } else {
                    this.qv_labelLastPayment.setText("N / A");
                }
                this.qv_labelGym.setText(this.modelMember.getModelGyms().getName());
                this.boxQuickView.setVisible(true);


                // shortcut
                this.initButtonAccess();
                this.s_buttonAccess.setOnAction(actionEvent -> CompletableFuture.runAsync(this::eventAccess));

                this.s_buttonPayDebt.setVisible(Styles.CREATIVE == Input.colorToStyle(this.titlePane.getStyleClass().get(0)));
                this.boxShortcut.setVisible(true);

                // photo
                this.photoHandler.setPhoto(this.modelMember.getModelMemberPhoto().getPhoto());

                // personal information
                this.pi_fieldName.setText(this.modelMember.getName());
                this.pi_fieldLastName.setText(this.modelMember.getLastName());
                this.pi_comboBoxGender.getSelectionModel().select(this.modelMember.getGender());
                this.pi_fieldPhone.setText(this.modelMember.getPhone());
                this.pi_fieldEmail.setText(this.modelMember.getEmail());
                this.pi_fieldNotes.setText(this.modelMember.getNotes());

                // membership
                if (this.modelMember.getModelPaymentMembership() != null) {
                    this.initRenewMembership();

                    this.ms_comboBoxMemberships.setDisable(true);
                    this.ms_boxRenewMembership.setVisible(true);

                    this.selectPreviousMembership();
                    this.ms_labelEndDate.setText(
                            DateFormatter.getDateWithDayName(LocalDate.parse(this.modelMember.getModelPaymentMembership().getEndDate()
                            ))
                    );
                    this.ms_boxEndDate.setVisible(true);
                }

                if (this.ms_comboBoxMemberships.getSelectionModel().getSelectedIndex() == -1) {
                    Notifications.warn("Sin membresía", "Última membresía no encontrada.");
                }

                this.boxPayment.setVisible(false);
                this.boxPayment.setDisable(false);

                Loading.close();
                this.controllerMembers.enableTable();
                new FadeInUp(this.boxScrollPane).play();
                this.boxMemberDetails.requestFocus();

                // End buttons
                this.buttonAction.setText("Guardar cambios");
                this.buttonAction.setDisable(true);
                this.buttonClear.setText("Cancelar");

                this.booleanUpdater.setListener(true);
            });
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Input.getScrollEvent(this.boxScrollPane);
        this.boxScrollPane.opacityProperty().addListener(((observableValue, oldValue, newValue) -> {
            if (this.modelMember != null) {
                if (newValue.doubleValue() > 0) {
                    this.boxScrollPane.setVvalue(1d);
                }
            } else {
                this.boxScrollPane.setVvalue(0d);
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


        // set max length
        Input.createMaxLengthEvent(this.pi_fieldName, Model_Member.nameLength);
        Input.createMaxLengthEvent(this.pi_fieldLastName, Model_Member.lastNameLength);
        Input.createMaxLengthEvent(this.pi_fieldPhone, Model_Member.phoneLength);
        Input.createMaxLengthEvent(this.pi_fieldEmail, Model_Member.emailLength);
        Input.createMaxLengthEvent(this.pi_fieldNotes, Model_Member.notesLength);
        // todo: ADD ALL MAX LENGTH RESTANTES

        // remove red line when input is focused
        this.pi_comboBoxGender.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            this.pi_comboBoxGender.getStyleClass().remove("red-border-input-line");
            if (newValue && !this.pi_comboBoxGender.isShowing()) {
                this.pi_comboBoxGender.show();
            }
        });
        this.ms_comboBoxMemberships.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            this.ms_comboBoxMemberships.getStyleClass().remove("red-border-input-line");
            if (newValue && !this.ms_comboBoxMemberships.isShowing()) {
                this.ms_comboBoxMemberships.show();
            }
        });

        // quick view
        Input.createVisibleProperty(this.boxQuickView);

        // shortcut
        Input.createVisibleProperty(this.boxShortcut);
        Input.createVisibleProperty(this.s_buttonPayDebt);
        this.s_buttonPayDebt.setOnAction(this.eventHandlerPayDebt()); // TODO

        // photo section
        this.photoHandler = new PhotoHandler(this.booleanUpdater, this.ph_imgMemberPhoto, this.ph_buttonUploadPhoto, this.ph_buttonDeletePhoto);

        // personal information
        JDBC_Member.ReadGenders().thenAccept(genders -> {
            this.pi_comboBoxGender.setItems(genders);
        });
        this.pi_comboBoxGender.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.H) {
                this.pi_comboBoxGender.getSelectionModel().select(0);
            } else if (keyEvent.getCode() == KeyCode.M) {
                this.pi_comboBoxGender.getSelectionModel().select(1);
            }
        });

        // fingerprint
        Fingerprint_Controller.setFingerprintBox(this.boxFingerprint, this.fp_boxFingerprint, this.fp_labelFingerprintCounter, this.fp_buttonCapture, this.fp_buttonRestartCapture);

        // membership
        Input.createVisibleProperty(this.ms_boxRenewMembership);
        this.ms_boxEndDate.setVisible(false);
        this.ms_comboBoxMemberships.requestFocus();
        this.ms_comboBoxMemberships.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                this.ms_labelEndDate.setText(DateFormatter.getDateWithDayName(DateFormatter.plusDaysToCurrentDate(newValue.getDays())));
                this.ms_boxEndDate.setVisible(true);
                this.boxPayment.setVisible(true);
                this.boxPayment.setDisable(false);

                if (!this.pym_togglePayment.isSelected()) {
                    this.pym_fieldOwe.setText(String.valueOf(newValue.getPrice()));
                }

                if (this.booleanUpdater.isListener()) {
                    if (this.modelMember != null && this.modelMember.getModelPaymentMembership() != null) {
                        this.booleanUpdater.change("changeMembership", false);
                    } else {
                        this.booleanUpdater.change("renewMembership", false);
                    }
                }
            } else {
                this.boxPayment.setDisable(true);
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

        JDBC_Membership.ReadMemberships().thenAccept(model_memberships -> {
            this.ms_comboBoxMemberships.setItems(model_memberships);
        });

        // payment
        Input.createVisibleProperty(this.boxPayment, true);
        this.boxPayment.disabledProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                this.pym_togglePayment.setSelected(true);
            }
        });
        this.boxPayment.setDisable(true);
        this.pym_togglePayment.selectedProperty().addListener((observable, oldValue, newValue) -> {
            paymentChanges(newValue); // have a debt

            if (this.booleanUpdater.isListener()) {
                if (this.modelMember != null && this.modelMember.getModelPaymentMembership() != null) {
                    this.booleanUpdater.change("changeMembership", false);
                } else {
                    this.booleanUpdater.change("renewMembership", false);
                }
            }
        });
        this.paymentChanges(this.pym_togglePayment.isSelected());
        this.pym_fieldPaidOut.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!this.pym_togglePayment.isSelected()) {
                if (Validator.moneyValidator(new InputDetails(this.pym_fieldPaidOut, this.pym_fieldPaidOut.getText()), false)) {
                    this.pym_fieldOwe.setText(
                            this.ms_comboBoxMemberships.getSelectionModel().getSelectedItem().getPrice().subtract(new BigDecimal(newValue)).toString()
                    );
                } else {
                    this.pym_fieldOwe.setText(
                            this.ms_comboBoxMemberships.getSelectionModel().getSelectedItem().getPrice().toString()
                    );
                }
            }
        });

        // end buttons section
        this.buttonAction.setOnAction(actionEvent -> CompletableFuture.runAsync(this::eventRegister));
        this.buttonClear.setOnAction((actionEvent) -> {
            if (this.buttonClear.getText().equals("Cancelar")) {
                this.boxPayment.setVisible(true);
            }
            this.clearForm(true);
            this.controllerMembers.unselectTable();
        });

        Platform.runLater(() -> new FadeIn(this.controllerMembers.boxMembersPane));
    }

    private void paymentChanges(boolean visible) {
        this.pym_boxOwe.setVisible(!visible);
        this.pym_boxOwe.setManaged(!visible);

        if (visible) {
            Input.clearInputs(this.pym_fieldPaidOut, this.pym_fieldOwe);
        } else {
            new FadeIn(this.pym_boxOwe).play();
            this.pym_fieldOwe.setText(String.valueOf(this.ms_comboBoxMemberships.getSelectionModel().getSelectedItem().getPrice()));
        }
    }

    private void clearForm(boolean animation) {
        Platform.runLater(() -> {
            this.modelMember = null;
            this.booleanUpdater.setListener(false);
            this.titlePane.getStyleClass().set(0, "default-style");
            this.t_labelTitle.setText("NUEVO SOCIO");

            this.boxQuickView.setVisible(false);
            this.boxShortcut.setVisible(false);

            this.photoHandler.resetHandler();

            Fingerprint_Controller.FB_RestartCapture();

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


            if (animation) {
                new FadeInUp(this.boxScrollPane).play();
            }

            this.buttonAction.setDisable(false);
            this.buttonAction.setText("Registrar");
            this.buttonClear.setText("Limpiar");
        });
    }

    // Init shortcut pane
    private void initButtonAccess() {
        Platform.runLater(() -> {
            if (this.modelMember.isAccess()) {
                this.s_buttonAccess.getStyleClass().set(3, Input.styleToColor(Styles.DANGER));
                this.s_buttonAccess.setText("Bloquear acceso");
                this.s_buttonOpenDoor.setDisable(false);
            } else {
                this.s_buttonAccess.getStyleClass().set(3, Input.styleToColor(Styles.SUCCESS));
                this.s_buttonAccess.setText("Desbloquear acceso");
                this.s_buttonOpenDoor.setDisable(true);
            }
        });
    }

    // Event Handlers
    private void eventRegister() {
        ArrayList<InputDetails> nodesRequired = new ArrayList<>();
        // Personal information section
        nodesRequired.add(new InputDetails(this.pi_fieldName, this.pi_fieldName.getText()));
        nodesRequired.add(new InputDetails(this.pi_fieldLastName, this.pi_fieldLastName.getText()));
        nodesRequired.add(new InputDetails(this.pi_comboBoxGender, String.valueOf(this.pi_comboBoxGender.getSelectionModel().getSelectedIndex())));
        nodesRequired.add(new InputDetails(Application.getCurrentGymNode(), String.valueOf(Application.getCurrentGymNode().getSelectionModel().getSelectedIndex())));

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

                Model_Member modelMember = new Model_Member();
                modelMember.setName(Input.capitalizeFirstLetterPerWord(this.pi_fieldName.getText()));
                modelMember.setLastName(Input.capitalizeFirstLetterPerWord(this.pi_fieldLastName.getText()));
                modelMember.setGender(this.pi_comboBoxGender.getSelectionModel().getSelectedItem());

                modelMember.setPhone(Input.spaceRemover(this.pi_fieldPhone.getText()));
                modelMember.setEmail(Input.spaceRemover(this.pi_fieldEmail.getText().toLowerCase()));
                modelMember.setNotes(Input.capitalizeFirstLetter(this.pi_fieldNotes.getText()));
                modelMember.setIdGym(Application.getCurrentGym().getIdGym());

                // Model_Membership
                Model_Membership modelMembership;
                modelMembership = this.ms_comboBoxMemberships.getSelectionModel().getSelectedItem();

                // Payment
                Model_Debt modelDebt = null;
                if (!this.pym_togglePayment.isSelected()) { // !false
                    BigDecimal paidOut = new BigDecimal(this.pym_fieldPaidOut.getText());
                    BigDecimal owe = new BigDecimal(this.pym_fieldOwe.getText());

                    if (owe.compareTo(BigDecimal.ZERO) == 0) {
                        formValid = false;
                        Notifications.danger("Error", "Se trata de un pago completo, no hay deuda.");
                        Validator.shakeInput(this.pym_fieldPaidOut);
                    } else if (owe.compareTo(BigDecimal.ZERO) < 0) {
                        formValid = false;
                        Notifications.danger("Error", "La deuda es mayor al total a pagar.");
                        Validator.shakeInput(this.pym_fieldPaidOut);
                    } else {
                        modelDebt = new Model_Debt();
                        modelDebt.setPaidOut(paidOut);
                        modelDebt.setOwe(owe);
                        modelDebt.setAmount(1);
                        modelDebt.setDescription(modelMembership.getDescription() + " (" + modelMembership.getDays() + ") días");
                    }
                }

                if (formValid) { // Save into data server
                    Model_Debt finalModelDebt = modelDebt;
                    if (this.booleanUpdater.isListener()) {
                        if (this.booleanUpdater.isChanged("photo")) {
                            JDBC_Member_Photo.UpdatePhoto(this.modelMember.getIdMember(), this.photoHandler.getPhoto());
                        }
                        if (this.booleanUpdater.isChanged("name")) {
                            JDBC_Member.UpdateName(this.modelMember.getIdMember(), modelMember.getName());
                        }

                        if (this.booleanUpdater.isChanged("lastName")) {
                            JDBC_Member.UpdateLastName(this.modelMember.getIdMember(), modelMember.getLastName());
                        }
                        if (this.booleanUpdater.isChanged("gender")) {
                            JDBC_Member.UpdateGender(this.modelMember.getIdMember(), modelMember.getGender());
                        }
                        if (this.booleanUpdater.isChanged("phone")) {
                            JDBC_Member.UpdatePhone(this.modelMember.getIdMember(), modelMember.getPhone());
                        }
                        if (this.booleanUpdater.isChanged("email")) {
                            JDBC_Member.UpdateEmail(this.modelMember.getIdMember(), modelMember.getEmail());
                        }
                        if (this.booleanUpdater.isChanged("notes")) {
                            JDBC_Member.UpdateNotes(this.modelMember.getIdMember(), modelMember.getNotes());
                        }
                        if (this.booleanUpdater.isChanged("fingerprint")) {
                            JDBC_Member_Fingerprint.CreateFingerprints(this.modelMember.getIdMember(), Fingerprint_Controller.getFingerprints());
                        }
                        if (this.booleanUpdater.isChanged("renewMembership")) {
                            JDBC_Payment_Membership.CreatePaymentMembership(this.modelMember.getIdMember(), modelMembership).thenAccept(idPaymentMembership -> {
                                if (finalModelDebt != null && idPaymentMembership > 0) {
                                    JDBC_Debt.CreateDebt(finalModelDebt, this.modelMember.getIdMember(), 1);
                                }
                            });
                        } else if (this.booleanUpdater.isChanged("changeMembership") && this.modelMember.getModelPaymentMembership() != null) {
                            JDBC_Membership.UpdateMembership(this.modelMember.getIdMember(), modelMembership);
                            if (modelDebt != null) {
                                JDBC_Debt.CreateDebt(modelDebt, this.modelMember.getIdMember(), 1);
                            }
                        }
                        Notifications.success("Información actualizada", "[ " + this.modelMember.getIdMember() + " ] " + modelMember.getName() + " información actualizada.");
                    } else {
                        Model_Membership finalModelMembership = modelMembership;
                        Model_Member finalModelMember = modelMember;
                        JDBC_Member.CreateMember(modelMember).thenAccept(idMember -> {
                            if (idMember > 0) {
                                JDBC_Member_Photo.CreatePhoto(idMember, photoHandler.getPhoto());
                                JDBC_Member_Fingerprint.CreateFingerprints(idMember, Fingerprint_Controller.getFingerprints());
                                JDBC_Payment_Membership.CreatePaymentMembership(idMember, finalModelMembership).thenAccept(idLastMembership -> {
                                    if (finalModelDebt != null && idLastMembership > 0) {
                                        JDBC_Debt.CreateDebt(finalModelDebt, idMember, 1);
                                    }
                                });

                                Notifications.success("Nuevo socio", "[ " + idMember + " ] " + finalModelMember.getName() + " ha sido registrado.");
                            }
                        });
                    }

                    FadeOutDown fadeOutDown = new FadeOutDown(this.boxScrollPane);
                    fadeOutDown.setOnFinished(actionEvent -> {
                        this.clearForm(true);
                        this.controllerMembers.refreshTable();
                    });
                    this.controllerMembers.disableTable();
                    fadeOutDown.play();

                    // Clear memory data
                    fadeOutDown = null;
                    nodesRequired = null;
                    formValid = null;
                    modelMember = null;
                    modelMembership = null;
                    modelDebt = null;
                }
            }
        }
    }

    private void eventAccess() {
        Platform.runLater(() -> {
            if (Styles.DANGER == Input.colorToStyle(this.s_buttonAccess.getStyleClass().get(3))) {
                Dialog dialog = new Dialog(
                        Styles.DANGER,
                        "Bloquear acceso",
                        "Se le bloqueara el acceso a todos los gimnasios a " + this.modelMember.getName() + " " + this.modelMember.getLastName() + ", ¿quieres continuar?",
                        DialogTypes.MESSAGE,
                        Dialog.YES, Dialog.CANCEL
                );
                if (dialog.show()) {
                    JDBC_Member.UpdateAccess(this.modelMember.getIdMember(), this.modelMember.isAccess()).thenAccept(bool_access -> {
                        if (bool_access) {
                            Platform.runLater(() -> {
                                this.modelMember.setAccess(!this.modelMember.isAccess());
                                this.s_buttonOpenDoor.setDisable(true);
                                this.s_buttonAccess.getStyleClass().set(3, Input.styleToColor(Styles.SUCCESS));
                                this.s_buttonAccess.setText("Desbloquear acceso");
                                Notifications.danger("Acceso bloqueado", this.modelMember.getName() + " ha perdido el acceso a los gimnasios");
                                this.boxScrollPane.requestFocus();
                            });
                        }
                    });
                }
            } else {
                Dialog dialog = new Dialog(
                        Styles.SUCCESS,
                        "Desbloquear acceso",
                        "Se desbloqueara el acceso a " + this.modelMember.getName() + " " + this.modelMember.getLastName() + ", ¿quieres continuar?",
                        DialogTypes.MESSAGE,
                        Dialog.YES, Dialog.CANCEL
                );
                if (dialog.show()) {
                    JDBC_Member.UpdateAccess(this.modelMember.getIdMember(), this.modelMember.isAccess()).thenAccept(bool_access -> {
                        Platform.runLater(() -> {
                            this.modelMember.setAccess(!this.modelMember.isAccess());
                            this.s_buttonOpenDoor.setDisable(false);
                            this.s_buttonAccess.getStyleClass().set(3, Input.styleToColor(Styles.DANGER));
                            this.s_buttonAccess.setText("Bloquear acceso");
                            Notifications.success("Acceso desbloqueado", this.modelMember.getName() + " puede entrar a los gimnasios nuevamente");
                            this.boxScrollPane.requestFocus();
                        });
                    });
                }
            }
        });
    }

    private EventHandler<ActionEvent> eventHandlerPayDebt() {
        return actionEvent -> {
            JDBC_Debt.ReadDebts(this.modelMember.getIdMember()).thenAccept(model_debts -> {
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
            if (Styles.SUCCESS == Input.colorToStyle(this.s_buttonAccess.getStyleClass().get(3))) {
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
            if (Styles.SUCCESS == Input.colorToStyle(this.s_buttonAccess.getStyleClass().get(3))) {
                new Shake(this.ms_buttonRenewMembership).play();
                Notifications.danger("Acceso bloqueado", "Para realizar esta acción, el usuario no debe tener bloqueos.");
            } else if (this.s_buttonPayDebt.isVisible()) {
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
        if (this.modelMember != null && this.modelMember.getModelPaymentMembership() != null) {
            for (Model_Membership modelMembership : this.ms_comboBoxMemberships.getItems()) {
                if (modelMembership.getIdMembership() == this.modelMember.getModelPaymentMembership().getIdMembership()) {
                    this.ms_comboBoxMemberships.getSelectionModel().select(modelMembership);
                }
            }
        }
    }

    private void enableComboMemberships(boolean enable) {
        if (enable) {
            new Shake(this.ms_comboBoxMemberships).play();
            this.boxPayment.setVisible(true);
            this.boxPayment.setDisable(this.ms_comboBoxMemberships.getSelectionModel().getSelectedIndex() == -1);
        } else {
            this.selectPreviousMembership();
        }

        if (this.modelMember != null && this.modelMember.getModelPaymentMembership() != null) {
            this.ms_comboBoxMemberships.setDisable(!enable);
            this.ms_boxEndDate.setVisible(true);
        } else {
            this.ms_comboBoxMemberships.setDisable(false);
            this.ms_boxEndDate.setVisible(false);
        }
    }

    private void initRenewMembership() {
        if (this.modelMember != null && this.modelMember.getModelPaymentMembership() != null) {
            if (DateFormatter.daysDifferenceToday(LocalDate.parse(this.modelMember.getModelPaymentMembership().getStartDate())) == 0) {
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

        if (this.modelMember != null && this.modelMember.getModelPaymentMembership() != null) {
            this.ms_labelEndDate.setText(
                    DateFormatter.getDateWithDayName(LocalDate.parse(this.modelMember.getModelPaymentMembership().getEndDate()))
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
                        Validator.compare(this.pi_fieldName.getText(), this.modelMember.getName())
                );
            }
        });
        this.pi_fieldLastName.setOnKeyTyped(keyEvent -> {
            if (this.booleanUpdater.isListener()) {
                this.booleanUpdater.change(
                        "lastName",
                        Validator.compare(this.pi_fieldLastName.getText(), this.modelMember.getLastName())
                );
            }
        });
        this.pi_comboBoxGender.setOnAction(keyEvent -> {
            if (this.booleanUpdater.isListener()) {
                this.booleanUpdater.change(
                        "gender",
                        Validator.compare(this.pi_comboBoxGender.getSelectionModel().getSelectedItem(), this.modelMember.getGender())
                );
            }
        });
        this.pi_fieldPhone.setOnKeyTyped(keyEvent -> {
            if (this.booleanUpdater.isListener()) {
                this.booleanUpdater.change(
                        "phone",
                        Validator.compare(this.pi_fieldPhone.getText(), this.modelMember.getPhone())
                );
            }
        });
        this.pi_fieldEmail.setOnKeyTyped(keyEvent -> {
            if (this.booleanUpdater.isListener()) {
                this.booleanUpdater.change(
                        "email",
                        Validator.compare(this.pi_fieldEmail.getText(), this.modelMember.getEmail())
                );
            }
        });
        this.pi_fieldNotes.setOnKeyTyped(keyEvent -> {
            if (this.booleanUpdater.isListener()) {
                this.booleanUpdater.change(
                        "notes",
                        Validator.compare(this.pi_fieldNotes.getText(), this.modelMember.getNotes())
                );
            }
        });
        this.fp_labelFingerprintCounter.textProperty().addListener((observable, oldValue, newValue) -> {
            if (this.booleanUpdater.isListener() && !oldValue.equals(newValue)) {
                this.booleanUpdater.change(
                        "fingerprint",
                        false
                );
            }
        });
    }
}
