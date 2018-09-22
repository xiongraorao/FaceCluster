package com.oceanai.cluster;

import java.util.ArrayList;
import java.util.List;

/**
 * .
 *
 * @author Xiong Raorao
 * @since 2018-09-06-15:45
 */
public class Cluster {

  private List<DataPoint> dataPoints = new ArrayList<>(); // 类簇中的样本点
  private String clusterName;

  public List<DataPoint> getDataPoints() {
    return dataPoints;
  }

  public void setDataPoints(List<DataPoint> dataPoints) {
    this.dataPoints = dataPoints;
  }

  public String getClusterName() {
    return clusterName;
  }

  public void setClusterName(String clusterName) {
    this.clusterName = clusterName;
  }
}
