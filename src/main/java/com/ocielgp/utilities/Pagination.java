package com.ocielgp.utilities;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.ocielgp.database.MembersData;
import com.ocielgp.model.MembersModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import java.time.LocalDate;

public class Pagination {
    public enum Sources {
        MEMBERS
    }

    private final TableView tableView;
    private final JFXTextField fieldRowsPerPage;
    private JFXButton buttonFilter;
    private Label labelPreviousPage;
    private Label labelPage;
    private Label labelNextPage;

    private int rows = 15;
    private final Sources source;

    public Pagination(TableView tableView, JFXTextField fieldRegistersPerPage, JFXButton buttonFilter, Label labelPreviusPage, Label labelPageCounter, Label labelNextPage, Sources source) {
        this.tableView = tableView;
        this.fieldRowsPerPage = fieldRegistersPerPage;
        this.buttonFilter = buttonFilter;
        this.labelPreviousPage = labelPreviusPage;
        this.labelPage = labelPageCounter;
        this.labelNextPage = labelNextPage;
        this.source = source;

        this.buttonFilter.addEventFilter(ActionEvent.ACTION, actionEvent -> {
            if (Validator.numberValidator(new InputDetails(this.fieldRowsPerPage, this.fieldRowsPerPage.getText()))) {
                int newRowsPerPage = Integer.parseInt(this.fieldRowsPerPage.getText());
                if (newRowsPerPage > 0) {
                    this.rows = Integer.parseInt(this.fieldRowsPerPage.getText());
                    this.loadData(1);
                } else {
                    NotificationHandler.danger("Error", "Cantidad de registros no válida.", 2);
                    Validator.shakeInput(this.fieldRowsPerPage);
                }
            }
        });
        this.labelPreviousPage.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEvent -> this.previousPage());
        this.labelNextPage.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEvent -> this.nextPage());
        this.tableView.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEvent -> this.itemSelected());

        this.loadData(1); // Initial data
    }

    public void itemSelected() {
        switch (source) {
            case MEMBERS: {
                //TODO: INVALIDATE NULL REFERENCES
                if (this.tableView.getSelectionModel().getSelectedItem() != null) {
                    MembersModel membersModel = (MembersModel) this.tableView.getSelectionModel().getSelectedItem();
                    System.out.println(membersModel.getIdMember());
                }
            }
        }
    }

    public void loadData(int page) {
        if (page > 0) {
            if (!this.fieldRowsPerPage.getText().equals(String.valueOf(this.rows))) {
                this.fieldRowsPerPage.setText(String.valueOf(this.rows));
            }
            switch (source) {
                case MEMBERS: {
                    ObservableList<MembersModel> data = MembersData.getMembers(this.rows, page);
                    if (data != null && data.size() > 0) {
                        this.tableView.setItems(data);
                        this.labelPage.setText(String.valueOf(page));
                        this.tableView.setRowFactory(row -> new TableRow<MembersModel>() {
                            @Override
                            public void updateItem(MembersModel member, boolean empty) {
                                super.updateItem(member, empty);
                                if (member != null) {
                                    /* DATES
                                     *  0 DAYS = DANGER
                                     * 1-3 DAYS = WARN
                                     * +3 DAYS = GREEN
                                     */
                                    // TODO: CENTER DATES
                                    //TODO: FIX WIDTH PER COLUMN
                                    if (member.getDaysLeft() <= 0) {
                                        if (getStyleClass().size() == 5) {
                                            getStyleClass().set(4, "danger-style"); // replace color style
                                        } else {
                                            getStyleClass().addAll("member-cell", "danger-style");
                                        }
                                    } else if (member.getDaysLeft() > 0 && member.getDaysLeft() <= 3) {
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
                    }
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
}
