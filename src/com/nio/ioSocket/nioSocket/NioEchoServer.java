package com.nio.ioSocket.nioSocket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NioEchoServer {
  private Selector selector;
  private ExecutorService tp = Executors.newCachedThreadPool();

  public static final Map<Socket, Long> time_state = new HashMap<>(10240);

  private void startServer() throws Exception {
    selector = SelectorProvider.provider().openSelector();
    ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
    serverSocketChannel.configureBlocking(false);

    serverSocketChannel.bind(new InetSocketAddress(8000));
    SelectionKey acceptKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

    for (;;) {
      selector.select();
      Set<SelectionKey> readyKeys = selector.selectedKeys();
      Iterator<SelectionKey> i = readyKeys.iterator();
      long e = 0;
      while (i.hasNext()) {
        SelectionKey sk =  i.next();
        i.remove();

        if (sk.isAcceptable()) {
          doAccept(sk);
        } else if (sk.isValid() && sk.isReadable()) {
          if (!time_state.containsKey(((SocketChannel) sk.channel()).socket())) {
            time_state.put(((SocketChannel) sk.channel()).socket(), System.currentTimeMillis());
          }
          doRead(sk);
        } else if (sk.isValid() && sk.isWritable()) {
          doWrite(sk);
          e = System.currentTimeMillis();
          long b = time_state.remove(((SocketChannel) sk.channel()).socket());
          System.out.println("spend" + (e - b) + "ms");
        }
      }
    }
  }

  private void doWrite(SelectionKey sk) {
    SocketChannel channel = (SocketChannel) sk.channel();
    EchoClient client = (EchoClient) sk.attachment();
    LinkedList<ByteBuffer> outq= client.getOutq();

    ByteBuffer bb = outq.getLast();

    try {
      int len = channel.write(bb);
      if (len == -1) {
        disconnect(sk);
        return;
      }
      if (bb.remaining() == 0) {
        outq.removeLast();
      }
    } catch (IOException e) {
      System.out.println("Fail to write to client");
      e.printStackTrace();
      disconnect(sk);
    }

    if (outq.size() == 0) {
      sk.interestOps(SelectionKey.OP_READ);
    }
  }

  private void doRead(SelectionKey sk) {
    SocketChannel channel = (SocketChannel) sk.channel();
    ByteBuffer bb = ByteBuffer.allocate(8192);
    int len;

    try {
      len = channel.read(bb);
      if (len < 0) {
        disconnect(sk);
        return;
      }
    } catch (IOException e) {
      System.out.println("Failed to read from client");
      e.printStackTrace();
      disconnect(sk);
      return;
    }
    bb.flip();
    tp.execute(new HandleMsg(sk,bb));
  }

  private void disconnect(SelectionKey sk) {
    SocketChannel sc = (SocketChannel) sk.channel();
    if (sc.isConnected()) {
      try {
        sc.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }


  private void doAccept(SelectionKey sk) {
    ServerSocketChannel server= (ServerSocketChannel) sk.channel();
    SocketChannel client;

    try {
      client = server.accept();
      client.configureBlocking(false);

      SelectionKey clientKey = client.register(selector, SelectionKey.OP_READ);
      EchoClient echoClient = new EchoClient();
      clientKey.attach(echoClient);
      InetAddress clientAdress = client.socket().getInetAddress();
      System.out.println("Accept connecton from " + clientAdress.getHostAddress());

    } catch (Exception e) {
      System.out.println("Fialed to accept new client");
      e.printStackTrace();
    }
  }


  class EchoClient {

    private LinkedList<ByteBuffer> outq;

    public EchoClient() {
      outq = new LinkedList<>();
    }

    public LinkedList<ByteBuffer> getOutq() {
      return outq;
    }

    public void enqueue(ByteBuffer byteBuffer) {
      outq.addFirst(byteBuffer);
    }
  }


  /**
   * 主要的业务逻辑的处理类
   */
  private class HandleMsg implements Runnable {

    SelectionKey sk;
    ByteBuffer bb;

    public HandleMsg(SelectionKey sk, ByteBuffer bb) {
      this.sk = sk;
      this.bb = bb;
    }

    @Override
    public void run() {
      EchoClient   echoClient = (EchoClient) sk.attachment();
      echoClient.enqueue(bb);
      sk.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
      selector.wakeup();
    }
  }

  public static void main(String[] args) {
    NioEchoServer server = new NioEchoServer();
    try {
      server.startServer();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
