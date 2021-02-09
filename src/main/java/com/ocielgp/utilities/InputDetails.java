package com.ocielgp.utilities;

import javafx.scene.Node;

public class InputDetails {
    private Node node;
    private String inputString;

    public InputDetails(Node node, String inputName) {
        this.node = node;
        this.inputString = inputName;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public String getInputString() {
        return inputString;
    }

    public void setInputString(String inputString) {
        this.inputString = inputString;
    }
}
