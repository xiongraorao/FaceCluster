package com.oceanai.util;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import javax.imageio.ImageIO;

/**
 * .
 *
 * @author Xiong Raorao
 * @since 2018-09-21-11:01
 */
public class ImageUtil {

  /**
   * 通过文件二进制流的方式读取，对于图像数据，不推荐使用
   */
  @Deprecated
  public static String encodeToBase64FromFile(String imgFile) {

    InputStream in = null;
    byte[] data = null;
    //读取图片字节数组
    try {
      in = new FileInputStream(imgFile);
      data = new byte[in.available()];
      in.read(data);
      in.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    //对字节数组Base64编码
    System.out.println("byte lenth: " + data.length);
    Encoder encoder = Base64.getEncoder();
    return encoder.encodeToString(data);//返回Base64编码过的字节数组字符串
  }


  public static String imageToBase64FromFile(String imageFile, String encoding) throws IOException {
    BufferedImage image = ImageIO.read(new File(imageFile));
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ImageIO.write(image, encoding, baos);
    Encoder encoder = Base64.getEncoder();
    return encoder.encodeToString(baos.toByteArray());
  }

  public static BufferedImage base64ToBufferedImage(String base64) throws IOException {
    Decoder decoder = Base64.getDecoder();
    byte[] bys = decoder.decode(base64);
    return bytesToImage(bys);
  }

  /**
   * Converts an image to byte buffer representing PNG (bytes as they would exist on disk)
   *
   * @param encoding the encoding to be used, one of: png, jpeg, bmp, wbmp, gif
   * @return byte[] representing the image
   * @throws IOException if the bytes[] could not be written
   */
  public static byte[] imageToBytes(BufferedImage image, String encoding) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ImageIO.write(image, encoding, baos);
    return baos.toByteArray();
  }

  /**
   * Converts the provided byte buffer into an BufferedImage
   *
   * @param buf byte[] of an image as it would exist on disk
   */
  public static BufferedImage bytesToImage(byte[] buf) throws IOException {
    ByteArrayInputStream bais = new ByteArrayInputStream(buf);
    return ImageIO.read(bais);
  }

  /**
   * @author WangRupeng
   */
  public static byte[] decodeToPixels(BufferedImage bufferedImage) {
    if (bufferedImage == null) {
      return null;
    }
    return ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
  }

  /**
   * @author WangRupeng
   */
  public static BufferedImage getImageFromArray(byte[] pixels, int width, int height) {
    if (pixels == null || width <= 0 || height <= 0) {
      return null;
    }
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
    byte[] array = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
    //int n=  array.length;
    //int m = pixels.length;
    //System.out.println(array.length);
    System.arraycopy(pixels, 0, array, 0, array.length);
    return image;
  }

  public static BufferedImage resize(BufferedImage image, double factor) {
    int width = (int) (image.getWidth() * factor);
    int height = (int) (image.getHeight() * factor);
    BufferedImage dimg = new BufferedImage(width, height, image.getType());
    Graphics2D g = dimg.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
        RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
    g.drawImage(image, 0, 0, width, height, 0, 0, image.getWidth(), image.getHeight(), null);
    g.dispose();
    return dimg;
  }
}
