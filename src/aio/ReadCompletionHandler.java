package aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Date;

public class ReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {

  private AsynchronousSocketChannel asyncSocketChannel;
  
  public ReadCompletionHandler(AsynchronousSocketChannel channel) {
    this.asyncSocketChannel = channel;
  }

  @Override
  public void completed(Integer result, ByteBuffer attachment) {
    attachment.flip();
    byte[] body = new byte[attachment.remaining()];
    attachment.get(body);
    try {
      String request = new String(body, "UTF-8").trim();
      System.out.println("The time server receive order : " + request);
      String response = request.equalsIgnoreCase("Query Time")
          ? new Date(System.currentTimeMillis()).toString() : "Bad Order";
      doWrite(response);
    } catch (Exception e) {
      System.out.println("Got exception when read request from client");
      e.printStackTrace();
    }
  }

  @Override
  public void failed(Throwable exc, ByteBuffer attachment) {
    System.out.println("Got exception when read request from client");
    exc.printStackTrace();
    try {
      asyncSocketChannel.close();
    } catch (Exception e) {
      System.out.println("Got exception when close socket channel");
      e.printStackTrace();
    }
  }

  private void doWrite(String response) {
    if (response != null && response.trim().length() > 0) {
      byte[] bytes = response.getBytes();
      ByteBuffer buffer = ByteBuffer.allocate(1024);
      buffer.put(bytes);
      buffer.flip();
      asyncSocketChannel.write(buffer, buffer, new WriteCompletionHandler(asyncSocketChannel));
    }
  }
}
