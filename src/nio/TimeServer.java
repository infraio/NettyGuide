package nio;

public class TimeServer {

  public static final int DEFAULT_PORT = 8080;

  public static void main(String[] args) throws InterruptedException {
    int port = DEFAULT_PORT;
    if (args != null && args.length > 0) {
      port = Integer.valueOf(args[0]);
    }

    MultiplexerTimeServer timeServer = new MultiplexerTimeServer(port);
    Thread t = new Thread(timeServer);
    t.start();
    System.out.println("Start time server, listen port is " + port);
    t.join();
  }

}
