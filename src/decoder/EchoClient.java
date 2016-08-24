package decoder;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public class EchoClient {

  public static final int DEFAULT_PORT = 8080;
  public static final String LOCALHOST = "127.0.0.1";

  public static void main(String[] args) throws Exception {
    int port = DEFAULT_PORT;
    if (args != null && args.length > 0) {
      port = Integer.valueOf(args[0]);
    }

    new EchoClient().connect(LOCALHOST, port);
  }

  public void connect(String host, int port) throws Exception {
    EventLoopGroup group = new NioEventLoopGroup();
    try {
      Bootstrap b = new Bootstrap();
      b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
      .handler(new ChannelInitializer<SocketChannel>() {

        @Override
        protected void initChannel(SocketChannel arg0) throws Exception {
          ByteBuf delimiter = Unpooled.copiedBuffer(EchoServer.DELIMITER.getBytes());
          arg0.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
          arg0.pipeline().addLast(new StringDecoder());
          arg0.pipeline().addLast(new EchoClientHandler());
        }
        
      });

      ChannelFuture f = b.connect(host, port).sync();
      f.channel().closeFuture().sync();
    } finally {
      group.shutdownGracefully();
    }
  }
}
