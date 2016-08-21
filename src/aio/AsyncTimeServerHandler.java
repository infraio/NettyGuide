package aio;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

public class AsyncTimeServerHandler implements Runnable {

  private final int port;
  AsynchronousServerSocketChannel asyncServerSocketChannel;
  CountDownLatch latch;
  
  public AsyncTimeServerHandler(int port) {
    this.port = port;
    latch = new CountDownLatch(1);
    try {
      asyncServerSocketChannel = AsynchronousServerSocketChannel.open();
      asyncServerSocketChannel.bind(new InetSocketAddress(this.port));
      System.out.println("Start time server, listen port is " + this.port);
    } catch (Exception e) {
      System.out.println("Got exception when start time server");
      e.printStackTrace();
    }
  }

  @Override
  public void run() {
    doAccept();
    try {
      latch.await();
    } catch (Exception e) {
      System.out.println("Got exception when serivce");
      e.printStackTrace();
    }
    
    try {
      System.out.println("Quit time server");
      asyncServerSocketChannel.close();
    } catch (Exception e) {
      System.out.println("Got exception when close server socket channel");
      e.printStackTrace();
    }
  }

  private void doAccept() {
    asyncServerSocketChannel.accept(this, new AcceptCompletionHandler());
  }
}
