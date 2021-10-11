package com.ocielgp.controller;

import com.ocielgp.app.UserPreferences;
import com.ocielgp.dao.JDBC_Member;
import com.ocielgp.utilities.Styles;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller_Popup implements Initializable {
    private static final String POPUP_NOTIFY = "NOTIFY";
    private static final String POPUP_CONFIRM = "CONFIRM";

    @FXML
    private VBox popup;
    @FXML
    private AnchorPane boxTitle;
    @FXML
    private Label labelTitle;
    @FXML
    private Label labelContent;
    @FXML
    private HBox boxButtons;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.popup.getStylesheets().add("styles.css");
        this.popup.getStyleClass().set(0, UserPreferences.getPreferenceString("THEME"));
    }

    public void init(Styles style, String title, String content, String popupType) {
        String style = Styles.
        this.labelTitle.setText(title);
        this.labelContent.setText(content);

        boxTitle.getStyleClass().forEach(System.out::println);
        if (popupType.equals(POPUP_NOTIFY)) {

        } else if (popupType.equals(POPUP_CONFIRM)) {

        }
    }
}
