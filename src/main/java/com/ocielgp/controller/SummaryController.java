package com.ocielgp.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class SummaryController implements Initializable {
    // Containers
    @FXML
    private VBox rootPane;
    @FXML
    private FlowPane cards;
    @FXML
    private FlowPane charts;

    // Controls
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.cards.getChildren().addAll(
//                Cards.createCard("gmi-person-add", "500", "Socios nuevos"),
//                Cards.createCard("gmi-person-add", "20 %", "Hola bro", Color.rgb(255, 0, 245), Color.rgb(255, 192, 0)),
//                Cards.createCard("gmi-person-add", "Hola", "Lola"),
//                Cards.createCard("gmi-person-add", "Hola", "Lola")
        );
//        charts.getChildren().add(pieChart);
    }
}
