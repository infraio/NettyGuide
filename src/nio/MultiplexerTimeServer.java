package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

public class MultiplexerTimeServer implements Runnable {

  private Selector selector;
  
  private ServerSocketChannel serveChannel;
  
  private boolean stopped = false;
 
  public MultiplexerTimeServer(int port) {
    try {
      // step1 : 打开ServerSocketChannel
      serveChannel = ServerSocketChannel.open();
      // step2 : 绑定端口
      serveChannel.socket().bind(new InetSocketAddress(port), 1024);
      serveChannel.configureBlocking(false);
      serveChannel.socket().setReuseAddress(true);
      // step3 : 创建Selector
      selector = Selector.open();
      // step4 : 注册ServerSocketChannel到Selector上，并监听ACCEPT事件
      serveChannel.register(selector, SelectionKey.OP_ACCEPT);
    } catch (IOException e) {
      System.out.println("Got exception when initialize server");
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
        // step5 : 轮询准备就绪的Key
        selector.select(1000);
        Set<SelectionKey> selectedKeys = selector.selectedKeys();
        Iterator<SelectionKey> it = selectedKeys.iterator();
        while (it.hasNext()) {
          SelectionKey key = it.next();
          it.remove();
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

  private void handleInput(SelectionKey key) throws IOException {
    if (key.isValid()) {
      if (key.isAcceptable()) {
        // step6 : 建立连接，并得到SocketChannel
        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
        SocketChannel sc = ssc.accept();
        sc.configureBlocking(false);
        // step7 : 注册SocketChannel到Selector，并监听READ事件
        sc.register(selector, SelectionKey.OP_READ);
      } else if (key.isReadable()) {
        // step8 : 读取Client请求到ByteBuffer
        SocketChannel sc = (SocketChannel) key.channel();
        ByteBuffer readBuffer = ByteBuffer.allocate(1024);
        int readBytes = sc.read(readBuffer);
        if (readBytes > 0) {
          readBuffer.flip();
          byte[] bytes = new byte[readBuffer.remaining()];
          readBuffer.get(bytes);
          // step9 : 对请求进行编解码
          String body = new String(bytes, "UTF-8").trim();
          System.out.println("The time server receive order : " + body);
          String response = body.equalsIgnoreCase("Query Time")
              ? new Date(System.currentTimeMillis()).toString() : "Bad Order";
          // step10 : 将response返回给Client
          ByteBuffer writeBuffer = ByteBuffer.wrap(response.getBytes());
          sc.write(writeBuffer);
        } else if (readBytes < 0) {
          key.cancel();
          sc.close();
        }
      }
    }
  }
}