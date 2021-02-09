package com.ocielgp.utilities;

import animatefx.animation.Shake;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.scene.Node;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;

public class Validator {
    public static boolean emptyValidator(InputDetails input) {
        int emptyInputs = 0;

        if (input.getInputString().trim().isEmpty() || input.getInputString().equals("-1")) {
            emptyInputs++;
            shakeInput(input.getNode());
        }

        return emptyInputs == 0;
    }

    public static boolean emptyValidator(InputDetails... inputs) {
        int emptyInputs = 0;

        for (InputDetails input : inputs) {
            if (input.getInputString().trim().isEmpty() || input.getInputString().equals("-1")) {
                emptyInputs++;
                shakeInput(input.getNode());
            }
        }

        if (emptyInputs == 0) {
            return true;
        } else {
            NotificationHandler.danger("Error", "Los campos en rojo no pueden estar vacios.", 2);
            return false;
        }
    }

    public static boolean onlyTextValidator(HashMap<String, InputDetails> inputs) {
        String regex = "[a-zA-Z ]+";

        for (Map.Entry<String, InputDetails> input : inputs.entrySet()) {
            if (!input.getKey().matches(regex)) {
                shakeInput(input.getValue().getNode());
                NotificationHandler.danger("Error", "[" + input.getValue().getInputString() + "]: Debe ser solo texto.", 2);
                return false;
            }
        }
        return true;
    }

    private static void shakeInput(Node input) {
        if (input.getClass() == JFXTextField.class) { // JFXTextField
            JFXTextField field = (JFXTextField) input;
            field.setUnFocusColor(Color.RED);
        } else if (input.getClass() == JFXPasswordField.class) { // JFXPasswordField
            JFXPasswordField field = (JFXPasswordField) input;
            field.setUnFocusColor(Color.RED);
        } else if (input.getClass() == JFXComboBox.class) { // JFXComboBox
            JFXComboBox comboBox = (JFXComboBox) input;
            if (!comboBox.getStyleClass().contains("red-border-input-line")) {
                comboBox.getStyleClass().add("red-border-input-line");
            }
        } else if (input.getClass() == JFXDatePicker.class) {
            JFXDatePicker datePicker = (JFXDatePicker) input;
            if (!datePicker.getStyleClass().contains("red-border-input-line")) {
                datePicker.getStyleClass().add("red-border-input-line");
            }
        }
        new Shake(input).play();
    }
}