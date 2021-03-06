package com.oceanai.util;

import com.google.gson.Gson;
import com.oceanai.model.DetectResult;
import com.oceanai.model.ExtractResult;
import com.oceanai.model.Face;
import com.oceanai.model.FaceFeature;
import com.oceanai.model.Feature;
import com.oceanai.model.SearchFeature;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;

/**
 * .
 *
 * @author Xiong Raorao
 * @since 2018-09-20-10:44
 */
public class FaceTool {

  private Socket mSocket;
  private ZContext mContext = new ZContext();
  private boolean isConnected;
  private Gson gson = new Gson();

  public FaceTool(String url) {
    mSocket = mContext.createSocket(ZMQ.DEALER);
    isConnected = mSocket.connect(url);
  }

  public void close() {
    mSocket.close();
  }

  /**
   * 发送json请求
   */
  private boolean send(String base64, int minFace) {
    try {
      JSONObject jsonParam = new JSONObject();
      jsonParam.put("api_key", "");
      jsonParam.put("interface", "5");
      jsonParam.put("image_base64", base64);
      jsonParam.put("minface_size", minFace);
      return mSocket.send(jsonParam.toString().getBytes(ZMQ.CHARSET), 0);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  /**
   * 接收处理结果
   */
  private String receiveResult() {
    byte[] reply = mSocket.recv(0);
    String result = new String(reply, ZMQ.CHARSET);
    return result.replace("\u0000", "");
  }

  public List<SearchFeature> detect(String base64) {
    return detect(base64, 80);
  }

  public List<SearchFeature> detect(BufferedImage bufferedImage, String encoding) {
    String base64 = ImageUtil.bufferedImageToBase64(bufferedImage, encoding);
    return detect(base64);
  }

  public List<SearchFeature> detect(BufferedImage bufferedImage, String encoding, int minFace) {
    String base64 = ImageUtil.bufferedImageToBase64(bufferedImage, encoding);
    return detect(base64, minFace);
  }

  public List<SearchFeature> detect(String base64, int minFace) {
    if (!isConnected) {
      mSocket.close();
      throw new RuntimeException("ZeroMQ is not connect ...");
    }
    boolean sendResult = send(base64, minFace);
    List<SearchFeature> searchFeatures = new ArrayList<>();
    if (sendResult) {
      String result = receiveResult();
      System.out.println(result);
      DetectResult detectResult = gson.fromJson(result, DetectResult.class);
      for (int i = 0; i < detectResult.getDetect_nums(); ++i) {
        FaceFeature faceFeature = detectResult.getResult()[i];
        int left = faceFeature.getLeft();
        int top = faceFeature.getTop();
        searchFeatures.add(
            new SearchFeature(top, left, faceFeature.getWidth(), faceFeature.getHeight(),
                faceFeature.getScore(), faceFeature.getLandmark(), faceFeature.getGlasses(),
                faceFeature.getQuality(), faceFeature.getSideFace()));
      }
      return searchFeatures;
    } else {
      return searchFeatures;
    }
  }

  private boolean sendFeatureMsg(Face[] faces) {
    try {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("api_key", "");
      jsonObject.put("interface", "6");
      JSONArray array = new JSONArray();
      for (Face face : faces) {
        JSONObject f = new JSONObject();
        f.put("image_base64", face.getImage_base64());
        //JSONArray arrayLandMark = new JSONArray();
        JSONObject landmark = new JSONObject();
        JSONArray x = new JSONArray();
        for (int i : face.getLandMark().getX()) {
          x.put(i);
        }
        JSONArray y = new JSONArray();
        for (int i : face.getLandMark().getY()) {
          y.put(i);
        }
        landmark.put("x", x);
        landmark.put("y", y);
        f.put("landmark", landmark);
        array.put(f);
      }
      jsonObject.put("faces", array);
      //String json = jsonObject.toString();
      return mSocket.send(jsonObject.toString().getBytes(ZMQ.CHARSET), 0);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  private List<Feature> extract(Face[] faces) {
    if (!isConnected) {
      mSocket.close();
      throw new RuntimeException("ZeroMQ is not connect ...");
    }
    boolean sendResult = sendFeatureMsg(faces);
    if (sendResult) {
      String result = receiveResult();
      ExtractResult extractResult = gson.fromJson(result, ExtractResult.class);
      if (extractResult.getError_message().equals("701")) {
        List<Feature> features = new ArrayList<>(faces.length);
        for (double[] feature : extractResult.getFeature()) {
          features.add(new Feature(feature));
        }
        return features;
      }
    }
    return null;
  }

  public List<Feature> extract(String base64) throws IOException {
    return extract(base64, 80);
  }

  public List<Feature> extract(String base64, int minFace) throws IOException {
    List<SearchFeature> detectResult = detect(base64, minFace);
    if (detectResult == null || detectResult.size() == 0) {
      return null;
    } else {
      Face[] faces = new Face[detectResult.size()];
      for (int i = 0; i < detectResult.size(); i++) {
        SearchFeature f = detectResult.get(i);
        faces[i] = new Face();
        faces[i].setImage_base64(
            getFaceBase64(ImageUtil.base64ToBufferedImage(base64), f.top, f.left, f.width,
                f.height));
        faces[i].setLandMark(f.landMark);
      }
      return extract(faces);
    }
  }

  private String getFaceBase64(BufferedImage bufferedImage, int top, int left, int width,
      int height)
      throws IOException {
    BufferedImage face = bufferedImage.getSubimage(left, top, width, height);
    byte[] bytes = ImageUtil.imageToBytes(face, "jpg");
    Base64.Encoder encoder = Base64.getEncoder();
    return encoder.encodeToString(bytes);
  }

}
