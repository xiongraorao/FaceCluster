package com.oceanai.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * .
 *
 * @author Xiong Raorao
 * @since 2018-09-30-10:03
 */
public class FileUtil {

  /**
   * copy files based on byte stream
   *
   * @param srcPath source file path
   * @param dstPath destination file path
   */
  public static void copy(String srcPath, String dstPath) throws IOException {
    FileInputStream ins = new FileInputStream(srcPath);
    FileOutputStream out = new FileOutputStream(dstPath);
    byte[] b = new byte[1024];
    int n;
    while ((n = ins.read(b)) != -1) {
      out.write(b, 0, n);
    }
    ins.close();
    out.close();
  }

  /**
   * copy files based on channels(NIO)
   *
   * @param srcPath source file path
   * @param dstPath destination file path
   */
  public static void copyByChannel(String srcPath, String dstPath)
      throws IOException {
    FileChannel inputChannel = null;
    FileChannel outputChannel = null;
    inputChannel = new FileInputStream(new File(srcPath)).getChannel();
    outputChannel = new FileOutputStream(new File(dstPath)).getChannel();
    outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
    inputChannel.close();
    outputChannel.close();
  }

  public static List<String> listAllFiles(String path) {
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
}
