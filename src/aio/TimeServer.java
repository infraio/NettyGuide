package aio;

public class TimeServer {

  public static final int DEFAULT_PORT = 8080;

  public static void main(String[] args) {
    int port = DEFAULT_PORT;
    if (args != null && args.length > 0) {
      port = Integer.valueOf(args[0]);
    }

    AsyncTimeServerHandler timeServer = new AsyncTimeServerHandler(port);
    Thread t = new Thread(timeServer);
    t.start();
  }

}
