package com.nio.ioSocket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

  private static ExecutorService tp = Executors.newCachedThreadPool();
  public static void main(String[] args) {
    ServerSocket echoServer = null;
    Socket socket = null;
    try {
      echoServer = new ServerSocket(8000);
    } catch (IOException e) {
      e.printStackTrace();
    }
    while (true) {
      try {
        socket = echoServer.accept();
        System.out.println(socket.getRemoteSocketAddress() + " connect!");
        tp.execute(new MultiThreadEchoServer.HandlMsg(socket));
      } catch (IOException e) {
        System.out.println(e);
      }

    }
  }
}
