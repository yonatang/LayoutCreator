package idc.storyalbum.layout.tools;

import com.google.common.collect.Lists;
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
public class FaceVectorCreator {


    static class ImageData {
        String filename;
        int[] faceCords;

        public ImageData(String filename, int... faceCords) {
            this.filename = filename;
            this.faceCords = faceCords;
        }

        public ImageData(String filename, boolean b, int... faceCords) {
            this.filename = filename;
            for (int i = 0; i < faceCords.length; i++) {
                if (i % 4 == 2 || i % 4 == 3) {
                    faceCords[i] += faceCords[i - 2];
                }
            }
            this.faceCords = faceCords;
        }
    }

    static final String ALBUM_ID="MP/72157630418695066";
    static final String IMG_DIR = "/Users/yonatan/Dropbox/Studies/Story Albums/sets/"+ALBUM_ID+"/images/";
    static final String FACESUM_IMG_DIR = "/Users/yonatan/Dropbox/Studies/Story Albums/sets/"+ALBUM_ID+"/faceSum/";
// ImageData - if true is set, than x,y,w,h otherwise x,y,x2,y2

    static List<ImageData> imageDatas = Lists.newArrayList(
        new ImageData("22.jpg",true,1148,656,436,476),
        new ImageData("29.jpg",true,598,90,282,402),
        new ImageData("3.jpg",true,687,780,522,222,293,846,114,156),
        new ImageData("10.jpg",true),
        new ImageData("14.jpg",true,568,508,160,192),
        new ImageData("13.jpg",true),
        new ImageData("46.jpg",true),
        new ImageData("20.jpg",true),
        new ImageData("47.jpg",true,747,372,438,480,101,234,264,276),
        new ImageData("7.jpg",true),
        new ImageData("24.jpg",true),
        new ImageData("30.jpg",true)
    );



//    //SET 72157622641789194
//    static List<ImageData> imageDatas = Lists.newArrayList(
//        new ImageData("cusco_97.jpg",true),
//        new ImageData("cusco_107a.jpg",true, 480,1020,504,552),
//        new ImageData("31.jpg",true, 2493, 972,534,486),
//        new ImageData("114.jpg",true),
//        new ImageData("89.jpg",true, 1798,978,714,312),
//        new ImageData("123.jpg",true,1516,1296,492,432),
//        new ImageData("37.jpg",true),
//        new ImageData("53.jpg",true),
//        new ImageData("mp_70.jpg",true,2469,414,480,486),
//        new ImageData("90.jpg",true),
//        new ImageData("mp_136.jpg",true),
//        new ImageData("mp_36.jpg",true)
//    );

//    //SET 72157604991613315
//    static List<ImageData> imageDatas = Lists.newArrayList(
//            //if true is set, than x,y,w,h otherwise x,y,x2,y2
//            new ImageData("cusco_18.jpg",true),
//            new ImageData("cusco_5.jpg",true,114,143,162,172),
//            new ImageData("cusco_16.jpg",true),
//            new ImageData("mp_35.jpg",true),
//            new ImageData("mp_69.jpg",true),
//            new ImageData("mp_110.jpg",true),
//            new ImageData("mp_27.jpg",true,510,416,214,302),
//            new ImageData("mp_10.jpg",true),
//            new ImageData("mp_21.jpg",true,530,152,322,212),
//            new ImageData("mp_66.jpg",true),
//            new ImageData("mp_118.jpg",true),
//            new ImageData("mp_64.jpg",true)
//    );


//    //SET 72157603658654812
//    static List<ImageData> imageDatas = Lists.newArrayList(
//            new ImageData("80.jpg",true,1872,947,516,623),
//            new ImageData("11.jpg",true,754,639,1211,1142),
//            new ImageData("88a.jpg",true,880,728,1217,1273),
//            new ImageData("69.jpg",true,970,179,779,420,1523,1214,926,765),
//            new ImageData("50.jpg",true,381,165,1216,1018),
//            new ImageData("111.jpg",true,1541,846,264,222),
//            new ImageData("3116760408_b80db8698d_o.jpg",true,1926,666,777,916),
//            new ImageData("18.jpg",true,1665,446,912,1028),
//            new ImageData("95.jpg",true,922,670,599,720,2760,1059,546,570),
//            new ImageData("61.jpg",true,828,1058,369,318,2204,345,416,433),
//            new ImageData("16.jpg",true,760,556,2324,2013),
//            new ImageData("14081097640_2ff9f3d682_o.jpg",true,901,454,599,863)
//    );


//    //SET 72157600312588222
//    static List<ImageData> imageDatas = Lists.newArrayList(
//            new ImageData("0.jpg", 666, 178, 791, 358, /**/ 1042, 247, 1153, 388),
//            new ImageData("33.jpg", 237, 446, 755,755),
//            new ImageData("36.jpg", true, 335, 114, 810, 755),
//            new ImageData("42.jpg", true, 5, 216, 328, 519,/**/ 802, 180, 453, 636),
//            new ImageData("46.jpg", true, 565, 404, 384, 344),
//            new ImageData("18.jpg", true, 479, 105, 542, 611),
//            new ImageData("19.jpg", true, 321, 113, 459, 443    ),
//            new ImageData("23.jpg", true, 668, 130, 442, 587),
//            new ImageData("7.jpg", true, 396, 298, 581, 444),
//            new ImageData("10.jpg", true, 107, 444, 313, 294),
//            new ImageData("15.jpg", true, 362, 400, 522, 281),
//            new ImageData("14779233740_b26bb78ec5_o.jpg", true, 1020, 909, 868, 936),
//            new ImageData("3083902281_7f8aeeca50_o.jpg", true, 83, 386, 900, 679)
//    );


    public static void main(String... args) throws Exception {
        for (ImageData imageData : imageDatas) {
            File file=new File(IMG_DIR,imageData.filename);
            System.out.println(file);
            BufferedImage img=ImageIO.read(file);
            boolean[][] bitmap=new boolean[img.getWidth()][];
            for (int i=0;i<img.getWidth();i++){
                 bitmap[i]=new boolean[img.getHeight()];
                for (int j=0;j<img.getHeight();j++){
                    bitmap[i][j]=false;
                }
            }
            for (int i=0;i<imageData.faceCords.length;i+=4) {
                for (int x=imageData.faceCords[0];x<imageData.faceCords[2];x++){
                    for (int y=imageData.faceCords[1];y<imageData.faceCords[3];y++){
                        bitmap[x][y]=true;
                    }
                }
            }
            List<Double> horVec = new ArrayList<>();
            for (int x = 0; x < img.getWidth(); x++) {
                double s = 0;
                for (int y=0;y<img.getHeight();y++){
                    if (bitmap[x][y]){
                        s++;
                    }
                }
                horVec.add(s);
            }
            List<Double> verVec = new ArrayList<>();
            for (int y=0;y<img.getHeight();y++){
                double s = 0;
                for (int x = 0; x < img.getWidth(); x++) {
                    if (bitmap[x][y]){
                        s++;
                    }
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

            String vecFilename = FilenameUtils.removeExtension(imageData.filename) + ".xml";
            File out = new File(FACESUM_IMG_DIR, vecFilename);
            FileUtils.write(out, sb.toString());
        }
    }

}
