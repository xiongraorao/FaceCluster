package com.oceanai.cluster.bean;

/**
 * .
 *
 * @author Xiong Raorao
 * @since 2018-09-21-10:07
 */
public class DataPoint {


  private String dataPointName; // 样本点名
  private Cluster cluster; // 样本点所属类簇
  private double vector[]; // 样本点的特征向量
  private int dim; // 样本点的维度

  public DataPoint() {

  }

  public DataPoint(double[] vector, String dataPointName) {
    this.dataPointName = dataPointName;
    this.vector = vector;
    this.dim = vector.length;
  }

  public int getDim() {
    return dim;
  }

  public void setDim(int dim) {
    this.dim = dim;
  }

  public double[] getVector() {
    return vector;
  }

  public void setVector(double[] vector) {
    this.vector = vector;
    this.dim = vector.length;
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
