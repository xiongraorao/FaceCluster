package com.oceanai.cluster.cost;

import java.util.List;

/**
 * Jaccard 系数.
 *
 * @author Xiong Raorao
 * @since 2018-09-30-16:29
 */
public class Jaccard implements Cost {

  @Override
  public double cost(List<String> groundTruth, List<String> predict) {
    int a = 0;
    int b = 0;
    int c = 0;
    int d = 0;
    int idx = 0;
    /**
     * todo
     * 样本对的生成
     * 各种系数的计算。
     */
    return 0;
  }
}
