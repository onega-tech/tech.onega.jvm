package tech.onega.jvm.std.crypto;

import java.security.MessageDigest;
import tech.onega.jvm.std.struct.bytes.IBytes;

final public class MD5 {

  public static IBytes digest(final IBytes data) {
    try {
      final MessageDigest digister = MessageDigest.getInstance("MD5");
      final byte[] result = digister.digest(data.toArray());
      return IBytes.wrap(result);
    }
    catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

}
