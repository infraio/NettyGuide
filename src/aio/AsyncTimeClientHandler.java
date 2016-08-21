package aio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

public class AsyncTimeClientHandler implements Runnable, CompletionHandler<Void, AsyncTimeClientHandler> {

  private String host;
  private int port;
  private AsynchronousSocketChannel socketChannel;
  private CountDownLatch latch;
  
  public AsyncTimeClientHandler(String host, int port) {
    this.host = host;
    this.port = port;
    latch = new CountDownLatch(1);
    try {
      socketChannel = AsynchronousSocketChannel.open();
    } catch (Exception e) {
      System.out.println("Got exception when start client");
      e.printStackTrace();
    }
  }

  @Override
  public void run() {
    socketChannel.connect(new InetSocketAddress(host, port), this, this);
    try {
      latch.await();
    } catch (Exception e) {
      System.out.println("Got excpetion when service");
      e.printStackTrace();
    }
    
    try {
      socketChannel.close();
    } catch (Exception e) {
      System.out.println("Got exception when close socket channel");
      e.printStackTrace();
    }
  }

  @Override
  public void completed(Void result, AsyncTimeClientHandler attachment) {
    ByteBuffer buffer = ByteBuffer.wrap("Query Time".getBytes());
    System.out.println("Send order to server");
    socketChannel.write(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {

      @Override
      public void completed(Integer result, ByteBuffer attachment) {
        if (attachment.hasRemaining()) {
          socketChannel.write(attachment, attachment, this);
        } else {
          ByteBuffer readBuffer = ByteBuffer.allocate(1024);
          socketChannel.read(readBuffer, readBuffer, new CompletionHandler<Integer, ByteBuffer>() {

            @Override
            public void completed(Integer result, ByteBuffer attachment) {
              attachment.flip();
              byte[] bytes = new byte[attachment.remaining()];
              attachment.get(bytes);
              try {
                String response = new String(bytes, "UTF-8").trim();
                System.out.println("Now is " + response);
                latch.countDown();
              } catch (Exception e) {
                System.out.println("Got exception when read response from server");
                e.printStackTrace();
              }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
              System.out.println("Got exception when read response from server");
              exc.printStackTrace();
              latch.countDown();
            }
            
          });
        }
      }

      @Override
      public void failed(Throwable exc, ByteBuffer attachment) {
        System.out.println("Got exception when write request to server");
        exc.printStackTrace();
        latch.countDown();
      }
      
    });
  }

  @Override
  public void failed(Throwable exc, AsyncTimeClientHandler attachment) {
    System.out.println("Got exception when connect to server");
    exc.printStackTrace();
    latch.countDown();  
  }
}
