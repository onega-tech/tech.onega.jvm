package tech.onega.jvm.std.codec.json;

import java.time.Instant;
import org.testng.annotations.Test;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import tech.onega.jvm.std.struct.hash.KV;
import tech.onega.jvm.std.struct.list.IList;
import tech.onega.jvm.std.struct.list.MList;
import tech.onega.jvm.std.struct.map.IMap;
import tech.onega.jvm.std.struct.map.IMultiMap;
import tech.onega.jvm.std.struct.map.MMap;
import tech.onega.jvm.std.struct.map.MMultiMap;
import tech.onega.jvm.std.struct.set.ISet;
import tech.onega.jvm.std.struct.set.MSet;
import tech.onega.jvm.std.validate.Check;

public class JsonCodecTest {

  static class From {

    final IList<Integer> iList = IList.of(1, 2, 3);

    final MList<Integer> mList = MList.of(1, 2, 3);

    final ISet<Integer> iSet = ISet.of(1, 2, 3);

    final MSet<Integer> mSet = MSet.of(1, 2, 3);

    final IMap<Integer, Integer> iMap = IMap.of(KV.of(1, 1), KV.of(2, 2));

    final MMap<Integer, Integer> mMap = MMap.of(KV.of(1, 1), KV.of(2, 2));

    final IMultiMap<Integer, Integer> iMultiMap = IMultiMap.of(KV.of(1, 1), KV.of(1, 2));

    final MMultiMap<Integer, Integer> mMultiMap = MMultiMap.of(KV.of(1, 1), KV.of(1, 2));

  }

  static class To {

    @JsonDeserialize(contentAs = String.class)
    IList<String> iList;

    @JsonDeserialize(contentAs = String.class)
    MList<String> mList;

    @JsonDeserialize(contentAs = String.class)
    ISet<String> iSet;

    @JsonDeserialize(contentAs = String.class)
    MSet<String> mSet;

    @JsonDeserialize(keyAs = String.class, contentAs = String.class)
    IMap<String, String> iMap;

    @JsonDeserialize(keyAs = String.class, contentAs = String.class)
    MMap<String, String> mMap;

    @JsonDeserialize(keyAs = String.class, contentAs = String.class)
    IMultiMap<String, String> iMultiMap;

    @JsonDeserialize(keyAs = String.class, contentAs = String.class)
    MMultiMap<String, String> mMultiMap;

  }

  @Test
  public void test() {
    final var from = new From();
    final var json = JsonCodec.toString(from);
    final var to = JsonCodec.parse(json, To.class);
    Check.equals(to.iList, from.iList.stream().map(String::valueOf).collect(IList.collector()));
    Check.equals(to.mList, from.mList.stream().map(String::valueOf).collect(MList.collector()));
    Check.equals(to.iSet, from.iSet.stream().map(String::valueOf).collect(ISet.collector()));
    Check.equals(to.mSet, from.mSet.stream().map(String::valueOf).collect(MSet.collector()));
    Check.equals(to.iMap,
      from.iMap.stream().map(kv -> KV.of(kv.key().toString(), kv.value().toString())).collect(IMap.collector()));
    Check.equals(to.mMap,
      from.mMap.stream().map(kv -> KV.of(kv.key().toString(), kv.value().toString())).collect(MMap.collector()));
    Check.equals(to.iMultiMap, from.iMultiMap.stream()
      .map(kv -> KV.of(kv.key().toString(), kv.value().toString())).collect(IMultiMap.collector()));
    Check.equals(to.mMultiMap, from.mMultiMap.stream()
      .map(kv -> KV.of(kv.key().toString(), kv.value().toString())).collect(MMultiMap.collector()));
  }

  @Test
  public void testInstant() {
    final var instant = Instant.now();
    final var json = JsonCodec.toString(instant);
    final var fromJson = JsonCodec.parse(json, Instant.class);
    Check.equals(instant.toEpochMilli(), fromJson.toEpochMilli());
  }

}
