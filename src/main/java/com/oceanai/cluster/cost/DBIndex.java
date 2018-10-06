package com.oceanai.cluster.cost;

import com.oceanai.cluster.bean.Cluster;
import com.oceanai.cluster.bean.DataPoint;
import com.oceanai.util.DistanceUtil;
import java.util.ArrayList;
import java.util.List;

/**
 * DB指数.
 *
 * @author Xiong Raorao
 * @since 2018-10-03-17:44
 */
public class DBIndex {

  private List<DataPoint> dps;
  private List<Cluster> clusters;
  private int size;

  public DBIndex(List<Cluster> clusters) {
    this.clusters = clusters;
    dps = new ArrayList<>();
    for (Cluster c : clusters) {
      dps.addAll(c.getDataPoints());
    }
    size = dps.size();
  }

  public double cost() {

    int clusterSize = clusters.size();
    double[] avg = new double[clusterSize];
    for (int i = 0; i < clusterSize; i++) {
      Cluster c = clusters.get(i);
      List<DataPoint> dps = c.getDataPoints();
      for (int j = 0; j < dps.size(); j++) {
        for (int k = j + 1; k < dps.size(); k++) {
          avg[i] += DistanceUtil.euclidean(dps.get(j), dps.get(k));
        }
      }
      avg[i] *= 2 / (dps.size() * (dps.size() - 1));
    }
    double DBI = 0;
    for (int i = 0; i < clusterSize; i++) {
      double tmp = Double.MIN_VALUE;
      for (int j = i + 1; j < clusterSize; j++) {
        double[] clusterDist = DistanceUtil.clusterDistance(clusters.get(i), clusters.get(j));
        double t = (avg[i] + avg[j]) / clusterDist[0];
        if (tmp < t) {
          tmp = t;
        }
      }
      DBI += tmp;
    }
    DBI = DBI / clusterSize;
    return DBI;
  }
}
