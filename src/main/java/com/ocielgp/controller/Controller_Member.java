package com.ocielgp.controller;

import animatefx.animation.FadeIn;
import animatefx.animation.FadeInRight;
import animatefx.animation.FadeOutRight;
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
import com.ocielgp.models.Model_Staff_Member;
import com.ocielgp.utilities.*;
import javafx.application.Platform;
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
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class Controller_Member implements Initializable {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox boxMember;

    // -> title [t]
    @FXML
    private Label t_labelTitle;
    @FXML
    private HBox boxTitle;

    // -> quick view [qv]
    @FXML
    private VBox boxQuickView;
    @FXML
    private Label qv_labelRegistrationDate;
    @FXML
    private Label qv_labelLastPayment;
    @FXML
    private Label qv_labelGym;
    @FXML
    private Label qv_staffName;

    // -> photo [ph]
    @FXML
    private ImageView ph_imgMemberPhoto;
    @FXML
    private JFXButton ph_buttonUploadPhoto;
    @FXML
    private JFXButton ph_buttonDeletePhoto;

    // -> personal information [pi]
    @FXML
    private JFXTextField pi_fieldName;
    @FXML
    private JFXTextField pi_fieldLastName;
    @FXML
    private JFXComboBox<String> pi_comboBoxGender;
    @FXML
    private JFXTextField pi_fieldPhone;
    @FXML
    private JFXTextField pi_fieldEmail;
    @FXML
    private JFXTextField pi_fieldNotes;


    // -> fingerprint [fp]
    @FXML
    private VBox boxFingerprint;
    @FXML
    private VBox fp_boxFingerprint;
    @FXML
    private Label fp_labelFingerprintCounter;
    @FXML
    private JFXButton fp_buttonCapture;
    @FXML
    private JFXButton fp_buttonRestartCapture;


    // -> memberships [ms]
    @FXML
    private JFXComboBox<Model_Membership> ms_comboBoxMemberships;
    @FXML
    private HBox ms_boxEndDate;
    @FXML
    private Label ms_labelEndDate;
    @FXML
    private HBox ms_boxButtons;

    // -> payment [pym]
    @FXML
    private VBox boxPayment;
    @FXML
    private JFXToggleButton pym_togglePayment;
    @FXML
    private VBox pym_boxOwe;
    @FXML
    private JFXTextField pym_fieldPaidOut;
    @FXML
    private JFXTextField pym_fieldOwe;

    // -> shortcut [s]
    @FXML
    private VBox boxShortcut;
    @FXML
    private JFXButton s_buttonOpenDoor;
    @FXML
    private JFXButton s_buttonAccess;
    @FXML
    private JFXButton s_buttonPayDebt;


    // -> end buttons
    @FXML
    private JFXButton buttonAction;
    @FXML
    private JFXButton buttonClear;

    // attributes
    private FormChangeListener formChangeListener;
    private PhotoHandler photoHandler;
    private Model_Member modelMember = null;
    private final Controller_Members controllerMembers;

    public Controller_Member(Controller_Members controllerMembers) {
        this.controllerMembers = controllerMembers;
    }

    private void configureForm() {
        Input.getScrollEvent(this.scrollPane);
        this.boxMember.heightProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue.doubleValue() > oldValue.doubleValue() && oldValue.doubleValue() != 0) {
                Platform.runLater(() -> this.scrollPane.setVvalue(1d));
            } else {
                if (this.modelMember != null) Platform.runLater(() -> this.scrollPane.setVvalue(0d));
            }
        });

        // set max length
        Input.createMaxLengthEvent(this.pi_fieldName, Model_Member.nameLength);
        Input.createMaxLengthEvent(this.pi_fieldLastName, Model_Member.lastNameLength);
        Input.createMaxLengthEvent(this.pi_fieldPhone, Model_Member.phoneLength);
        Input.createMaxLengthEvent(this.pi_fieldEmail, Model_Member.emailLength);
        Input.createMaxLengthEvent(this.pi_fieldNotes, Model_Member.notesLength);
        // todo: ADD ALL MAX LENGTH RESTANTES
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configureForm();
        createFormChangeListener();
        Input.createComboBoxListener(this.pi_comboBoxGender, this.ms_comboBoxMemberships);

        // quick view
        Input.createVisibleProperty(this.boxQuickView, false);

        // photo section
        this.photoHandler = new PhotoHandler(this.formChangeListener, this.ph_imgMemberPhoto, this.ph_buttonUploadPhoto, this.ph_buttonDeletePhoto);

        // personal information
        JDBC_Member.ReadGenders().thenAccept(genders -> this.pi_comboBoxGender.setItems(genders));
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
        JDBC_Membership.ReadMemberships().thenAccept(model_memberships -> this.ms_comboBoxMemberships.setItems(model_memberships));
        Input.createVisibleProperty(this.ms_boxEndDate, false);
        this.ms_comboBoxMemberships.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Platform.runLater(() -> { // TODO: SUPPORTS MONTHS, DAYS, THEN ADD THE AMOUNT IF IS MONTH
                    this.ms_labelEndDate.setText(DateFormatter.getDateWithDayName(DateFormatter.plusDaysToCurrentDate(newValue.getDays())));
                    this.ms_boxEndDate.setVisible(true);
                    this.boxPayment.setVisible(true);
                });
            } else {
                Platform.runLater(() -> {
                    this.ms_boxEndDate.setVisible(false);
                    this.boxPayment.setVisible(false);
                });
            }
        });

        // payment -> hide
        Input.createVisibleProperty(this.boxPayment, false);
        Input.createVisibleProperty(this.pym_boxOwe, false);
        this.boxPayment.visibleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) Platform.runLater(() -> this.pym_togglePayment.setSelected(true));
        });
        this.pym_togglePayment.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            Platform.runLater(() -> this.pym_boxOwe.setVisible(!newValue));
            if (newValue) { // hide boxOwe
                Input.clearInputs(this.pym_fieldPaidOut, this.pym_fieldOwe);
            } else { // show boxOwe
                Platform.runLater(() -> {
                    this.pym_fieldOwe.setText(this.ms_comboBoxMemberships.getSelectionModel().getSelectedItem().getPrice().toString());
                    this.pym_fieldPaidOut.requestFocus();
                    Platform.runLater(() -> this.scrollPane.setVvalue(1d));
                });
            }
        });
        this.pym_fieldPaidOut.textProperty().addListener((observable, oldValue, newValue) -> {
            if (Validator.moneyValidator(new InputDetails(this.pym_fieldPaidOut, this.pym_fieldPaidOut.getText()), false)) {
                this.pym_fieldOwe.setText(this.ms_comboBoxMemberships.getSelectionModel().getSelectedItem().getPrice().subtract(new BigDecimal(newValue)).toString());
            } else {
                this.pym_fieldOwe.setText(this.ms_comboBoxMemberships.getSelectionModel().getSelectedItem().getPrice().toString());
            }
        });

        // shortcut
        Input.createVisibleProperty(this.boxShortcut, false);
        Input.createVisibleProperty(this.s_buttonPayDebt, false);

        // end buttons
        this.buttonAction.setOnAction(actionEvent -> createMember());
        this.buttonClear.setOnAction((actionEvent) -> {
            eventClearForm(false);
            this.controllerMembers.unselectTable();
        });
    }

    public Controller_Member(Controller_Members controllerMembers, int idMember, String style) {
        this.controllerMembers = controllerMembers;
        getMemberData(idMember, style);
    }

    public void getMemberData(int idMember, String style) {
        new FadeOutRight(this.scrollPane).play();
        JDBC_Member.ReadMember(idMember).thenAccept(model_member -> {
            this.modelMember = model_member;
            this.modelMember.setIdMember(idMember);
            this.modelMember.setStyle(style);

            Fingerprint_Controller.loadFingerprints(idMember);
            JDBC_Payment_Membership.ReadLastPayment(idMember)
                    .thenAccept(model_payments_memberships -> {
                        this.modelMember.setModelPaymentMembership(model_payments_memberships);
                        if (model_payments_memberships == null) {
                            JDBC_Gym.ReadGym(this.modelMember.getIdGym())
                                    .thenAccept(model_gyms -> {
                                        this.modelMember.setModelGyms(model_gyms);
                                        eventClearForm(true);
                                    });
                        } else {
                            JDBC_Member.ReadMember(model_payments_memberships.getIdStaff()).thenAccept(model_member_staff -> {
                                JDBC_Gym.ReadGym(model_payments_memberships.getIdGym())
                                        .thenAccept(model_gyms -> {
                                            this.modelMember.setModelGyms(model_gyms);
                                            Model_Staff_Member modelStaffMember = new Model_Staff_Member();
                                            modelStaffMember.setIdMember(model_member_staff.getIdMember());
                                            modelStaffMember.setName(model_member_staff.getName());
                                            modelStaffMember.setLastName(model_member_staff.getLastName());
                                            this.modelMember.setModelStaffMember(modelStaffMember);
                                            eventClearForm(true);
                                        });
                            });
                        }
                    });
        });
    }

    private void fillMemberForm() {
        Platform.runLater(() -> {
            // init form
            this.boxTitle.getStyleClass().set(0, this.modelMember.getStyle());
            this.t_labelTitle.setText("[ID: " + this.modelMember.getIdMember() + "] " + this.modelMember.getName());

            // quick view
            this.qv_labelRegistrationDate.setText(
                    DateTime.getDateWithDayName(
                            this.modelMember.getRegistrationDateTime()
                    )
            );
            this.qv_staffName.setText(this.modelMember.getModelStaffMember().getName() + " " + this.modelMember.getModelStaffMember().getLastName());
            this.qv_labelLastPayment.setText(
                    DateTime.getDateWithDayName(
                            this.modelMember.getModelPaymentMembership().getStartDateTime()
                    )
            );
            this.qv_labelGym.setText(this.modelMember.getModelGyms().getName());
            this.boxQuickView.setVisible(true);

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
                this.ms_comboBoxMemberships.setDisable(true);
                this.ms_comboBoxMemberships.getItems().forEach(model_membership -> { // select current membership
                    if (model_membership.getIdMembership() == this.modelMember.getModelPaymentMembership().getIdMembership()) {
                        this.ms_comboBoxMemberships.getSelectionModel().select(model_membership);
                        // set endDate
                        Platform.runLater(() -> {
                            this.ms_labelEndDate.setText(DateTime.getDateWithDayName(this.modelMember.getModelPaymentMembership().getEndDateTime()));
                            this.boxPayment.setVisible(false);
                        });
                    }
                });
            } else {
                this.qv_labelLastPayment.setText("N / A");
                Notifications.Warn("Pago no encontrado", "Último pago no encontrado");
            }

            // shortcut
            if (this.modelMember.isAccess()) {
                this.s_buttonAccess.getStyleClass().set(3, Styles.DANGER);
                this.s_buttonAccess.setText("Bloquear acceso");
                this.s_buttonOpenDoor.setDisable(false);
            } else {
                this.s_buttonAccess.getStyleClass().set(3, Styles.SUCCESS);
                this.s_buttonAccess.setText("Desbloquear acceso");
                this.s_buttonOpenDoor.setDisable(true);
            }
            this.s_buttonAccess.setOnAction(actionEvent -> eventAccess());
            this.s_buttonPayDebt.setVisible(this.modelMember.getStyle().equals(Styles.CREATIVE));
            this.boxShortcut.setVisible(true);


            // end buttons
            this.buttonAction.setText("Guardar cambios");
            this.buttonAction.setDisable(true);
            this.buttonClear.setText("Cancelar");

            FadeInRight fadeInRightScrollPane = new FadeInRight(this.scrollPane);
            fadeInRightScrollPane.setOnFinished(actionEvent -> {
                Loading.close();
                this.formChangeListener.setListener(true);
            });
            fadeInRightScrollPane.play();
        });
    }

    private void eventClearForm(boolean fillMemberForm) {
        this.formChangeListener.setListener(false);
        Platform.runLater(() -> {
            this.boxTitle.getStyleClass().set(0, Styles.DEFAULT);
            this.t_labelTitle.setText("Socio nuevo");

            this.boxQuickView.setVisible(false);

            this.photoHandler.resetHandler();

            Fingerprint_Controller.FB_RestartCapture();

            Input.clearInputs(
                    this.pi_fieldName,
                    this.pi_fieldLastName,
                    this.pi_comboBoxGender,
                    this.pi_fieldPhone,
                    this.pi_fieldEmail,
                    this.pi_fieldNotes,
                    this.ms_comboBoxMemberships
            );

            this.ms_comboBoxMemberships.setDisable(false);
            this.boxShortcut.setVisible(false);

            this.buttonAction.setDisable(false);
            this.buttonAction.setText("Registrar");
            this.buttonClear.setText("Limpiar");

            if (fillMemberForm) {
                fillMemberForm();
            } else {
                this.modelMember = null;
                this.scrollPane.setVvalue(0d);
                new FadeIn(this.scrollPane).play();
                this.ph_buttonUploadPhoto.requestFocus();
            }
        });
    }

    private void createMember() {
        CompletableFuture.runAsync(() -> {
            ArrayList<InputDetails> nodesRequired = new ArrayList<>();
            // personal information
            nodesRequired.add(new InputDetails(this.pi_fieldName, this.pi_fieldName.getText()));
            nodesRequired.add(new InputDetails(this.pi_fieldLastName, this.pi_fieldLastName.getText()));
            nodesRequired.add(new InputDetails(this.pi_comboBoxGender, String.valueOf(this.pi_comboBoxGender.getSelectionModel().getSelectedIndex())));
            nodesRequired.add(new InputDetails(Application.getCurrentGymNode(), String.valueOf(Application.getCurrentGymNode().getSelectionModel().getSelectedIndex())));

            // membership
            nodesRequired.add(new InputDetails(this.ms_comboBoxMemberships, String.valueOf(this.ms_comboBoxMemberships.getSelectionModel().getSelectedIndex())));

            // payment
            if (!pym_togglePayment.isSelected()) {
                nodesRequired.add(new InputDetails(this.pym_fieldPaidOut, this.pym_fieldPaidOut.getText()));
            }

            Boolean formValid = Validator.emptyValidator(nodesRequired.listIterator());
            if (formValid) { // text validator
                nodesRequired.clear();
                nodesRequired.add(new InputDetails(this.pi_fieldName, this.pi_fieldName.getText()));
                nodesRequired.add(new InputDetails(this.pi_fieldLastName, this.pi_fieldLastName.getText()));
                formValid = Validator.textValidator(nodesRequired.listIterator());

                if (formValid) { // number validator
                    if (this.pi_fieldPhone.getText().length() != 0) { // phone validator
                        formValid = Validator.phoneValidator(new InputDetails(this.pi_fieldPhone, this.pi_fieldPhone.getText()));
                    }
                }

                if (formValid) {
                    if (this.pi_fieldEmail.getText().length() != 0) { // email validator
                        formValid = Validator.emailValidator(new InputDetails(this.pi_fieldEmail, this.pi_fieldEmail.getText()));
                    }
                }

                if (formValid) { // money validator
                    nodesRequired.clear();
                    if (!this.pym_togglePayment.isSelected()) {
                        nodesRequired.add(new InputDetails(this.pym_fieldPaidOut, this.pym_fieldPaidOut.getText()));
                    }
                    formValid = Validator.moneyValidator(nodesRequired.listIterator());
                }

                if (formValid) { // form 100% valid
                    // modelMember
                    Model_Member modelMember = new Model_Member();
                    modelMember.setName(Input.capitalizeFirstLetterPerWord(this.pi_fieldName.getText()));
                    modelMember.setLastName(Input.capitalizeFirstLetterPerWord(this.pi_fieldLastName.getText()));
                    modelMember.setGender(this.pi_comboBoxGender.getSelectionModel().getSelectedItem());

                    modelMember.setPhone(Input.spaceRemover(this.pi_fieldPhone.getText()));
                    modelMember.setEmail(Input.spaceRemover(this.pi_fieldEmail.getText().toLowerCase()));
                    modelMember.setNotes(Input.capitalizeFirstLetter(this.pi_fieldNotes.getText()));
                    modelMember.setIdGym(Application.getCurrentGym().getIdGym());

                    // modelMembership
                    Model_Membership modelMembership = this.ms_comboBoxMemberships.getSelectionModel().getSelectedItem();

                    // modelDebt
                    final Model_Debt modelDebt = new Model_Debt();
                    if (!this.pym_togglePayment.isSelected()) { // have debt
                        BigDecimal paidOut = new BigDecimal(this.pym_fieldPaidOut.getText());
                        BigDecimal owe = new BigDecimal(this.pym_fieldOwe.getText());

                        if (owe.compareTo(BigDecimal.ZERO) == 0) {
                            formValid = false;
                            Notifications.Danger("Error", "Se trata de un pago completo, no hay deuda");
                            Validator.shakeInput(this.pym_fieldPaidOut);
                        } else if (owe.compareTo(BigDecimal.ZERO) < 0) {
                            formValid = false;
                            Notifications.Danger("Error", "La deuda es mayor al total a pagar");
                            Validator.shakeInput(this.pym_fieldPaidOut);
                        } else {
                            modelDebt.setPaidOut(paidOut);
                            modelDebt.setOwe(owe);
                            modelDebt.setAmount(1);
                            modelDebt.setDescription(modelMembership.getDescription() + " (" + modelMembership.getDays() + ") días");
                        }
                    }

                    if (formValid) { // insert into database
                        if (this.formChangeListener.isListener()) { // update changes
                            if (this.formChangeListener.isChanged("photo")) {
                                JDBC_Member_Photo.UpdatePhoto(this.modelMember.getIdMember(), this.photoHandler.getPhoto());
                            }
                            if (this.formChangeListener.isChanged("name")) {
                                JDBC_Member.UpdateName(this.modelMember.getIdMember(), modelMember.getName());
                            }

                            if (this.formChangeListener.isChanged("lastName")) {
                                JDBC_Member.UpdateLastName(this.modelMember.getIdMember(), modelMember.getLastName());
                            }
                            if (this.formChangeListener.isChanged("gender")) {
                                JDBC_Member.UpdateGender(this.modelMember.getIdMember(), modelMember.getGender());
                            }
                            if (this.formChangeListener.isChanged("phone")) {
                                JDBC_Member.UpdatePhone(this.modelMember.getIdMember(), modelMember.getPhone());
                            }
                            if (this.formChangeListener.isChanged("email")) {
                                JDBC_Member.UpdateEmail(this.modelMember.getIdMember(), modelMember.getEmail());
                            }
                            if (this.formChangeListener.isChanged("notes")) {
                                JDBC_Member.UpdateNotes(this.modelMember.getIdMember(), modelMember.getNotes());
                            }
                            if (this.formChangeListener.isChanged("fingerprint")) {
                                JDBC_Member_Fingerprint.CreateFingerprints(this.modelMember.getIdMember(), Fingerprint_Controller.getFingerprints());
                            }
                            Notifications.Success("Información actualizada", "[ID:  " + this.modelMember.getIdMember() + "] " + modelMember.getName() + " información actualizada");
                        } else { // new member
                            JDBC_Member.CreateMember(modelMember).thenAccept(idMember -> {
                                if (idMember > 0) {
                                    JDBC_Member_Photo.CreatePhoto(idMember, photoHandler.getPhoto());
                                    JDBC_Member_Fingerprint.CreateFingerprints(idMember, Fingerprint_Controller.getFingerprints());
                                    JDBC_Payment_Membership.CreatePaymentMembership(idMember, modelMembership).thenAccept(idLastMembership -> {
                                        if (modelDebt.getAmount() != 0 && idLastMembership > 0) {
                                            JDBC_Debt.CreateDebt(modelDebt, idMember, 1);
                                        }
                                    });

                                    Notifications.Success("Nuevo socio", "[ " + idMember + " ] " + modelMember.getName() + " ha sido registrado.");
                                }
                            });
                        }

                        this.controllerMembers.refreshTable();
                        eventClearForm(false);

                        // clear memory
                        nodesRequired = null;
                        formValid = null;
                    }
                }
            }
        });
    }

    private void eventAccess() { // TODO: REFRESH TABLE AFTER CHANGE
        Platform.runLater(() -> {
            String style = this.s_buttonAccess.getStyleClass().get(3);
            if (Styles.DANGER.equals(style)) {
                Controller_Popup popup = new Controller_Popup(
                        style,
                        "Bloquear acceso",
                        "Se le bloqueara el acceso a todos los gimnasios a " + this.modelMember.getName() + " " + this.modelMember.getLastName() + ", ¿quieres continuar?",
                        Controller_Popup.POPUP_CONFIRM
                );
                if (popup.showAndWait()) {
                    JDBC_Member.UpdateAccess(this.modelMember.getIdMember(), this.modelMember.isAccess()).thenAccept(bool_access -> {
                        if (bool_access) {
                            Platform.runLater(() -> {
                                this.modelMember.setAccess(!this.modelMember.isAccess());
                                this.s_buttonOpenDoor.setDisable(true);
                                this.s_buttonAccess.getStyleClass().set(3, Styles.SUCCESS);
                                this.s_buttonAccess.setText("Desbloquear acceso");
                                Notifications.Danger("Acceso bloqueado", this.modelMember.getName() + " ha perdido el acceso a los gimnasios");
                                this.scrollPane.requestFocus();
                            });
                        }
                    });
                }
            } else {
                Controller_Popup popup = new Controller_Popup(
                        style,
                        "Desbloquear acceso",
                        "Se desbloqueara el acceso a " + this.modelMember.getName() + " " + this.modelMember.getLastName() + ", ¿quieres continuar?",
                        Controller_Popup.POPUP_CONFIRM
                );
                if (popup.showAndWait()) {
                    JDBC_Member.UpdateAccess(this.modelMember.getIdMember(), this.modelMember.isAccess()).thenAccept(bool_access -> {
                        Platform.runLater(() -> {
                            this.modelMember.setAccess(!this.modelMember.isAccess());
                            this.s_buttonOpenDoor.setDisable(false);
                            this.s_buttonAccess.getStyleClass().set(3, Styles.DANGER);
                            this.s_buttonAccess.setText("Bloquear acceso");
                            Notifications.Success("Acceso desbloqueado", this.modelMember.getName() + " puede entrar a los gimnasios nuevamente");
                            this.scrollPane.requestFocus();
                        });
                    });
                }
            }
        });
    }

    // Update listener
    private void createFormChangeListener() {
        this.formChangeListener = new FormChangeListener(this.buttonAction);
        formChangeListener.add("photo");
        formChangeListener.add("name");
        formChangeListener.add("lastName");
        formChangeListener.add("gender");
        formChangeListener.add("phone");
        formChangeListener.add("email");
        formChangeListener.add("notes");
        formChangeListener.add("fingerprint");
        this.pi_fieldName.setOnKeyTyped(keyEvent -> {
            if (this.formChangeListener.isListener()) {
                this.formChangeListener.change(
                        "name",
                        Validator.compare(this.pi_fieldName.getText(), this.modelMember.getName())
                );
            }
        });
        this.pi_fieldLastName.setOnKeyTyped(keyEvent -> {
            if (this.formChangeListener.isListener()) {
                this.formChangeListener.change(
                        "lastName",
                        Validator.compare(this.pi_fieldLastName.getText(), this.modelMember.getLastName())
                );
            }
        });
        this.pi_comboBoxGender.setOnAction(keyEvent -> {
            if (this.formChangeListener.isListener()) {
                this.formChangeListener.change(
                        "gender",
                        Validator.compare(this.pi_comboBoxGender.getSelectionModel().getSelectedItem(), this.modelMember.getGender())
                );
            }
        });
        this.pi_fieldPhone.setOnKeyTyped(keyEvent -> {
            if (this.formChangeListener.isListener()) {
                this.formChangeListener.change(
                        "phone",
                        Validator.compare(this.pi_fieldPhone.getText(), this.modelMember.getPhone())
                );
            }
        });
        this.pi_fieldEmail.setOnKeyTyped(keyEvent -> {
            if (this.formChangeListener.isListener()) {
                this.formChangeListener.change(
                        "email",
                        Validator.compare(this.pi_fieldEmail.getText(), this.modelMember.getEmail())
                );
            }
        });
        this.pi_fieldNotes.setOnKeyTyped(keyEvent -> {
            if (this.formChangeListener.isListener()) {
                this.formChangeListener.change(
                        "notes",
                        Validator.compare(this.pi_fieldNotes.getText(), this.modelMember.getNotes())
                );
            }
        });
        this.fp_labelFingerprintCounter.textProperty().addListener((observable, oldValue, newValue) -> {
            if (this.formChangeListener.isListener() && !oldValue.equals(newValue)) {
                this.formChangeListener.change(
                        "fingerprint",
                        false
                );
            }
        });
    }
}
