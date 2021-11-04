package com.ocielgp.controller;

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
import com.ocielgp.utilities.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

import java.lang.invoke.MethodHandles;
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
    private Label qv_labelAdmin;

    // -> photo [ph]
    @FXML
    private ImageView ph_imgMemberPhoto;
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
    private VBox ms_boxEndDate;
    @FXML
    private HBox ms_boxMonths;
    @FXML
    private FontIcon ms_iconSubtractMonth;
    @FXML
    private Label ms_labelMonth;
    @FXML
    private FontIcon ms_iconAddMonth;
    @FXML
    private Label ms_labelEndDate;
    @FXML
    private HBox ms_boxButtons;

    // -> payment [pym]
    @FXML
    private VBox boxPayment;
    @FXML
    private Label pym_labelPrice;
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
    private short totalMonths = 1;
    private final ObjectProperty<BigDecimal> membershipPrice = new SimpleObjectProperty<>(new BigDecimal(0));
    private FormChangeListener formChangeListener;
    private PhotoHandler photoHandler;
    private Model_Member modelMember = null;
    private Model_Member modelAdmin = null;
    private final Pagination pagination;

    public Controller_Member(Pagination pagination) {
        this.pagination = pagination;
    }

    private void configureForm() {
        Input.getScrollEvent(this.scrollPane);
        this.boxMember.heightProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue.doubleValue() > oldValue.doubleValue() && oldValue.doubleValue() != 0) {
                Platform.runLater(() -> this.scrollPane.setVvalue(1d));
            } else {
                if (this.modelMember == null) {
                    Platform.runLater(() -> this.scrollPane.setVvalue(0d));
                }
            }
        });

        // set max length
        Input.createMaxLengthEvent(this.pi_fieldName, Model_Member.nameLength);
        Input.createMaxLengthEvent(this.pi_fieldLastName, Model_Member.lastNameLength);
        Input.createMaxLengthEvent(this.pi_fieldNotes, Model_Member.notesLength);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configureForm();
        createFormChangeListener();
        Input.createComboBoxListener(this.pi_comboBoxGender, this.ms_comboBoxMemberships);

        // quick view
        Input.createVisibleEvent(this.boxQuickView, false);

        // photo section
        this.photoHandler = new PhotoHandler(this.formChangeListener, this.ph_imgMemberPhoto, this.ph_buttonDeletePhoto);

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
        JDBC_Membership.ReadMemberships(Model_Membership.MONTHLY).thenAccept(model_memberships -> {
            this.ms_comboBoxMemberships.setItems(model_memberships);
            Application.isChildLoaded = true;
            Loading.close();
        });
        Input.createVisibleEvent(this.ms_boxEndDate, false);
        Input.createVisibleEvent(this.ms_boxMonths, false);
        this.ms_comboBoxMemberships.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Platform.runLater(() -> eventUpdatePrice(true));
            } else {
                Platform.runLater(() -> {
                    this.ms_boxEndDate.setVisible(false);
                    this.boxPayment.setVisible(false);
                });
            }
        });
        this.ms_iconSubtractMonth.setOnMouseClicked(mouseEvent -> eventSubtractMonth());
        this.ms_iconAddMonth.setOnMouseClicked(mouseEvent -> eventAddMonth());
        Input.createVisibleEvent(this.ms_boxButtons, false);

        // payment -> hide
        Input.createVisibleEvent(this.boxPayment, false);
        Input.createVisibleEvent(this.pym_boxOwe, false);
        this.boxPayment.visibleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                Platform.runLater(() -> this.pym_togglePayment.setSelected(true));
            }
        });
        this.pym_togglePayment.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            Platform.runLater(() -> this.pym_boxOwe.setVisible(!newValue));
            if (newValue) { // hide boxOwe
                Input.clearInputs(this.pym_fieldPaidOut, this.pym_fieldOwe);
            } else { // show boxOwe
                Platform.runLater(() -> {
                    this.pym_fieldPaidOut.requestFocus();
                    Platform.runLater(() -> this.scrollPane.setVvalue(1d));
                });
            }
        });
        this.pym_labelPrice.textProperty().bind(Bindings.concat("$ ", membershipPrice.asString(), " MXN"));
        this.pym_fieldPaidOut.textProperty().addListener((observable, oldValue, newValue) -> {
            if (Validator.moneyValidator(this.pym_fieldPaidOut, false)) {
                this.pym_fieldOwe.setText(this.membershipPrice.get().subtract(new BigDecimal(newValue)).toString());
            } else {
                this.pym_fieldOwe.setText(this.membershipPrice.get().toString());
            }
        });

        // shortcut
        Input.createVisibleEvent(this.boxShortcut, false);
        Input.createVisibleEvent(this.s_buttonPayDebt, false);

        // end buttons
        this.buttonAction.setOnAction(actionEvent -> createMember());
        this.buttonClear.setOnAction((actionEvent) -> {
            eventClearForm(false);
            this.pagination.unselectTable();
        });
    }

    public Controller_Member(Pagination pagination, int idMember, String style) {
        this.pagination = pagination;
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
                                    .thenAccept(model_gym -> {
                                        this.modelMember.setModelGym(model_gym);
                                        eventClearForm(true);
                                    });
                        } else {
                            JDBC_Member.ReadMember(model_payments_memberships.getIdAdmin()).thenAccept(model_admin -> JDBC_Gym.ReadGym(model_payments_memberships.getIdGym())
                                    .thenAccept(model_gym -> {
                                        this.modelMember.setModelGym(model_gym);
                                        this.modelAdmin = model_admin;
                                        eventClearForm(true);
                                    }));
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
            this.qv_labelGym.setText(this.modelMember.getModelGym().getName());
            this.boxQuickView.setVisible(true);

            // photo
            this.photoHandler.setPhoto(this.modelMember.getModelMemberPhoto().getPhoto());

            // personal information
            this.pi_fieldName.setText(this.modelMember.getName());
            this.pi_fieldLastName.setText(this.modelMember.getLastName());
            this.pi_comboBoxGender.getSelectionModel().select(this.modelMember.getGender());
            this.pi_fieldNotes.setText(this.modelMember.getNotes());

            // membership
            if (this.modelMember.getModelPaymentMembership() != null) {
                this.qv_labelAdmin.setText(this.modelAdmin.getName() + " " + this.modelAdmin.getLastName());
                this.qv_labelLastPayment.setText(
                        DateTime.getDateWithDayName(
                                this.modelMember.getModelPaymentMembership().getStartDateTime()
                        )
                );
                this.ms_comboBoxMemberships.setDisable(true);
                this.ms_comboBoxMemberships.getItems().forEach(model_membership -> { // select current membership
                    if (model_membership.getIdMembership() == this.modelMember.getModelPaymentMembership().getIdMembership()) {
                        this.ms_comboBoxMemberships.getSelectionModel().select(model_membership);
                        this.ms_labelEndDate.setText(DateTime.getDateWithDayName(this.modelMember.getModelPaymentMembership().getEndDateTime()));
                        createMembershipButtons();
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
                Loading.closeNow();
                this.formChangeListener.setListen(true);
            });
            fadeInRightScrollPane.play();
        });
    }

    private void eventClearForm(boolean fillMemberForm) {
        this.formChangeListener.setListen(false);
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
                    this.pi_fieldNotes,
                    this.ms_comboBoxMemberships
            );

            this.ms_comboBoxMemberships.setDisable(false);
            this.ms_boxMonths.setVisible(false);
            this.ms_boxButtons.getChildren().clear();
            this.ms_boxButtons.setDisable(false);
            this.boxShortcut.setVisible(false);
            this.ms_labelEndDate.getStyleClass().remove("strikethrough");

            this.buttonAction.setDisable(false);
            this.buttonAction.setText("Registrar");
            this.buttonClear.setText("Limpiar");

            if (fillMemberForm) {
                fillMemberForm();
            } else {
                this.modelMember = null;
                this.modelAdmin = null;
                this.scrollPane.setVvalue(0d);
                FadeInRight fadeInRightScrollPane = new FadeInRight(this.scrollPane);

                fadeInRightScrollPane.play();
            }
        });
    }

    private void createMember() {
        CompletableFuture.runAsync(() -> {
            System.out.println("va");
            System.out.println("fue");
            ArrayList<Node> nodesRequired = new ArrayList<>();
            // personal information
            nodesRequired.add(this.pi_fieldName);
            nodesRequired.add(this.pi_fieldLastName);
            nodesRequired.add(this.pi_comboBoxGender);
            nodesRequired.add(Application.getCurrentGymNode());

            // membership
            nodesRequired.add(this.ms_comboBoxMemberships);

            // payment
            if (!pym_togglePayment.isSelected()) {
                nodesRequired.add(this.pym_fieldPaidOut);
            }

            boolean formValid = Validator.emptyValidator(nodesRequired.toArray(new Node[]{}));
            if (formValid) { // text validator
                nodesRequired.clear();
                nodesRequired.add(this.pi_fieldName);
                nodesRequired.add(this.pi_fieldLastName);
                formValid = Validator.textValidator(nodesRequired.toArray(new Node[]{}));

                if (formValid) { // money validator
                    nodesRequired.clear();
                    if (!this.pym_togglePayment.isSelected()) {
                        formValid = Validator.moneyValidator(this.pym_fieldPaidOut, true);
                    }
                }

                if (formValid) { // form 100% valid
                    // modelMember
                    Model_Member modelMember = new Model_Member();
                    modelMember.setName(Input.capitalizeFirstLetterPerWord(this.pi_fieldName.getText()));
                    modelMember.setLastName(Input.capitalizeFirstLetterPerWord(this.pi_fieldLastName.getText()));
                    modelMember.setGender(this.pi_comboBoxGender.getSelectionModel().getSelectedItem());

                    modelMember.setNotes(Input.capitalizeFirstLetter(this.pi_fieldNotes.getText()));
                    modelMember.setIdGym(Application.getCurrentGym().getIdGym());

                    // modelMembership
                    Model_Membership modelMembership = this.ms_comboBoxMemberships.getSelectionModel().getSelectedItem();
                    modelMembership.setPrice(this.membershipPrice.get());

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
                            modelDebt.setAmount(this.totalMonths);
                            modelDebt.setDescription(modelMembership.getName());
                            modelDebt.setIsMembership(true);
                        }
                    }

                    if (formValid) { // insert into database
                        Loading.show();
                        try {
                            if (this.formChangeListener.isListen()) { // update changes
                                boolean isOk = true;
                                if (this.formChangeListener.isChanged("membershipRenew")) {
                                    int newIdMembership = JDBC_Payment_Membership.CreatePaymentMembership(this.modelMember.getIdMember(), modelMembership, this.totalMonths);
                                    isOk = newIdMembership > 0;
                                    if (modelDebt.getAmount() > 0 && newIdMembership > 0) {
                                        isOk = JDBC_Debt.CreateDebt(this.modelMember.getIdMember(), modelDebt);
                                    }
                                }
                                if (isOk && this.formChangeListener.isChanged("membershipChange")) {
                                    isOk = JDBC_Payment_Membership.UpdatePaymentMembership(modelMembership, this.modelMember.getModelPaymentMembership());
                                    if (modelDebt.getAmount() > 0) {
                                        isOk = JDBC_Debt.CreateDebt(this.modelMember.getIdMember(), modelDebt);
                                    }
                                }
                                if (isOk && this.formChangeListener.isChanged("membershipRemove")) {
                                    isOk = JDBC_Payment_Membership.DeletePaymentMembership(this.modelMember.getModelPaymentMembership());
                                }
                                if (isOk && this.formChangeListener.isChanged("photo")) {
                                    isOk = JDBC_Member_Photo.UpdatePhoto(this.modelMember.getIdMember(), this.photoHandler.getPhoto());
                                }
                                if (isOk && this.formChangeListener.isChanged("name")) {
                                    isOk = JDBC_Member.UpdateName(this.modelMember.getIdMember(), modelMember.getName());
                                }

                                if (isOk && this.formChangeListener.isChanged("lastName")) {
                                    isOk = JDBC_Member.UpdateLastName(this.modelMember.getIdMember(), modelMember.getLastName());
                                }
                                if (isOk && this.formChangeListener.isChanged("gender")) {
                                    isOk = JDBC_Member.UpdateGender(this.modelMember.getIdMember(), modelMember.getGender());
                                }
                                if (isOk && this.formChangeListener.isChanged("notes")) {
                                    isOk = JDBC_Member.UpdateNotes(this.modelMember.getIdMember(), modelMember.getNotes());
                                }
                                if (isOk && this.formChangeListener.isChanged("fingerprint")) {
                                    isOk = JDBC_Member_Fingerprint.CreateFingerprints(this.modelMember.getIdMember(), Fingerprint_Controller.getFingerprints());
                                }
                                if (isOk) {
                                    Notifications.Success("Nuevos cambios", "[ID:  " + this.modelMember.getIdMember() + "] " + modelMember.getName() + " información actualizada");
                                }
                            } else { // new member
                                int idMember = JDBC_Member.CreateMember(modelMember);
                                if (idMember > 0) {
                                    JDBC_Member_Photo.CreatePhoto(idMember, photoHandler.getPhoto());
                                    JDBC_Member_Fingerprint.CreateFingerprints(idMember, Fingerprint_Controller.getFingerprints());
                                    int idNewMembership = JDBC_Payment_Membership.CreatePaymentMembership(idMember, modelMembership, this.totalMonths);
                                    if (modelDebt.getAmount() != 0 && idNewMembership > 0) {
                                        JDBC_Debt.CreateDebt(idMember, modelDebt);
                                    }
                                    Notifications.Success("Nuevo socio", "[ID: " + idMember + " ] " + modelMember.getName() + " ha sido registrado");
                                }
                            }

                            this.pagination.restartTable();
                            eventClearForm(false);
                        } catch (Exception exception) {
                            Notifications.CatchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], exception.getMessage(), exception);
                        }
                    }
                }
            }
        });
    }

    private void createMembershipButtons() {
        if (DateTime.isToday(this.modelMember.getModelPaymentMembership().getStartDateTime())) { // today's payment
            JFXButton buttonChangeMembership = new JFXButton("Cambiar");
            buttonChangeMembership.getStyleClass().addAll("btn-colorful", "warn-style");
            buttonChangeMembership.setOnAction(actionEvent -> eventMembershipChange());
            JFXButton buttonRemoveMembership = new JFXButton("Quitar");
            buttonRemoveMembership.getStyleClass().addAll("btn-colorful", "danger-style");
            buttonRemoveMembership.setOnAction(actionEvent -> eventMembershipRemove());
            this.ms_boxButtons.getChildren().setAll(buttonChangeMembership, buttonRemoveMembership);
            this.ms_boxButtons.setVisible(true);
        } else {
            JFXButton buttonRenewMembership = new JFXButton("Renovar");
            buttonRenewMembership.getStyleClass().addAll("btn-colorful", "epic-style");
            buttonRenewMembership.setOnAction(actionEvent -> eventMembershipRenew());
            this.ms_boxButtons.getChildren().setAll(buttonRenewMembership);
            this.ms_boxButtons.setVisible(true);
        }
    }

    private void eventMembershipRenew() {
        this.ms_boxButtons.setDisable(true);
        this.ms_comboBoxMemberships.setDisable(false);
        this.ms_boxMonths.setVisible(true);
        this.boxPayment.setVisible(true);
        eventUpdatePrice(true);
        formChangeListener.change("membershipRenew", false);
    }

    private void eventMembershipChange() {
        Popup popup = new Popup();
        popup.password();
        if (popup.showAndWait()) {
            Notifications.Default("gmi-event", "Membresía", "Selecciona un nuevo plan");
            this.ms_boxButtons.setDisable(true);
            this.ms_comboBoxMemberships.setDisable(false);
            this.ms_boxMonths.setVisible(true);
            this.boxPayment.setVisible(true);
            eventUpdatePrice(true);
            formChangeListener.change("membershipChange", false);
        }
    }

    private void eventMembershipRemove() {
        Popup popup = new Popup();
        popup.password();
        if (popup.showAndWait()) {
            this.ms_labelEndDate.getStyleClass().add("strikethrough");
            formChangeListener.change("membershipRemove", false);
            this.ms_boxButtons.setDisable(true);
        }
    }

    private void eventAddMonth() {
        if (this.totalMonths <= Model_Debt.MAX_AMOUNT) {
            this.totalMonths++;
            eventUpdatePrice(false);
        }
    }

    private void eventSubtractMonth() {
        if (this.totalMonths != 1) {
            this.totalMonths--;
            eventUpdatePrice(false);
        }
    }

    private void eventUpdatePrice(boolean restartMonth) {
        Platform.runLater(() -> {
            if (restartMonth) {
                this.totalMonths = 1;
            }
            this.ms_boxEndDate.setVisible(true);

            if (this.modelMember == null || this.modelMember.getModelPaymentMembership() == null) {
                this.ms_boxMonths.setVisible(true);
                this.boxPayment.setVisible(true);

            }
            if (this.formChangeListener.isListen()) {
                if (this.modelMember != null && this.modelMember.getModelPaymentMembership() == null) {
                    this.formChangeListener.change("membershipRenew", false);

                }
                if (this.formChangeListener.isChanged("membershipRenew") || this.formChangeListener.isChanged("membershipChange") || this.formChangeListener.isChanged("membershipRemove")) {
                    this.ms_labelEndDate.setText(
                            DateTime.getEndDate(this.totalMonths)
                    );
                }
            } else {
                this.ms_labelEndDate.setText(
                        DateTime.getEndDate(this.totalMonths)
                );
            }
            this.ms_labelMonth.setText(this.totalMonths + ((this.totalMonths == 1) ? " MES" : " MESES"));
            if (this.boxPayment.isVisible()) {
                this.membershipPrice.set(
                        this.ms_comboBoxMemberships.getSelectionModel().getSelectedItem().getPrice().multiply(
                                BigDecimal.valueOf(this.totalMonths)
                        )
                );
                Input.clearInputs(this.pym_fieldPaidOut);
                this.pym_fieldOwe.setText(this.membershipPrice.get().toString());
            }
        });
    }

    private void eventAccess() { // TODO: REFRESH TABLE AFTER CHANGE
        Popup popup = new Popup();
        Platform.runLater(() -> {
            String style = this.s_buttonAccess.getStyleClass().get(3);
            if (Styles.DANGER.equals(style)) {
                popup.confirm(
                        style,
                        "Bloquear acceso",
                        "Se le bloqueara el acceso a todos los gimnasios a " + this.modelMember.getName() + " " + this.modelMember.getLastName() + ", ¿quieres continuar?"
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
                popup.confirm(
                        style,
                        "Desbloquear acceso",
                        "Se desbloqueara el acceso a " + this.modelMember.getName() + " " + this.modelMember.getLastName() + ", ¿quieres continuar?"
                );
                if (popup.showAndWait()) {
                    JDBC_Member.UpdateAccess(this.modelMember.getIdMember(), this.modelMember.isAccess()).thenAccept(bool_access -> Platform.runLater(() -> {
                        this.modelMember.setAccess(!this.modelMember.isAccess());
                        this.s_buttonOpenDoor.setDisable(false);
                        this.s_buttonAccess.getStyleClass().set(3, Styles.DANGER);
                        this.s_buttonAccess.setText("Bloquear acceso");
                        Notifications.Success("Acceso desbloqueado", this.modelMember.getName() + " puede entrar a los gimnasios nuevamente");
                        this.scrollPane.requestFocus();
                    }));
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
        formChangeListener.add("notes");
        formChangeListener.add("fingerprint");
        formChangeListener.add("membershipRenew");
        formChangeListener.add("membershipChange");
        formChangeListener.add("membershipRemove");
        this.pi_fieldName.setOnKeyTyped(keyEvent -> {
            if (this.formChangeListener.isListen()) {
                this.formChangeListener.change(
                        "name",
                        Validator.compare(this.pi_fieldName.getText(), this.modelMember.getName())
                );
            }
        });
        this.pi_fieldLastName.setOnKeyTyped(keyEvent -> {
            if (this.formChangeListener.isListen()) {
                this.formChangeListener.change(
                        "lastName",
                        Validator.compare(this.pi_fieldLastName.getText(), this.modelMember.getLastName())
                );
            }
        });
        this.pi_comboBoxGender.setOnAction(keyEvent -> {
            if (this.formChangeListener.isListen()) {
                this.formChangeListener.change(
                        "gender",
                        Validator.compare(this.pi_comboBoxGender.getSelectionModel().getSelectedItem(), this.modelMember.getGender())
                );
            }
        });
        this.pi_fieldNotes.setOnKeyTyped(keyEvent -> {
            if (this.formChangeListener.isListen()) {
                this.formChangeListener.change(
                        "notes",
                        Validator.compare(this.pi_fieldNotes.getText(), this.modelMember.getNotes())
                );
            }
        });
        this.fp_labelFingerprintCounter.textProperty().addListener((observable, oldValue, newValue) -> {
            if (this.formChangeListener.isListen() && !oldValue.equals(newValue)) {
                this.formChangeListener.change(
                        "fingerprint",
                        false
                );
            }
        });
    }
}
