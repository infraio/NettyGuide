package nettyio;

import java.util.Date;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TimeServerHandler extends ChannelInboundHandlerAdapter {

  private int counter = 0;

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    String body = (String) msg;
    System.out.println("The time server receive order : " + body + " ; the counter is " + ++counter);
    String response = body.equalsIgnoreCase("Query Time")
        ? new Date(System.currentTimeMillis()).toString() : "Bad Order";
    response += System.getProperty("line.separator");
    ByteBuf writeBuf = Unpooled.copiedBuffer(response.getBytes());
    ctx.write(writeBuf);
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    ctx.flush();
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    ctx.close();
  }
}
