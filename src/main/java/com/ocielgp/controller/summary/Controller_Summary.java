package com.ocielgp.controller.summary;

import animatefx.animation.FadeIn;
import animatefx.animation.FadeInUp;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTimePicker;
import com.ocielgp.dao.JDBC_Summary;
import com.ocielgp.utilities.Cards;
import com.ocielgp.utilities.InputProperties;
import com.ocielgp.utilities.Loading;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import java.util.ResourceBundle;

public class Controller_Summary implements Initializable {
    // Containers
    @FXML
    private VBox rootPane;
    @FXML
    private JFXDatePicker startDate;
    @FXML
    private JFXTimePicker startTime;
    @FXML
    private JFXDatePicker endDate;
    @FXML
    private JFXTimePicker endTime;
    @FXML
    private JFXButton buttonSearch;
    @FXML
    private FlowPane generalCards;
    @FXML
    private FlowPane paymentsCards;
    @FXML
    private FlowPane membershipsCards;
    @FXML
    private JFXComboBox<String> comboBoxShift;

    private BigDecimal totalMoney = new BigDecimal(0);
    private final Pair<String, LocalTime[]>[] comboBoxShiftOptions = new Pair[]{
            // string view | [startTime, endTime]
            new Pair<>("Mañana", new LocalTime[]{LocalTime.MIN, LocalTime.of(14, 0)}),
            new Pair<>("Tarde", new LocalTime[]{LocalTime.of(14, 0), LocalTime.MAX}),
            new Pair<>("Todo el día", new LocalTime[]{LocalTime.MIN, LocalTime.MAX})
    };

    private void generalCards() {
        JDBC_Summary.ReadCheckIn(InputProperties.concatDateTime(this.startDate, this.startTime), InputProperties.concatDateTime(this.endDate, this.endTime))
                .thenAccept(membersObservableList -> membersObservableList
                        .forEach(modelAdmin ->
                                addCardToSummary(
                                        this.generalCards,
                                        Cards.createCard("gmi-fingerprint", (modelAdmin.getMetadata() == null) ? "0" : modelAdmin.getMetadata(), "Entradas", "#ffffff", Color.hsb(152, 0.81, 0.53), Color.hsb(152, 0.81, 0.43))
                                )))
                .thenRun(() -> JDBC_Summary.ReadNewMembers(InputProperties.concatDateTime(this.startDate, this.startTime), InputProperties.concatDateTime(this.endDate, this.endTime))
                        .thenAccept(membersObservableList -> membersObservableList
                                .forEach(modelAdmin ->
                                        addCardToSummary(
                                                this.generalCards,
                                                Cards.createCard("gmi-group-add", modelAdmin.getMetadata(), modelAdmin.getName() + " inscripciones", "#ffffff", Color.hsb(152, 0.81, 0.53), Color.hsb(152, 0.81, 0.43))
                                        )))
                        .thenRun(() -> JDBC_Summary.ReadMembers(InputProperties.concatDateTime(this.startDate, this.startTime), InputProperties.concatDateTime(this.endDate, this.endTime))
                                .thenAccept(membersObservableList -> membersObservableList
                                        .forEach(modelAdmin ->
                                                addCardToSummary(
                                                        this.generalCards,
                                                        Cards.createCard("gmi-group-add", modelAdmin.getMetadata(), modelAdmin.getName() + " mensualidades", "#ffffff", Color.hsb(152, 0.81, 0.53), Color.hsb(152, 0.81, 0.43))
                                                )))
                                .thenRun(() -> JDBC_Summary.ReadProducts(InputProperties.concatDateTime(this.startDate, this.startTime), InputProperties.concatDateTime(this.endDate, this.endTime)).thenAccept(membersObservableList -> membersObservableList
                                        .forEach(modelAdmin ->
                                                addCardToSummary(
                                                        this.generalCards,
                                                        Cards.createCard("gmi-add-shopping-cart", modelAdmin.getMetadata(), modelAdmin.getName() + " productos", "#ffffff", Color.hsb(152, 0.81, 0.53), Color.hsb(152, 0.81, 0.43))
                                                ))
                                ))));
    }

