package serialization;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class SerializationPerformanceTest {

  public static final int LOOP = 100_000_000;

  public static void main(String[] args) {
    UserInfo info = new UserInfo();
    info.buildUserId(1).buildUserName("Netty");

    long startTime = System.currentTimeMillis();
    for (int i = 0; i < LOOP; i++) {
      try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
          ObjectOutputStream oos = new ObjectOutputStream(baos);) {
        oos.writeObject(info);
        oos.flush();
        byte[] bytes = baos.toByteArray();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    long endTime = System.currentTimeMillis();
    System.out.println("Java serialization cost time : " + (endTime - startTime) + " ms");

    startTime = System.currentTimeMillis();
    for (int i = 0; i < LOOP; i++) {
      byte[] bytes = info.codeC();
    }
    endTime = System.currentTimeMillis();
    System.out.println("Byte array serialization cost time : " + (endTime - startTime) + " ms");
  }

}
