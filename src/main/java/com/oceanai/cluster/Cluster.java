package com.oceanai.cluster;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

/**
 * .
 *
 * @author Xiong Raorao
 * @since 2018-09-06-15:45
 */
public class Cluster {

  static {
    System.load("D:\\users\\xiongraorao\\IdeaProjects\\cluster\\src\\opencv_java320.dll");
  }

  public static void main(String[] args) {
    Mat src = Imgcodecs.imread("F:\\sunset.jpg");
  }
}
