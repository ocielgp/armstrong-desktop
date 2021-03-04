package com.ocielgp.utilities;

import com.jfoenix.controls.JFXButton;
import com.ocielgp.RunApp;
import com.ocielgp.app.DialogController;
import com.ocielgp.model.CustomDialogModel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.*;

import java.io.IOException;
import java.util.*;

public class DialogHandler {
    // Containers
    private static Stage stage;

    // Attributes
    private static String selectedButton;

    // Styles
    public static final String PRIMARY_TYPE = "btn-primary";
    public static final String SECONDARY_TYPE = "btn-secondary";

    public static final String DEFAULT_STYLE = "default-style";
    public static final String SUCESS_STYLE = "sucess-style";
    public static final String WARNING_STYLE = "warning-style";
    public static final String DANGER_STYLE = "danger-style";
    public static final String EPIC_STYLE = "epic-style";

    static {
        // Init dialog container
        stage = new Stage();
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);

        stage.setAlwaysOnTop(true); // Fix dialog, always on front
    }

    public static JFXButton createButton(String content, String type, String style) {
        JFXButton button = new JFXButton(content);
        button.getStyleClass().addAll(type, style);
        return button;
    }

    public static String createDialog(String icon, String title, String content, JFXButton[] buttons) {
        setSelectedButton(""); // Init selected button
        CustomDialogModel dialog = new CustomDialogModel(
                icon,
                title,
                content,
                buttons
        );
        FXMLLoader template = new FXMLLoader(
                Objects.requireNonNull(DialogHandler.class.getClassLoader().getResource("dialog.fxml"))
        );
        DialogController controller = new DialogController(dialog);
        template.setController(controller);
        GridPane gridPane = null;
        try {
            gridPane = template.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scene scene = new Scene(gridPane);
        scene.getStylesheets().add(String.valueOf(RunApp.class.getClassLoader().getResource("dialogs.css")));

        stage.setTitle(dialog.getTitle());
        stage.setScene(scene);
        scene.setFill(Color.TRANSPARENT);


        for (JFXButton button : dialog.getButtons()) {
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    setSelectedButton(((Button) actionEvent.getSource()).getText());
                    stage.hide();
                }
            });
        }

        // Calculate UI pixels
        stage.show();
        stage.hide();

        stage.setOnShown(evt -> {
            stage.setX(
                    (Screen.getPrimary().getBounds().getWidth() / 2) - (stage.getWidth() / 2)
            );
            stage.setY(
                    (Screen.getPrimary().getBounds().getHeight() / 2) - (stage.getHeight() / 2)
            );
        });

        stage.showAndWait();

        return getSelectedButton();
    }

    public static String getSelectedButton() {
        return selectedButton;
    }

    public static void setSelectedButton(String selectedButton) {
        DialogHandler.selectedButton = selectedButton;
    }
}
