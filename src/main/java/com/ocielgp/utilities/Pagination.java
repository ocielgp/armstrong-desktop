package com.ocielgp.utilities;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.ocielgp.app.Application;
import com.ocielgp.app.UserPreferences;
import com.ocielgp.dao.JDBC_Member;
import com.ocielgp.models.Model_Member;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class Pagination {
    private final Sources source;

    public enum Sources {
        MEMBERS
    }

    private final JFXTextField fieldSearch;
    private final Label labelTotalRows;
    private final TableView tableView;
    private final JFXTextField fieldRowsPerPage;
    private final Label labelCurrentPage;
    private final Label labelTotalPages;
    private final AtomicInteger page = new AtomicInteger();
    private Integer rows;

    public Pagination(JFXTextField fieldSearch, JFXButton buttonSearch, Label labelTotalRows, TableView tableView, JFXTextField fieldRegistersPerPage, Label labelPreviusPage, Label labelCurrentPage, Label labelTotalPages, Label labelNextPage, Sources source) {
        this.fieldSearch = fieldSearch;
        this.labelTotalRows = labelTotalRows;
        this.tableView = tableView;
        this.fieldRowsPerPage = fieldRegistersPerPage;
        this.labelCurrentPage = labelCurrentPage;
        this.labelTotalPages = labelTotalPages;
        this.source = source;

        this.fieldSearch.setOnKeyPressed((keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                this.restartTable();
            }
        }));
        buttonSearch.setOnAction(actionEvent -> this.restartTable());

        this.fieldRowsPerPage.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                this.updateRowsPerPage();
            }
        });
        labelPreviusPage.setOnMouseClicked(this.previousPage());
        labelNextPage.setOnMouseClicked(this.nextPage());
        this.rows = UserPreferences.getPreferenceInt("PAGINATION_MAX_ROWS");

        // Listener on ComboBox GymModel
        EventHandler<ActionEvent> gymChange = actionEvent -> {
            if (!UserPreferences.getPreferenceBool("FILTER_MEMBER_ALL_GYMS")) {
                this.restartTable();
            }
        };
        Application.getCurrentGymNode().removeEventHandler(ActionEvent.ACTION, gymChange);
        Application.getCurrentGymNode().addEventHandler(ActionEvent.ACTION, gymChange);

        this.fieldSearch.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (oldScene != null) {
                Application.getCurrentGymNode().removeEventHandler(ActionEvent.ACTION, gymChange);
            }
        });
    }

    private void updateRowsPerPage() {
        if (Validator.numberValidator(new InputDetails(this.fieldRowsPerPage, this.fieldRowsPerPage.getText()), false, true)) {
            int newRowsPerPage = Integer.parseInt(this.fieldRowsPerPage.getText());
            if (newRowsPerPage > 0) {
                this.rows = Integer.parseInt(this.fieldRowsPerPage.getText());
                UserPreferences.setPreference("PAGINATION_MAX_ROWS", Integer.parseInt(this.fieldRowsPerPage.getText()));
                this.restartTable();
            } else {
                Notifications.danger("Error", "Cantidad de registros no válida.", 2);
                Validator.shakeInput(this.fieldRowsPerPage);
            }
        }
    }


    public EventHandler<MouseEvent> previousPage() {
        return mouseEvent -> {
            CompletableFuture.runAsync(() -> {
                if ((this.page.get() - 1) > 0) {
                    CompletableFuture.runAsync(() -> {
                        this.page.set(this.page.get() - 1);
                        this.fillTable();
                    });
                }
            });
        };
    }

    public EventHandler<MouseEvent> nextPage() {
        return mouseEvent -> {
            CompletableFuture.runAsync(() -> {
                if ((page.get() + 1) <= Integer.parseInt(this.labelTotalPages.getText())) {
                    CompletableFuture.runAsync(() -> {
                        this.page.set(this.page.get() + 1);
                        this.fillTable();
                    });
                }
            });
        };
    }


    public void restartTable() {
        this.page.set(1);
        this.fillTable();
    }

    public void fillTable() {
        Platform.runLater(() -> this.fieldRowsPerPage.setText(this.rows.toString()));

        switch (source) {
            case MEMBERS: {
                CompletableFuture.runAsync(this::loadMembers);
            }
        }
    }

    private void loadMembers() {
        JDBC_Member.ReadMembers(this.rows, this.page, this.fieldSearch.getText()).thenAccept(queryRows -> Platform.runLater(() -> {
            if (queryRows.getData().size() > 0) {
                this.labelTotalPages.setText(queryRows.getPages().toString());
                this.labelTotalRows.setText(queryRows.getRows().toString());
                this.tableView.setRowFactory(row -> new TableRow<Model_Member>() {
                    @Override
                    public void updateItem(Model_Member modelMember, boolean empty) {
                        super.updateItem(modelMember, empty);
                        if (modelMember != null) {
                            String style = Input.styleToColor(modelMember.getStyle());
                            if (getStyleClass().size() == 5) {
                                getStyleClass().set(4, style); // replace color style
                            } else {
                                getStyleClass().addAll("member-cell", style);
                            }
                        } else {
                            if (getStyleClass().size() == 5) {
                                getStyleClass().remove(4); // remove member-cell
                                getStyleClass().remove(3); // remove color style
                            }
                        }
                    }
                });
                this.tableView.setItems(queryRows.getData());
                this.labelCurrentPage.setText(this.page.toString());
            } else {
                this.restartCounters();
            }

            Application.controllerDashboard.enableRoutes();
            Loading.close();
            this.tableView.setDisable(false);
        }));
    }

    public void restartCounters() {
        Platform.runLater(() -> {
            this.tableView.setItems(null);
            this.labelTotalRows.setText("0");
            this.labelCurrentPage.setText("0");
            this.labelTotalPages.setText("0");
        });
    }
}
