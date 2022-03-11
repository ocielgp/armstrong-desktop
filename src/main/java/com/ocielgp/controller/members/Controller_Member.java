package com.ocielgp.controller.members;

import animatefx.animation.FadeInRight;
import animatefx.animation.FadeOutRight;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import com.ocielgp.app.Application;
import com.ocielgp.controller.Popup;
import com.ocielgp.dao.*;
import com.ocielgp.fingerprint.Fingerprint_Capture_Box;
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

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
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

    // -> historical [h]
    @FXML
    private VBox boxHistorical;
    @FXML
    private Label h_labelCreatedAt;
    @FXML
    private Label h_labelLastPayment;
    @FXML
    private Label h_labelGym;
    @FXML
    private Label h_labelAdmin;

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
    private FormChangeListener formChangeListener;
    private int idMember = -1;
    private String style;
    private Model_Member modelMember = null;
    private Model_Member modelAdmin = null;
    private PhotoHandler photoHandler;
    private Fingerprint_Capture_Box fingerprintCaptureBox;
    private final Pagination pagination;
    private short totalMonths = 1;
    private final ObjectProperty<BigDecimal> membershipPrice = new SimpleObjectProperty<>(new BigDecimal(0));

    public Controller_Member(Pagination pagination) {
        this.pagination = pagination;
    }

    public Controller_Member(Pagination pagination, int idMember, String style) {
        this.pagination = pagination;
        this.idMember = idMember;
        this.style = style;
    }

    private void checkIfMemberExists() {
        if (!this.pi_fieldName.getText().isEmpty() && !this.pi_fieldLastName.getText().isEmpty()) {
            JDBC_Member.ReadIsNewMember(
                    InputProperties.capitalizeFirstLetterPerWord(this.pi_fieldName.getText()),
                    InputProperties.capitalizeFirstLetterPerWord(this.pi_fieldLastName.getText())
            ).thenAccept(idMember -> {
                if (idMember > 0) {
                    Platform.runLater(() -> {
                        Popup popup = new Popup();
                        popup.alert(Styles.DANGER, "Socio encontrado", "Socio con este nombre registrado (ID: " + idMember + ")");
                        popup.showAndWait();
                    });
                }
            });
        }
    }

    private void configureForm() {
        InputProperties.getScrollEvent(this.scrollPane);

        // max length
        InputProperties.createMaxLengthEvent(this.pi_fieldName, Model_Member.nameLength);
        InputProperties.createMaxLengthEvent(this.pi_fieldLastName, Model_Member.lastNameLength);
        InputProperties.createMaxLengthEvent(this.pi_fieldNotes, Model_Member.notesLength);
        InputProperties.createComboBoxListener(this.pi_comboBoxGender, this.ms_comboBoxMemberships);

        this.pi_fieldName.focusedProperty().addListener(((observableValue, oldValue, newValue) -> {
            if (!newValue) checkIfMemberExists();
        }));

        this.pi_fieldLastName.focusedProperty().addListener(((observableValue, oldValue, newValue) -> {
            if (!newValue) checkIfMemberExists();
        }));

        // -> historical
        InputProperties.createVisibleEvent(this.boxHistorical, false);

        // -> membership
        InputProperties.createVisibleEvent(this.ms_boxEndDate, false);
        InputProperties.createVisibleEvent(this.ms_boxMonths, false);
        InputProperties.createVisibleEvent(this.ms_boxButtons, false);

        // -> payment
        InputProperties.createVisibleEvent(this.boxPayment, false);
        InputProperties.createVisibleEvent(this.pym_boxOwe, false);

        // -> shortcut
        InputProperties.createVisibleEvent(this.boxShortcut, false);
        InputProperties.createVisibleEvent(this.s_buttonPayDebt, false);

        // properties binding
        // -> scrollPane
        this.boxMember.heightProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue.doubleValue() > oldValue.doubleValue() && oldValue.doubleValue() != 0) {
                Platform.runLater(() -> this.scrollPane.setVvalue(1d));
            } else {
                if (this.modelMember == null) {
                    Platform.runLater(() -> this.scrollPane.setVvalue(0d));
                }
            }
        });

        // -> membership
        this.ms_comboBoxMemberships.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Platform.runLater(() -> updateMembershipPrice(true));
            } else {
                Platform.runLater(() -> {
                    this.ms_boxEndDate.setVisible(false);
                    this.boxPayment.setVisible(false);
                });
            }
        });

        // -> payment
        this.boxPayment.visibleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                Platform.runLater(() -> this.pym_togglePayment.setSelected(true));
            }
        });
        this.pym_togglePayment.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            Platform.runLater(() -> this.pym_boxOwe.setVisible(!newValue));
            if (newValue) { // hide boxOwe
                InputProperties.clearInputs(this.pym_fieldPaidOut, this.pym_fieldOwe);
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
    }

    private void configureData() {
        JDBC_Member.ReadGenders().thenAccept(genders -> this.pi_comboBoxGender.setItems(genders));
        this.pi_comboBoxGender.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.H) {
                this.pi_comboBoxGender.getSelectionModel().select(0);
            } else if (keyEvent.getCode() == KeyCode.M) {
                this.pi_comboBoxGender.getSelectionModel().select(1);
            }
        });

        JDBC_Membership.ReadMemberships(Model_Membership.MONTHLY).thenAccept(model_memberships -> {
            if (this.idMember == -1) {
                Loading.isChildLoaded.set(true);
                this.pagination.clearSelection();
            }
            this.ms_comboBoxMemberships.setItems(model_memberships);
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configureForm();
        configureData();
        createFormChangeListener();

        // photo section
        this.photoHandler = new PhotoHandler(this.formChangeListener, this.ph_imgMemberPhoto, this.ph_buttonDeletePhoto);

        // fingerprint
        this.fingerprintCaptureBox = new Fingerprint_Capture_Box(this.boxFingerprint, this.fp_boxFingerprint, this.fp_labelFingerprintCounter, this.fp_buttonCapture, this.fp_buttonRestartCapture);

        // membership
        this.ms_iconSubtractMonth.setOnMouseClicked(mouseEvent -> eventSubtractMonth());
        this.ms_iconSubtractMonth.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER || keyEvent.getCode() == KeyCode.SPACE) eventSubtractMonth();
        });
        this.ms_iconAddMonth.setOnMouseClicked(mouseEvent -> eventAddMonth());
        this.ms_iconAddMonth.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER || keyEvent.getCode() == KeyCode.SPACE) eventAddMonth();
        });

        // end buttons
        this.buttonAction.setOnAction(actionEvent -> validateAndUpdateMember());
        this.buttonClear.setOnAction((actionEvent) -> {
            eventClearForm(false);
            this.pagination.clearSelection();
        });

        if (this.idMember > -1) {
            getMemberData(this.idMember, this.style);
        }
    }

    public void getMemberData(int idMember, String style) {
        Platform.runLater(() -> new FadeOutRight(this.scrollPane).play()); // hide when data is loading
        JDBC_Member.ReadMember(idMember).thenAccept(model_member -> {
            this.modelMember = model_member;
            this.modelMember.setIdMember(idMember);
            this.modelMember.setStyle(style);

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
                            JDBC_Member.ReadMember(model_payments_memberships.getCreatedBy()).thenAccept(model_admin -> JDBC_Gym.ReadGym(model_payments_memberships.getIdGym())
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
            this.boxTitle.getStyleClass().set(1, this.modelMember.getStyle());
            this.t_labelTitle.setText("[ID: " + this.modelMember.getIdMember() + "] " + this.modelMember.getName());

            // quick view
            this.h_labelCreatedAt.setText(
                    DateTime.getDateWithDayName(
                            this.modelMember.getCreatedAt()
                    )
            );
            this.h_labelGym.setText(this.modelMember.getModelGym().getName());
            this.boxHistorical.setVisible(true);

            // photo
            this.photoHandler.setPhoto(this.modelMember.getModelMemberPhoto().getPhoto());

            // personal information
            this.pi_fieldName.setText(this.modelMember.getName());
            this.pi_fieldLastName.setText(this.modelMember.getLastName());
            this.pi_comboBoxGender.setValue(this.modelMember.getGender());
            this.pi_fieldNotes.setText(this.modelMember.getNotes());

            // fingerprint
            this.fingerprintCaptureBox.getFingerprints(this.modelMember.getIdMember());

            // membership
            if (this.modelMember.getModelPaymentMembership() != null) {
                this.h_labelAdmin.setText(this.modelAdmin.getName() + " " + this.modelAdmin.getLastName());
                this.h_labelLastPayment.setText(
                        DateTime.getDateTime(
                                this.modelMember.getModelPaymentMembership().getStartDateTime()
                        )
                );
                this.ms_comboBoxMemberships.setDisable(true);
                this.ms_comboBoxMemberships.getItems().forEach(model_membership -> { // select current membership
                    if (model_membership.getIdMembership().equals(this.modelMember.getModelPaymentMembership().getIdMembership())) {
                        this.ms_comboBoxMemberships.setValue(model_membership);
                        createMembershipButtons();
                    }
                });
            } else {
                this.h_labelLastPayment.setText("N / A");
                Notifications.Warn("Pago no encontrado", "Último pago no encontrado");
            }

            // shortcut
            if (this.modelMember.getAccess()) {
                this.s_buttonAccess.getStyleClass().set(3, Styles.DANGER);
                this.s_buttonAccess.setText("Bloquear acceso");
                this.s_buttonOpenDoor.setDisable(false);
            } else {
                this.s_buttonAccess.getStyleClass().set(3, Styles.SUCCESS);
                this.s_buttonAccess.setText("Desbloquear acceso");
                this.s_buttonOpenDoor.setDisable(true);
            }
            this.s_buttonOpenDoor.setDisable(this.modelMember.getStyle().equals(Styles.DANGER));
            this.s_buttonOpenDoor.setOnAction(actionEvent -> {
                this.s_buttonOpenDoor.setDisable(true);
                CompletableFuture.runAsync(() -> JDBC_Check_In.CheckInByAdmin(this.modelMember.getIdMember()));
            });
            if (this.modelMember.getStyle().equals(Styles.CREATIVE)) {
                this.s_buttonPayDebt.setVisible(true);
                this.s_buttonOpenDoor.setDisable(true);
            } else {
                this.s_buttonPayDebt.setVisible(false);
            }
            if (!this.modelMember.getAccess()) {
                this.ms_boxButtons.setVisible(false);
            }
            this.s_buttonAccess.setOnAction(actionEvent -> changeAccess());
            this.s_buttonPayDebt.setOnAction(actionEvent -> {
                Popup popup = new Popup();
                popup.debt(this.modelMember.getIdMember());
                if (popup.showAndWait()) {
                    eventClearForm(false);
                    this.pagination.refillTable(1);
                }
            });
            this.boxShortcut.setVisible(true);

            // end buttons
            this.buttonAction.setText("Guardar cambios");
            this.buttonAction.setDisable(true);
            this.buttonClear.setText("Cancelar");

            this.formChangeListener.setListen(true);
            if (this.modelMember.getModelPaymentMembership() != null)
                this.ms_labelEndDate.setText(DateTime.getDateWithDayName(this.modelMember.getModelPaymentMembership().getEndDateTime()));

            FadeInRight fadeInRightScrollPane = new FadeInRight(this.scrollPane);
            fadeInRightScrollPane.setOnFinished(actionEvent -> {
                if (this.idMember > -1) {
                    Loading.isChildLoaded.set(true);
                    this.idMember = -1;
                    this.style = null;
                } else {
                    Loading.closeNow();
                }
            });
            fadeInRightScrollPane.play();
        });
    }

    private void eventClearForm(boolean fillMemberForm) {
        this.formChangeListener.setListen(false);
        Platform.runLater(() -> {
            this.boxTitle.getStyleClass().set(1, Styles.DEFAULT);
            this.t_labelTitle.setText("Socio nuevo");

            this.boxHistorical.setVisible(false);

            this.photoHandler.restartPane();

            this.fingerprintCaptureBox.initialStatePane(true);

            InputProperties.clearInputs(
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
            this.ms_labelEndDate.getStyleClass().remove("strikethrough");

            this.s_buttonAccess.setDisable(false);
            this.boxShortcut.setVisible(false);

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
                fadeInRightScrollPane.setOnFinished(actionEvent -> this.pi_fieldName.requestFocus());
                fadeInRightScrollPane.play();
            }
        });
    }

    private boolean formValidator() {
        ArrayList<Node> nodesRequired = new ArrayList<>();
        // personal information
        nodesRequired.add(this.pi_fieldName);
        nodesRequired.add(this.pi_fieldLastName);
        nodesRequired.add(this.pi_comboBoxGender);
        nodesRequired.add(Application.GetCurrentGymNode());

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
        }
        return formValid;
    }

    private void validateAndUpdateMember() {
        CompletableFuture.runAsync(() -> {
            try {
                if (formValidator()) { // form 100% valid
                    Model_Membership modelMembership = prepareMembership();
                    Model_Debt modelDebt = prepareDebt();

                    if (modelDebt != null) { // insert into database
                        Loading.show();
                        if (this.formChangeListener.isListen()) saveChanges(modelMembership, modelDebt);
                        else createMember(modelMembership, modelDebt);
                        this.pagination.refillTable(1);
                        eventClearForm(false);
                        Loading.closeNow();
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    private Model_Membership prepareMembership() {
        Model_Membership modelMembership = new Model_Membership();
        modelMembership.setIdMembership(this.ms_comboBoxMemberships.getValue().getIdMembership());
        modelMembership.setPrice(this.membershipPrice.get());
        return modelMembership;
    }

    private Model_Debt prepareDebt() {
        final Model_Debt modelDebt = new Model_Debt();
        if (this.boxPayment.isVisible()) {
            if (!this.pym_togglePayment.isSelected()) { // have debt
                BigDecimal paidOut = new BigDecimal(this.pym_fieldPaidOut.getText());
                BigDecimal owe = new BigDecimal(this.pym_fieldOwe.getText());

                if (owe.compareTo(BigDecimal.ZERO) == 0) {
                    Validator.shakeInput(this.pym_fieldPaidOut);
                    Notifications.Danger("Error", "Se trata de un pago completo, no hay deuda");
                    return null;
                } else if (owe.compareTo(BigDecimal.ZERO) < 0) {
                    Validator.shakeInput(this.pym_fieldPaidOut);
                    Notifications.Danger("Error", "La deuda es mayor al total a pagar");
                    return null;
                } else {
                    modelDebt.setPaidOut(paidOut);
                    modelDebt.setOwe(owe);
                }
            }
        }
        return modelDebt;
    }

    private void createMember(Model_Membership modelMembership, Model_Debt modelDebt) {
        Model_Member modelMember = new Model_Member();
        modelMember.setName(InputProperties.capitalizeFirstLetterPerWord(this.pi_fieldName.getText()));
        modelMember.setLastName(InputProperties.capitalizeFirstLetterPerWord(this.pi_fieldLastName.getText()));
        modelMember.setGender(this.pi_comboBoxGender.getValue());
        modelMember.setNotes(InputProperties.capitalizeFirstLetter(this.pi_fieldNotes.getText()));
        int idMember = JDBC_Member.CreateMember(modelMember);
        if (idMember > 0) {
            JDBC_Member_Photo.CreatePhoto(idMember, photoHandler.getPhoto());
            JDBC_Member_Fingerprint.CreateFingerprints(idMember, fingerprintCaptureBox.getFingerprintsListIterator());
            int idNewMembership = JDBC_Payment_Membership.CreatePaymentMembership(idMember, modelMembership, this.totalMonths, true);
            if (modelDebt.getOwe() != null && idNewMembership > 0) {
                JDBC_Debt.CreateDebt(idMember, modelDebt);
            }
            Notifications.Success("Nuevo socio", "[ID: " + idMember + " ] " + modelMember.getName() + " ha sido registrado");
        }
    }

    private void saveChanges(Model_Membership modelMembership, Model_Debt modelDebt) {
        Model_Member modelMember = new Model_Member();
        modelMember.setIdMember(this.modelMember.getIdMember());

        boolean isOk = true;
        if (this.formChangeListener.isChanged("membershipRenew")) {
            int newIdMembership = JDBC_Payment_Membership.CreatePaymentMembership(this.modelMember.getIdMember(), modelMembership, this.totalMonths, false);
            isOk = newIdMembership > 0;
            if (modelDebt.getOwe() != null && newIdMembership > 0) {
                isOk = JDBC_Debt.CreateDebt(this.modelMember.getIdMember(), modelDebt);
            }
        }
        if (isOk && this.formChangeListener.isChanged("membershipChange")) {
            this.modelMember.getModelPaymentMembership().setMonths(this.totalMonths);
            isOk = JDBC_Payment_Membership.UpdatePaymentMembership(modelMembership, this.modelMember.getModelPaymentMembership());
            if (modelDebt.getOwe() != null) {
                isOk = JDBC_Debt.CreateDebt(this.modelMember.getIdMember(), modelDebt);
            }
        }
        if (isOk && this.formChangeListener.isChanged("membershipDelete")) {
            isOk = JDBC_Payment_Membership.DeletePaymentMembership(this.modelMember.getModelPaymentMembership());
        }
        if (isOk && this.formChangeListener.isChanged("photo")) {
            isOk = JDBC_Member_Photo.UpdatePhoto(this.modelMember.getIdMember(), this.photoHandler.getPhoto());
        }

        if (isOk && this.formChangeListener.isChanged("name")) {
            modelMember.setName(InputProperties.capitalizeFirstLetterPerWord(this.pi_fieldName.getText()));
            this.modelMember.setName(modelMember.getName());
        }
        if (isOk && this.formChangeListener.isChanged("lastName")) {
            modelMember.setLastName(InputProperties.capitalizeFirstLetterPerWord(this.pi_fieldLastName.getText()));
        }
        if (isOk && this.formChangeListener.isChanged("gender")) {
            modelMember.setGender(this.pi_comboBoxGender.getValue());
        }
        if (isOk && this.formChangeListener.isChanged("notes")) {
            modelMember.setNotes(InputProperties.capitalizeFirstLetter(this.pi_fieldNotes.getText()));
        }
        if (isOk && this.formChangeListener.isChanged("access")) {
            modelMember.setAccess(!this.modelMember.getAccess());
        }
        isOk = JDBC_Member.UpdateMember(modelMember);

        if (isOk && this.formChangeListener.isChanged("fingerprint")) {
            isOk = JDBC_Member_Fingerprint.CreateFingerprints(this.modelMember.getIdMember(), Objects.requireNonNull(fingerprintCaptureBox.getFingerprintsListIterator()));
        }

        if (isOk) {
            Notifications.Success("Nuevos cambios", "[ID:  " + this.modelMember.getIdMember() + "] " + this.modelMember.getName() + " información actualizada");
        }
    }


    private void createMembershipButtons() {
        if (DateTime.isToday(this.modelMember.getModelPaymentMembership().getStartDateTime())) { // today's payment
            JFXButton buttonChangeMembership = new JFXButton("Cambiar");
            buttonChangeMembership.getStyleClass().addAll("btn-colorful", "warn-style");
            buttonChangeMembership.setOnAction(actionEvent -> eventMembershipChange());
            JFXButton buttonRemoveMembership = new JFXButton("Quitar");
            buttonRemoveMembership.getStyleClass().addAll("btn-colorful", "danger-style");
            buttonRemoveMembership.setOnAction(actionEvent -> eventMembershipDelete());
            this.ms_boxButtons.getChildren().setAll(buttonChangeMembership, buttonRemoveMembership);
            this.ms_boxButtons.setVisible(true);
        } else if (!this.modelMember.getStyle().equals(Styles.CREATIVE)) {
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
        updateMembershipPrice(true);
        formChangeListener.change("membershipRenew", false);
    }

    private void eventMembershipChange() {
        if (Objects.equals(this.modelAdmin.getIdMember(), Application.GetModelAdmin().getIdAdmin()) || Application.GetModelAdmin().getIdAdmin() == 1) {
            Popup popup = new Popup();
            popup.password();
            if (popup.showAndWait()) {
                Notifications.Default("gmi-event", "Membresía", "Selecciona un nuevo plan");
                this.ms_boxButtons.setDisable(true);
                this.ms_comboBoxMemberships.setDisable(false);
                this.ms_boxMonths.setVisible(true);
                this.boxPayment.setVisible(true);
                updateMembershipPrice(true);
                formChangeListener.change("membershipChange", false);
            }
        } else {
            Notifications.Danger("Sin permiso", "Solo el que registro el pago puede cambiar la mensualidad");
        }
    }

    private void eventMembershipDelete() {
        if (Objects.equals(this.modelAdmin.getIdMember(), Application.GetModelAdmin().getIdAdmin()) || Application.GetModelAdmin().getIdAdmin() == 1) {
            Popup popup = new Popup();
            popup.password();
            if (popup.showAndWait()) {
                this.ms_labelEndDate.getStyleClass().add("strikethrough");
                formChangeListener.change("membershipDelete", false);
                this.ms_boxButtons.setDisable(true);
            }
        } else {
            Notifications.Danger("Sin permiso", "Solo el que registro el pago puede quitar la mensualidad");
        }
    }

    private void eventAddMonth() {
        if (this.totalMonths < Model_Debt.MAX_AMOUNT) {
            this.totalMonths++;
            updateMembershipPrice(false);
        }
    }

    private void eventSubtractMonth() {
        if (this.totalMonths != 1) {
            this.totalMonths--;
            updateMembershipPrice(false);
        }
    }

    private void updateMembershipPrice(boolean restartMonth) {
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
                if (this.modelMember != null) {
                    if (this.modelMember.getModelPaymentMembership() == null) {
                        this.formChangeListener.change("membershipRenew", false);
                    }
                    if (this.formChangeListener.isChanged("membershipRenew") || this.formChangeListener.isChanged("membershipChange") || this.formChangeListener.isChanged("membershipDelete")) {
                        this.ms_labelEndDate.setText(
                                DateTime.getEndDateWithDayName(this.totalMonths)
                        );
                    }
                }
            } else {
                this.ms_labelEndDate.setText(
                        DateTime.getEndDateWithDayName(this.totalMonths)
                );
            }
            this.ms_labelMonth.setText(this.totalMonths + ((this.totalMonths == 1) ? " MES" : " MESES"));
            this.membershipPrice.set(
                    this.ms_comboBoxMemberships.getValue().getPrice().multiply(
                            BigDecimal.valueOf(this.totalMonths)
                    )
            );
            if (this.boxPayment.isVisible()) {
                InputProperties.clearInputs(this.pym_fieldPaidOut);
                this.pym_fieldOwe.setText(this.membershipPrice.get().toString());
            }
        });
    }

    private void changeAccess() {
        Popup popup = new Popup();
        Platform.runLater(() -> {
            boolean isMemberAccess = this.modelMember.getAccess();
            popup.confirm(
                    (isMemberAccess) ? Styles.DANGER : Styles.SUCCESS,
                    (isMemberAccess) ? "Bloquear acceso" : "Desbloquear acceso",
                    (isMemberAccess)
                            ? "Se le bloqueara el acceso a todos los gimnasios a " + this.modelMember.getName() + " " + this.modelMember.getLastName() + ", ¿quieres continuar?"
                            : "Se desbloqueara el acceso a " + this.modelMember.getName() + " " + this.modelMember.getLastName() + ", ¿quieres continuar?"
            );
            boolean isConfirm = popup.showAndWait();
            if (isConfirm) {
                this.formChangeListener.change("access", false);
                this.s_buttonAccess.setDisable(true);
            }
        });
    }

    private void createFormChangeListener() {
        this.formChangeListener = new FormChangeListener(this.buttonAction);
        formChangeListener.add("photo");
        formChangeListener.add("name");
        formChangeListener.add("lastName");
        formChangeListener.add("gender");
        formChangeListener.add("notes");
        formChangeListener.add("access");
        formChangeListener.add("fingerprint");
        formChangeListener.add("membershipRenew");
        formChangeListener.add("membershipChange");
        formChangeListener.add("membershipDelete");
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
                        Validator.compare(this.pi_comboBoxGender.getValue(), this.modelMember.getGender())
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
