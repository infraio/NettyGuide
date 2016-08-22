package nettyio;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TimeClientHandler extends ChannelInboundHandlerAdapter {

  private final byte[] request;
  private int counter;

  public TimeClientHandler() {
    request = ("Query Time" + System.getProperty("line.separator")).getBytes();
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    for (int i = 0; i < 100; i++) {
      ByteBuf message = Unpooled.buffer(request.length);
      message.writeBytes(request);
      ctx.writeAndFlush(message);
    }
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    String body = (String) msg;
    System.out.println("Now is " + body + " ; the counter is " + ++counter);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
          throws Exception {
    ctx.close();
  }
}
