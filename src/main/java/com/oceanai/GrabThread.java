package com.oceanai;

import com.oceanai.util.LogUtil;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.Java2DFrameConverter;

/**
 * .
 *
 * @author Xiong Raorao
 * @since 2018-09-22-11:05
 */
public class GrabThread implements Runnable {

  private Logger logger = LogUtil.newInstance(GrabThread.class, Level.INFO);
  private FFmpegFrameGrabber grabber;
  private Java2DFrameConverter converter = new Java2DFrameConverter();
  private boolean running = true;
  private BlockingQueue<BufferedImage> bufferedImages;
  private String rtspURL;
  private float freq; // 抓帧的频率， 默认1帧每秒

  /**
   * 抓图线程的构造函数
   *
   * @param rtspURL 抓图的rtsp地址
   * @param bufferedImages 抓图缓存队列
   */
  public GrabThread(String rtspURL, BlockingQueue<BufferedImage> bufferedImages, float freq) {
    this.bufferedImages = bufferedImages;
    this.rtspURL = rtspURL;
    grabber = new FFmpegFrameGrabber(rtspURL);
    grabber.setOption("rtsp_transport", "tcp");
    this.freq = freq;
    try {
      grabber.start();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public GrabThread(String rtspURL, BlockingQueue<BufferedImage> bufferedImages) {
    this(rtspURL, bufferedImages, 1.0f);
  }

  public static void main(String[] args) {
    BlockingQueue<BufferedImage> bufferedImages = new LinkedBlockingQueue<>();
    BlockingQueue<BufferedImage> bufferedImages2 = new LinkedBlockingQueue<>();
    GrabThread grabThread = new GrabThread(
        "rtsp://admin:123456@192.168.1.62:554/h264/ch1/main/av_stream", bufferedImages);
    GrabThread grabThread2 = new GrabThread(
        "rtsp://admin:123456@192.168.1.61:554/h264/ch1/main/av_stream", bufferedImages2);
    Thread t = new Thread(grabThread);
    t.start();
    Thread t2 = new Thread(grabThread2);
    t2.start();
    while (true) {
      try {
        BufferedImage bufferedImage = bufferedImages.take();
        BufferedImage bufferedImage2 = bufferedImages2.take();
        ImageIO.write(bufferedImage, "jpg",
            new File("F:\\camera\\62\\" + UUID.randomUUID().toString() + ".jpg"));
        ImageIO.write(bufferedImage2, "jpg",
            new File("F:\\camera\\61\\" + UUID.randomUUID().toString() + ".jpg"));
      } catch (InterruptedException | IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * 停止grab线程
   */
  public void stop() {
    this.running = false;
  }


  @Override
  public void run() {
    long count = 0;
    long start;
    logger.info("Start to grab frame");
    try {
      while (running) {
        Frame frame;
        start = System.currentTimeMillis();
        if ((frame = grabber.grabImage()) == null) { //内存泄漏
          continue;
        }
        if (bufferedImages == null) {
          logger.info("BufferedImageList hasn't been init!");
          throw new NullPointerException();
        }
        if (count % (int) (25 / freq) == 0) {
          bufferedImages.offer(converter.getBufferedImage(frame));
          logger.info(
              "Grab image " + count / (int) (25 / freq) + " , time used " + (
                  System.currentTimeMillis() - start) + " ms.");
        }
        count++;
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

}
