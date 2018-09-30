package com.oceanai.test;

import com.google.gson.Gson;
import com.oceanai.model.SearchFeature;
import com.oceanai.util.FaceTool;
import com.oceanai.util.ImageUtil;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * .
 *
 * @author Xiong Raorao
 * @since 2018-09-21-10:44
 */
public class FaceToolTest {

  public static void main(String[] args) throws IOException {
    FaceTool tool = new FaceTool("tcp://192.168.1.6:5559");
    //String base64 = ImageUtil.imageToBase64FromFile("F:\\test.jpg", "jpg");
    BufferedImage bufferedImage = ImageIO
        .read(new File("F:\\camera\\b0f707d8-3fb0-4950-a764-80e5b767ba57-origin.jpg"));
    //System.out.println(base64);
    List<SearchFeature> result = tool.detect(bufferedImage, "jpg");
    //List<Feature> features = tool.extract(base64, 80);
    Gson gson = new Gson();
    System.out.println(gson.toJson(result));
    for (SearchFeature feature : result) {
      System.out
          .println("origin image: " + bufferedImage.getHeight() + ", " + bufferedImage.getWidth());
      BufferedImage sub = ImageUtil
          .subImage(bufferedImage, feature.left, feature.top, feature.width - 5000, feature.height);
      String path = "F:\\camera" + File.separator + "tt" + ".jpg";
      if (sub != null) {
        ImageIO.write(sub, "jpg", new File(path));
      }
    }
  }
}
