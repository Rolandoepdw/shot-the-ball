package com.rolandoepdw.shottheball.ui;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/** Loads images and cursors from the JAR resources (ClassLoader). */
public final class Resources {

    private static final Logger LOG = Logger.getLogger(Resources.class.getName());
    private static final String PREFIX = "images/";

    private Resources() { }

    public static BufferedImage loadImage(String fileName) {
        String resource = PREFIX + fileName;
        try (InputStream in = Resources.class.getClassLoader().getResourceAsStream(resource)) {
            if (in == null) {
                LOG.warning("Image not found: " + resource);
                return null;
            }
            return ImageIO.read(in);
        } catch (IOException e) {
            LOG.log(Level.WARNING, "Could not read " + resource, e);
            return null;
        }
    }

    public static ImageIcon icon(String fileName, int width, int height) {
        Image image = loadImage(fileName);
        if (image == null) {
            return new ImageIcon();
        }
        return new ImageIcon(image.getScaledInstance(width, height, Image.SCALE_SMOOTH));
    }

    public static Cursor cursor(String fileName) {
        Image image = loadImage(fileName);
        if (image == null) {
            return Cursor.getDefaultCursor();
        }
        return Toolkit.getDefaultToolkit().createCustomCursor(image, new Point(15, 15), "Crosshair");
    }
}
