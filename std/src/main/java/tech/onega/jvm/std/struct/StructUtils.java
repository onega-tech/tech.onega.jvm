package tech.onega.jvm.std.struct;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.stream.Collector;
import tech.onega.jvm.std.struct.hash.KV;
import tech.onega.jvm.std.struct.stream.StreamUtils;

final public class StructUtils {

  public static <V> Collector<V, ?, ? extends ArrayList<V>> arrayListCollector(final int initialSize) {
    return StreamUtils.selfCollector(() -> new ArrayList<>(initialSize), ArrayList::add);
  }

  public static <K, V> Collector<java.util.Map.Entry<K, V>, ?, ? extends LinkedHashMap<K, V>> linkedHashMapEntryCollector(
    final int initialSize) {
    return StreamUtils.selfCollector(() -> new LinkedHashMap<>(initialSize), (a, v) -> a.put(v.getKey(), v.getValue()));
  }

  public static <K, V> Collector<KV<K, V>, ?, ? extends LinkedHashMap<K, V>> linkedHashMapKVCollector(
    final int initialSize) {
    return StreamUtils.selfCollector(() -> new LinkedHashMap<>(initialSize), (a, v) -> a.put(v.key(), v.value()));
  }

  public static <V> Collector<V, ?, ? extends LinkedHashSet<V>> linkedHashSetCollector(final int initialSize) {
    return StreamUtils.selfCollector(() -> new LinkedHashSet<>(initialSize), LinkedHashSet::add);
  }

}
