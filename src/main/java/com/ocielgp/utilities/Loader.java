package com.ocielgp.utilities;

import com.ocielgp.controller.Controller_App;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Objects;

public class Loader {
    public static Node Load(String viewFileName, String viewSource, boolean visible) {
        Node node = null;
        try {
            FXMLLoader view = new FXMLLoader(
                    Objects.requireNonNull(Controller_App.class.getClassLoader().getResource(viewFileName))
            );
            node = view.load();
            if (!visible) {
                node.setOpacity(0);
            }
        } catch (IOException ioException) {
            Notifications.CatchException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], ioException);
        }
        return node;
    }

    public static Node Load(String viewFileName, String viewSource, boolean visible, Object controller) {
        Node node = null;
        try {
            FXMLLoader view = new FXMLLoader(
                    Objects.requireNonNull(Controller_App.class.getClassLoader().getResource(viewFileName))
            );
            view.setController(controller);
            node = view.load();
            if (!visible) {
                node.setOpacity(0);
            }
        } catch (IOException ioException) {
            Notifications.CatchException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], ioException);
        }
        return node;
    }

}
