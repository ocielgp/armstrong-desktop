package com.ocielgp.utilities;

import animatefx.animation.FadeInUp;
import animatefx.animation.Shake;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.ocielgp.app.Application;
import com.ocielgp.models.Model_Debt;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Dialog {

    // Buttons
    public static final String YES = "Sí";
    public static final String NO = "No";
    public static final String OK = "Aceptar";
    public static final String CANCEL = "Cancelar";

    private final Stage stage = new Stage(StageStyle.TRANSPARENT);
    private VBox dialogView;
    public DialogView dialogController;
    public boolean answerStatus = false;

    public void closeModal() {
        this.stage.close();
    }

    public Dialog(String style, String title, String content, DialogTypes type, String... buttonsText) {
        this.dialogController = new DialogView(this, style, title, content, type, buttonsText);
    }

    public Dialog(String style, String title, List<Model_Debt> debtList, String... buttonsText) {
        this.dialogController = new DialogView(this, style, title, DialogTypes.DEBT, debtList, buttonsText);
    }

    public boolean show() {

        this.dialogView = (VBox) Loader.Load(
                "dialog.fxml",
                "Dialogs",
                false,
                this.dialogController
        );
        this.dialogView.setOnKeyPressed(this.eventHandlerEscapeKey());
        Scene scene = new Scene(this.dialogView);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add("styles.css");
        this.stage.getIcons().setAll(FileLoader.getIconApp());
        this.stage.initModality(Modality.APPLICATION_MODAL);
        this.stage.setAlwaysOnTop(true);
        this.stage.setScene(scene);
        this.stage.show();
        this.stage.setX(
                Screen.getPrimary().getVisualBounds().getWidth() / 2 - stage.getWidth() / 2
        );
        this.stage.setY(
                Screen.getPrimary().getVisualBounds().getHeight() / 2 - stage.getHeight() / 2
        );
        this.stage.close();
        this.stage.showingProperty().addListener((observable, oldValue, newValue) -> {
            new FadeInUp(this.dialogView).play();
        });
        this.stage.showAndWait();
        return this.answerStatus;
    }

    private class DialogView implements Initializable {
        @FXML
        private VBox boxDialog;
        @FXML
        private HBox boxContent;
        @FXML
        private Label labelTitle;
        @FXML
        private ScrollPane scrollPane;
        @FXML
        private VBox boxContentBuilder;
        @FXML
        private Label labelContent;
        @FXML
        private HBox boxDebt;
        @FXML
        private Label labelDebt;
        @FXML
        public JFXTextField input;
        @FXML
        private JFXButton buttonOk;
        @FXML
        private JFXButton buttonCancel;

        // Attributes
        private final Dialog controller;
        private final String style;
        private final String title;
        private String content;
        public final DialogTypes dialogType;
        private final String[] buttonsText;

        private List<Model_Debt> debtList;

        public DialogView(Dialog controller, String style, String title, String content, DialogTypes dialogType, String... buttonText) {
            this.controller = controller;
            this.style = style;
            this.title = title;
            this.content = content;
            this.dialogType = dialogType;
            this.buttonsText = buttonText;
        }

        public DialogView(Dialog controller, String style, String title, DialogTypes dialogType, List<Model_Debt> debtList, String... buttonText) {
            this.controller = controller;
            this.style = style;
            this.title = title;
            this.dialogType = dialogType;
            this.debtList = debtList;
            this.buttonsText = buttonText;
        }

        @Override
        public void initialize(URL url, ResourceBundle resourceBundle) {
            this.boxDialog.requestFocus();
            this.boxDialog.setMinWidth(500);
            this.boxDialog.setMaxWidth(500);
            this.boxDialog.getStyleClass().addAll(Application.getThemeType());
            this.boxContent.getStyleClass().add(this.style);
            this.labelTitle.setText(this.title.toUpperCase());
            Input.getScrollEvent(this.scrollPane);
            this.labelContent.setText(this.content);

            this.buttonOk.getStyleClass().add(style);
            this.buttonOk.setText(this.buttonsText[0]);
            this.buttonOk.setOnAction(this.eventHandlerButtonOk());
            if (this.buttonsText.length > 1) {
                this.buttonCancel.setText(this.buttonsText[1]);
                this.buttonCancel.setOnAction(this.eventHandlerButtonCancel());
            } else {
                Input.createVisibleProperty(this.buttonCancel);
            }
            this.initUI();
            if (this.input.isVisible()) {
                this.input.setOnKeyPressed(this.eventHandlerEnterKey());
                this.buttonOk.setDisable(true);
            }

            Platform.runLater(() -> {
                if (this.input.isVisible()) {
                    this.input.requestFocus();
                }
            });
        }

        private EventHandler<ActionEvent> eventHandlerButtonOk() {
            return actionEvent -> {
                switch (dialogType) {
                    case MESSAGE: {
                        this.controller.answerStatus = true;
                        this.controller.closeModal();
                    }
                    break;
                    case PASSWORD: {
                        if (Hash.generateHash(this.input.getText()).equals(Application.getStaffUserModel().getModelStaffMember().getPassword())) {
                            this.controller.answerStatus = true;
                            this.controller.closeModal();
                        } else {
                            Notifications.danger("Error", "La contraseña no es válida.");
                            new Shake(this.input).play();
                        }
                    }
                    break;
                    case DEBT: {
                        this.controller.closeModal();
                    }
                    break;
                }
            };
        }

        private EventHandler<ActionEvent> eventHandlerButtonCancel() {
            return actionEvent -> {
                this.controller.answerStatus = false;
                this.controller.closeModal();
            };
        }

        private void initUI() {
            switch (dialogType) {
                case MESSAGE: {
                    Input.createVisibleProperty(this.boxDebt);
                    Input.createVisibleProperty(this.input);
                }
                break;
                case PASSWORD: {
                    Input.createVisibleProperty(this.boxDebt);
                    this.input.textProperty().addListener((observable, oldValue, newValue) -> {
                        this.buttonOk.setDisable(newValue.equals(""));
                    });
                }
                break;
                case DEBT: {
                    this.boxDialog.setMinWidth(700);
                    this.boxDialog.setMaxWidth(700);
                    this.scrollPane.setPrefHeight(500);
                    this.labelContent.setText("[fecha y hora](cantidad)[deuda restante] descripción");
                    this.labelContent.getStyleClass().add("bold");
                    for (Model_Debt modelDebt : debtList) {
                        Label label = new Label(
                                "[" + modelDebt.getDateTime() + "](" + modelDebt.getAmount() + ")[$" + modelDebt.getOwe() + "] " + modelDebt.getDescription()
                        );
                        label.setWrapText(true);
                        label.setStyle("-fx-text-fill: -fx-color-text");
                        this.boxContentBuilder.getChildren().add(label);
                    }
                    this.labelDebt.setText(String.valueOf(this.debtList.get(0).getTotalOwe()));
                    this.input.setPromptText("Ingresa la cantidad a abonar");
                    this.input.textProperty().addListener((observable, oldValue, newValue) -> {
                        boolean flag = Validator.moneyValidator(
                                new InputDetails(
                                        this.input,
                                        this.input.getText()
                                ),
                                false
                        );

                        if (flag) {
                            BigDecimal number = new BigDecimal(this.input.getText());
                            BigDecimal owe = this.debtList.get(0).getTotalOwe().subtract(number);
                            if (number.compareTo(BigDecimal.ZERO) > 0 && owe.compareTo(BigDecimal.ZERO) >= 0) {
                                this.labelDebt.setText(String.valueOf(owe));
                                this.buttonOk.setDisable(false);
                            } else {
                                this.labelDebt.setText(String.valueOf(this.debtList.get(0).getTotalOwe()));
                                this.buttonOk.setDisable(true);
                                new Shake(this.input).play();
                            }
                        } else {
                            this.labelDebt.setText(String.valueOf(this.debtList.get(0).getTotalOwe()));
                            this.buttonOk.setDisable(true);
                        }
                    });
                }
            }
        }

        // Event handlers
        private EventHandler<KeyEvent> eventHandlerEnterKey() {
            return keyEvent -> {
                if (keyEvent.getCode() == KeyCode.ENTER && !this.buttonOk.isDisable()) {
                    this.buttonOk.fire();
                }
            };
        }
    }

    // Event handlers
    private EventHandler<KeyEvent> eventHandlerEscapeKey() {
        return keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                this.closeModal();
            }
        };
    }
}
