package bio;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TimeClient {

  public static final int DEFAULT_PORT = 8080;
  public static final String LOCALHOST = "127.0.0.1";

  public static void main(String[] args) {
    int port = DEFAULT_PORT;
    if (args != null && args.length > 0) {
      port = Integer.valueOf(args[0]);
    }

    try (Socket socket = new Socket(LOCALHOST, port);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
      out.println("Query Time");
      System.out.println("Send order to server");
      String response = in.readLine();
      System.out.println("Now is " + response);
    } catch (Exception e) {
      System.out.println("Got exception " + e);
      e.printStackTrace();
    }
  }

}
