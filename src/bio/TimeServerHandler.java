package bio;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

public class TimeServerHandler implements Runnable {

  private Socket socket;

  public TimeServerHandler(Socket socket) {
    this.socket = socket;
  }

  @Override
  public void run() {
    try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
      while (true) {
        String body = in.readLine();
        if (body == null) {
          break;
        }
        System.out.println("The time server receive order : " + body);
        String response = body.equalsIgnoreCase("Query Time")
            ? new Date(System.currentTimeMillis()).toString() : "Bad Order";
        out.println(response);
      }
    } catch (Exception e) {
      System.out.println("Got exception " + e);
      e.printStackTrace();
    } finally {
      try {
        socket.close();
      } catch (Exception e) {
        System.out.println("Close socket got exception " + e);
      }
    }
  }

}
