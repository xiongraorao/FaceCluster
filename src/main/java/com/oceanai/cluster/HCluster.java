package com.oceanai.cluster;

import com.oceanai.model.Feature;
import com.oceanai.util.FaceTool;
import com.oceanai.util.ImageUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * .
 *
 * @author Xiong Raorao
 * @since 2018-09-21-9:59
 */
public class HCluster {

  public static void main(String[] args) {
    HCluster hc = new HCluster();
    List<DataPoint> dp = new ArrayList<>();

    dp = hc.readDataMulti("F:\\faces");

    /*
     * freq代表了聚类的终止条件，判断还有没有距离小于freq的两个类簇，若有则合并后继续迭代，否则终止迭代
     */
    double freq = 0.5;

    List<Cluster> clusters = hc.startCluster(dp, freq);

    System.out.println();
    // 输出聚类的结果，两个类簇中间使用----隔开
    System.out.println();
    System.out.println("结果输出---：");
    for (Cluster cl : clusters) {
      List<DataPoint> tempDps = cl.getDataPoints();
      for (DataPoint tempdp : tempDps) {
        System.out.println(tempdp.getDataPointName());
      }
      System.out.println("----");

      // show image


      // save to disk
      File tmp = new File("F:\\output\\" + cl.getClusterName());
      tmp.mkdir();
      for (DataPoint tempdp : tempDps) {
        try {
          FileInputStream fis = new FileInputStream(new File(tempdp.getDataPointName()));
          FileOutputStream fos = new FileOutputStream(
              tmp.getPath() + File.separator + UUID.randomUUID().toString() + ".jpg");
          byte[] bytes = new byte[fis.available()];
          fis.read(bytes);
          fos.write(bytes);
          fos.close();
          fis.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

  }

  private static List<String> getFiles(String path) {
    List<String> list = new ArrayList<>();
    find(path, list);
    return list;
  }

  private static void find(String path, List<String> list) {
    File file = new File(path);
    if (!file.exists()) {
      System.err.println("file is not exist");
    } else if (file.isFile()) {
      list.add(file.getPath());
    } else if (file.isDirectory()) {
      String[] files = file.list();
      for (String f : files) {
        find(path + File.separator + f, list);
      }
    }
  }

  private List<Cluster> startCluster(List<DataPoint> dp, double freq) {
    // 声明cluster类，存放类名和类簇中含有的样本
    List<Cluster> finalClusters;
    // 初始化类簇，开始时认为每一个样本都是一个类簇并将初始化类簇赋值给最终类簇
    List<Cluster> originalClusters = initialCluster(dp);
    finalClusters = originalClusters;
    // flag为判断标志
    boolean flag = true;
    int it = 1;
    while (flag) {
      System.out.println("第" + it + "次迭代");
      // 临时表量，存放类簇间余弦相似度的最大值
      double max = -1;
      // mergeIndexA和mergeIndexB表示每一次迭代聚类最小的两个类簇，也就是每一次迭代要合并的两个类簇
      int mergeIndexA = 0;
      int mergeIndexB = 0;
      /*
       * 迭代开始，分别去计算每个类簇之间的距离，将距离小的类簇合并
       */
      for (int i = 0; i < finalClusters.size() - 1; i++) {
        for (int j = i + 1; j < finalClusters.size(); j++) {
          // 得到任意的两个类簇
          Cluster clusterA = finalClusters.get(i);
          Cluster clusterB = finalClusters.get(j);
          // 得到这两个类簇中的样本
          List<DataPoint> dataPointsA = clusterA.getDataPoints();
          List<DataPoint> dataPointsB = clusterB.getDataPoints();
          /*
           * 定义临时变量tempDis存储两个类簇的大小，这里采用的计算两个类簇的距离的方法是
           * 得到两个类簇中所有的样本的距离的和除以两个类簇中的样本数量的积，其中两个样本 之间的距离用的是余弦相似度。
           * 注意：这个地方的类簇之间的距离可以 换成其他的计算方法
           */
          double tempDis = 0;
          /*
           * 此处计算距离可以优化，事先一次性将两两样本点之间的余弦距离计算好存放一个MAP中，
           * 这个地方使用的时候直接取出来，就不用每次再去计算了，可节省很多时间。
           * 注意：若是类簇间的距离计算换成了别的方法，也就没有这种优化的说法了
           */
          for (int m = 0; m < dataPointsA.size(); m++) {
            for (int n = 0; n < dataPointsB.size(); n++) {
              tempDis = tempDis + getDistance(dataPointsA.get(m), dataPointsB.get(n));
            }
          }
          tempDis = tempDis / (dataPointsA.size() * dataPointsB.size());
          if (tempDis >= max) {
            max = tempDis;
            mergeIndexA = i;
            mergeIndexB = j;
          }
        }
      }
      /*
       * 若是余弦相似度的最大值都小于给定的阈值， 那说明当前的类簇没有再进一步合并的必要了，
       * 当前的聚类可以作为结果了，否则的话合并余弦相似度值最大的两个类簇，继续进行迭代 注意：这个地方你可以设定别的聚类迭代的结束条件
       */
      if (max < freq) {
        flag = false;
      } else {
        finalClusters = mergeCluster(finalClusters, mergeIndexA, mergeIndexB);
      }
      it++;
    }
    return finalClusters;
  }

  private List<Cluster> mergeCluster(List<Cluster> finalClusters, int mergeIndexA,
      int mergeIndexB) {
    if (mergeIndexA != mergeIndexB) {
      // 将cluster[mergeIndexB]中的DataPoint加入到 cluster[mergeIndexA]
      Cluster clusterA = finalClusters.get(mergeIndexA);
      Cluster clusterB = finalClusters.get(mergeIndexB);

      List<DataPoint> dpA = clusterA.getDataPoints();
      List<DataPoint> dpB = clusterB.getDataPoints();

      for (DataPoint dp : dpB) {
        DataPoint tempDp = new DataPoint();
        tempDp.setDataPointName(dp.getDataPointName());
        tempDp.setDimensioin(dp.getDimensioin());
        tempDp.setCluster(clusterA);
        dpA.add(tempDp);
      }
      clusterA.setDataPoints(dpA);
      finalClusters.remove(mergeIndexB);
    }
    return finalClusters;
  }

  private List<Cluster> initialCluster(List<DataPoint> dps) {
    List<Cluster> originalClusters = new ArrayList<>();
    int cnt = 0;
    for (DataPoint dp : dps) {
      Cluster tempCluster = new Cluster();
      tempCluster.setClusterName("Cluster" + cnt++);
      List<DataPoint> tempList = new ArrayList<>();
      tempList.add(dp);
      tempCluster.setDataPoints(tempList);
      dp.setCluster(tempCluster);
      originalClusters.add(tempCluster);
    }
    return originalClusters;
  }

  private List<DataPoint> readData(String dir) throws IOException {
    FaceTool faceTool = new FaceTool("tcp://192.168.1.6:5559");
    List<String> files = getFiles(dir);
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

  private double getDistance(DataPoint dataPoint, DataPoint dataPoint2) {
    double distance = 0;
    double[] dimA = dataPoint.getDimensioin();
    double[] dimB = dataPoint2.getDimensioin();
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
    return distance;
  }

  private List<DataPoint> readDataMulti(String dir) {
    return readDataMulti(dir, 10);
  }

  private List<DataPoint> readDataMulti(String dir, int threadNum) {
    List<String> files = getFiles(dir);
    List<DataPoint> res = new ArrayList<>();
    ExecutorService executors = Executors.newFixedThreadPool(threadNum);
    List<FaceTool> tools = new ArrayList<>();
    for (int i = 0; i < threadNum + 1; i++) {
      FaceTool tool = new FaceTool("tcp://192.168.1.6:5559");
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
      DataPoint dp = new DataPoint(feature.getFeature(), feature.getName()); // 文件名作为dp的name
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
