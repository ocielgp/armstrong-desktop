package com.ocielgp.utilities;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.ocielgp.app.GlobalController;
import com.ocielgp.database.QueryRows;
import com.ocielgp.database.members.DATA_MEMBERS;
import com.ocielgp.files.ConfigFiles;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;

import java.util.Objects;

public class Pagination {
    public enum Sources {
        MEMBERS
    }

    private final JFXTextField fieldSearch;
    private final TableView tableView;
    private final JFXTextField fieldRowsPerPage;
    private final Label labelCurrentPage;
    private final Label labelTotalRows;
    private final Label labelTotalPages;

    private int rows;
    private final Sources source;

    public Pagination(JFXTextField fieldSearch, JFXButton buttonSearch, Label labelTotalRows, TableView tableView, JFXTextField fieldRegistersPerPage, Label labelPreviusPage, Label labelCurrentPage, Label labelTotalPages, Label labelNextPage, Sources source) {
        this.fieldSearch = fieldSearch;
        this.tableView = tableView;
        this.labelTotalRows = labelTotalRows;
        this.fieldRowsPerPage = fieldRegistersPerPage;
        this.labelCurrentPage = labelCurrentPage;
        this.labelTotalPages = labelTotalPages;
        this.source = source;

        // Fieldsearch logic
        this.fieldSearch.setOnKeyPressed((keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                this.loadData(1);
            }
        }));
        buttonSearch.setOnAction(actionEvent -> this.loadData(1));

        // Pagination logic
        // TODO: ADD NUMBER OF ROWS, CURRENT SHOWING ROWS OF TOTAL
        this.fieldRowsPerPage.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                this.updateRowsPerPage();
            }
        });
        this.fieldRowsPerPage.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                this.updateRowsPerPage();
            }
        });
        labelPreviusPage.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEvent -> this.previousPage());
        labelNextPage.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEvent -> this.nextPage());
//        this.tableView.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEvent -> this.itemSelected());
        this.rows = Integer.parseInt(Objects.requireNonNull(ConfigFiles.readProperty(ConfigFiles.File.APP, "paginationRows")));
        this.loadData(1); // Initial data

        // Listener on ComboBox GymModel
        EventHandler<ActionEvent> gymChange = actionEvent -> {
            if (!Boolean.parseBoolean(ConfigFiles.readProperty(ConfigFiles.File.APP, "memberAllGyms"))) {
                this.loadData(1);
            }
        };
        // DELETE IF BEFORE HAS ADDED
        GlobalController.getCurrentGymNode().removeEventHandler(ActionEvent.ACTION, gymChange);
        GlobalController.getCurrentGymNode().addEventHandler(ActionEvent.ACTION, gymChange);

        this.fieldSearch.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (oldScene != null) {
                GlobalController.getCurrentGymNode().removeEventHandler(ActionEvent.ACTION, gymChange);
            }
        });
    }

    private void updateRowsPerPage() {
        if (Validator.numberValidator(new InputDetails(this.fieldRowsPerPage, this.fieldRowsPerPage.getText()), false, true)) {
            int newRowsPerPage = Integer.parseInt(this.fieldRowsPerPage.getText());
            if (newRowsPerPage > 0) {
                this.rows = Integer.parseInt(this.fieldRowsPerPage.getText());
                ConfigFiles.saveProperty(ConfigFiles.File.APP, "paginationRows", this.fieldRowsPerPage.getText());
                this.loadData(1);
            } else {
                Notifications.danger("Error", "Cantidad de registros no válida.", 2);
                Validator.shakeInput(this.fieldRowsPerPage);
            }
        }
    }

//    public void itemSelected() {
//        switch (source) {
//            case MEMBERS: {
//                if (this.tableView.getSelectionModel().getSelectedItem() != null) {
//                    MembersModel membersModel = (MembersModel) this.tableView.getSelectionModel().getSelectedItem();
//                    System.out.println(membersModel.getIdMember());
//                }
//            }
//        }
//    }

    public void previousPage() {
        this.loadData(Integer.parseInt(this.labelCurrentPage.getText()) - 1);
    }

    public void nextPage() {
        this.loadData(Integer.parseInt(this.labelCurrentPage.getText()) + 1);
    }

    public void loadData(int page) {
        if (page > 0) {
            if (!this.fieldRowsPerPage.getText().equals(String.valueOf(this.rows))) {
                this.fieldRowsPerPage.setText(String.valueOf(this.rows));
            }
            switch (source) {
                case MEMBERS: {
                    this.loadMembers(page);
                }
            }
        }
    }

    private void loadMembers(int page) {
        Task<QueryRows> queryRowsTask = DATA_MEMBERS.ReadMembers(
                this.rows,
                page,
                this.fieldSearch.getText(),
                this.tableView

        );
        queryRowsTask.setOnSucceeded(workerStateEvent -> {
            QueryRows queryRows = queryRowsTask.getValue();
            Platform.runLater(() -> {
                if (queryRows != null && queryRows.getData().size() > 0) {
                    this.labelTotalPages.setText(String.valueOf(queryRows.getPages()));
                    this.labelTotalRows.setText(String.valueOf(queryRows.getRows()));
//                    this.tableView.setItems(queryRows.getData());
                    this.labelCurrentPage.setText(String.valueOf(page));
                } else if (page == 1) {
                    this.restartCounters();
                }
            });
        });
        new Thread(queryRowsTask).start();
    }

    public void restartCounters() {
        this.tableView.setItems(null);
        this.labelTotalRows.setText("0");
        this.labelCurrentPage.setText("0");
        this.labelTotalPages.setText("0");
    }
}
