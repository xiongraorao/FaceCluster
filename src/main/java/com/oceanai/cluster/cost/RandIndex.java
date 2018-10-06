package com.oceanai.cluster.cost;

import java.util.List;

/**
 * Rande 指数，简称RI.
 *
 * @author Xiong Raorao
 * @since 2018-10-03-17:34
 */
public class RandIndex extends Cost {

  public RandIndex(List<String> groundTruth, List<String> predict) {
    super(groundTruth, predict);
  }

  @Override
  public double cost() {
    return 2 * (a + d) / (size * (size - 1));
  }
}
