package decoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class EchoClientHandler extends ChannelInboundHandlerAdapter {

  private final String request;
  private int counter;

  public EchoClientHandler() {
    request = "Welcome to Netty! " + EchoServer.DELIMITER;
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    for (int i = 0; i < 100; i++) {
      ByteBuf message = Unpooled.copiedBuffer(request.getBytes());
      ctx.writeAndFlush(message);
    }
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    String body = (String) msg;
    System.out.println("Receive from server : " + body + " ; the counter is " + ++counter);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
          throws Exception {
    ctx.close();
  }
}
