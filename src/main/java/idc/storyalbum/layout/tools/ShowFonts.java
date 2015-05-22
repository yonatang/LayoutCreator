package idc.storyalbum.layout.tools;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by yonatan on 21/5/2015.
 */
public class ShowFonts extends JScrollPane {


    public ShowFonts() {
        super(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }

    public static void main(String[] a) throws Exception {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

        Font[] allFonts = ge.getAllFonts();
        BufferedImage bi = new BufferedImage(800, allFonts.length * 40 + 170, BufferedImage.TYPE_INT_RGB);
        Graphics g = bi.getGraphics();

        for (int i = 0; i < allFonts.length; i++) {
            Font f = allFonts[i].deriveFont(30.0f);
            if (f.getName().equalsIgnoreCase("GiddyupStd")) {
                System.out.println(f);
                System.out.println(f.getFontName());
                System.out.println(f.getName());
                Font ff = new Font(f.getName(), Font.PLAIN, 20);
                System.out.println(ff.getName() + " " + ff.getFontName());
            }
            g.setFont(f);
            g.setColor(Color.white);
            g.drawString((i + 1) + ". Welcome to Disney World, " + f.getFontName(), 10, 40 * i + 80);
        }
        ImageIO.write(bi, "png", new File("/tmp/fonts.png"));
    }

    public void paint(Graphics g) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

        Font[] allFonts = ge.getAllFonts();

        for (int i = 0; i < allFonts.length; i++) {
            Font f = allFonts[i].deriveFont(10.0f);
            g.setFont(f);

            g.setColor(Color.black);
            g.drawString("Hello, " + f.getName(), 10, 20 * i);

        }
    }
}
