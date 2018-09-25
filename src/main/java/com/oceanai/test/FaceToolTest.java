package com.oceanai.test;

import com.google.gson.Gson;
import com.oceanai.model.Feature;
import com.oceanai.model.SearchFeature;
import com.oceanai.util.FaceTool;
import com.oceanai.util.ImageUtil;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
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
    BufferedImage bufferedImage = ImageIO.read(new File("F:\\test.jpg"));
    //System.out.println(base64);
    List<SearchFeature> result = tool.detect(bufferedImage, "jpg");
    //List<Feature> features = tool.extract(base64, 80);
    Gson gson = new Gson();
    System.out.println(gson.toJson(result));
    for (SearchFeature feature : result) {
      SearchFeature.Point leftTop = feature.bbox.left_top;
      BufferedImage sub = bufferedImage
          .getSubimage(leftTop.x, leftTop.y, feature.width, feature.height);
      String path = "F:\\camera" + File.separator + "61" + File.separator + UUID
          .randomUUID().toString() + ".jpg";
      ImageIO.write(sub, "jpg", new File(path));
    }
  }
}
