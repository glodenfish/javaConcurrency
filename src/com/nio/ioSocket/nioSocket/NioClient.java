package com.nio.ioSocket.nioSocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;

public class NioClient {
  private Selector selector;

  /**
   * this method is init the connection and register channel on the selector
   * @param ip ip is server
   * @param port
   * @throws IOException
   */
  public void init(String ip,int port)throws IOException {
    SocketChannel sc = SocketChannel.open();
    sc.configureBlocking(false);
    this.selector = SelectorProvider.provider().openSelector();
    sc.connect(new InetSocketAddress(ip, port));
    SelectionKey selectionKey = sc.register(selector, SelectionKey.OP_CONNECT);
  }

  public void working() throws IOException{
    while (true) {
      if (!selector.isOpen()) {
        break;
      }
      selector.select();
      Iterator<SelectionKey> ite=this.selector.selectedKeys().iterator();
      while (ite.hasNext()) {
        SelectionKey key = ite.next();
        ite.remove();
        //connection event is happend
        if (key.isConnectable()) {
          connection(key);
        } else if (key.isReadable()) {
          read(key);
        }
      }
    }
  }

  private void read(SelectionKey key) throws IOException{
    SocketChannel channel= (SocketChannel) key.channel();
    ByteBuffer buffer = ByteBuffer.allocate(100);
    channel.read(buffer);
    byte[] data = buffer.array();

    String msg = new String(data).trim();
    System.out.println("client received information:" + msg);
    channel.close();
    key.selector().close();
  }

  /**
   * channel是非阻塞的。连接方法返回时，连接不一定成功。
   * @param key
   * @throws IOException
   */
  private void connection(SelectionKey key)throws IOException {
    SocketChannel channel = (SocketChannel) key.channel();

    if (channel.isConnectionPending()) {
      channel.finishConnect();
    }

    channel.configureBlocking(false);
    channel.write(ByteBuffer.wrap(new String("hello world!\r\n").getBytes()));
    channel.register(selector, SelectionKey.OP_READ);
  }

  public static void main(String[] args) {
    NioClient client = new NioClient();
    try {
      client.init("localhost", 8000);
      client.working();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
