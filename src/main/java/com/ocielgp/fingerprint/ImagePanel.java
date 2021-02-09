package com.ocielgp.fingerprint;

import java.awt.image.BufferedImage;

import com.digitalpersona.uareu.Fid;
import com.digitalpersona.uareu.Fid.Fiv;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImagePanel extends ImageView {
    private BufferedImage bufferedImage;

    public void showImage(Fid image) {
        Fiv view = image.getViews()[0];
        this.bufferedImage = new BufferedImage(view.getWidth(), view.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        this.bufferedImage.getRaster().setDataElements(0, 0, view.getWidth(), view.getHeight(), view.getImageData());
        Image toFXImage = SwingFXUtils.toFXImage(bufferedImage, null);
        this.displayFXImage(toFXImage);
        System.out.println("Pinto la IMG");

//        repaint();
    }

    public void displayFXImage(Image img) {
        super.setImage(img);
    }

//    public void paint(Graphics g) {
//        g.drawImage(bufferedImage, 0, 0, null);
//    }

}