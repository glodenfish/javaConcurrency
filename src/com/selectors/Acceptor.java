package com.selectors;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Acceptor implements Runnable {
  private final ServerSocketChannel ssc;
  private final Selector selector;

  public Acceptor(ServerSocketChannel ssc, Selector selector) {
    this.ssc = ssc;
    this.selector = selector;
  }

  @Override
  public void run() {
    try {
      SocketChannel sc = ssc.accept();
      System.out.println("client :"+sc.socket().getRemoteSocketAddress().toString()+ " :is connected!");
      if(sc != null){
        sc.configureBlocking(false);
        //这里socketchannel需要注册到selector上
        SelectionKey tmpKey = sc.register(selector, SelectionKey.OP_READ);
        selector.wakeup();
        tmpKey.attach(new TCPHandler(tmpKey,sc));
      }
    } catch (IOException e) {
      System.out.println("Acceptor class throws a exception: "+ e);
    }

  }
}
