package com.selectors;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class TCPHandler implements Runnable{
  private final SelectionKey sk;
  private final SocketChannel sc;

  int state=0;

  public TCPHandler(SelectionKey sk, SocketChannel sc) {
    this.sk = sk;
    this.sc = sc;
  state =0; //initialization state is read
}

  @Override
  public void run() {
    try {
      if (state == 0) {
        read();
      } else {
        send();
      }
    } catch (IOException e) {
      System.out.println("TcpHandler  is closed!");
      closeChannel();
    }
  }

  private void send() throws IOException{
    String str =
        "server send a message to :" + sc.socket().getRemoteSocketAddress().toString() + "\r\n";
    ByteBuffer byteBuffer = ByteBuffer.wrap(str.getBytes());

    while (byteBuffer.hasRemaining()) {
      sc.write(byteBuffer);
    }
    state = 0;
    sk.interestOps(SelectionKey.OP_READ);
    sk.selector().wakeup();
  }

  private void closeChannel() {
    try {
      sk.cancel();
      sc.close();
    } catch (IOException e1) {
      e1.printStackTrace();
    }
  }

  private synchronized void read() throws IOException {
    byte[] by=new byte[1024];
    ByteBuffer bb= ByteBuffer.wrap(by);
    int readBytes = sc.read(bb);
    if (readBytes == -1) {
      System.out.println("sychronized read() tell you channel is closed!");
      closeChannel();
      return;
    }

    String readStr = new String(by);//read data from socketchannel
    if (null != readStr && " " != readStr) {
      process(readStr);
      System.out.println("handler read from :"+sc.socket().getRemoteSocketAddress().toString()+":"+readStr);
      state = 1;
      sk.interestOps(SelectionKey.OP_WRITE);
      sk.selector().wakeup();//改变了通道的监听事件需要告诉selector
    }

  }

  private void process(String readStr) {
    System.out.println("process method handle the readStr"+readStr);
    readStr = readStr.replace("c", "get a way");
  }
}
