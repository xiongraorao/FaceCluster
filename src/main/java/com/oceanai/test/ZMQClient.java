package com.oceanai.test;

import org.zeromq.ZMQ;

/**
 * .
 *
 * @author Xiong Raorao
 * @since 2018-09-20-11:29
 */
public class ZMQClient {

  public static void main(String[] args) {
    ZMQ.Context context = ZMQ.context(1);
    ZMQ.Socket socket = context.socket(ZMQ.SUB);
    socket.connect("tcp://localhost:5561");

    //设置订阅条件"setsockopt"
    socket.subscribe("".getBytes());
    int update_nbr = 0;
    while (true) {
      byte[] stringValue = socket.recv(0);

      String string = new String(stringValue);

      update_nbr++;
      System.out.println("Received " + update_nbr + " updates. :" + string);


    }

  }

}
