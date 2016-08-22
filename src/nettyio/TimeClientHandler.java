package nettyio;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TimeClientHandler extends ChannelInboundHandlerAdapter {

  private final ByteBuf request;

  public TimeClientHandler() {
    byte[] bytes = "Query Time".getBytes();
    request = Unpooled.buffer(bytes.length);
    request.writeBytes(bytes);
    System.out.println("Send order to server");
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    ctx.writeAndFlush(request);
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    ByteBuf buf = (ByteBuf) msg;
    byte[] response = new byte[buf.readableBytes()];
    buf.readBytes(response);
    String body = new String(response, "UTF-8").trim();
    System.out.println("Now is " + body);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
          throws Exception {
    ctx.close();
  }
}
