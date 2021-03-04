package com.ocielgp.utilities;

import com.ocielgp.controller.RootController;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;

import java.util.Objects;

public class Loader {
    public static Node Load(String viewFileName, String viewSource, boolean visible) {
        Node node = null;
        try {
            FXMLLoader view = new FXMLLoader(
                    Objects.requireNonNull(RootController.class.getClassLoader().getResource(viewFileName))
            );
            node = view.load();
            if (!visible) {
                node.setOpacity(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            NotificationHandler.danger("Loader", "[" + viewSource + "]: Hubo un problema al cargar una vista.", 5);
        }
        return node;
    }

}
