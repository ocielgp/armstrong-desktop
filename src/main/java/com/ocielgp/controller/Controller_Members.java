package com.ocielgp.controller;

import animatefx.animation.FadeIn;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import com.ocielgp.app.UserPreferences;
import com.ocielgp.dao.JDBC_Member_Fingerprint;
import com.ocielgp.models.Model_Member;
import com.ocielgp.utilities.Loader;
import com.ocielgp.utilities.Loading;
import com.ocielgp.utilities.Pagination;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class Controller_Members implements Initializable {
    @FXML
    public GridPane boxMembersPane;
    @FXML
    private VBox memberPane;

    @FXML
    private JFXTextField fieldSearch;
    @FXML
    private JFXButton buttonSearch;
    @FXML
    private Label labelTotalRows;
    @FXML
    private TableView<Model_Member> tableViewMembers;
    @FXML
    private TableColumn<Model_Member, Integer> tableColumnId;
    @FXML
    private TableColumn<Model_Member, String> tableColumnName;
    @FXML
    private TableColumn<Model_Member, String> tableColumnLastName;
    @FXML
    private TableColumn<Model_Member, String> tableColumnEndDate;
    @FXML
    private JFXTextField fieldRowsPerPage;
    @FXML
    private Label labelPreviousPage;
    @FXML
    private Label labelCurrentPage;
    @FXML
    private Label labelTotalPages;
    @FXML
    private Label labelNextPage;

    @FXML
    private JFXCheckBox checkBoxAllGyms;
    @FXML
    private JFXCheckBox checkBoxOnlyActiveMembers;
    @FXML
    private JFXCheckBox checkBoxOnlyDebtors;
    @FXML
    private JFXRadioButton radioButtonGender0;
    @FXML
    private JFXRadioButton radioButtonGender1;
    @FXML
    private JFXRadioButton radioButtonGender2;
    @FXML
    private JFXRadioButton radioButtonOrderBy0;
    @FXML
    private JFXRadioButton radioButtonOrderBy1;

    // Attributes
    private Pagination pagination;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        JDBC_Member_Fingerprint.SCANNING = false;

        this.tableColumnId.setSortable(false);
        this.tableColumnName.setSortable(false);
        this.tableColumnLastName.setSortable(false);
        this.tableColumnEndDate.setSortable(false);
        this.tableColumnId.setCellValueFactory(new PropertyValueFactory<>("idMember"));
        this.tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        this.tableColumnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        this.tableColumnEndDate.setCellValueFactory(new PropertyValueFactory<>("endDate"));

        Controller_Member controllerMember = new Controller_Member(this);
        Node memberFXML = Loader.Load(
                "member.fxml",
                "Members",
                true,
                controllerMember
        );

        this.tableViewMembers.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Loading.show();
                CompletableFuture.runAsync(() -> controllerMember.getMemberData(newValue.getIdMember(), newValue.getStyle()));
            }
        });
        this.memberPane.getChildren().setAll(memberFXML);

        // filters
        this.pagination = new Pagination(this.fieldSearch, this.buttonSearch, this.labelTotalRows, this.tableViewMembers, this.fieldRowsPerPage, this.labelPreviousPage, this.labelCurrentPage, this.labelTotalPages, this.labelNextPage, Pagination.Sources.MEMBERS);
        this.checkBoxAllGyms.setSelected(UserPreferences.getPreferenceBool("FILTER_MEMBER_ALL_GYMS"));
        this.checkBoxOnlyActiveMembers.setSelected(UserPreferences.getPreferenceBool("FILTER_MEMBER_ACTIVE_MEMBERS"));
        this.checkBoxOnlyDebtors.setSelected(UserPreferences.getPreferenceBool("FILTER_MEMBER_DEBTORS"));

        this.checkBoxAllGyms.selectedProperty().addListener(UserPreferences.listenerSaver("FILTER_MEMBER_ALL_GYMS", pagination));
        this.checkBoxOnlyActiveMembers.selectedProperty().addListener(UserPreferences.listenerSaver("FILTER_MEMBER_ACTIVE_MEMBERS", pagination));
        this.checkBoxOnlyDebtors.selectedProperty().addListener(UserPreferences.listenerSaver("FILTER_MEMBER_DEBTORS", pagination));

        ToggleGroup toggleGender = new ToggleGroup();
        this.radioButtonGender0.setToggleGroup(toggleGender);
        this.radioButtonGender1.setToggleGroup(toggleGender);
        this.radioButtonGender2.setToggleGroup(toggleGender);
        UserPreferences.createSelectedToggleProperty(toggleGender, "radioButtonGender", "FILTER_MEMBER_GENDERS", pagination);

        ToggleGroup toggleOrderBy = new ToggleGroup();
        this.radioButtonOrderBy0.setToggleGroup(toggleOrderBy);
        this.radioButtonOrderBy1.setToggleGroup(toggleOrderBy);
        UserPreferences.createSelectedToggleProperty(toggleOrderBy, "radioButtonOrderBy", "FILTER_MEMBER_ORDER_BY", pagination);

        Platform.runLater(() -> {
            new FadeIn(this.boxMembersPane).play();
            this.pagination.restartTable();
        });
    }

    public void refreshTable() {
        this.pagination.restartTable();
    }

    public void unselectTable() {
        this.tableViewMembers.getSelectionModel().select(null);
    }
}
