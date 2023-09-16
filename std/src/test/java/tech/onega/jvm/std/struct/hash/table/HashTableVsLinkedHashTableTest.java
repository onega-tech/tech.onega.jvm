package tech.onega.jvm.std.struct.hash.table;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import org.testng.annotations.Test;
import tech.onega.jvm.std.lang.BenchMetter;
import tech.onega.jvm.std.lang.RandUtils;
import tech.onega.jvm.std.struct.hash.KV;
import tech.onega.jvm.std.validate.Check;

public class HashTableVsLinkedHashTableTest {

  private static class TestKit {

    private final Set<Integer> set;

    private final HashTable<Integer, Object> table;

    private TestKit() {
      this.set = new LinkedHashSet<>();
      this.table = new HashTable<>();
    }

    private TestKit(final int size) {
      this.set = new LinkedHashSet<>(size);
      this.table = new HashTable<>(size);
    }

    private void add(final int min, final int max) {
      final var count = RandUtils.randInt(min, max);
      for (var i = 0; i < count; i++) {
        final var v = RandUtils.randInt(min, max);
        this.set.add(v);
        this.table.replace(KV.of(v, null));
      }
    }

    private void assertEquals() {
      Check.equals(this.set.size(), this.table.size());
      final var data = this.toArray();
      Check.equals(data[0], data[1]);
      final var items = this.toArray();
      for (var i = 0; i < items[0].length; i++) {
        Check.isTrue(this.set.contains(items[0][i]));
        Check.isTrue(this.table.get(items[0][i]) != null);
        Check.isTrue(this.set.contains(items[1][i]));
        Check.isTrue(this.table.get(items[1][i]) != null);
      }
    }

    private void clear() {
      this.set.clear();
      this.table.clear();
    }

    private void remove(final int min, final int max) {
      final var count = RandUtils.randInt(min, max);
      for (var i = 0; i < count; i++) {
        final var v = RandUtils.randInt(min, max);
        this.set.remove(v);
        this.table.remove(v);
      }
    }

    private int[][] toArray() {
      Check.equals(this.set.size(), this.table.size());
      final var size = this.set.size();
      final var sa = new int[size];
      final var ta = new int[size];
      final var iSet = this.set.iterator();
      final var iTable = this.table.iterator();
      var i = 0;
      while (iSet.hasNext()) {
        sa[i] = iSet.next();
        final KV<Integer, Object> tk = iTable.next();
        ta[i] = tk.key;
        i++;
      }
      Arrays.sort(sa);
      Arrays.sort(ta);
      return new int[][] { sa, ta };
    }

  }

  @Test(enabled = false)
  public void benchMark() {
    final var repeat = 400;
    final var size = 10000;
    final var tableStat = new BenchMetter();
    final var setStat = new BenchMetter();
    for (var i = 0; i < repeat; i++) {
      tableStat.mark(() -> {
        final var t = new HashTable<Integer, Object>();
        for (var z = 0; z < size; z++) {
          final var v = RandUtils.randInt();
          t.replace(KV.of(v, null));
          Check.isTrue(t.contains(v));
        }
      });
    }
    for (var i = 0; i < repeat; i++) {
      setStat.mark(() -> {
        final var t = new LinkedHashSet<Integer>();
        for (var z = 0; z < size; z++) {
          final var v = RandUtils.randInt();
          t.add(v);
          Check.isTrue(t.contains(v));
        }
      });
    }
  }

  @Test
  public void testSame() {
    final var testKit = new TestKit();
    testKit.assertEquals();
    testKit.add(10_000, 20_000);
    testKit.assertEquals();
    testKit.clear();
    testKit.add(5_000, 10_000);
    testKit.assertEquals();
    testKit.remove(2_000, 5_000);
    testKit.assertEquals();
  }

}
