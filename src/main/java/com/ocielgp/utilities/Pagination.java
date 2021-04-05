package com.ocielgp.utilities;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.ocielgp.app.AppController;
import com.ocielgp.database.MembersData;
import com.ocielgp.files.ConfigFiles;
import com.ocielgp.model.MembersModel;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.TableRow;
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
    private final Label labelPage;

    private int rows = 15;
    private final Sources source;

    public Pagination(JFXTextField fieldSearch, JFXButton buttonSearch, TableView tableView, JFXTextField fieldRegistersPerPage, Label labelPreviusPage, Label labelPageCounter, Label labelNextPage, Sources source) {
        this.fieldSearch = fieldSearch;
        this.tableView = tableView;
        this.fieldRowsPerPage = fieldRegistersPerPage;
        this.labelPage = labelPageCounter;
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
        this.tableView.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEvent -> this.itemSelected());
        this.rows = Integer.parseInt(Objects.requireNonNull(ConfigFiles.readProperty(ConfigFiles.File.APP, "paginationRows")));
        this.loadData(1); // Initial data

        // Listener on ComboBox GymModel
        EventHandler<ActionEvent> gymChange = actionEvent -> {
            if (!Boolean.parseBoolean(ConfigFiles.readProperty(ConfigFiles.File.APP, "memberAllGyms"))) {
                this.loadData(1);
            }
        };
        AppController.getCurrentGymNode().addEventHandler(ActionEvent.ACTION, gymChange);
        this.fieldSearch.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (oldScene != null) {
                AppController.getCurrentGymNode().removeEventHandler(ActionEvent.ACTION, gymChange);
            }
        });
    }

    private void updateRowsPerPage() {
        if (Validator.numberValidator(new InputDetails(this.fieldRowsPerPage, this.fieldRowsPerPage.getText()))) {
            int newRowsPerPage = Integer.parseInt(this.fieldRowsPerPage.getText());
            if (newRowsPerPage > 0) {
                this.rows = Integer.parseInt(this.fieldRowsPerPage.getText());
                ConfigFiles.saveProperty(ConfigFiles.File.APP, "paginationRows", this.fieldRowsPerPage.getText());
                this.loadData(1);
            } else {
                NotificationHandler.danger("Error", "Cantidad de registros no válida.", 2);
                Validator.shakeInput(this.fieldRowsPerPage);
            }
        }
    }

    public void itemSelected() {
        switch (source) {
            case MEMBERS: {
                if (this.tableView.getSelectionModel().getSelectedItem() != null) {
                    MembersModel membersModel = (MembersModel) this.tableView.getSelectionModel().getSelectedItem();
                    System.out.println(membersModel.getIdMember());
                }
            }
        }
    }

    public void previousPage() {
        this.loadData(Integer.parseInt(this.labelPage.getText()) - 1);
    }

    public void nextPage() {
        this.loadData(Integer.parseInt(this.labelPage.getText()) + 1);
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
        ObservableList<MembersModel> data = MembersData.getMembers(this.rows, page, this.fieldSearch.getText());
        if (data != null && data.size() > 0) {
            this.tableView.setItems(data);
            this.labelPage.setText(String.valueOf(page));
            this.tableView.setRowFactory(row -> new TableRow<MembersModel>() {
                @Override
                public void updateItem(MembersModel member, boolean empty) {
                    super.updateItem(member, empty);
                    if (member != null) {
                        /* DATES
                         *  - 0 DAYS = DANGER
                         * 1-3 DAYS = WARN
                         * + 3 DAYS = SUCESS
                         */
                        // TODO: CENTER DATES
                        //TODO: FIX WIDTH PER COLUMN
                        if (member.getDaysLeft() < 0) {
                            if (getStyleClass().size() == 5) {
                                getStyleClass().set(4, "danger-style"); // replace color style
                            } else {
                                getStyleClass().addAll("member-cell", "danger-style");
                            }
                        } else if (member.getDaysLeft() >= 0 && member.getDaysLeft() <= 3) {
                            if (getStyleClass().size() == 5) {
                                getStyleClass().set(4, "warn-style"); // replace color style
                            } else {
                                getStyleClass().addAll("member-cell", "warn-style");
                            }
                        } else if (member.getDaysLeft() > 3) {
                            if (getStyleClass().size() == 5) {
                                getStyleClass().set(4, "sucess-style"); // replace color style
                            } else {
                                getStyleClass().addAll("member-cell", "sucess-style");
                            }
                        }
                    } else {
                        if (getStyleClass().size() == 5) {
                            getStyleClass().remove(4); // remove member-cell
                            getStyleClass().remove(3); // remove color style
                        }
                    }
                }
            });
        } else if (page == 1) {
            this.tableView.setItems(null);
        }
    }
}
