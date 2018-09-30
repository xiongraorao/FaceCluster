package com.oceanai.util;


import com.google.gson.Gson;
import com.oceanai.cluster.bean.DataPoint;
import com.oceanai.cluster.bean.F;
import com.oceanai.model.Feature;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 聚类的相关工具类.
 *
 * @author Xiong Raorao
 * @since 2018-09-30-11:21
 */
public class ClusterUtil {

  public static List<DataPoint> extractData(String dir, String serverUrl) throws IOException {
    FaceTool faceTool = new FaceTool(serverUrl);
    List<String> files = FileUtil.listAllFiles(dir);
    List<DataPoint> res = new ArrayList<>();
    for (String file : files) {
      List<Feature> f = faceTool.extract(ImageUtil.imageToBase64FromFile(file, "jpg"));
      if (f != null) {
        res.add(new DataPoint(f.get(0).getFeature(), file));
      }
    }
    faceTool.close();
    return res;
  }

  public static List<DataPoint> extractMulti(String dir, String serverUrl) {
    return extractMulti(dir, serverUrl, 10);
  }

  public static List<DataPoint> extractMulti(String dir, String serverUrl, int threadNum) {
    List<String> files = FileUtil.listAllFiles(dir);
    List<DataPoint> res = new ArrayList<>();
    ExecutorService executors = Executors.newFixedThreadPool(threadNum);
    List<FaceTool> tools = new ArrayList<>();
    for (int i = 0; i < threadNum + 1; i++) {
      FaceTool tool = new FaceTool(serverUrl);
      tools.add(tool);
    }

    int sections = files.size() / threadNum;
    CountDownLatch countDownLatch = new CountDownLatch(threadNum + 1);
    List<Feature> features = new ArrayList<>();
    List<Future<List<Feature>>> futures = new ArrayList<>();
    for (int i = 0; i < threadNum + 1; i++) {
      Future<List<Feature>> future = executors
          .submit(new ExtractFeature(tools.get(i), files.subList(i * sections,
              (i + 1) * sections > files.size() ? files.size() : (i + 1) * sections)));
      futures.add(future);
      countDownLatch.countDown();
    }
    try {
      countDownLatch.await();
      for (Future<List<Feature>> future : futures) {
        features.addAll(future.get());
      }
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }

    for (Feature feature : features) {
      DataPoint dp = new DataPoint(feature.getFeature(),
          System.currentTimeMillis() + "-" + feature.getName()); // 时间戳+文件名作为dp的name
      res.add(dp);
    }
    // close
    for (FaceTool tool : tools) {
      tool.close();
    }
    executors.shutdown();
    System.out.println("read data success!");
    return res;
  }

  /**
   * read data sets from file
   *
   * @param filePath data file path
   * @return data sets collection
   */
  public static List<DataPoint> readData(String filePath) {
    File file = new File(filePath);
    Gson gson = new Gson();
    List<DataPoint> ret = new ArrayList<>();
    try {
      FileReader fr = new FileReader(file);
      BufferedReader br = new BufferedReader(fr);
      String line;
      while ((line = br.readLine()) != null) {
        F face = gson.fromJson(line, F.class);
        ret.add(new DataPoint(face.getFeature(), face.getPath()));
      }
      fr.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return ret;
  }

  private static class ExtractFeature implements Callable<List<Feature>> {

    private FaceTool tool;
    private List<String> files;

    public ExtractFeature(FaceTool tool, List<String> files) {
      this.tool = tool;
      this.files = files;
    }

    @Override
    public List<Feature> call() throws Exception {
      List<Feature> res = new ArrayList<>();
      for (String file : files) {
        List<Feature> features = tool.extract(ImageUtil.imageToBase64FromFile(file, "jpg"));
        if (features != null) {
          features.get(0).setName(file);
          res.add(features.get(0));
        }
      }
      return res;
    }
  }
}
