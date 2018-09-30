package com.oceanai.test;

import com.google.gson.Gson;
import com.oceanai.cluster.HCluster;
import com.oceanai.cluster.bean.DataPoint;
import com.oceanai.util.ClusterUtil;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * get flw features to file.
 *
 * @author Xiong Raorao
 * @since 2018-09-29-16:47
 */
public class LFWFeature {

  public static void main(String[] args) throws IOException {
    HCluster hc = new HCluster();
    long start = System.currentTimeMillis();
    List<DataPoint> dps = ClusterUtil.extractMulti("F:\\secretstar", "tcp://192.168.1.6:5559", 20);
    int latency = (int) ((System.currentTimeMillis() - start) / 1000);
    System.out.println("process time: " + latency + " s");
    FileWriter fw = new FileWriter("F:\\secretstar.txt");
    Gson gson = new Gson();
    for (DataPoint dp : dps) {
      Face face = new Face();
      face.feature = dp.getVector();
      face.path = dp.getDataPointName();
      String s = face.path;
      int a = s.lastIndexOf("\\");
      int c = s.substring(0, a).lastIndexOf("\\");
      face.label = s.substring(c + 1, a);
      fw.write(gson.toJson(face));
      fw.write("\n");
    }
  }

  static class Face {

    String path;
    double[] feature;
    String label;
  }

}
