package com.oceanai.cluster.dbscan;

/**
 * .
 *
 * @author Xiong Raorao
 * @since 2018-09-29-16:52
 */
public class Point {

  private double x;
  private double y;
  private double[] vector;
  private int dim;
  private boolean isVisit;
  private String cluster;
  private boolean isNoised;
  private String label;
  private String path;

  public Point(double[] vector, String label, String path) {
    this.vector = vector;
    this.label = label;
    this.path = path;
    this.dim = vector.length;
    this.cluster = String.valueOf(0);
  }

  public Point(double x, double y) {
    this.x = x;
    this.y = y;
    this.isVisit = false;
    this.cluster = String.valueOf(0);
    this.isNoised = false;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public int getDim() {
    return dim;
  }

  public double[] getVector() {
    return vector;
  }

  public void setVector(double[] vector) {
    this.vector = vector;
  }

  public double getDistance(Point point) {
    return Math.sqrt((x - point.x) * (x - point.x) + (y - point.y) * (y - point.y));
  }

  public double getX() {
    return x;
  }

  public void setX(double x) {
    this.x = x;
  }

  public double getY() {
    return y;
  }

  public void setY(double y) {
    this.y = y;
  }

  public boolean getVisit() {
    return isVisit;
  }

  public void setVisit(boolean isVisit) {
    this.isVisit = isVisit;
  }

  public String getCluster() {
    return cluster;
  }

  public void setCluster(String cluster) {
    this.cluster = cluster;
  }

  public boolean getNoised() {
    return this.isNoised;
  }

  public void setNoised(boolean isNoised) {
    this.isNoised = isNoised;
  }

  @Override
  public String toString() {
    return x + " " + y + " " + cluster + " " + (isNoised ? 1 : 0);
  }
}
