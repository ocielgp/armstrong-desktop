package com.ocielgp.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import com.ocielgp.files.ConfigFiles;
import com.ocielgp.model.MembersModel;
import com.ocielgp.utilities.Input;
import com.ocielgp.utilities.Loader;
import com.ocielgp.utilities.Pagination;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

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
    private JFXTextField fieldSearch;
    @FXML
    private JFXButton buttonSearch;
    @FXML
    private Label labelTotalRows;
    @FXML
    private TableView<MembersModel> tableViewMembers;
    @FXML
    private TableColumn<MembersModel, Integer> tableColumnId;
    @FXML
    private TableColumn<MembersModel, String> tableColumnName;
    @FXML
    private TableColumn<MembersModel, String> tableColumnLastName;
    @FXML
    private TableColumn<MembersModel, String> tableColumnEndDate;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.tableColumnId.setCellValueFactory(new PropertyValueFactory<>("idMember"));
        this.tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        this.tableColumnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        this.tableColumnEndDate.setCellValueFactory(new PropertyValueFactory<>("endDate"));

        Pagination pagination = new Pagination(this.fieldSearch, this.buttonSearch, this.labelTotalRows, this.tableViewMembers, this.fieldRowsPerPage, this.labelPreviousPage, this.labelCurrentPage, this.labelTotalPages, this.labelNextPage, Pagination.Sources.MEMBERS);

        Node memberFXML = Loader.Load(
                "member.fxml",
                "Members",
                true,
                new MemberController(pagination)
        );
        this.memberPane.setContent(memberFXML);
        Input.getScrollEvent(this.memberPane);

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

        //TODO: ENCAPSULE METHOD ON CONFIG FILES FOR AUTOMATITATION
        toggleGender.selectedToggleProperty().addListener(((observable, oldValue, newValue) -> {
            if (oldValue != null && oldValue != newValue) {
                JFXRadioButton selected = (JFXRadioButton) newValue;
                ConfigFiles.saveProperty(ConfigFiles.File.APP, "memberGender", String.valueOf(selected.getId().charAt(selected.getId().length() - 1)));
                pagination.loadData(1);
            }
        }));
        byte genderFilter = Byte.parseByte(Objects.requireNonNull(ConfigFiles.readProperty(ConfigFiles.File.APP, "memberGender")));
        for (byte i = 0; i < toggleGender.getToggles().size(); i++) {
            if (((JFXRadioButton) toggleGender.getToggles().get(i)).getId().equals("radioButtonGender" + genderFilter)) {
                toggleGender.getToggles().get(i).setSelected(true);
            }
        }

        ToggleGroup toggleOrderBy = new ToggleGroup();
        this.radioButtonOrderBy0.setToggleGroup(toggleOrderBy);
        this.radioButtonOrderBy1.setToggleGroup(toggleOrderBy);

        //TODO: ENCAPSULE METHOD ON CONFIG FILES FOR AUTOMATITATION
        toggleOrderBy.selectedToggleProperty().addListener(((observable, oldValue, newValue) -> {
            if (oldValue != null && oldValue != newValue) {
                JFXRadioButton selected = (JFXRadioButton) newValue;
                ConfigFiles.saveProperty(ConfigFiles.File.APP, "memberOrderBy", String.valueOf(selected.getId().charAt(selected.getId().length() - 1)));
                pagination.loadData(1);
            }
        }));
        byte orderByFilter = Byte.parseByte(Objects.requireNonNull(ConfigFiles.readProperty(ConfigFiles.File.APP, "memberOrderBy")));
        for (byte i = 0; i < toggleOrderBy.getToggles().size(); i++) {
            if (((JFXRadioButton) toggleOrderBy.getToggles().get(i)).getId().equals("radioButtonOrderBy" + orderByFilter)) {
                toggleOrderBy.getToggles().get(i).setSelected(true);
            }
        }

    }
}
