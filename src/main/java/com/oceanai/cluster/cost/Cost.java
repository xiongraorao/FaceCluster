package com.oceanai.cluster.cost;

import java.util.List;

/**
 * .
 *
 * @author Xiong Raorao
 * @since 2018-09-30-16:29
 */
public interface Cost {

  double cost(List<String> groundTruth, List<String> predict);
}
