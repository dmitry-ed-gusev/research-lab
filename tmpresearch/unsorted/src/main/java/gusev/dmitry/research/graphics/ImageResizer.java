package gusev.dmitry.research.graphics;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Scanner;

/**
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 11.04.13)
*/

public class ImageResizer {

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        BufferedImage in = ImageIO.read(new File("c:/temp/1.jpg"));

        BufferedImage out = AbstractImageResizer.resize(in, 3400, 3400);
        ImageIO.write(out, "jpg", new File("c:/temp/img1_2.jpg"));

        out = AbstractImageResizer.resize(in, 800, 600);
        ImageIO.write(out, "jpg", new File("c:/temp/img2_2.jpg"));

        out = AbstractImageResizer.resize(in, 100, 100);
        ImageIO.write(out, "jpg", new File("c:/temp/img3_2.jpg"));

        FileInputStream inImage = new FileInputStream("c:/temp/img2_2.jpg");
        FileOutputStream outImage = new FileOutputStream("c:/temp/img2_rescaled.jpg");
        ImageResizer.rescale(inImage, outImage, 80, 80);

        Scanner scanner = new Scanner(System.in);
        scanner.next();
    }

    public static abstract class AbstractImageResizer {
        public static BufferedImage resize(BufferedImage imageToResize, int width, int height) {
            float dx = ((float) width) / imageToResize.getWidth();
            float dy = ((float) height) / imageToResize.getHeight();

            int genX, genY;
            int startX, startY;

            if (imageToResize.getWidth() <= width && imageToResize.getHeight() <= height) {
                genX = imageToResize.getWidth();
                genY = imageToResize.getHeight();
            } else {
                if (dx <= dy) {
                    genX = width;
                    genY = (int) (dx * imageToResize.getHeight());
                } else {
                    genX = (int) (dy * imageToResize.getWidth());
                    genY = height;
                }
            }

            startX = (width - genX) / 2;
            startY = (height - genY) / 2;

            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = null;

            try {
                graphics2D = bufferedImage.createGraphics();
                graphics2D.fillRect(0, 0, width, height);
                //graphics2D.drawImage(imageToResize, startX, startY, genX, genY, null);
                graphics2D.drawImage(imageToResize.getScaledInstance(genX, genY, Image.SCALE_SMOOTH), startX, startY, null);
            } finally {
                if (graphics2D != null) {
                    graphics2D.dispose();
                }
            }

            return bufferedImage;
        }
    }

    private static void rescale(InputStream input, OutputStream output, int tw, int th) throws IOException {
        // Create the image
        BufferedImage image = new BufferedImage(tw, th, BufferedImage.TYPE_INT_RGB);
        // Create the graphics
        Graphics2D graphics = image.createGraphics();
        try {
            // Set rendering hints
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            // Open the source image
            BufferedImage source = ImageIO.read(input);
            // Get width & geight
            int w = source.getWidth(), h = source.getHeight();
            // Calculate the scale
            double scale = Math.min((double) tw / (double) w, (double) th / (double) h);
            // Calculate the coordinates
            int x = (int) (((double) tw - (double) w * scale) / 2.0d), y = (int) (((double) th - (double) h * scale) / 2.0d);
            // Get the scaled instance
            Image scaled = source.getScaledInstance((int) (w * scale), (int) (h * scale), Image.SCALE_SMOOTH);
            // Set the color
            graphics.setColor(Color.WHITE);
            // Paint the white rectangle
            graphics.fillRect(0, 0, tw, th);
            // Draw the image
            graphics.drawImage(scaled, x, y, null);
        } finally {
            // Always dispose the graphics
            graphics.dispose();
        }
        // Write the rescaled image
        ImageIO.write(image, "png", output);
        output.flush();
        output.close();
    }

}
