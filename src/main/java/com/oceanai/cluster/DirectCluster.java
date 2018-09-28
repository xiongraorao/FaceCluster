package com.oceanai.cluster;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 直接聚类法.
 *
 * @author Xiong Raorao
 * @since 2018-09-28-15:13
 */
public class DirectCluster {

  public static void main(String[] args) {
    DirectCluster dc = new DirectCluster();
    List<DataPoint> dps = dc.readData("C:\\Users\\xiongraorao\\Desktop\\faces.txt");
    double threshold = 0.6;// 分类依据，如果相似度大于0.75的为一类
    long start = System.currentTimeMillis();
    List<Cluster> finalCluster = dc.startCluster(dps, threshold);
    long latency = System.currentTimeMillis() - start;
    // print
    System.out.println("INFO:====");
    System.out.println("Cluster Number: " + finalCluster.size());
    System.out.println("Total Time: " + latency);
    for (Cluster cluster : finalCluster) {
      System.out.println("====cluster: " + cluster.getClusterName() + "==========");
      List<DataPoint> dataPoints = cluster.getDataPoints();
      dataPoints.forEach(e -> System.out.println(e.dataPointName));
      System.out.println("=========================");
    }
  }

  private List<Cluster> startCluster(List<DataPoint> dps, double threshold) {
    List<Cluster> finalCluster = initialCluster(dps);

    boolean flag = true;
    while (flag) {
      flag = false;
      for (int i = 0; i < finalCluster.size(); i++) {
        for (int j = i + 1; j < finalCluster.size(); j++) {
          if (getDistance(finalCluster.get(i), finalCluster.get(j)) > threshold) {
            // j合并到i中
            finalCluster = mergeCluster(finalCluster, i, j);
            flag = true;
          }
        }
      }
    }
    return finalCluster;
  }

  private List<Cluster> mergeCluster(List<Cluster> finalClusters, int mergeIndexA,
      int mergeIndexB) {
    if (mergeIndexA != mergeIndexB) {
      // 将cluster[mergeIndexB]中的DataPoint加入到 cluster[mergeIndexA]
      Cluster clusterA = finalClusters.get(mergeIndexA);
      Cluster clusterB = finalClusters.get(mergeIndexB);

      List<DataPoint> dpA = clusterA.getDataPoints();
      List<DataPoint> dpB = clusterB.getDataPoints();

      for (DataPoint dp : dpB) {
        DataPoint tempDp = new DataPoint();
        tempDp.setDataPointName(dp.getDataPointName());
        tempDp.setDimensioin(dp.getDimensioin());
        tempDp.setCluster(clusterA);
        dpA.add(tempDp);
      }

      // 重新计算dpA的中心点
      DataPoint mid = new DataPoint();
      double[] d = new double[dpA.get(0).getDimensioin().length];
      for (int i = 0; i < d.length; i++) {
        double t = 0;
        for (int j = 0; j < dpA.size(); j++) {
          t += dpA.get(j).getDimensioin()[i];
        }
        d[i] = t / dpA.size();
      }
      // 归一化
      double sum = 0;
      for (int i = 0; i < d.length; i++) {
        sum += d[i] * d[i];
      }
      for (int i = 0; i < d.length; i++) {
        d[i] = d[i] / sum;
      }
      mid.setDimensioin(d);
      clusterA.setMid(mid);
      clusterA.setDataPoints(dpA);
      finalClusters.remove(mergeIndexB);
    }
    return finalClusters;
  }

  private double getDistance(Cluster c1, Cluster c2) {
    DataPoint d1 = c1.getMid();
    DataPoint d2 = c2.getMid();
    return getDistance(d1, d2);
  }

  private double getDistance(DataPoint dataPoint, DataPoint dataPoint2) {
    double distance = 0;
    double[] dimA = dataPoint.getDimensioin();
    double[] dimB = dataPoint2.getDimensioin();
    if (dimA.length == dimB.length) {
      double mdimA = 0;// dimA的莫
      double mdimB = 0;// dimB的莫
      double proAB = 0;// dimA和dimB的向量积
      for (int i = 0; i < dimA.length; i++) {
        proAB = proAB + dimA[i] * dimB[i];
        mdimA = mdimA + dimA[i] * dimA[i];
        mdimB = mdimB + dimB[i] * dimB[i];
      }
      distance = proAB / (Math.sqrt(mdimA) * Math.sqrt(mdimB));
    }
    return distance;
  }

  private List<Cluster> initialCluster(List<DataPoint> dps) {
    List<Cluster> originalClusters = new ArrayList<>();
    int cnt = 0;
    for (DataPoint dp : dps) {
      Cluster tempCluster = new Cluster();
      tempCluster.setClusterName("Cluster" + cnt++);
      List<DataPoint> tempList = new ArrayList<>();
      tempList.add(dp);
      tempCluster.setDataPoints(tempList);
      tempCluster.setMid(dp);
      dp.setCluster(tempCluster);
      originalClusters.add(tempCluster);
    }
    return originalClusters;
  }


  private List<DataPoint> readData(String filePath) {
    File file = new File(filePath);
    Gson gson = new Gson();
    List<DataPoint> ret = new ArrayList<>();
    try {
      FileReader fr = new FileReader(file);
      BufferedReader br = new BufferedReader(fr);
      String line;
      while ((line = br.readLine()) != null) {
        Face face = gson.fromJson(line, Face.class);
        ret.add(new DataPoint(face.feature, face.path));
      }
      fr.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return ret;
  }

  static class Face {

    String path;
    double[] feature;
    String label;
  }
}
