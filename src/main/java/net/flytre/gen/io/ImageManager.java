package net.flytre.gen.io;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageManager {

    public static BufferedImage getImage(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static Color[][] getColorArray(BufferedImage b) {

        Color[][] pixels = new Color[b.getWidth()][b.getHeight()];
        for (int i = 0; i < b.getWidth(); i++)
            for (int j = 0; j < b.getHeight(); j++)
                pixels[i][j] = new Color(b.getRGB(i, j), true);
        return pixels;
    }


}
