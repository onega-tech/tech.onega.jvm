package tech.onega.jvm.std.struct.hash;

import java.io.Serializable;
import tech.onega.jvm.std.lang.Equals;

final public class HashFunctionBasic<K> implements HashFunction<K>, Serializable {

  private static final long serialVersionUID = 1L;

  @Override
  public boolean equals(final Object keyA, final Object keyB) {
    return Equals.yes(keyA, keyB);
  }

  @Override
  public int hash(final Object key) {
    return key == null ? 0 : key.hashCode();
  }

}
