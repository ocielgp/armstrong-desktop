package com.ocielgp.controller.summary;

import animatefx.animation.FadeIn;
import animatefx.animation.FadeInUp;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTimePicker;
import com.ocielgp.dao.JDBC_Summary;
import com.ocielgp.utilities.Cards;
import com.ocielgp.utilities.InputProperties;
import com.ocielgp.utilities.Loading;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
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

    private String concatStartDateTime() {
        return this.startDate.getValue() + " " + this.startTime.getValue();
    }

    private String concatEndDateTime() {
        return this.endDate.getValue() + " " + this.endTime.getValue();
    }

    private void cardsCheckIn() {
        JDBC_Summary.ReadCheckIn(concatStartDateTime(), concatEndDateTime()).thenAccept(membersObservableList -> membersObservableList.forEach(modelAdmin ->
                addCardToSummary(
                        this.generalCards,
                        Cards.createCard("gmi-fingerprint", modelAdmin.getMetadata(), "Entradas", "#ffffff", Color.hsb(152, 0.81, 0.53), Color.hsb(152, 0.81, 0.43))
                )));
    }

    private void cardsTotalMembers() {
        JDBC_Summary.ReadTotalMembers(concatStartDateTime(), concatEndDateTime()).thenAccept(membersObservableList -> membersObservableList.forEach(modelAdmin ->
                addCardToSummary(
                        this.generalCards,
                        Cards.createCard("gmi-group-add", modelAdmin.getMetadata(), modelAdmin.getName() + " socios", "#ffffff", Color.hsb(152, 0.81, 0.53), Color.hsb(152, 0.81, 0.43))
                )));
    }

    private void cardsPaymentsMemberships() {
        JDBC_Summary.ReadTotalPaymentsMemberships(concatStartDateTime(), concatEndDateTime()).thenAccept(membersObservableList -> membersObservableList.forEach(modelAdmin ->
                addCardToSummary(
                        this.paymentsCards,
                        Cards.createCard("gmi-monetization-on", modelAdmin.getMetadata(), modelAdmin.getName() + " mensualidades", "#ffffff", Color.hsb(45, 0.97, 0.90), Color.hsb(45, 0.97, 0.90))
                )));
    }

    private void cardsPaymentsVisits() {
        JDBC_Summary.ReadPaymentsVisits(concatStartDateTime(), concatEndDateTime()).thenAccept(membersObservableList -> membersObservableList.forEach(modelAdmin ->
                addCardToSummary(
                        this.paymentsCards,
                        Cards.createCard("gmi-local-play", modelAdmin.getMetadata(), modelAdmin.getName() + " visitas", "#ffffff", Color.hsb(45, 0.97, 0.90), Color.hsb(45, 0.97, 0.90))
                )));
    }

    private void cardsMemberships() {
        JDBC_Summary.ReadTotalMembersByMembership(concatStartDateTime(), concatEndDateTime()).thenAccept(membersObservableList -> membersObservableList.forEach(modelAdmin ->
                addCardToSummary(
                        this.membershipsCards,
                        Cards.createCard("gmi-fitness-center", modelAdmin.getMetadata(), modelAdmin.getName(), "#ffffff", Color.hsb(190, 0.95, 0.94), Color.hsb(190, 0.95, 0.94))
                )));
    }

    private void addCardToSummary(FlowPane flowPane, HBox card) {
        Platform.runLater(() -> {
            flowPane.getChildren().add(card);
            new FadeInUp(card).play();
            Loading.closeNow();
        });
    }

    private void eventCreateCards() {
        Loading.show();
        this.generalCards.getChildren().clear();
        this.paymentsCards.getChildren().clear();
        this.membershipsCards.getChildren().clear();

        cardsCheckIn();
        cardsTotalMembers();

        cardsPaymentsMemberships();
        cardsPaymentsVisits();

        cardsMemberships();
    }

    // Controls
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        YearMonth month = YearMonth.now();
//        this.startDate.setValue(month.atDay(1));
        this.startDate.setValue(LocalDate.now());
        this.startTime.setValue(LocalTime.MIN);
//        this.endDate.setValue(YearMonth.now().atEndOfMonth());
        this.endDate.setValue(LocalDate.now());
        this.endTime.setValue(LocalTime.now());
        InputProperties.autoShow(this.startTime, this.endDate, this.endTime);
        this.startTime.set24HourView(true);
        this.endTime.set24HourView(true);
        eventCreateCards();


        this.buttonSearch.setOnAction(actionEvent -> eventCreateCards());

        Platform.runLater(() -> {
            new FadeIn(this.rootPane).play();
//            FadeIn fadeIn = new FadeIn(this.rootPane);
//            fadeIn.setOnFinished(actionEvent -> {
//            });
//            fadeIn.play();
        });
    }
}
