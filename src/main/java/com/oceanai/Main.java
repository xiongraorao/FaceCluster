package com.oceanai;

import com.oceanai.util.CVTool;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

/**
 * .
 *
 * @author Xiong Raorao
 * @since 2018-09-06-15:08
 */
public class Main {

  static {
    System.load("D:\\users\\xiongraorao\\IdeaProjects\\cluster\\src\\opencv_java320.dll");
  }

  public static void main(String[] args) {
    Mat mat = Imgcodecs.imread("F:\\01.jpg");

    CVTool.showImage(mat, "test");
  }
}
