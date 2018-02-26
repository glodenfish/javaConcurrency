package com.nio.ioSocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.LockSupport;

public class HeavySocketClient {

  public static ExecutorService tp = Executors.newCachedThreadPool();
  public static final int sleep_time = 1000 * 1000 * 1000;
  static class EchoClient implements Runnable{

    @Override
    public void run() {
      Socket client = null;
      PrintWriter os = null;
      BufferedReader is = null;

      try {
        client = new Socket();
        client.connect(new InetSocketAddress(8000));
        os = new PrintWriter(client.getOutputStream());
        is = new BufferedReader(new InputStreamReader(client.getInputStream()));

        os.print("H");
        LockSupport.parkNanos(sleep_time);
        os.print("E");
        LockSupport.parkNanos(sleep_time);
        os.print("L");
        LockSupport.parkNanos(sleep_time);
        os.print("L");
        LockSupport.parkNanos(sleep_time);
        os.print("O");
        LockSupport.parkNanos(sleep_time);
        os.print("!");
        LockSupport.parkNanos(sleep_time);
        os.println();
        os.flush();

        is = new BufferedReader(new InputStreamReader(client.getInputStream()));
        System.out.println("from Server:"+ is.readLine());
      } catch (Exception e) {
        e.printStackTrace();
      }finally {
        try {
          if (is != null) {
            is.close();
          }
          if (os != null) {
            os.close();
          }
          if (client != null) {
            client.close();
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

    }

    public static void main(String[] args) throws IOException {
      EchoClient echoClient= new EchoClient();
      for (int i = 0; i < 10; i++) {
        tp.execute(echoClient);
      }

    }
  }
}
