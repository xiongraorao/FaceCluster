package com.oceanai.cluster.cost;


import java.util.List;

/**
 * Jaccard 系数.
 *
 * @author Xiong Raorao
 * @since 2018-09-30-16:29
 */
public class Jaccard extends Cost {


  public Jaccard(List<String> groundTruth, List<String> predict) {
    super(groundTruth, predict);
  }

  @Override
  public double cost() {
    return a / (a + b + c);
  }
}
