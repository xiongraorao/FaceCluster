package com.oceanai;

import com.oceanai.util.LogUtil;
import java.awt.image.BufferedImage;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
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
  private boolean running = false;
  private BlockingQueue<BufferedImage> bufferedImages;
  private String rtspURL;

  /**
   * 抓图线程的构造函数
   *
   * @param rtspURL 抓图的rtsp地址
   * @param bufferedImages 抓图缓存队列
   */
  public GrabThread(String rtspURL, BlockingQueue<BufferedImage> bufferedImages) {
    this.bufferedImages = bufferedImages;
    this.rtspURL = rtspURL;
    grabber = new FFmpegFrameGrabber(rtspURL);
    grabber.setOption("rtsp_transport", "tcp");
    try {
      grabber.start();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    BlockingQueue<BufferedImage> bufferedImages = new LinkedBlockingQueue<>();
    GrabThread grabThread = new GrabThread("", bufferedImages);
    Thread t = new Thread(grabThread);
    t.start();

  }

  /**
   * 停止grab线程
   */
  public void stop() {
    this.running = false;
  }


  @Override
  public void run() {
    int count = 0;
    long start;
    logger.info("Start to grab frame");
    int nullFrameCount = 0;
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
        bufferedImages.offer(converter.getBufferedImage(frame));
        logger.info("Grab image " + count++ + " , time used " + (System.currentTimeMillis() - start)
            + " ms.");
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

}
