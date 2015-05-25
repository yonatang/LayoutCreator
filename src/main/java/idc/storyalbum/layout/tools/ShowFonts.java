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
        int all=5;
        BufferedImage bi = new BufferedImage(800, all * 40 + 170, BufferedImage.TYPE_INT_RGB);
        Graphics g = bi.getGraphics();

        for (int i = 0; i < all; i++) {
            Font f = allFonts[i].deriveFont(30.0f);
            g.setFont(f);
            Integer decode = Integer.decode("0x0");
            System.out.println(decode);

//            g.setColor(new Color(Integer.decode("0x0")));
            g.setColor(Color.white);
            g.drawString((i + 1) + ". Welcome to Disney World, " + f.getFontName(), 10, 40 * i + 80);
        }
        ImageIO.write(bi, "png", new File("/tmp/fonts.png"));
    }

    public void paint(Graphics g) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

        Font[] allFonts = ge.getAllFonts();

        for (int i = 0; i < allFonts.length; i++) {
            if (i>10) break;
            Font f = allFonts[i].deriveFont(10.0f);
            g.setFont(f);
            g.setColor(Color.getColor("black"));
//            g.setColor(Color.black);
            g.drawString("Hello, " + f.getName(), 10, 20 * i);

        }
    }
}
