package com.oceanai.util;

import com.oceanai.cluster.bean.DataPoint;

/**
 * 距离计算方式.
 *
 * @author Xiong Raorao
 * @since 2018-09-30-10:58
 */
public class DistanceUtil {

  /**
   * 闵可夫斯基距离，Lp范数
   *
   * @param dp1 point 1
   * @param dp2 point 2
   * @return distance
   */
  public static double minkowski(DataPoint dp1, DataPoint dp2, int p) {
    if (dp1.getDim() != dp1.getDim()) {
      return -1;
    } else {
      double sum = 0;
      double[] v1 = dp1.getVector();
      double[] v2 = dp2.getVector();
      for (int i = 0; i < dp1.getDim(); i++) {
        sum += Math.pow(Math.abs(v1[i] - v2[i]), p);
      }
      return Math.pow(sum, 1 / p);
    }
  }

  /**
   * 欧式距离
   *
   * @param dp1 point 1
   * @param dp2 point 2
   * @return distance
   */
  public static double euclidean(DataPoint dp1, DataPoint dp2) {
    return minkowski(dp1, dp2, 2);
  }

  /**
   * 曼哈顿距离
   *
   * @param dp1 point 1
   * @param dp2 point 2
   * @return distance
   */
  public static double manhattan(DataPoint dp1, DataPoint dp2) {
    return minkowski(dp1, dp2, 1);
  }

  /**
   * 余弦距离（归一化到0-1之间，值越大，表示距离越近）
   *
   * @param dp1 point 1
   * @param dp2 point 2
   * @return similarity
   */
  public static double cosine(DataPoint dp1, DataPoint dp2) {
    if (dp1.getDim() != dp2.getDim()) {
      return -1;
    } else {
      double distance = 0;
      double[] dimA = dp1.getVector();
      double[] dimB = dp2.getVector();
      if (dimA.length == dimB.length) {
        double mdimA = 0;// dimA的莫
        double mdimB = 0;// dimB的莫
        double proAB = 0;// dimA和dimB的向量积
        for (int i = 0; i < dimA.length; i++) {
          proAB = proAB + dimA[i] * dimB[i];
          mdimA = mdimA + dimA[i] * dimA[i];
          mdimB = mdimB + dimB[i] * dimB[i];
        }
        distance = proAB / (Math.sqrt(mdimA) * Math.sqrt(mdimB));
      }
      distance = (1 + distance) / 2; // 归一化到0-1之间
      return distance;
    }
  }

}
