package com.oceanai;

import com.oceanai.model.SearchFeature;
import com.oceanai.util.FaceTool;
import com.oceanai.util.LogUtil;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * 收集人脸.
 *
 * @author Xiong Raorao
 * @since 2018-09-25-10:25
 */
public class FaceCollect {

  private static final String RTSP61 = "rtsp://admin:123456@192.168.1.61:554/h264/ch1/main/av_stream"; // 门口
  private static final String RTSP62 = "rtsp://admin:123456@192.168.1.62:554/h264/ch1/main/av_stream"; // 大厅
  private static Logger logger = LogUtil.newInstance(GrabThread.class, Level.INFO);

  public static void main(String[] args) {
    ExecutorService service = Executors.newCachedThreadPool();
    BlockingQueue<BufferedImage> b61 = new LinkedBlockingQueue<>();
    BlockingQueue<BufferedImage> b62 = new LinkedBlockingQueue<>();
    service.execute(new GrabThread(RTSP61, b61));
    service.execute(new GrabThread(RTSP62, b62));
    FaceTool tool = new FaceTool("tcp://192.168.1.6:5559");
    String outputPath = "F:\\camera2";
    logger.info("start face collection");
    while (true) {
      try {
        BufferedImage bImg61 = b61.take();
        BufferedImage bImg62 = b62.take();
        List<SearchFeature> searchFeatureList = tool.detect(bImg61, "jpg", 100);
        List<SearchFeature> searchFeatureList2 = tool.detect(bImg62, "jpg", 100);
        if (searchFeatureList.size() != 0) {
          logger.info("start collect face in camera 61");
          for (SearchFeature feature : searchFeatureList) {
            if (feature.quality == 1.0) {
              SearchFeature.Point leftTop = feature.bbox.left_top;
              BufferedImage sub = bImg61
                  .getSubimage(leftTop.x, leftTop.y, feature.width, feature.height);
              String uuid = UUID.randomUUID().toString();
              String path = outputPath + File.separator + "61" + File.separator + uuid + ".jpg";
              String originPath = outputPath + File.separator + "61" + File.separator + uuid + "-origin" + ".jpg";
              ImageIO.write(sub, "jpg", new File(path));
              ImageIO.write(bImg61, "jpg", new File(originPath));
              logger.info("save to " + path + "successfully!");
            }else {
              logger.info("image quality is poor!");
            }
          }
        }

        if (searchFeatureList2.size() != 0) {
          logger.info("start collect face in camera 62");
          for (SearchFeature feature : searchFeatureList2) {
            SearchFeature.Point leftTop = feature.bbox.left_top;
            BufferedImage sub = bImg62
                .getSubimage(leftTop.x, leftTop.y, feature.width, feature.height);
            String uuid = UUID.randomUUID().toString();
            String path = outputPath + File.separator + "62" + File.separator + uuid + ".jpg";
            String originPath = outputPath + File.separator + "62" + File.separator + uuid + "-origin" + ".jpg";
            if (feature.quality == 1.0) {
              ImageIO.write(sub, "jpg", new File(path));
              ImageIO.write(bImg62, "jpg", new File(originPath));
            }
            logger.info("save to " + path + "successfully!");
          }
        }
      } catch (InterruptedException | IOException e) {
        e.printStackTrace();
        break;
      }
    }
  }
}
