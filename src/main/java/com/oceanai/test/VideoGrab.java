package com.oceanai.test;

import com.oceanai.GrabThread;
import com.oceanai.model.SearchFeature;
import com.oceanai.util.FaceTool;
import com.oceanai.util.LogUtil;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * .
 *
 * @author Xiong Raorao
 * @since 2018-09-25-14:44
 */
public class VideoGrab {

  private static Logger logger = LogUtil.newInstance(GrabThread.class, Level.INFO);

  public static void main(String[] args) {
    BlockingQueue<BufferedImage> bufferedImages = new LinkedBlockingQueue<>();
    GrabThread grabThread = new GrabThread("F:\\secretstar.mkv", bufferedImages);
    FaceTool tool = new FaceTool("tcp://192.168.1.6:5559");
    Thread t = new Thread(grabThread);
    t.start();
    int count = 0;
    while (true) {
      try {
        BufferedImage bufferedImage = bufferedImages.take();
        List<SearchFeature> searchFeatureList = tool.detect(bufferedImage, "jpg", 100);
        if (searchFeatureList.size() != 0) {
          logger.info("start collect face in camera 61");
          for (SearchFeature feature : searchFeatureList) {
            SearchFeature.Point leftTop = feature.bbox.left_top;
            BufferedImage sub = bufferedImage
                .getSubimage(leftTop.x, leftTop.y, feature.width, feature.height);
            String path = "F:\\secretstar\\" + String.format("%010d", count++) + ".jpg";
            ImageIO.write(sub, "jpg", new File(path));
            logger.info("save to " + path + "successfully!");
          }
        }
      } catch (InterruptedException | IOException e) {
        e.printStackTrace();
      }
    }
  }

}
