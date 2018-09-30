package com.oceanai.cluster.bean;

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
  private DataPoint mid; // 类簇的中心点

  public DataPoint getMid() {
    return mid;
  }

  public void setMid(DataPoint mid) {
    this.mid = mid;
  }

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
