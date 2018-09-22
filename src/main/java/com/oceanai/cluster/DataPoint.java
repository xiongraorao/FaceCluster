package com.oceanai.cluster;

/**
 * .
 *
 * @author Xiong Raorao
 * @since 2018-09-21-10:07
 */
public class DataPoint {


  String dataPointName; // 样本点名
  Cluster cluster; // 样本点所属类簇
  private double dimensioin[]; // 样本点的维度

  public DataPoint() {

  }

  public DataPoint(double[] dimensioin, String dataPointName) {
    this.dataPointName = dataPointName;
    this.dimensioin = dimensioin;
  }

  public double[] getDimensioin() {
    return dimensioin;
  }

  public void setDimensioin(double[] dimensioin) {
    this.dimensioin = dimensioin;
  }

  public Cluster getCluster() {
    return cluster;
  }

  public void setCluster(Cluster cluster) {
    this.cluster = cluster;
  }

  public String getDataPointName() {
    return dataPointName;
  }

  public void setDataPointName(String dataPointName) {
    this.dataPointName = dataPointName;
  }
}
