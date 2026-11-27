package com.rolandoepdw.shottheball.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.JPanel;

/** Panel that paints an image at its original size, like the original launcher header. */
public final class ImagePanel extends JPanel {

    private final Image image;

    public ImagePanel(Image image, Dimension preferredSize) {
        this.image = image;
        setPreferredSize(preferredSize);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image == null) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.drawImage(image, 0, 0, this);
        } finally {
            g2.dispose();
        }
    }
}
