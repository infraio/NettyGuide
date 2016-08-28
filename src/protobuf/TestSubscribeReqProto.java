package protobuf;

import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.InvalidProtocolBufferException;

public class TestSubscribeReqProto {

  public static void main(String[] args) throws InvalidProtocolBufferException {
    SubscribeReqProto.SubscribeReq req = createSubscribeReq();
    System.out.println("Before encode : " + req.toString());
    SubscribeReqProto.SubscribeReq req2 = decode(encode(req));
    System.out.println("After encode : " + req2.toString());
    System.out.println("Assert equal : " + req2.equals(req));
  }

  private static SubscribeReqProto.SubscribeReq createSubscribeReq() {
    SubscribeReqProto.SubscribeReq.Builder builder = SubscribeReqProto.SubscribeReq.newBuilder();
    builder.setSubReqID(1);
    builder.setUserName("xiaoming");
    builder.setProductName("netty in action");
    List<String> addresses = new ArrayList<String>();
    addresses.add("BeiJing");
    addresses.add("ShangHai");
    builder.addAllAddress(addresses);
    return builder.build();
  }

  private static byte[] encode(SubscribeReqProto.SubscribeReq req) {
    return req.toByteArray();
  }

  private static SubscribeReqProto.SubscribeReq decode(byte[] body)
      throws InvalidProtocolBufferException {
    return SubscribeReqProto.SubscribeReq.parseFrom(body);
  }
}
