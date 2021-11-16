package com.ocielgp.utilities;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.ocielgp.app.UserPreferences;
import com.ocielgp.dao.JDBC_Member;
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
        MEMBERS
    }

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

        InputProperties.createEventEnter(buttonSearch, this.fieldSearch);
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
            CompletableFuture.runAsync(this::refillMembers);
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
