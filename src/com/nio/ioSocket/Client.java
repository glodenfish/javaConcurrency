package com.nio.ioSocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client {

  private static Socket client = null;
  private static PrintWriter writer = null;
  private static BufferedReader reader = null;

  public static void main(String[] args) {
    try {
      client = new Socket();
      client.connect(new InetSocketAddress(8000));
      writer = new PrintWriter(client.getOutputStream(), true);
      writer.println("hello!");
      writer.flush();

      reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
      System.out.println("from server:" + reader.readLine());
    } catch (IOException e) {
      e.printStackTrace();
    }finally {
      try {
        if (writer != null) {
          writer.close();
        }
        if (reader != null) {
          reader.close();
        }
        if (client != null) {
          client.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

}
