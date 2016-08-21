package aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class WriteCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {

  private AsynchronousSocketChannel asyncSocketChannel;
  
  public WriteCompletionHandler(AsynchronousSocketChannel channel) {
    this.asyncSocketChannel = channel;
  }

  @Override
  public void completed(Integer result, ByteBuffer attachment) {
    if (attachment.hasRemaining()) {
      asyncSocketChannel.write(attachment, attachment, this);
    }
  }

  @Override
  public void failed(Throwable exc, ByteBuffer attachment) {
    System.out.println("Got exception when write response to client");
    exc.printStackTrace();
    try {
      asyncSocketChannel.close();
    } catch (Exception e) {
      System.out.println("Got exception when close socket channel");
      e.printStackTrace();
    }
  }

}
