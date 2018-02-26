package com.nio.ioSocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadEchoServer{

  static class HandlMsg implements Runnable{
    Socket clientSocket;

    public HandlMsg(Socket clientSocket) {
      this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
      BufferedReader is = null;
      PrintWriter os = null;

      try {
        is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        os = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()),true);

        String inputLine = null ;
        long b = System.currentTimeMillis();
        while ((inputLine = is.readLine()) != null) {
          os.println(inputLine);
        }
        long end = System.currentTimeMillis();
        System.out.println("spend:"+ (end-b)+"ms");
      } catch (IOException e) {
        e.printStackTrace();
      }finally {
        try {
          if (is != null) {
            is.close();
          }
          if (os != null) {
            os.close();
          }
          clientSocket.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

}
