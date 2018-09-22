package com.oceanai.model;

public class Face {

  private String image_base64;
  private LandMark landMark;

  public String getImage_base64() {
    return image_base64;
  }

  public void setImage_base64(String image_base64) {
    this.image_base64 = image_base64;
  }

  public LandMark getLandMark() {
    return landMark;
  }

  public void setLandMark(LandMark landMark) {
    this.landMark = landMark;
  }

  @Override
  public String toString() {
    return "{ image_base64:" + image_base64 + ", landmark:" + landMark.toString() + "}";
  }

}
