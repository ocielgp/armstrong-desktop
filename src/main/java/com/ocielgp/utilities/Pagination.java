package com.ocielgp.utilities;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTimePicker;
import com.ocielgp.app.UserPreferences;
import com.ocielgp.dao.JDBC_Admin;
import com.ocielgp.dao.JDBC_Check_In;
import com.ocielgp.dao.JDBC_Member;
import com.ocielgp.models.Model_Admin;
import com.ocielgp.models.Model_Check_In;
import com.ocielgp.models.Model_Member;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class Pagination {

    public enum Tables {
        MEMBERS,
        ADMINS,
        CHECK_IN,
    }

    JFXDatePicker startDate;
    JFXTimePicker startTime;
    JFXDatePicker endDate;
    JFXTimePicker endTime;

    private final JFXTextField fieldSearch;
    private final TableView tableView;
    private final Label labelTotalRows;
    private final JFXTextField fieldRowsPerPage;
    private final Label labelCurrentPage;
    private final Label labelTotalPages;

    // attributes
    private final AtomicInteger page = new AtomicInteger();
    private Integer rows;
    private final Tables tables;

    public Pagination(Tables tables, JFXTextField fieldSearch, JFXButton buttonSearch, Label labelTotalRows, TableView tableView, JFXTextField fieldRowsPerPage, FontIcon iconPreviousPage, Label labelCurrentPage, Label labelTotalPages, FontIcon iconNextPage) {
        this.tables = tables;
        this.fieldSearch = fieldSearch;
        this.labelTotalRows = labelTotalRows;
        this.tableView = tableView;
        this.fieldRowsPerPage = fieldRowsPerPage;
        this.labelCurrentPage = labelCurrentPage;
        this.labelTotalPages = labelTotalPages;

        if (this.fieldSearch != null) InputProperties.createEventEnter(buttonSearch, this.fieldSearch);
        buttonSearch.setOnAction(actionEvent -> this.refillTable(1));

        this.fieldRowsPerPage.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                updateRowsPerPage();
            }
        });
        iconPreviousPage.setOnMouseClicked(eventPreviousPage());
        iconNextPage.setOnMouseClicked(eventNextPage());
        this.rows = UserPreferences.GetPreferenceInt("PAGINATION_MAX_ROWS");
    }

    public Pagination(JFXDatePicker startDate, JFXTimePicker startTime, JFXDatePicker endDate, JFXTimePicker endTime, Tables tables, JFXTextField fieldSearch, JFXButton buttonSearch, Label labelTotalRows, TableView tableView, JFXTextField fieldRowsPerPage, FontIcon iconPreviousPage, Label labelCurrentPage, Label labelTotalPages, FontIcon iconNextPage) {
        this(tables, fieldSearch, buttonSearch, labelTotalRows, tableView, fieldRowsPerPage, iconPreviousPage, labelCurrentPage, labelTotalPages, iconNextPage);
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
    }

    private void updateRowsPerPage() {
        if (Validator.numberValidator(this.fieldRowsPerPage, true)) {
            int newRowsPerPage = Integer.parseInt(this.fieldRowsPerPage.getText());
            if (newRowsPerPage > 0) {
                this.rows = Integer.parseInt(this.fieldRowsPerPage.getText());
                UserPreferences.SetPreference("PAGINATION_MAX_ROWS", Integer.parseInt(this.fieldRowsPerPage.getText()));
                refillTable(1);
            } else {
                Notifications.Danger("Error", "Cantidad de registros no válida", 2);
                Validator.shakeInput(this.fieldRowsPerPage);
            }
        }
    }

    public EventHandler<MouseEvent> eventPreviousPage() {
        return mouseEvent -> CompletableFuture.runAsync(() -> {
            if ((this.page.get() - 1) > 0) {
                CompletableFuture.runAsync(() -> refillTable(this.page.get() - 1));
            }
        });
    }

    public EventHandler<MouseEvent> eventNextPage() {
        return mouseEvent -> CompletableFuture.runAsync(() -> {
            if ((page.get() + 1) <= Integer.parseInt(this.labelTotalPages.getText())) {
                CompletableFuture.runAsync(() -> refillTable(this.page.get() + 1));
            }
        });
    }

    public void refillTable(int page) {
        this.page.set(page);
        Platform.runLater(() -> this.fieldRowsPerPage.setText(this.rows.toString()));
        if (tables == Tables.MEMBERS) {
            refillMembers();
        } else if (tables == Tables.ADMINS) {
            refillAdmins();
        } else if (tables == Tables.CHECK_IN) {
            refillCheckIn();
        }
    }

    private void refillMembers() {
        JDBC_Member.ReadMembers(this.rows, this.page, this.fieldSearch.getText()).thenAccept(queryRows -> Platform.runLater(() -> {
            if (queryRows.getData().size() > 0) {
                this.labelTotalPages.setText(queryRows.getPages().toString());
                this.labelTotalRows.setText(queryRows.getRows().toString());
                this.tableView.setRowFactory(row -> new TableRow<Model_Member>() {
                    @Override
                    public void updateItem(Model_Member modelMember, boolean empty) {
                        super.updateItem(modelMember, empty);
                        if (modelMember != null) {
                            String style = modelMember.getStyle();
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
                initialStateCounters();
            }
            this.tableView.setDisable(false);
        }));
    }

    private void refillAdmins() {
        JDBC_Admin.ReadAdmins(this.rows, this.page, this.fieldSearch.getText()).thenAccept(queryRows -> Platform.runLater(() -> {
            if (queryRows.getData().size() > 0) {
                this.labelTotalPages.setText(queryRows.getPages().toString());
                this.labelTotalRows.setText(queryRows.getRows().toString());
                this.tableView.setRowFactory(row -> new TableRow<Model_Admin>() {
                    @Override
                    public void updateItem(Model_Admin modelAdmin, boolean empty) {
                        super.updateItem(modelAdmin, empty);
                        if (modelAdmin != null) {
                            String style = modelAdmin.getStyle();
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
                initialStateCounters();
            }
            this.tableView.setDisable(false);
        }));
    }

    private void refillCheckIn() {
        JDBC_Check_In.ReadAllCheckIn(this.rows, this.page, this.fieldSearch.getText(), InputProperties.concatDateTime(this.startDate, this.startTime), InputProperties.concatDateTime(this.endDate, this.endTime)).thenAccept(queryRows -> Platform.runLater(() -> {
            if (queryRows.getData().size() > 0) {
                this.labelTotalPages.setText(queryRows.getPages().toString());
                this.labelTotalRows.setText(queryRows.getRows().toString());
                this.tableView.setRowFactory(row -> new TableRow<Model_Check_In>() {
                    @Override
                    public void updateItem(Model_Check_In modelCheckIn, boolean empty) {
                        super.updateItem(modelCheckIn, empty);
                        if (modelCheckIn != null) {
                            String style = (modelCheckIn.isOpenedBySystem()) ? Styles.SUCCESS : Styles.WARN;
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
                initialStateCounters();
            }
            Loading.closeNow();
            this.tableView.setDisable(false);
        }));
    }

    public void clearSelection() {
        this.tableView.getSelectionModel().clearSelection();
    }

    public void initialStateCounters() {
        Platform.runLater(() -> {
            this.tableView.getItems().clear();
            this.labelTotalRows.setText("0");
            this.labelCurrentPage.setText("0");
            this.labelTotalPages.setText("0");
        });
    }
}
