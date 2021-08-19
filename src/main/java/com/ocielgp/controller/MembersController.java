package com.ocielgp.controller;

import animatefx.animation.FadeIn;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import com.ocielgp.database.members.MODEL_MEMBERS;
import com.ocielgp.database.payments.MODEL_PAYMENTS_MEMBERSHIPS;
import com.ocielgp.files.ConfigFiles;
import com.ocielgp.utilities.Input;
import com.ocielgp.utilities.Loader;
import com.ocielgp.utilities.Pagination;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

public class MembersController implements Initializable {
    // Containers
    @FXML
    public GridPane boxMembersPane;
    @FXML
    private ScrollPane memberPane;

    // Controls
    @FXML
    private JFXTextField fieldSearch;
    @FXML
    private JFXButton buttonSearch;
    @FXML
    private Label labelTotalRows;
    @FXML
    private TableView<MODEL_MEMBERS> tableViewMembers;
    @FXML
    private TableColumn<MODEL_MEMBERS, Integer> tableColumnId;
    @FXML
    private TableColumn<MODEL_MEMBERS, String> tableColumnName;
    @FXML
    private TableColumn<MODEL_MEMBERS, String> tableColumnLastName;
    @FXML
    private TableColumn<MODEL_MEMBERS, String> tableColumnEndDate;
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
        this.tableColumnId.setCellValueFactory(new PropertyValueFactory<>("idMember"));
        this.tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        this.tableColumnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        this.tableColumnEndDate.setCellValueFactory(new PropertyValueFactory<>("endDate"));

        this.pagination = new Pagination(this.fieldSearch, this.buttonSearch, this.labelTotalRows, this.tableViewMembers, this.fieldRowsPerPage, this.labelPreviousPage, this.labelCurrentPage, this.labelTotalPages, this.labelNextPage, Pagination.Sources.MEMBERS);
        MemberDetailController memberDetailController = new MemberDetailController(this);
        Node memberFXML = Loader.Load(
                "memberDetail.fxml",
                "Members",
                true,
                memberDetailController
        );

        this.tableViewMembers.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                memberDetailController.loadMember(newValue.getIdMember());
            }
        });
        this.memberPane.setContent(memberFXML);
        Input.getScrollEvent(this.memberPane);
        memberFXML = null;

        this.checkBoxAllGyms.setSelected(Boolean.parseBoolean(ConfigFiles.readProperty(ConfigFiles.File.APP, "memberAllGyms")));
        this.checkBoxOnlyActiveMembers.setSelected(Boolean.parseBoolean(ConfigFiles.readProperty(ConfigFiles.File.APP, "memberOnlyActiveMembers")));
        this.checkBoxOnlyDebtors.setSelected(Boolean.parseBoolean(ConfigFiles.readProperty(ConfigFiles.File.APP, "memberOnlyDebtors")));

        this.checkBoxAllGyms.selectedProperty().addListener(ConfigFiles.listenerSaver(ConfigFiles.File.APP, "memberAllGyms", pagination));
        this.checkBoxOnlyActiveMembers.selectedProperty().addListener(ConfigFiles.listenerSaver(ConfigFiles.File.APP, "memberOnlyActiveMembers", pagination));
        this.checkBoxOnlyDebtors.selectedProperty().addListener(ConfigFiles.listenerSaver(ConfigFiles.File.APP, "memberOnlyDebtors", pagination));

        ToggleGroup toggleGender = new ToggleGroup();
        this.radioButtonGender0.setToggleGroup(toggleGender);
        this.radioButtonGender1.setToggleGroup(toggleGender);
        this.radioButtonGender2.setToggleGroup(toggleGender);
        ConfigFiles.createSelectedToggleProperty(toggleGender, "radioButtonGender", "memberGender", pagination);

        ToggleGroup toggleOrderBy = new ToggleGroup();
        this.radioButtonOrderBy0.setToggleGroup(toggleOrderBy);
        this.radioButtonOrderBy1.setToggleGroup(toggleOrderBy);
        ConfigFiles.createSelectedToggleProperty(toggleOrderBy, "radioButtonOrderBy", "memberOrderBy", pagination);

        Platform.runLater(() -> new FadeIn(this.boxMembersPane).play());
    }

    public void refreshTable() {
        this.pagination.loadData(1);
    }

    public void unselectTable() {
        this.tableViewMembers.getSelectionModel().select(null);
    }
}
