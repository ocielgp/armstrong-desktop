package com.ocielgp.controller.check_in;

import animatefx.animation.FadeIn;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTimePicker;
import com.ocielgp.models.Model_Check_In;
import com.ocielgp.utilities.InputProperties;
import com.ocielgp.utilities.Pagination;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class Controller_Check_In implements Initializable {
    @FXML
    public VBox boxRoot;

    // table
    @FXML
    private JFXTextField fieldSearch;
    @FXML
    private JFXButton buttonSearch;
    @FXML
    private JFXDatePicker startDate;
    @FXML
    private JFXTimePicker startTime;
    @FXML
    private JFXDatePicker endDate;
    @FXML
    private JFXTimePicker endTime;
    @FXML
    private Label labelTotalRows;
    @FXML
    private TableView<Model_Check_In> tableViewMembers;
    @FXML
    private TableColumn<Model_Check_In, String> tableColumnDateTime;
    @FXML
    private TableColumn<Model_Check_In, String> tableColumnMemberName;
    @FXML
    private TableColumn<Model_Check_In, String> tableColumnAdminName;
    @FXML
    private TableColumn<Model_Check_In, String> tableColumnGymName;
    @FXML
    private JFXTextField fieldRowsPerPage;
    @FXML
    private FontIcon iconPreviousPage;
    @FXML
    private Label labelCurrentPage;
    @FXML
    private Label labelTotalPages;
    @FXML
    private FontIcon iconNextPage;

    // attributes
    private Pagination pagination;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configureForm();
        this.pagination = new Pagination(this.startDate, this.startTime, this.endDate, this.endTime, Pagination.Tables.CHECK_IN, this.fieldSearch, this.buttonSearch, this.labelTotalRows, this.tableViewMembers, this.fieldRowsPerPage, this.iconPreviousPage, this.labelCurrentPage, this.labelTotalPages, this.iconNextPage);
        bindTable();

        Platform.runLater(() -> {
            FadeIn fadeIn = new FadeIn(this.boxRoot);
            fadeIn.setOnFinished(actionEvent -> this.fieldSearch.requestFocus());
            fadeIn.play();
            this.pagination.refillTable(1);
        });
    }

    private void configureForm() {
        this.startDate.setValue(LocalDate.now());
        this.startTime.setValue(LocalTime.MIN);
        this.endDate.setValue(LocalDate.now());
        this.endTime.setValue(LocalTime.MAX);
        InputProperties.autoShow(this.startDate, this.startTime, this.endDate, this.endTime);
        this.startTime.set24HourView(true);
        this.endTime.set24HourView(true);
    }

    private void bindTable() {
        this.tableColumnDateTime.setStyle("-fx-alignment: CENTER");
        this.tableColumnDateTime.setSortable(false);
        this.tableColumnAdminName.setSortable(false);
        this.tableColumnMemberName.setSortable(false);
        this.tableColumnGymName.setStyle("-fx-alignment: CENTER");
        this.tableColumnGymName.setSortable(false);
        this.tableColumnDateTime.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        this.tableColumnAdminName.setCellValueFactory(new PropertyValueFactory<>("adminName"));
        this.tableColumnMemberName.setCellValueFactory(new PropertyValueFactory<>("memberName"));
        this.tableColumnGymName.setCellValueFactory(new PropertyValueFactory<>("gymName"));
    }
}
