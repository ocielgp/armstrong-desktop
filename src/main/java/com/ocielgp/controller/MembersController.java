package com.ocielgp.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class MembersController implements Initializable {
    // Containers
    @FXML
    private GridPane membersPane;
    @FXML
    private VBox memberPane;

    // Controls
    @FXML
    private TableView membersTable;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println(memberPane.getChildren());
        FXMLLoader view = new FXMLLoader(
                Objects.requireNonNull(DashboardController.class.getClassLoader().getResource("member.fxml"))
        );
        try {
            this.memberPane.getChildren().setAll((ScrollPane) view.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
