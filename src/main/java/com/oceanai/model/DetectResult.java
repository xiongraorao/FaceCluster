package com.oceanai.model;

public class DetectResult {

  private String error_message;
  private int detect_nums;
  private int dimension;
  private String request_id;
  private int time_used;
  private FaceFeature[] detect;

  public DetectResult(String error_message, int face_nums, int dimension, String request_id,
      int time_used, FaceFeature[] result) {
    this.error_message = error_message;
    this.detect_nums = face_nums;
    this.dimension = dimension;
    this.request_id = request_id;
    this.time_used = time_used;
    this.detect = result;
  }

  public int getDetect_nums() {
    return detect_nums;
  }

  public void setDetect_nums(int detect_nums) {
    this.detect_nums = detect_nums;
  }

  public String getError_message() {
    return error_message;
  }

  public void setError_message(String error_message) {
    this.error_message = error_message;
  }


  public int getDimension() {
    return dimension;
  }

  public void setDimension(int dimension) {
    this.dimension = dimension;
  }

  public String getRequest_id() {
    return request_id;
  }

  public void setRequest_id(String request_id) {
    this.request_id = request_id;
  }

  public int getTime_used() {
    return time_used;
  }

  public void setTime_used(int time_used) {
    this.time_used = time_used;
  }

  public FaceFeature[] getResult() {
    return detect;
  }

  public void setResult(FaceFeature[] result) {
    this.detect = result;
  }
}
