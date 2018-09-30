package com.oceanai.cluster.dbscan;

import com.google.gson.Gson;
import com.oceanai.cluster.bean.F;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 基于密度的聚类方法.
 *
 * 算法：DBScan，基于密度的聚类算法
 *
 * 输入： D：一个包含n个数据的数据集 r：半径参数 minPts：领域密度阈值
 *
 * 输出： 基于密度的聚类集合
 *
 * @author Xiong Raorao
 * @since 2018-09-29-16:49
 */
public class DBScan {

  private double radius;
  private int minPts;

  public DBScan(double radius, int minPts) {
    this.radius = radius;
    this.minPts = minPts;
  }

  public static void main(String[] args) throws IOException {
    DBScan scan = new DBScan(0.4, 4);
    List<Point> points = scan.read("F:\\secretstar.txt");

    long start = System.currentTimeMillis();
    List<Cluster> finalCluster = scan.startCluster(points);
    long latency = System.currentTimeMillis() - start;
    // print
    System.out.println("INFO:====");
    System.out.println("Cluster Number: " + finalCluster.size());
    System.out.println("Total Time: " + latency);
    for (Cluster cluster : finalCluster) {
      List<Point> dataPoints = cluster.getPoints();
      System.out.println(
          "====cluster: " + cluster.getClusterName() + "==========, items count = " + dataPoints
              .size());
      dataPoints.forEach(e -> System.out.println(e.getLabel()));
      System.out.println("=========================");
    }
  }

  public List<Point> read(String filePath) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(filePath));
    List<Point> points = new ArrayList<>();
    String line;
    Gson gson = new Gson();
    while ((line = br.readLine()) != null) {
      F face = gson.fromJson(line, F.class);
      Point p = new Point(face.getFeature(), face.getLabel(), face.getPath());
      points.add(p);
    }
    return points;
  }

  public List<Cluster> startCluster(List<Point> points) {
    int cluster = 1;
    List<Cluster> clusters = new ArrayList<>();
    for (Point p : points) {
      if (!p.getVisit()) {
        p.setVisit(true);
        List<Point> adjacentPoints = getAdjacentPoints(p, points);

        if (adjacentPoints.size() < minPts) {
          p.setNoised(true);
        } else {
          p.setCluster(String.valueOf(cluster));
          Cluster c = new Cluster();
          for (int i = 0; i < adjacentPoints.size(); i++) {
            Point adjacentPoint = adjacentPoints.get(i);
            //only check unvisited point, cause only unvisited have the chance to add new adjacent points
            if (!adjacentPoint.getVisit()) {
              adjacentPoint.setVisit(true);
              List<Point> adjacentAdjacentPoints = getAdjacentPoints(adjacentPoint, points);
              //add point which adjacent points not less than minPts noised
              if (adjacentAdjacentPoints != null && adjacentAdjacentPoints.size() >= minPts) {
                adjacentPoints.addAll(adjacentAdjacentPoints);
              }
            }
            //add point which doest not belong to any cluster
            if (adjacentPoint.getCluster().equals("0")) {
              adjacentPoint.setCluster(String.valueOf(cluster));
              //set point which marked noised before non-noised
              if (adjacentPoint.getNoised()) {
                adjacentPoint.setNoised(false);
              }
            }
          }
          c.setClusterName(String.valueOf(cluster));
          c.setPoints(adjacentPoints);
          clusters.add(c);
          cluster++;
        }
      }
    }
    return clusters;
  }

  private List<Point> getAdjacentPoints(Point centerPoint, List<Point> points) {
    List<Point> adjacentPoints = new ArrayList<>();
    for (Point p : points) {
      //include centerPoint itself
      double distance = getDistance(centerPoint, p);
      if (distance <= radius) {
        adjacentPoints.add(p);
      }
    }
    return adjacentPoints;
  }


  private double getDistance(Point p1, Point p2) {
    double distance = 0;
    double[] dimA = p1.getVector();
    double[] dimB = p2.getVector();
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
    distance = (1 + distance) / 2;
    distance = 1 - distance;
    return distance;
  }

}
