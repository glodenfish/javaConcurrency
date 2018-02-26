package com.nio.ioSocket.aioSocket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AioClient {

  public static void main(String[] args) throws Exception{
    //1.open channel
    final AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
    //2.linenumber:16-31 client connect server and register events
    client.connect(new InetSocketAddress(8000), null, new CompletionHandler<Void, Object>() {
      @Override
      public void completed(Void result, Object attachment) {
       client.write(ByteBuffer.wrap("hello".getBytes()), null,
           new CompletionHandler<Integer, Object>() {
             @Override
             public void completed(Integer result, Object attachment) {
               ByteBuffer buffer = ByteBuffer.allocate(1024);
               client.read(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                 @Override
                 public void completed(Integer result, ByteBuffer attachment) {
                   buffer.flip();
                   System.out.println(new String(buffer.array()));
                   try {
                     client.close();
                   } catch (IOException e) {
                     e.printStackTrace();
                   }
                 }

                 @Override
                 public void failed(Throwable exc, ByteBuffer attachment) {

                 }
               });
             }

             @Override
             public void failed(Throwable exc, Object attachment) {

             }
           });
      }

      @Override
      public void failed(Throwable exc, Object attachment) {

      }
    });
    //3.linenumber:56 wait
    Thread.sleep(1000);
  }

}
