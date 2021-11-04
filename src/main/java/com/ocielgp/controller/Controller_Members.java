package com.ocielgp.controller;

import animatefx.animation.FadeIn;
import animatefx.animation.FadeInRight;
import animatefx.animation.FadeOutRight;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import com.ocielgp.app.Application;
import com.ocielgp.app.UserPreferences;
import com.ocielgp.controller.dashboard.Controller_Membership;
import com.ocielgp.dao.JDBC_Member_Fingerprint;
import com.ocielgp.models.Model_Member;
import com.ocielgp.utilities.Loader;
import com.ocielgp.utilities.Loading;
import com.ocielgp.utilities.Pagination;
import com.ocielgp.utilities.Styles;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class Controller_Members implements Initializable {
    @FXML
    public GridPane boxMembersPane;
    @FXML
    private VBox memberPane;
    @FXML
    private FlowPane boxButtons;

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

    private void createTabButton(String icon, String tabName, String tabResource, Object controller, boolean transition, String... styles) {
        Platform.runLater(() -> {
            JFXButton button = new JFXButton(tabName);
            button.getStyleClass().addAll(styles);
            button.setGraphic(new FontIcon(icon));
            if (transition) {
                button.setOnAction(actionEvent -> {
                    changeTab(
                            Loader.Load(
                                    tabResource,
                                    "Controller_Members",
                                    true,
                                    controller
                            ));
                });
            } else {
                button.setOnAction(actionEvent -> {
                    Loader.Load(
                            tabResource,
                            "Controller_Members",
                            true,
                            controller
                    );
                });
            }
            this.boxButtons.getChildren().add(button);
        });
    }

    private void changeTab(Node node) {
        Platform.runLater(() -> {
            Loading.show();
            FadeOutRight fadeOutRight = new FadeOutRight(this.memberPane);
            fadeOutRight.setOnFinished(actionEvent -> {
                this.memberPane.getChildren().setAll(node);
                FadeInRight fadeInRight = new FadeInRight(this.memberPane);
                fadeInRight.setOnFinished(actionEvent1 -> {
                    Application.isAnimationFinished = true;
                    Loading.close();
                });
                fadeInRight.play();
            });
            fadeOutRight.play();
        });
    }

    private void createButtons() {
        try {
            Platform.runLater(() -> {
                createTabButton("gmi-group-add",
                        "Socio nuevo",
                        "member.fxml",
                        new Controller_Member(this.pagination),
                        true,
                        "btn-colorful", Styles.SUCCESS
                );
                createTabButton("gmi-local-play",
                        "Nueva visita",
                        "new-visit.fxml",
                        new Controller_Dashboard_Visit(),
                        false,
                        "btn-colorful", Styles.SUCCESS
                );
                createTabButton("gmi-calendar-today",
                        "Membresias",
                        "memberships.fxml",
                        new Controller_Membership(),
                        true,
                        "btn-colorful", Styles.WARN
                );
            });
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        JDBC_Member_Fingerprint.SCANNING = false;

        this.pagination = new Pagination(this.fieldSearch, this.buttonSearch, this.labelTotalRows, this.tableViewMembers, this.fieldRowsPerPage, this.labelPreviousPage, this.labelCurrentPage, this.labelTotalPages, this.labelNextPage, Pagination.Sources.MEMBERS);
        createButtons();

        this.tableColumnId.setSortable(false);
        this.tableColumnName.setSortable(false);
        this.tableColumnLastName.setSortable(false);
        this.tableColumnEndDate.setSortable(false);
        this.tableColumnId.setCellValueFactory(new PropertyValueFactory<>("idMember"));
        this.tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        this.tableColumnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        this.tableColumnEndDate.setCellValueFactory(new PropertyValueFactory<>("endDate"));

        Controller_Member controllerMember = new Controller_Member(pagination);
        Node memberFXML = Loader.Load(
                "member.fxml",
                "Members",
                true,
                controllerMember
        );

        this.tableViewMembers.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Loading.show();
                if (this.memberPane.getChildren().get(0) == memberFXML) {
                    CompletableFuture.runAsync(() -> controllerMember.getMemberData(newValue.getIdMember(), newValue.getStyle()));
                } else {
                    CompletableFuture.runAsync(() -> controllerMember.getMemberData(newValue.getIdMember(), newValue.getStyle()));
                    changeTab(memberFXML);
                }
            }
        });
        this.memberPane.getChildren().setAll(memberFXML);

        // filters
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

    public void unselectTable() {
        this.tableViewMembers.getSelectionModel().select(null);
    }
}
