package com.selectors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

  public static void main(String[] args) {
    Socket socket=null;
//      System.out.println("connection to host:"+args[0]+" and port:"+args[1]);
    try {
      socket = new Socket("localhost", 2000);
      System.out.println("connected to localhost");

      PrintWriter out = new PrintWriter(socket.getOutputStream());
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

      String input="";

      while ((input = stdIn.readLine()) != null) {
        out.write(input);
        out.flush();

        if (input.equals("exit")) {
          break;
        }
        System.out.println("server:"+in.readLine());
      }
      socket.close();
    }catch (UnknownHostException e) {
      e.printStackTrace();
      System.out.println("client UnknownHostException !");
    }catch (IOException e) {
      e.printStackTrace();
      System.out.println("client I/O is failed!");
    }
  }

}
