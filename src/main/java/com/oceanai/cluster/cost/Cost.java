package com.oceanai.cluster.cost;

import java.util.List;

/**
 * .
 *
 * @author Xiong Raorao
 * @since 2018-09-30-16:29
 */
public abstract class Cost {

  double a;
  double b;
  double c;
  double d;
  int size;
  List<String> groundTruth;
  List<String> predict;

  public Cost(List<String> groundTruth, List<String> predict) {
    this.groundTruth = groundTruth;
    this.predict = predict;
    init(groundTruth, predict);
  }

  public Cost() {
  }

  public abstract double cost();

  public void init(List<String> groundTruth, List<String> predict) {
    this.groundTruth = groundTruth;
    this.predict = predict;
    // 样本两两配对
    size = groundTruth.size();
    for (int i = 0; i < size; i++) {
      for (int j = i + 1; j < size; j++) {
        if (groundTruth.get(i).equals(groundTruth.get(j)) && predict.get(i)
            .equals(predict.get(j))) {
          a++;
        } else if (groundTruth.get(i).equals(groundTruth.get(j)) && !predict.get(i)
            .equals(predict.get(j))) {
          b++;
        } else if (!groundTruth.get(i).equals(groundTruth.get(j)) && predict.get(i)
            .equals(predict.get(j))) {
          c++;
        } else {
          d++;
        }
      }
    }
  }
}
