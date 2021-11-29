package com.ocielgp.controller.admins;

import animatefx.animation.FadeIn;
import animatefx.animation.FadeInRight;
import animatefx.animation.FadeOutRight;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import com.ocielgp.app.Application;
import com.ocielgp.app.UserPreferences;
import com.ocielgp.models.Model_Admin;
import com.ocielgp.utilities.*;
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

public class Controller_Admins implements Initializable {
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
    private TableView<Model_Admin> tableViewAdmins;
    @FXML
    private TableColumn<Model_Admin, Integer> tableColumnId;
    @FXML
    private TableColumn<Model_Admin, String> tableColumnUsername;
    @FXML
    private TableColumn<Model_Admin, String> tableColumnName;
    @FXML
    private TableColumn<Model_Admin, String> tableColumnRole;
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
    private JFXCheckBox checkBoxOnlyActiveMembers;
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
    public boolean isAdminTab = true;
    private Controller_Admin controllerAdmin;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.pagination = new Pagination(Pagination.Tables.ADMINS, this.fieldSearch, this.buttonSearch, this.labelTotalRows, this.tableViewAdmins, this.fieldRowsPerPage, this.iconPreviousPage, this.labelCurrentPage, this.labelTotalPages, this.iconNextPage);
        this.controllerAdmin = new Controller_Admin(this.pagination);
        createButtons();
        createFilters();
        bindTable();

        this.tableViewAdmins.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (isAdminTab) {
                    if (newValue.getIdRole() > Application.GetModelAdmin().getIdRole() || Application.GetModelAdmin().getIdMember() == newValue.getIdMember()) {
                        Loading.show();
                        CompletableFuture.runAsync(() -> this.controllerAdmin.getMemberData(newValue.getIdMember(), newValue.getStyle()));
                    } else {
                        Notifications.Danger("Sin permiso", "Solo puedes editar niveles inferiores");
                    }
                } else {
                    this.controllerAdmin = new Controller_Admin(this.pagination, newValue.getIdMember(), newValue.getStyle());
                    changeTab("admin.fxml", this.controllerAdmin);
                }
            }
        });

        Platform.runLater(() -> {
            FadeIn fadeIn = new FadeIn(this.boxRoot);
            fadeIn.setOnFinished(actionEvent -> this.fieldSearch.requestFocus());
            fadeIn.play();
            this.pagination.refillTable(1);
            changeTab("admin.fxml", this.controllerAdmin); // initial tab
        });
    }

    private void createButtons() {
        Platform.runLater(() -> {
            createTabButton("gmi-group-add",
                    "Administrador nuevo",
                    "admin.fxml",
                    this.controllerAdmin,
                    true,
                    "btn-colorful", Styles.SUCCESS
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
                button.setOnAction(actionEvent -> Loader.Load(tabResource, "Controller_Admins", true, controller));
            }
            this.boxButtons.getChildren().add(button);
        });
    }

    private void changeTab(String tabResource, Object controller) {
        if (tabResource.equals("admin.fxml")) {
            isAdminTab = true;
        } else {
            isAdminTab = false;
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
                Loader.Load(tabResource, "Controller_Admins", true, (tabResource.equals("admin.fxml")) ? this.controllerAdmin : controller)
        );
    }

    private void createFilters() {
        this.checkBoxOnlyActiveMembers.setSelected(UserPreferences.GetPreferenceBool("FILTER_MEMBER_ACTIVE_MEMBERS"));

        this.checkBoxOnlyActiveMembers.selectedProperty().addListener(UserPreferences.ListenerSaver("FILTER_MEMBER_ACTIVE_MEMBERS", pagination));

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
        this.tableColumnUsername.setSortable(false);
        this.tableColumnName.setSortable(false);
        this.tableColumnRole.setSortable(false);
        this.tableColumnId.setCellValueFactory(new PropertyValueFactory<>("idMember"));
        this.tableColumnUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        this.tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        this.tableColumnRole.setCellValueFactory(new PropertyValueFactory<>("roleName"));
    }
}
