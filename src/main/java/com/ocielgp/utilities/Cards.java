package com.ocielgp.utilities;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import org.kordamp.ikonli.javafx.FontIcon;

public class Cards {
    public static HBox createCard(String iconCode, String titleText, String descriptionText) {
        Label title = new Label(titleText);
        title.getStyleClass().add("title");
        Label description = new Label(descriptionText);
        description.getStyleClass().add("description");
        description.setWrapText(true);
        description.setTextAlignment(TextAlignment.CENTER);
        VBox info = new VBox(title, description);

        HBox icon = new HBox(new FontIcon(iconCode));
        icon.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(icon, Priority.ALWAYS);
        HBox.setMargin(icon.getChildren().get(0), new Insets(0, 0, 0, 50));

        HBox card = new HBox(info, icon);
        card.getStyleClass().add("card");
        card.setOpacity(0);
        return card;
    }

    public static HBox createCard(String iconCode, String titleText, String descriptionText, String hexFontColor, Color leftColor, Color rightColor) {
        HBox card = createCard(iconCode, titleText, descriptionText);
        card.setStyle("-fx-color-text: " + hexFontColor + "; -fx-background-color: linear-gradient(to right, rgba(" + leftColor.getRed() * 255 + "," + leftColor.getGreen() * 255 + "," + leftColor.getBlue() * 255 + "," + leftColor.getOpacity() * 255 + ") 0%, rgb(" + rightColor.getRed() * 255 + "," + rightColor.getGreen() * 255 + "," + rightColor.getBlue() * 255 + "," + rightColor.getOpacity() * 255 + ") 100%);");
        return card;
    }
}
