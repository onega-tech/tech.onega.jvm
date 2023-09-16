package tech.onega.jvm.std.struct.hash;

public interface HashFunction<K> {

  boolean equals(K keyA, K keyB);

  int hash(K key);

}