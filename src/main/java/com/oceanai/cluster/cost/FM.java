package com.oceanai.cluster.cost;

import java.util.List;

/**
 * FM指数.
 *
 * @author Xiong Raorao
 * @since 2018-10-03-17:33
 */
public class FM extends Cost {

  public FM(List<String> groundTruth, List<String> predict) {
    super(groundTruth, predict);
  }

  @Override
  public double cost() {
    return Math.sqrt(a / (a + b) * a / (a + c));
  }
}
