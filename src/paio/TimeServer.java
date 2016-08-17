package paio;

import java.net.ServerSocket;
import java.net.Socket;

import bio.TimeServerHandler;

public class TimeServer {

  public static final int DEFAULT_PORT = 8080;
  public static final int DEFAULT_POOL_SIZE = 100;
  public static final int DEFAULT_QUEUE_SIZE = 10000;

  public static void main(String[] args) {
    int port = DEFAULT_PORT;
    if (args != null && args.length > 0) {
      port = Integer.valueOf(args[0]);
    }

    try (ServerSocket server = new ServerSocket(port);
        TimeServerExecutePool executor =
            new TimeServerExecutePool(DEFAULT_POOL_SIZE, DEFAULT_QUEUE_SIZE);) {
      System.out.println("Start time server, listen port is " + port);
      while (true) {
        Socket socket = server.accept();
        executor.execute(new TimeServerHandler(socket));
      }
    } catch (Exception e) {
      System.out.println("Got excpeiton " + e);
      e.printStackTrace();
    } finally {
      System.out.println("Close time server");
    }
  }

}
