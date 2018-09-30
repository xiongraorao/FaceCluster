package com.oceanai.model;

public class SearchFeature {

  public double score;
  public LandMark landMark;
  public double glasses;
  public double quality;
  public double sideFace;
  public int top;
  public int left;
  public int width;
  public int height;

  public SearchFeature(int top, int left, int width, int height, double score, LandMark landMark,
      double glasses, double quality, double sideFace) {
    this.score = score;
    this.landMark = landMark;
    this.glasses = glasses;
    this.quality = quality;
    this.sideFace = sideFace;
    this.top = top;
    this.left = left;
    this.width = width;
    this.height = height;
  }


}
