package com.ocielgp.controller;

import com.ocielgp.utilities.Input;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class MembersController implements Initializable {
    // Containers
    @FXML
    private GridPane membersPane;
    @FXML
    private ScrollPane memberPane;

    // Controls
    @FXML
    private TableView membersTable;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        FXMLLoader view = new FXMLLoader(
                Objects.requireNonNull(DashboardController.class.getClassLoader().getResource("member.fxml"))
        );
        try {
            this.memberPane.setContent(view.load());
            Input.getScrollEvent(this.memberPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
