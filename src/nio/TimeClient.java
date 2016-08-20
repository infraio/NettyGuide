package nio;

public class TimeClient {

  public static final int DEFAULT_PORT = 8080;
  public static final String LOCALHOST = "127.0.0.1";

  public static void main(String[] args) {
    int port = DEFAULT_PORT;
    if (args != null && args.length > 0) {
      port = Integer.valueOf(args[0]);
    }
    
    Thread t = new Thread(new TimeClientHandler(LOCALHOST, port));
    t.start();
  }
}
