package com.ocielgp.controller.members;

import animatefx.animation.FadeIn;
import animatefx.animation.FadeInRight;
import animatefx.animation.FadeOutRight;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import com.ocielgp.app.UserPreferences;
import com.ocielgp.models.Model_Member;
import com.ocielgp.utilities.Loader;
import com.ocielgp.utilities.Loading;
import com.ocielgp.utilities.Pagination;
import com.ocielgp.utilities.Styles;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
    public GridPane boxRoot;
    @FXML
    private VBox boxRightTab;
    @FXML
    private FlowPane boxButtons;

    // table
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
    private FontIcon iconPreviousPage;
    @FXML
    private Label labelCurrentPage;
    @FXML
    private Label labelTotalPages;
    @FXML
    private FontIcon iconNextPage;

    // filters
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

    // attributes
    private Pagination pagination;
    public boolean isMemberTab = true;
    private Controller_Member controllerMember;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.pagination = new Pagination(Pagination.Tables.MEMBERS, this.fieldSearch, this.buttonSearch, this.labelTotalRows, this.tableViewMembers, this.fieldRowsPerPage, this.iconPreviousPage, this.labelCurrentPage, this.labelTotalPages, this.iconNextPage);
        this.controllerMember = new Controller_Member(this.pagination);
        createButtons();
        createFilters();
        bindTable();

        this.tableViewMembers.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (isMemberTab) {
                    Loading.show();
                    CompletableFuture.runAsync(() -> this.controllerMember.getMemberData(newValue.getIdMember(), newValue.getStyle()));
                } else {
                    this.controllerMember = new Controller_Member(this.pagination, newValue.getIdMember(), newValue.getStyle());
                    changeTab("member.fxml", this.controllerMember);
                }
            }
        });

        Platform.runLater(() -> {
            FadeIn fadeIn = new FadeIn(this.boxRoot);
            fadeIn.setOnFinished(actionEvent -> this.fieldSearch.requestFocus());
            fadeIn.play();
            this.pagination.refillTable(1);
            changeTab("member.fxml", this.controllerMember); // initial tab
        });
    }

    private void createButtons() {
        Platform.runLater(() -> {
            createTabButton("gmi-group-add",
                    "Socio nuevo",
                    "member.fxml",
                    this.controllerMember,
                    true,
                    "btn-colorful", Styles.SUCCESS
            );
            createTabButton("gmi-local-play",
                    "Nueva visita",
                    "visit.fxml",
                    new Controller_Visit(),
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
    }

    private void createTabButton(String icon, String tabName, String tabResource, Object controller, boolean transition, String... styles) {
        Platform.runLater(() -> {
            JFXButton button = new JFXButton(tabName);
            button.getStyleClass().addAll(styles);
            button.setGraphic(new FontIcon(icon));
            if (transition) {
                button.setOnAction(actionEvent -> changeTab(tabResource, controller));
            } else {
                button.setOnAction(actionEvent -> Loader.Load(tabResource, "Controller_Members", true, controller));
            }
            this.boxButtons.getChildren().add(button);
        });
    }

    private void changeTab(String tabResource, Object controller) {
        if (tabResource.equals("member.fxml")) {
            isMemberTab = true;
        } else {
            isMemberTab = false;
            this.pagination.clearSelection();
        }
        Loading.show();
        if (this.boxRightTab.getChildren().size() > 0) {
            FadeOutRight fadeOutRight = new FadeOutRight(this.boxRightTab);
            fadeOutRight.setOnFinished(actionEvent -> {
                getNode(tabResource, controller);
                FadeInRight fadeInRight = new FadeInRight(this.boxRightTab);
                fadeInRight.setOnFinished(actionEvent1 -> Loading.isAnimationFinished.set(true));
                fadeInRight.play();
            });
            fadeOutRight.play();
        } else {
            getNode(tabResource, controller);
            FadeInRight fadeInRight = new FadeInRight(this.boxRightTab);
            fadeInRight.setOnFinished(actionEvent1 -> Loading.isAnimationFinished.set(true));
            fadeInRight.play();
        }
    }

    private void getNode(String tabResource, Object controller) {
        this.boxRightTab.getChildren().setAll(
                Loader.Load(tabResource, "Controller_Members", true, (tabResource.equals("member.fxml")) ? this.controllerMember : controller)
        );
    }

    private void createFilters() {
        this.checkBoxAllGyms.setSelected(UserPreferences.GetPreferenceBool("FILTER_MEMBER_ALL_GYMS"));
        this.checkBoxOnlyActiveMembers.setSelected(UserPreferences.GetPreferenceBool("FILTER_MEMBER_ACTIVE_MEMBERS"));
        this.checkBoxOnlyDebtors.setSelected(UserPreferences.GetPreferenceBool("FILTER_MEMBER_DEBTORS"));

        this.checkBoxAllGyms.selectedProperty().addListener(UserPreferences.ListenerSaver("FILTER_MEMBER_ALL_GYMS", pagination));
        this.checkBoxOnlyActiveMembers.selectedProperty().addListener(UserPreferences.ListenerSaver("FILTER_MEMBER_ACTIVE_MEMBERS", pagination));
        this.checkBoxOnlyDebtors.selectedProperty().addListener(UserPreferences.ListenerSaver("FILTER_MEMBER_DEBTORS", pagination));

        ToggleGroup toggleGender = new ToggleGroup();
        this.radioButtonGender0.setToggleGroup(toggleGender);
        this.radioButtonGender1.setToggleGroup(toggleGender);
        this.radioButtonGender2.setToggleGroup(toggleGender);
        UserPreferences.CreateSelectedToggleProperty(toggleGender, "radioButtonGender", "FILTER_MEMBER_GENDERS", pagination);

        ToggleGroup toggleOrderBy = new ToggleGroup();
        this.radioButtonOrderBy0.setToggleGroup(toggleOrderBy);
        this.radioButtonOrderBy1.setToggleGroup(toggleOrderBy);
        UserPreferences.CreateSelectedToggleProperty(toggleOrderBy, "radioButtonOrderBy", "FILTER_MEMBER_ORDER_BY", pagination);
    }

    private void bindTable() {
        this.tableColumnId.setSortable(false);
        this.tableColumnName.setSortable(false);
        this.tableColumnLastName.setSortable(false);
        this.tableColumnEndDate.setSortable(false);
        this.tableColumnId.setCellValueFactory(new PropertyValueFactory<>("idMember"));
        this.tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        this.tableColumnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        this.tableColumnEndDate.setCellValueFactory(new PropertyValueFactory<>("endDate"));
    }
}
