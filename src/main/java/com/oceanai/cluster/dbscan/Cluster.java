package com.oceanai.cluster.dbscan;

import java.util.ArrayList;
import java.util.List;

/**
 * .
 *
 * @author Xiong Raorao
 * @since 2018-09-29-17:45
 */
public class Cluster {

  private String clusterName;
  private List<Point> points = new ArrayList<>();

  public String getClusterName() {
    return clusterName;
  }

  public void setClusterName(String clusterName) {
    this.clusterName = clusterName;
  }

  public List<Point> getPoints() {
    return points;
  }

  public void setPoints(List<Point> points) {
    this.points = points;
  }
}
