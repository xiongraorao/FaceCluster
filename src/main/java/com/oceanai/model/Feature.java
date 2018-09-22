package com.oceanai.model;

import java.util.Arrays;

public class Feature {

  private double[] feature;
  private int dimension;
  private String name;

  public Feature(double[] feature) {
    this.feature = feature;
    this.dimension = feature.length;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getDimension() {
    return dimension;
  }

  public double[] getFeature() {
    return feature;
  }

  public void setFeature(double[] feature) {
    this.feature = feature;
  }

  @Override
  public String toString() {
    return Arrays.toString(feature);
  }
}
