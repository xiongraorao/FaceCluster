package com.oceanai.test;

import com.google.gson.Gson;
import com.oceanai.model.Feature;
import com.oceanai.model.SearchFeature;
import com.oceanai.util.FaceTool;
import com.oceanai.util.ImageUtil;
import java.io.IOException;
import java.util.List;

/**
 * .
 *
 * @author Xiong Raorao
 * @since 2018-09-21-10:44
 */
public class FaceToolTest {

  public static void main(String[] args) throws IOException {
    FaceTool tool = new FaceTool("tcp://192.168.1.11:5559");
    String base64 = ImageUtil.imageToBase64FromFile("F:\\01.jpg", "jpg");
    System.out.println(base64);
    List<SearchFeature> result = tool.detect(base64, 80);
    List<Feature> features = tool.extract(base64, 80);
    Gson gson = new Gson();
    System.out.println(gson.toJson(features));
  }
}
