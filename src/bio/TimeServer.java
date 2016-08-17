package bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TimeServer {

  public static final int DEFAULT_PORT = 8080;
  
  public static void main(String[] args) {
    int port = DEFAULT_PORT;
    if (args != null && args.length > 0) {
      port = Integer.valueOf(args[0]);
    }

    try (ServerSocket server = new ServerSocket(port)) {
      System.out.println("Start time server, listen port is " + port);
      Socket socket = null;
      while (true) {
        socket = server.accept();
        new Thread(new TimeServerHandler(socket)).start();
      }
    } catch (IOException e) {
      System.out.println("Got exception " + e);
      e.printStackTrace();
    } finally {
      System.out.println("Close time server");
    }
  }
}
