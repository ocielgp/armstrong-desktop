package com.ocielgp.utilities;

import animatefx.animation.Shake;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.scene.Node;
import javafx.scene.paint.Color;

public class Input {
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
