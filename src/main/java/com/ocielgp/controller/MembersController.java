package com.ocielgp.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.ocielgp.app.AppController;
import com.ocielgp.database.MembersData;
import com.ocielgp.model.MembersModel;
import com.ocielgp.utilities.Input;
import com.ocielgp.utilities.Pagination;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
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
    private TableView<MembersModel> tableViewMembers;
    @FXML
    private TableColumn<MembersModel, Integer> tableColumnId;
    @FXML
    private TableColumn<MembersModel, String> tableColumnName;
    @FXML
    private TableColumn<MembersModel, String> tableColumnLastName;
    @FXML
    private JFXTextField fieldRowsPerPage;
    @FXML
    private JFXButton buttonFilter;
    @FXML
    private Label labelPreviousPage;
    @FXML
    private Label labelPage;
    @FXML
    private Label labelNextPage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.tableColumnId.setCellValueFactory(new PropertyValueFactory<>("idMember"));
        this.tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        this.tableColumnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        Pagination pagination = new Pagination(this.tableViewMembers, this.fieldRowsPerPage, this.buttonFilter, this.labelPreviousPage, this.labelPage, this.labelNextPage, Pagination.Sources.MEMBERS);
        AppController.setPagination(pagination);

//        ObservableList<MembersModel> members = MembersData.getMembers(15, 1);
//        if (members != null) {
//            this.tableViewMembers.getItems().addAll(members);
//        }

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
