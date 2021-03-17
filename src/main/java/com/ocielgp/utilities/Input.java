package com.ocielgp.utilities;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyEvent;

import java.util.Locale;

public class Input {
    private static final double SPEED_SCROLL = 0.003;

    public static void createMaxLengthEvent(TextInputControl textInput, int maxLength) {
        textInput.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
            if (((TextInputControl) keyEvent.getSource()).getText().length() > maxLength - 1) {
                keyEvent.consume();
            }
            if (textInput.getText().length() >= maxLength + 1) {
                textInput.setText(textInput.getText().substring(0, maxLength));
                textInput.end();
            }
        });
    }

    public static void getScrollEvent(ScrollPane scrollPane) {
        scrollPane.getContent().setOnScroll(scrollEvent -> {
            double deltaY = scrollEvent.getDeltaY() * SPEED_SCROLL;
            scrollPane.setVvalue(scrollPane.getVvalue() - deltaY);
        });
    }

    public static String spaceRemover(String text) {
        return text.replace(" ", "");
    }

    public static String replaceWhitespaces(String text) {
        return text.trim().replaceAll(" +", " ");
    }

    public static String capitalizeFirstLetter(String text) {
        text = text.toLowerCase();
        text = replaceWhitespaces(text);
        if (!text.isEmpty()) {
            text = text.substring(0, 1).toUpperCase() + text.substring(1);
        }
        return text;
    }

    public static String capitalizeFirstLetterPerWord(String text) {
        text = text.toLowerCase();
        text = replaceWhitespaces(text);
        String[] words = text.split("\\s");
        StringBuilder capitalizeWord = new StringBuilder();
        for (String w : words) {
            String first = w.substring(0, 1);
            String afterFirst = w.substring(1);
            capitalizeWord.append(first.toUpperCase()).append(afterFirst).append(" ");
        }
        return capitalizeWord.toString().trim();
    }

    public static void clearInputs(Node... inputs) {
        for (Node input : inputs) {
            if (input.getClass() == JFXTextField.class) { // JFXTextField
                JFXTextField field = (JFXTextField) input;
                field.setText("");
            } else if (input.getClass() == JFXPasswordField.class) { // JFXPasswordField
                JFXPasswordField field = (JFXPasswordField) input;
                field.setText("");
            } else if (input.getClass() == JFXComboBox.class) { // JFXComboBox
                JFXComboBox comboBox = (JFXComboBox) input;
                comboBox.getSelectionModel().select(-1);
            } else if (input.getClass() == JFXDatePicker.class) {
                JFXDatePicker datePicker = (JFXDatePicker) input;
                datePicker.getEditor().setText("");
            }
        }
    }

}
