package com.ocielgp.fingerprint;

import java.awt.image.BufferedImage;

import com.digitalpersona.uareu.Fid;
import com.digitalpersona.uareu.Fid.Fiv;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImagePanel extends ImageView {

    public void showImage(Fid image) {
        Fiv view = image.getViews()[0];
        BufferedImage bufferedImage = new BufferedImage(view.getWidth(), view.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        bufferedImage.getRaster().setDataElements(0, 0, view.getWidth(), view.getHeight(), view.getImageData());
        Image toFXImage = SwingFXUtils.toFXImage(bufferedImage, null);
        super.setImage(toFXImage);
        System.out.println("Pinto la IMG");
//        super.setPreserveRatio(true);
        super.setFitWidth(150);
        super.setFitHeight(150);
    }

}