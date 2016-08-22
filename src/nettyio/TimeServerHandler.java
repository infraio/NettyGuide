package nettyio;

import java.util.Date;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TimeServerHandler extends ChannelInboundHandlerAdapter {

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    ByteBuf buf = (ByteBuf) msg;
    byte[] request = new byte[buf.readableBytes()];
    buf.readBytes(request);
    String body = new String(request, "UTF-8").trim();
    System.out.println("The time server receive order : " + body);
    String response = body.equalsIgnoreCase("Query Time")
        ? new Date(System.currentTimeMillis()).toString() : "Bad Order";
    ByteBuf writeBuf = Unpooled.copiedBuffer(response.getBytes());
    ctx.write(writeBuf);
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    ctx.flush();
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
          throws Exception {
      ctx.close();
  }
}
