package decoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class EchoServerHandler extends ChannelInboundHandlerAdapter {

  private int counter;
  
  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    String body = (String) msg;
    System.out.println("Receive from client : " + body + " ; the counter is " + ++counter);
    body += EchoServer.DELIMITER;
    ByteBuf writeBuf = Unpooled.copiedBuffer(body.getBytes());
    ctx.writeAndFlush(writeBuf);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    ctx.close();
  }
}