    private void paymentsCards() {
        this.totalMoney = new BigDecimal("0");
        JDBC_Summary.ReadTotalPaymentsMembershipsFromNewMembers(InputProperties.concatDateTime(this.startDate, this.startTime), InputProperties.concatDateTime(this.endDate, this.endTime))
                .thenAccept(membersObservableList -> membersObservableList
                        .forEach(modelAdmin -> {
                            this.totalMoney = this.totalMoney.add(new BigDecimal(modelAdmin.getMetadata()));
                            addCardToSummary(
                                    this.paymentsCards,
                                    Cards.createCard("gmi-monetization-on", "$ " + modelAdmin.getMetadata(), modelAdmin.getName() + " inscripciones", "#ffffff", Color.hsb(45, 0.97, 0.90), Color.hsb(45, 0.97, 0.90))
                            );
                        }))
                .thenRun(() -> JDBC_Summary.ReadTotalPaymentsMembershipsFromMembers(InputProperties.concatDateTime(this.startDate, this.startTime), InputProperties.concatDateTime(this.endDate, this.endTime))
                        .thenAccept(membersObservableList -> membersObservableList
                                .forEach(modelAdmin -> {
                                    this.totalMoney = this.totalMoney.add(new BigDecimal(modelAdmin.getMetadata()));
                                    addCardToSummary(
                                            this.paymentsCards,
                                            Cards.createCard("gmi-monetization-on", "$ " + modelAdmin.getMetadata(), modelAdmin.getName() + " mensualidades", "#ffffff", Color.hsb(45, 0.97, 0.90), Color.hsb(45, 0.97, 0.90))
                                    );
                                }))
                        .thenRun(() -> JDBC_Summary.ReadPaymentsVisits(InputProperties.concatDateTime(this.startDate, this.startTime), InputProperties.concatDateTime(this.endDate, this.endTime))
                                .thenAccept(membersObservableList -> membersObservableList
                                        .forEach(modelAdmin -> {
                                            this.totalMoney = this.totalMoney.add(new BigDecimal(modelAdmin.getMetadata()));
                                            addCardToSummary(
                                                    this.paymentsCards,
                                                    Cards.createCard("gmi-local-play", "$ " + modelAdmin.getMetadata(), modelAdmin.getName() + " visitas", "#ffffff", Color.hsb(45, 0.97, 0.90), Color.hsb(45, 0.97, 0.90))
                                            );
                                        }))
                                .thenRun(() -> JDBC_Summary.ReadPaymentsProducts(InputProperties.concatDateTime(this.startDate, this.startTime), InputProperties.concatDateTime(this.endDate, this.endTime))
                                        .thenAccept(membersObservableList -> membersObservableList
                                                .forEach(modelAdmin -> {
                                                    this.totalMoney = this.totalMoney.add(new BigDecimal(modelAdmin.getMetadata()));
                                                    addCardToSummary(
                                                            this.paymentsCards,
                                                            Cards.createCard("gmi-shopping-cart", "$ " + modelAdmin.getMetadata(), modelAdmin.getName() + " productos", "#ffffff", Color.hsb(45, 0.97, 0.90), Color.hsb(45, 0.97, 0.90))
                                                    );
                                                }))
                                        .thenRun(() -> addCardToSummary(
                                                this.paymentsCards,
                                                Cards.createCard("gmi-payment", "$ " + this.totalMoney, "Total generado", "#ffffff", Color.hsb(45, 0.97, 1), Color.hsb(45, 0.97, 0.90))
                                        ))
                                )));
    }

    private void membershipsCards() {
        JDBC_Summary.ReadTotalMembersByMembership(InputProperties.concatDateTime(this.startDate, this.startTime), InputProperties.concatDateTime(this.endDate, this.endTime)).thenAccept(membersObservableList -> membersObservableList.forEach(modelAdmin ->
                addCardToSummary(
                        this.membershipsCards,
                        Cards.createCard("gmi-fitness-center", modelAdmin.getMetadata(), modelAdmin.getName(), "#ffffff", Color.hsb(190, 0.95, 0.94), Color.hsb(190, 0.95, 0.94))
                ))
        );
    }

    private void addCardToSummary(FlowPane flowPane, HBox card) {
        Platform.runLater(() -> {
            flowPane.getChildren().add(card);
            new FadeInUp(card).play();
            Loading.closeNow();
        });
    }

    private void createCards() {
        Loading.show();
        this.generalCards.getChildren().clear();
        this.paymentsCards.getChildren().clear();
        this.membershipsCards.getChildren().clear();

        generalCards();
        paymentsCards();
        membershipsCards();
    }

    // Controls
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> comboBoxShiftOptionsString = FXCollections.observableArrayList();
        for (Pair<String, LocalTime[]> comboBoxShiftOption : this.comboBoxShiftOptions) {
            comboBoxShiftOptionsString.add(comboBoxShiftOption.getKey());
        }
        this.comboBoxShift.setItems(comboBoxShiftOptionsString);

        this.comboBoxShift.valueProperty().addListener((observable, oldValue, newValue) -> {
            for (Pair<String, LocalTime[]> comboBoxShiftOption : this.comboBoxShiftOptions) {
                if (Objects.equals(newValue, comboBoxShiftOption.getKey())) {
                    this.startTime.setValue(comboBoxShiftOption.getValue()[0]);
                    this.endTime.setValue(comboBoxShiftOption.getValue()[1]);
                    break;
                }
            }
        });

        LocalDateTime now = LocalDateTime.now();
        if (now.getHour() < 14) { // morning shift
            this.comboBoxShift.setValue("Mañana");
        } else { // afternoon shift
            this.comboBoxShift.setValue("Tarde");
        }
        this.startDate.setValue(LocalDate.now());
        this.endDate.setValue(LocalDate.now());
        this.startTime.set24HourView(true);
        this.endTime.set24HourView(true);
        InputProperties.autoShow(this.startTime, this.endDate, this.endTime);

        createCards();

        this.buttonSearch.setOnAction(actionEvent -> createCards());

        Platform.runLater(() -> new FadeIn(this.rootPane).play());
    }
}
