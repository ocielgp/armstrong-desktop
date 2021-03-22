package com.ocielgp.utilities;

import animatefx.animation.Shake;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.scene.Node;
import javafx.scene.paint.Color;

import java.util.ListIterator;

public class Validator {
    public static boolean emptyValidator(ListIterator<InputDetails> inputs) {
        int inputsInvalid = 0;
        while (inputs.hasNext()) {
            InputDetails input = inputs.next();
            if (input.getMetadata().replace(" ", "").length() == 0 || input.getMetadata().equals("-1")) {
                inputsInvalid++;
                shakeInput(input.getNode());
            }
        }
        if (inputsInvalid == 0) {
            return true;
        } else {
            NotificationHandler.danger("Error", "Los campos en rojo no pueden estar vacios.", 2);
            return false;
        }
    }

    public static boolean textValidator(ListIterator<InputDetails> inputs) {
        String regex = "[a-zA-ZáéíóúñÑ ]+";
        int inputsInvalid = 0;
        while (inputs.hasNext()) {
            InputDetails inputDetails = inputs.next();
            if (!inputDetails.getMetadata().matches(regex)) {
                inputsInvalid++;
                shakeInput(inputDetails.getNode());
            }
        }
        if (inputsInvalid == 0) {
            return true;
        } else {
            NotificationHandler.danger("Error", "Los campos en rojo deben ser solo texto.", 2);
            return false;
        }
    }

    public static boolean numberValidator(InputDetails input) {
        String regex = "[0-9]+";
        int inputsInvalid = 0;
            if (!input.getMetadata().replace(" ", "").matches(regex)) {
                shakeInput(input.getNode());
                inputsInvalid++;
            }
        if (inputsInvalid == 0) {
            return true;
        } else {
            NotificationHandler.danger("Error", "Los campos en rojo deben ser solo numéros.", 2);
            return false;
        }
    }

    public static boolean numberValidator(ListIterator<InputDetails> inputs) {
        String regex = "[0-9]+";
        int inputsInvalid = 0;
        while (inputs.hasNext()) {
            InputDetails input = inputs.next();
            if (!input.getMetadata().replace(" ", "").matches(regex)) {
                shakeInput(input.getNode());
                inputsInvalid++;
            }
        }
        if (inputsInvalid == 0) {
            return true;
        } else {
            NotificationHandler.danger("Error", "Los campos en rojo deben ser solo numéros.", 2);
            return false;
        }
    }

    public static boolean phoneValidator(InputDetails phoneInput) {
        String regex = "[0-9]+";
        String phone = phoneInput.getMetadata().replace(" ", "");
        if (phone.matches(regex)) {
            if (phone.length() != 10) {
                shakeInput(phoneInput.getNode());
                NotificationHandler.danger("Error", "El teléfono debe tener 10 numéros.", 2);
                return false;
            }
            return true;
        } else {
            shakeInput(phoneInput.getNode());
            NotificationHandler.danger("Error", "El teléfono debe tener solo numéros.", 2);
            return false;
        }
    }

    public static boolean emailValidator(InputDetails emailInput) {
        String regex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
        String phone = emailInput.getMetadata().replace(" ", "");
        if (phone.matches(regex)) {
            return true;
        } else {
            shakeInput(emailInput.getNode());
            NotificationHandler.danger("Error", "El correo no es válido.", 2);
            return false;
        }
    }

    public static boolean moneyValidator(ListIterator<InputDetails> inputs) {
        int inputsInvalid = 0;
        while (inputs.hasNext()) {
            InputDetails input = inputs.next();
            try {
                Double.parseDouble(input.getMetadata());
            } catch (NumberFormatException ignored) {
                shakeInput(input.getNode());
                inputsInvalid++;
            }
        }
        if (inputsInvalid == 0) {
            return true;
        } else {
            NotificationHandler.danger("Error", "Los campos en rojo deben tener solo numéros.", 2);
            return false;
        }
    }

    public static void shakeInput(Node... inputs) {
        for (Node input : inputs) {
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
}