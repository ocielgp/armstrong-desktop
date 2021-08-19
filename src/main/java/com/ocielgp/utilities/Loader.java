package com.ocielgp.utilities;

import com.ocielgp.controller.AppController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.util.Objects;

public class Loader {
    public static Node Load(String viewFileName, String viewSource, boolean visible, Object... controller) {
        Node node = null;
        try {
            FXMLLoader view = new FXMLLoader(
                    Objects.requireNonNull(AppController.class.getClassLoader().getResource(viewFileName))
            );
            if (controller.length > 0) {
                view.setController(controller[0]);
            }
            node = view.load();
            if (!visible) {
                node.setOpacity(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Notifications.danger("Loader", "[" + viewSource + "]: Hubo un problema al cargar una vista.", 5);
        }
        return node;
    }

}
