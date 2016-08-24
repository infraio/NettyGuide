package decoder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public class EchoServer {

  public static final int DEFAULT_PORT = 8080;
  public static final String DELIMITER = "$_";

  public static void main(String[] args) {
    int port = DEFAULT_PORT;
    if (args != null && args.length > 0) {
      port = Integer.valueOf(args[0]);
    }

    new EchoServer().bind(port);
  }

  public void bind(int port) {
    EventLoopGroup bossGroup = new NioEventLoopGroup();
    EventLoopGroup workerGroup = new NioEventLoopGroup();

    try {
      ServerBootstrap b = new ServerBootstrap();
      b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
          .option(ChannelOption.SO_BACKLOG, 1024)
          .childHandler(new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel arg0) throws Exception {
              ByteBuf delimiter = Unpooled.copiedBuffer(DELIMITER.getBytes());
              arg0.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
              arg0.pipeline().addLast(new StringDecoder());
              arg0.pipeline().addLast(new EchoServerHandler());
            }

          });
      ChannelFuture f = b.bind(port).sync();
      System.out.println("Start echo server, listen port is " + port);
      f.channel().closeFuture().sync();
    } catch (Exception e) {
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
    }
  }
}
