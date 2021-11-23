package com.ocielgp.controller.admins;

import animatefx.animation.FadeInRight;
import animatefx.animation.FadeOutRight;
import animatefx.animation.Shake;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.ocielgp.app.Application;
import com.ocielgp.controller.Popup;
import com.ocielgp.dao.*;
import com.ocielgp.fingerprint.Fingerprint_Capture_Box;
import com.ocielgp.models.Model_Admin;
import com.ocielgp.models.Model_Admin_Role;
import com.ocielgp.models.Model_Member;
import com.ocielgp.utilities.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class Controller_Admin implements Initializable {
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
    private Label h_labelGym;
    @FXML
    private Label h_label;
    @FXML
    private Label h_updatedAt;
    @FXML
    private Label h_updatedBy;

    // -> administration [a]
    @FXML
    private JFXTextField a_fieldUsername;
    @FXML
    private JFXTextField a_fieldPassword;
    @FXML
    private JFXComboBox<Model_Admin_Role> a_comboBoxRoles;

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

    // -> shortcut [s]
    @FXML
    private VBox boxShortcut;
    @FXML
    private JFXButton s_buttonOpenDoor;
    @FXML
    private JFXButton s_buttonAccess;


    // -> end buttons
    @FXML
    private JFXButton buttonAction;
    @FXML
    private JFXButton buttonClear;

    // attributes
    private FormChangeListener formChangeListener;
    private int idMember = -1;
    private String style;
    private Model_Admin modelAdmin = null;
    private Model_Member modelMemberAdded = null;
    private PhotoHandler photoHandler;
    private Fingerprint_Capture_Box fingerprintCaptureBox;
    private final Pagination pagination;

    public Controller_Admin(Pagination pagination) {
        this.pagination = pagination;
    }

    public Controller_Admin(Pagination pagination, int idMember, String style) {
        this.pagination = pagination;
        this.idMember = idMember;
        this.style = style;
    }

    private void configureForm() {
        InputProperties.getScrollEvent(this.scrollPane);

        // max length
        InputProperties.createMaxLengthEvent(this.a_fieldUsername, Model_Admin.usernameLength);
        InputProperties.createMaxLengthEvent(this.pi_fieldName, Model_Member.nameLength);
        InputProperties.createMaxLengthEvent(this.pi_fieldLastName, Model_Member.lastNameLength);
        InputProperties.createMaxLengthEvent(this.pi_fieldNotes, Model_Member.notesLength);
        InputProperties.createComboBoxListener(this.pi_comboBoxGender, this.a_comboBoxRoles);

        InputProperties.createVisibleEvent(this.boxHistorical, false);
        InputProperties.createVisibleEvent(this.boxShortcut, false);

        // properties binding
        // -> scrollPane
        this.boxMember.heightProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue.doubleValue() > oldValue.doubleValue() && oldValue.doubleValue() != 0) {
                Platform.runLater(() -> this.scrollPane.setVvalue(1d));
            } else {
                if (this.modelAdmin == null) {
                    Platform.runLater(() -> this.scrollPane.setVvalue(0d));
                }
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

        JDBC_Admins_Role.ReadRoles().thenAccept(modelAdminsRoles -> {
            if (this.idMember == -1) {
                Loading.isChildLoaded.set(true);
                this.pagination.clearSelection();
            }
            this.a_comboBoxRoles.setItems(modelAdminsRoles);
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
        JDBC_Admin.ReadAdmin(idMember).thenAccept(model_admin -> {
            this.modelAdmin = model_admin;
            this.modelAdmin.setIdMember(idMember);
            this.modelAdmin.setStyle(style);

            this.fingerprintCaptureBox.getFingerprints(this.idMember);
            JDBC_Gym.ReadGym(this.modelAdmin.getIdGym())
                    .thenAccept(model_gym -> {
                        this.modelAdmin.setModelGym(model_gym);

                        int updatedBy;
                        if (this.modelAdmin.getUpdatedBy() == 0) {
                            Platform.runLater(() -> {
                                this.h_label.setText("Creado");
                                this.h_updatedAt.setText(DateTime.getDateWithDayName(this.modelAdmin.getCreatedAt()));
                            });
                            updatedBy = this.modelAdmin.getCreatedBy();
                        } else {
                            Platform.runLater(() -> {
                                this.h_label.setText("Modificado");
                                this.h_updatedAt.setText(DateTime.getDateWithDayName(this.modelAdmin.getUpdatedAt()));
                            });
                            updatedBy = this.modelAdmin.getUpdatedBy();
                        }
                        JDBC_Member.ReadMember(updatedBy).thenAccept(model_member -> {
                            this.modelMemberAdded = model_member;
                            eventClearForm(true);
                        });
                    });
        });
    }

    private void fillMemberForm() {
        Platform.runLater(() -> {
            // init form
            this.boxTitle.getStyleClass().set(1, this.modelAdmin.getStyle());
            this.t_labelTitle.setText("[ID: " + this.modelAdmin.getIdAdmin() + "] " + this.modelAdmin.getName());

            // quick view
            this.h_labelCreatedAt.setText(
                    DateTime.getDateWithDayName(
                            this.modelAdmin.getCreatedAt()
                    )
            );
            this.h_labelGym.setText(this.modelAdmin.getModelGym().getName());
            this.h_updatedBy.setText(this.modelMemberAdded.getName() + " " + this.modelMemberAdded.getLastName());
            this.boxHistorical.setVisible(true);

            // photo
            this.photoHandler.setPhoto(this.modelAdmin.getModelMemberPhoto().getPhoto());

            // administration
            this.a_fieldUsername.setText(this.modelAdmin.getUsername());
            this.a_comboBoxRoles.getItems().forEach(model_admin_role -> { // select current role
                if (model_admin_role.getIdRole().equals(this.modelAdmin.getIdRole())) {
                    this.a_comboBoxRoles.setValue(model_admin_role);
                }
            });

            // personal information
            this.pi_fieldName.setText(this.modelAdmin.getName());
            this.pi_fieldLastName.setText(this.modelAdmin.getLastName());
            this.pi_comboBoxGender.setValue(this.modelAdmin.getGender());
            this.pi_fieldNotes.setText(this.modelAdmin.getNotes());

            // shortcut
            if (this.modelAdmin.getAccess()) {
                this.s_buttonAccess.getStyleClass().set(3, Styles.DANGER);
                this.s_buttonAccess.setText("Bloquear acceso");
                this.s_buttonOpenDoor.setDisable(false);
            } else {
                this.s_buttonAccess.getStyleClass().set(3, Styles.SUCCESS);
                this.s_buttonAccess.setText("Desbloquear acceso");
                this.s_buttonOpenDoor.setDisable(true);
            }
            this.s_buttonOpenDoor.setOnAction(actionEvent -> {
                this.s_buttonOpenDoor.setDisable(true);
                CompletableFuture.runAsync(() -> JDBC_Check_In.CheckInByAdmin(this.modelAdmin.getIdAdmin()));
            });
            this.boxShortcut.setVisible(true);

            // end buttons
            this.s_buttonAccess.setOnAction(actionEvent -> changeAccess());

            this.buttonAction.setText("Guardar cambios");
            this.buttonAction.setDisable(true);
            this.buttonClear.setText("Cancelar");

            this.formChangeListener.setListen(true);

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
            this.t_labelTitle.setText("Administrador nuevo");

            this.boxHistorical.setVisible(false);

            this.photoHandler.restartPane();

            this.fingerprintCaptureBox.initialStatePane(true);

            InputProperties.clearInputs(
                    this.a_fieldUsername,
                    this.a_fieldPassword,
                    this.a_comboBoxRoles,
                    this.pi_fieldName,
                    this.pi_fieldLastName,
                    this.pi_comboBoxGender,
                    this.pi_fieldNotes
            );

            this.s_buttonAccess.setDisable(false);
            this.boxShortcut.setVisible(false);

            this.buttonAction.setDisable(false);
            this.buttonAction.setText("Registrar");
            this.buttonClear.setText("Limpiar");

            if (fillMemberForm) {
                fillMemberForm();
            } else {
                this.modelAdmin = null;
                this.modelMemberAdded = null;
                this.scrollPane.setVvalue(0d);
                FadeInRight fadeInRightScrollPane = new FadeInRight(this.scrollPane);
                fadeInRightScrollPane.setOnFinished(actionEvent -> this.a_fieldUsername.requestFocus());
                fadeInRightScrollPane.play();
            }
        });
    }

    private boolean formValidator() {
        ArrayList<Node> nodesRequired = new ArrayList<>();
        // administration
        nodesRequired.add(this.a_fieldUsername);
        if (this.modelAdmin == null) nodesRequired.add(this.a_fieldPassword);
        nodesRequired.add(this.a_comboBoxRoles);

        // personal information
        nodesRequired.add(this.pi_fieldName);
        if (this.modelAdmin == null) nodesRequired.add(this.pi_fieldLastName);
        nodesRequired.add(this.pi_comboBoxGender);
        nodesRequired.add(Application.GetCurrentGymNode());

        boolean formValid = Validator.emptyValidator(nodesRequired.toArray(new Node[]{}));
        if (formValid) { // text validator
            nodesRequired.clear();
            nodesRequired.add(this.pi_fieldName);
            if (this.pi_fieldLastName.getText().length() > 0) nodesRequired.add(this.pi_fieldLastName);
            formValid = Validator.textValidator(nodesRequired.toArray(new Node[]{}));
        }
        return formValid;
    }

    private void validateAndUpdateMember() {
        CompletableFuture.runAsync(() -> {
            try {
                if (formValidator()) { // form 100% valid
                    Model_Admin_Role modelAdminRole = a_comboBoxRoles.getValue();

                    Loading.show();
                    if (this.formChangeListener.isListen()) saveChanges();
                    else createAdmin();
                    Loading.closeNow();
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    private void createAdmin() {
        if (JDBC_Admin.ReadAdminAvailable(this.a_fieldUsername.getText())) {
            Model_Admin modelAdmin = new Model_Admin();
            modelAdmin.setName(InputProperties.capitalizeFirstLetterPerWord(this.pi_fieldName.getText()));
            modelAdmin.setLastName(InputProperties.capitalizeFirstLetterPerWord(this.pi_fieldLastName.getText()));
            modelAdmin.setGender(this.pi_comboBoxGender.getValue());
            modelAdmin.setNotes(InputProperties.capitalizeFirstLetter(this.pi_fieldNotes.getText()));
            int idMember = JDBC_Member.CreateMember(modelAdmin);

            modelAdmin.setUsername(this.a_fieldUsername.getText());
            modelAdmin.setPassword(this.a_fieldPassword.getText());
            modelAdmin.setIdRole(this.a_comboBoxRoles.getValue().getIdRole());

            if (idMember > 0) {
                modelAdmin.setIdMember(idMember);
                JDBC_Admin.CreateAdmin(modelAdmin);
                JDBC_Member_Photo.CreatePhoto(idMember, photoHandler.getPhoto());
                JDBC_Member_Fingerprint.CreateFingerprints(idMember, fingerprintCaptureBox.getFingerprintsListIterator());
                Notifications.Success("Nuevo administrador", "[ID: " + idMember + " ] " + modelAdmin.getName() + " ha sido registrado");
                eventClearForm(false);
                this.pagination.refillTable(1);
            }
        } else {
            Validator.shakeInput(this.a_fieldUsername);
            Notifications.Danger("Usuario", "Nombre de usuario registrado");
        }
    }

    private void saveChanges() {
        System.out.println("fire");
        if (this.formChangeListener.isChanged("username")) {
            if (!JDBC_Admin.ReadAdminAvailable(this.a_fieldUsername.getText())) {
                Validator.shakeInput(this.a_fieldUsername);
                Notifications.Danger("Usuario", "Nombre de usuario registrado");
                return;
            }
        }

        Model_Member modelMember = new Model_Member();
        modelMember.setIdMember(this.modelAdmin.getIdAdmin());
        Model_Admin modelAdmin = new Model_Admin();
        modelAdmin.setIdAdmin(this.modelAdmin.getIdAdmin());

        boolean isOk;
        if (this.formChangeListener.isChanged("username")) {
            modelAdmin.setUsername(this.a_fieldUsername.getText());
        }
        if (this.formChangeListener.isChanged("password")) {
            modelAdmin.setPassword(Hash.generateHash(this.a_fieldPassword.getText()));
        }
        if (this.formChangeListener.isChanged("role")) {
            modelAdmin.setIdRole(this.a_comboBoxRoles.getValue().getIdRole());
        }
        isOk = JDBC_Admin.UpdateAdmin(modelAdmin);
        System.out.println("isok: " + isOk);

        if (isOk && this.formChangeListener.isChanged("photo")) {
            isOk = JDBC_Member_Photo.UpdatePhoto(this.modelAdmin.getIdAdmin(), this.photoHandler.getPhoto());
        }

        if (isOk && this.formChangeListener.isChanged("name")) {
            modelMember.setName(InputProperties.capitalizeFirstLetterPerWord(this.pi_fieldName.getText()));
            this.modelAdmin.setName(modelMember.getName());
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
            modelMember.setAccess(!this.modelAdmin.getAccess());
        }
        isOk = JDBC_Member.UpdateMember(modelMember);

        if (isOk && this.formChangeListener.isChanged("fingerprint")) {
            isOk = JDBC_Member_Fingerprint.CreateFingerprints(this.modelAdmin.getIdAdmin(), Objects.requireNonNull(fingerprintCaptureBox.getFingerprintsListIterator()));
        }

        if (isOk) {
            Notifications.Success("Nuevos cambios", "[ID:  " + this.modelAdmin.getIdAdmin() + "] " + this.modelAdmin.getName() + " información actualizada");
            eventClearForm(false);
            this.pagination.refillTable(1);
        }
    }

    private void changeAccess() {
        if (!this.modelAdmin.getIdAdmin().equals(Application.GetModelAdmin().getIdAdmin())) {
            Popup popup = new Popup();
            Platform.runLater(() -> {
                boolean isMemberAccess = this.modelAdmin.getAccess();
                popup.confirm(
                        (isMemberAccess) ? Styles.DANGER : Styles.SUCCESS,
                        (isMemberAccess) ? "Bloquear acceso" : "Desbloquear acceso",
                        (isMemberAccess)
                                ? "Se le bloqueara el acceso a todos los gimnasios a " + this.modelAdmin.getName() + " " + this.modelAdmin.getLastName() + ", ¿quieres continuar?"
                                : "Se desbloqueara el acceso a " + this.modelAdmin.getName() + " " + this.modelAdmin.getLastName() + ", ¿quieres continuar?"
                );
                boolean isConfirm = popup.showAndWait();
                if (isConfirm) {
                    this.formChangeListener.change("access", false);
                    this.s_buttonAccess.setDisable(true);
                }
            });
        } else {
            new Shake(this.s_buttonAccess).play();
        }
    }

    private void createFormChangeListener() {
        this.formChangeListener = new FormChangeListener(this.buttonAction);
        formChangeListener.add("username");
        formChangeListener.add("password");
        formChangeListener.add("role");
        formChangeListener.add("photo");
        formChangeListener.add("name");
        formChangeListener.add("lastName");
        formChangeListener.add("gender");
        formChangeListener.add("notes");
        formChangeListener.add("access");
        formChangeListener.add("fingerprint");
        this.a_fieldUsername.setOnKeyTyped(keyEvent -> {
            if (this.formChangeListener.isListen()) {
                this.formChangeListener.change(
                        "username",
                        Validator.compare(this.a_fieldUsername.getText(), this.modelAdmin.getUsername())
                );
            }
        });
        this.a_fieldPassword.setOnKeyTyped(keyEvent -> {
            if (this.formChangeListener.isListen()) {
                this.formChangeListener.change(
                        "password",
                        Validator.compare(this.a_fieldPassword.getText(), this.modelAdmin.getName())
                );
            }
        });
        this.a_comboBoxRoles.setOnAction(keyEvent -> {
            if (this.formChangeListener.isListen()) {
                this.formChangeListener.change(
                        "role",
                        Validator.compare(this.a_comboBoxRoles.getValue().getIdRole().toString(), this.modelAdmin.getIdRole().toString())
                );
            }
        });
        this.pi_fieldName.setOnKeyTyped(keyEvent -> {
            if (this.formChangeListener.isListen()) {
                this.formChangeListener.change(
                        "name",
                        Validator.compare(this.pi_fieldName.getText(), this.modelAdmin.getName())
                );
            }
        });
        this.pi_fieldLastName.setOnKeyTyped(keyEvent -> {
            if (this.formChangeListener.isListen()) {
                this.formChangeListener.change(
                        "lastName",
                        Validator.compare(this.pi_fieldLastName.getText(), this.modelAdmin.getLastName())
                );
            }
        });
        this.pi_comboBoxGender.setOnAction(keyEvent -> {
            if (this.formChangeListener.isListen()) {
                this.formChangeListener.change(
                        "gender",
                        Validator.compare(this.pi_comboBoxGender.getValue(), this.modelAdmin.getGender())
                );
            }
        });
        this.pi_fieldNotes.setOnKeyTyped(keyEvent -> {
            if (this.formChangeListener.isListen()) {
                this.formChangeListener.change(
                        "notes",
                        Validator.compare(this.pi_fieldNotes.getText(), this.modelAdmin.getNotes())
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
