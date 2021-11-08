package com.ocielgp.utilities;

import animatefx.animation.Shake;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.math.BigDecimal;

public class Validator {
    private static String getMetadataFromNode(Node node) {
        if (node.getClass() == JFXTextField.class || node.getClass() == JFXPasswordField.class) {
            return ((TextField) node).getText();
        } else if (node.getClass() == JFXComboBox.class) {
            return String.valueOf(((JFXComboBox<?>) node).getSelectionModel().getSelectedIndex());
        } else {
            return "";
        }
    }

    public static boolean emptyValidator(Node... nodes) {
        boolean formValid = true;
        for (Node node : nodes) {
            String metadata = getMetadataFromNode(node);
            if (metadata.replace(" ", "").length() == 0 || metadata.equals("-1")) {
                if (formValid) formValid = false;
                shakeInput(node);
            }
        }
        if (!formValid) {
            Notifications.Danger("Error", "Los campos en rojo no pueden estar vacios");
        }
        return formValid;
    }

    public static boolean textValidator(Node... nodes) {
        String regex = "[a-zA-ZáÁéÉíÍóÓúÚñÑ ]+";
        boolean formValid = true;
        for (Node node : nodes) {
            String metadata = getMetadataFromNode(node);
            if (!metadata.matches(regex)) {
                if (formValid) formValid = false;
                shakeInput(node);
            }
        }
        if (!formValid) {
            Notifications.Danger("Error", "Los campos en rojo deben ser solo texto");
        }
        return formValid;
    }

    public static boolean numberValidator(Node node, boolean requestFocus) {
        String regex = "[0-9]+";
        String metadata = getMetadataFromNode(node);
        boolean formValid = true;
        if (!metadata.replace(" ", "").matches(regex)) {
            if (formValid) formValid = false;
            shakeInput(node);
        }
        if (!formValid && requestFocus) {
            node.requestFocus();
        }
        return formValid;
    }

    public static boolean phoneValidator(Node node) {
        String regex = "[0-9]+";
        String phone = getMetadataFromNode(node).replace(" ", "");
        if (phone.matches(regex)) {
            if (phone.length() != 10) {
                shakeInput(node);
                Notifications.Danger("Error", "El teléfono debe tener 10 numéros");
                return false;
            }
            return true;
        } else {
            shakeInput(node);
            Notifications.Danger("Error", "El teléfono debe tener solo numéros");
            return false;
        }
    }

    public static boolean emailValidator(Node node) {
        String regex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])";
        String email = InputProperties.spaceRemover(getMetadataFromNode(node)).toLowerCase();
        if (email.matches(regex)) {
            return true;
        } else {
            shakeInput(node);
            Notifications.Danger("Error", "El correo no es válido");
            return false;
        }
    }

    public static boolean moneyValidator(Node node, boolean notify) {
        try {
            new BigDecimal(getMetadataFromNode(node));
            return true;
        } catch (NumberFormatException ignored) {
//            if (!node.getMetadata().isEmpty()) {
//                shakeInput(node.getNode());
//            }
        }
        if (notify) {
            Notifications.Danger("Error", "Cantidad no válida");
            shakeInput(node);
        }
        return false;
    }


    /*public static boolean moneyValidator(Node... nodes) {
        int inputsInvalid = 0;
        while (nodes.hasNext()) {
            InputMetadata input = nodes.next();
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
            Notifications.Danger("Error", "Los campos en rojo deben tener solo numéros");
            return false;
        }
    }*/

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

    public static boolean compare(String newText, String originalText) {
        return newText.equalsIgnoreCase(originalText);
    }
}