package com.oceanai.util;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.opencv.core.Mat;
import org.opencv.features2d.FlannBasedMatcher;

/**
 * java显示图片.
 *
 * @author Xiong Raorao
 * @since 2018-09-06-16:04
 */
public class CVTool {


  private CVTool() {

  }

  public static void flann(double[][] dataset, int n) {
    FlannBasedMatcher matcher = new FlannBasedMatcher();
    //matcher.match();
  }


  public static void showImage(Mat mat, String windowsName) {
    BufferedImage image = toBufferedImage(mat);
    showIamge0(image, image.getWidth(), image.getHeight(), windowsName);
  }


  private static void showIamge0(BufferedImage image, int width, int height, String windowName) {
    JFrame frame = new JFrame(windowName);
    frame.setBounds(600, 300, width + 50, height + 50);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().setLayout(null);

    JLabel label = new JLabel();
    label.setBounds(0, 0, width, height);
    frame.getContentPane().add(label);

    label.setIcon(new ImageIcon(image));
    frame.setVisible(true);

  }

  private static BufferedImage toBufferedImage(Mat matrix) {
    int type = BufferedImage.TYPE_BYTE_GRAY;
    if (matrix.channels() > 1) {
      type = BufferedImage.TYPE_3BYTE_BGR;
    }
    int bufferSize = matrix.channels() * matrix.cols() * matrix.rows();
    byte[] buffer = new byte[bufferSize];
    matrix.get(0, 0, buffer); // 获取所有的像素点
    BufferedImage image = new BufferedImage(matrix.cols(), matrix.rows(), type);
    final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
    System.arraycopy(buffer, 0, targetPixels, 0, buffer.length);
    return image;
  }
}
