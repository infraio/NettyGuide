package serialization;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Random;

public class SerializationLengthTest {

  private static Random random = new Random(System.currentTimeMillis());
  private static int NUM = 10000;

  public static void main(String[] args) {
    for (int i = 0; i < NUM; i++) {
      UserInfo info = generateRandomUserInfo();
      testSerializationLength(info);
    }
  }

  public static void testSerializationLength(UserInfo info) {
    System.out.println("UserId : " + info.getUserId() + ", UserName : " + info.getUserName());
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);) {
      oos.writeObject(info);
      oos.flush();
      byte[] bytes = baos.toByteArray();
      System.out.println("Java serialization length is " + bytes.length);
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.println("Byte array serialization length is " + info.codeC().length);
    System.out.println("------------------------------------------");
  }
  
  public static UserInfo generateRandomUserInfo() {
    UserInfo info = new UserInfo();
    int id = random.nextInt(NUM);
    StringBuilder sb = new StringBuilder();
    for (int j = 0; j <= (id % 100); j++) {
      sb.append((char)(Math.random() * 25 + 65));
    }
    info.buildUserId(id).buildUserName(sb.toString());
    return info;
  }
}
