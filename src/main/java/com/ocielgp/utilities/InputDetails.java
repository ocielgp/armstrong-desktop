package com.ocielgp.utilities;

import javafx.scene.Node;

public class InputDetails {
    private final Node node;
    private final String metadata;

    public InputDetails(Node node, String metadata) {
        this.node = node;
        this.metadata = metadata;
    }

    public Node getNode() {
        return node;
    }

    public String getMetadata() {
        return metadata;
    }

}
