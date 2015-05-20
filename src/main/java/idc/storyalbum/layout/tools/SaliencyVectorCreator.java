package idc.storyalbum.layout.tools;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by yonatan on 20/5/2015.
 */
public class SaliencyVectorCreator {

    static final String IMG_DIR = "/Users/yonatan/Dropbox/Studies/Story Albums/sets/Zoo/72157600312588222/images/";
    static final String SAL_IMG_DIR = "/Users/yonatan/Dropbox/Studies/Story Albums/sets/Zoo/72157600312588222/saliency/";
    static final String SALSUM_IMG_DIR = "/Users/yonatan/Dropbox/Studies/Story Albums/sets/Zoo/72157600312588222/saliencySum/";

    public static void main(String... args) throws Exception {
        File salDir = new File(SAL_IMG_DIR);
        File imgDir = new File(IMG_DIR);
        Collection<File> files = FileUtils.listFiles(salDir, new String[]{"jpg"}, false);
        for (File salFile : files) {
            BufferedImage salImg = ImageIO.read(salFile);
            String imgFilename = salFile.getName().substring(2);
            System.out.println(imgFilename);
            File imgFile = new File(imgDir, imgFilename);
            BufferedImage img = ImageIO.read(imgFile);

            BufferedImage scaledSal = getScaledImage(salImg, img.getWidth(), img.getHeight());
            List<Double> horVec = new ArrayList<>();
            for (int x = 0; x < scaledSal.getWidth(); x++) {
                double s = 0;
                double[] pixels = scaledSal.getData().getPixels(x, 0, 1, scaledSal.getHeight(), (double[]) null);
                for (double pixel : pixels) {
                    s += pixel;
                }
                horVec.add(s);
            }

            List<Double> verVec = new ArrayList<>();
            for (int y = 0; y < scaledSal.getHeight(); y++) {
                double s = 0;
                double[] pixels = scaledSal.getData().getPixels(0, y, scaledSal.getWidth(), 1, (double[]) null);
                for (double pixel : pixels) {
                    s += pixel;
                }
                verVec.add(s);
            }
            StringBuilder sb = new StringBuilder();
            sb.append("<dataTypes.SalientSum>\n" +
                    "  <horizontalVector>\n");
            for (Double aDouble : horVec) {
                sb.append("    <double>").append(aDouble).append("</double>\n");
            }
            sb.append("  </horizontalVector>\n").append("  <verticalVector>\n");
            for (Double aDouble : verVec) {
                sb.append("    <double>").append(aDouble).append("</double>\n");
            }
            sb.append("  </verticalVector>\n").append("</dataTypes.SalientSum>");

            String vecFilename = FilenameUtils.removeExtension(imgFilename) + ".xml";
            File out = new File(SALSUM_IMG_DIR, vecFilename);
            FileUtils.write(out, sb.toString());
        }


    }


    private static BufferedImage getScaledImage(BufferedImage image, int newWidth, int newHeight) {
        BufferedImage scaledImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = scaledImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(image, 0, 0, newWidth, newHeight, 0, 0, image.getWidth(), image.getHeight(), null);
        g.dispose();
        return scaledImage;
    }
}
