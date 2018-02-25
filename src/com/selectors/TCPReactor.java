package com.selectors;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

public class TCPReactor implements Runnable{
  private  final ServerSocketChannel ssc;
  private  final Selector selector;

  public TCPReactor(int port) throws IOException {
    selector = Selector.open();
    ssc = ServerSocketChannel.open();
    ssc.socket().bind(new InetSocketAddress(port));
    ssc.configureBlocking(false);
    //这里是serversocketchannel注册到selector上
    SelectionKey skey =ssc.register(selector,SelectionKey.OP_ACCEPT);
    skey.attach(new Acceptor(ssc,selector));
  }

  @Override
  public void run() {
    while (!Thread.interrupted()) {
      System.out.println(" wait an event on port:"+ssc.socket().getLocalPort()+" ...");
      try {
        if (selector.select() == 0) {
          continue;
        }
      } catch (IOException e) {
        e.printStackTrace();
        System.out.println("tpc selector.select() ");
      }

      Set<SelectionKey> setKeys = selector.selectedKeys();
      Iterator<SelectionKey> itrKeys = setKeys.iterator();
      while (itrKeys.hasNext()) {
        dispatch(itrKeys.next());
        itrKeys.remove();
      }

    }

  }

  private void dispatch(SelectionKey next) {
    Runnable newEvent = (Runnable) next.attachment();
    if (newEvent != null) {
      newEvent.run();
    }
  }
}
