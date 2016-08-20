package nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class TimeClientHandler implements Runnable {

  private String host;
  private int port;

  private Selector selector;
  
  private boolean stopped;

  public TimeClientHandler(String host, int port) {
    this.host = host;
    this.port = port;
    try {
      SocketChannel channel = SocketChannel.open();
      channel.configureBlocking(false);
      selector = Selector.open();
      channel.connect(new InetSocketAddress(this.host, this.port));
      channel.register(selector, SelectionKey.OP_CONNECT);
    } catch (Exception e) {
      System.out.println("Got exception when initilize a time client");
      e.printStackTrace();
    }
  }

  private boolean isStopped() {
    return this.stopped;
  }
  
  private void stop() {
    this.stopped = true;
  }
  
  @Override
  public void run() {
    while (!isStopped()) {
      try {
        selector.select(1000);
        Set<SelectionKey> selectKeys = selector.selectedKeys();
        Iterator<SelectionKey> iterator = selectKeys.iterator();
        while (iterator.hasNext()) {
          SelectionKey key = iterator.next();
          iterator.remove();
          try {
            handleInput(key);
          } catch (Exception e) {
            System.out.println("Got exception when handle input");
            e.printStackTrace();
            if (key != null) {
              key.cancel();
              if (key.channel() != null) {
                key.channel().close();
              }
            }
          }
        }
      } catch (Exception e) {
        System.out.println("Got exception when service");
        e.printStackTrace();
        stop();
      }
    }
    
    if (selector != null) {
      try {
        selector.close();
      } catch (Exception e) {
        System.out.println("Got exception when close selector");
        e.printStackTrace();
      }
    }
  }

  private void handleInput(SelectionKey key) throws Exception {
    if (key.isValid()) {
      if (key.isConnectable()) {
        SocketChannel sc = (SocketChannel) key.channel();
        if (sc.isConnectionPending()) {
          sc.finishConnect();
        }
        sc.configureBlocking(false);
        String order = "Query Time";
        sc.write(ByteBuffer.wrap(order.getBytes()));
        System.out.println("Send order to server");
        sc.register(selector, SelectionKey.OP_READ);
      } else if (key.isReadable()){
        SocketChannel sc = (SocketChannel) key.channel();
        ByteBuffer readBuffer = ByteBuffer.allocate(1024);
        int readBytes = sc.read(readBuffer);
        if (readBytes > 0) {
          readBuffer.flip();
          byte[] bytes = new byte[readBuffer.remaining()];
          readBuffer.get(bytes);
          String body = new String(bytes, "UTF-8").trim();
          System.out.println("Now is : " + body);
        } else if (readBytes < 0) {
          key.cancel();
          sc.close();
        }
      }
    }
  }

}
