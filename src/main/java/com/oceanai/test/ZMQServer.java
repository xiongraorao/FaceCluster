package com.oceanai.test;

import org.zeromq.ZMQ;

/**
 * .
 *
 * @author Xiong Raorao
 * @since 2018-09-20-11:02
 */
public class ZMQServer {

  public static void main(String[] args) throws InterruptedException {
    ZMQ.Context context = ZMQ.context(1);
    ZMQ.Socket socket = context.socket(ZMQ.PUB);
    socket.bind("tcp://*:5561");
    Thread.sleep(1000);

    int update_nbr;

    for (update_nbr = 20; update_nbr < 40; update_nbr++) {
      String a =
          "{\"magicNum\":\"CHINSOFT\",\"varName\":\"ZJ_YD_1\",\"varType\":\"5\",\"varValue\":"
              + update_nbr + ",\"varQuality\":\"1111\",\"varTime\":"
              + System.currentTimeMillis() / 1000 + "}";
      socket.send(a.getBytes(), ZMQ.NOBLOCK);
      System.out.println(update_nbr);
      Thread.sleep(1000);
    }

    socket.close();
    context.term();

  }
}
