package tech.onega.jvm.std.struct.hash;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

final public class PerfectHash<K> {

  public static class LongHash implements UniversalHash<Long> {

    @Override
    public int hashCode(final Long o, final int index, final int seed) {
      if (index == 0) {
        return o.hashCode();
      }
      else if (index < 8) {
        long x = o;
        x += index;
        x = ((x >>> 32) ^ x) * 0x45d9f3b;
        x = ((x >>> 32) ^ x) * 0x45d9f3b;
        return (int) (x ^ (x >>> 32));
      }
      final int shift = (index & 1) * 32;
      return (int) (o.longValue() >>> shift);
    }

  }

  public static class StringHash implements UniversalHash<String> {

    public static int getFastHash(final String o, final int index, final int seed) {
      int x = (index * 0x9f3b) ^ seed;
      int result = seed + o.length();
      for (int i = 0; i < o.length(); i++) {
        x = 31 + x * 0x9f3b;
        result ^= x * (1 + o.charAt(i));
      }
      return result;
    }

    public static int getSipHash24(final byte[] b, final int start, final int end, final long k0, final long k1) {
      long v0 = k0 ^ 0x736f6d6570736575L;
      long v1 = k1 ^ 0x646f72616e646f6dL;
      long v2 = k0 ^ 0x6c7967656e657261L;
      long v3 = k1 ^ 0x7465646279746573L;
      int repeat;
      for (int off = start; off <= end + 8; off += 8) {
        long m;
        if (off <= end) {
          m = 0;
          int i = 0;
          for (; i < 8 && off + i < end; i++) {
            m |= ((long) b[off + i] & 255) << (8 * i);
          }
          if (i < 8) {
            m |= ((long) end - start) << 56;
          }
          v3 ^= m;
          repeat = 2;
        }
        else {
          m = 0;
          v2 ^= 0xff;
          repeat = 4;
        }
        for (int i = 0; i < repeat; i++) {
          v0 += v1;
          v2 += v3;
          v1 = Long.rotateLeft(v1, 13);
          v3 = Long.rotateLeft(v3, 16);
          v1 ^= v0;
          v3 ^= v2;
          v0 = Long.rotateLeft(v0, 32);
          v2 += v1;
          v0 += v3;
          v1 = Long.rotateLeft(v1, 17);
          v3 = Long.rotateLeft(v3, 21);
          v1 ^= v2;
          v3 ^= v0;
          v2 = Long.rotateLeft(v2, 32);
        }
        v0 ^= m;
      }
      return (int) (v0 ^ v1 ^ v2 ^ v3);
    }

    public static int getSipHash24(final String o, final long k0, final long k1) {
      final byte[] b = o.getBytes(StandardCharsets.UTF_8);
      return getSipHash24(b, 0, b.length, k0, k1);
    }

    @Override
    public int hashCode(final String o, final int index, final int seed) {
      if (index == 0) {
        return o.hashCode();
      }
      else if (index < 8) {
        return getFastHash(o, index, seed);
      }
      return getSipHash24(o, index, seed);
    }

  }

  public interface UniversalHash<T> {

    int hashCode(T o, int index, int seed);

  }

  private static final int DIVIDE = 6;

  private static final int SPEEDUP = 11;

  private static final int MAX_SIZE = 14;

  private static final int[] MAX_OFFSETS = { 0, 0, 8, 18, 47, 123, 319, 831, 2162, 5622, 14617, 38006, 98815, 256920,
    667993 };

  private static final int SPLIT_MANY = 3;

  private static final int[] SIZE_OFFSETS = new int[MAX_OFFSETS.length + 1];

  private static final SecureRandom RANDOM = new SecureRandom();
  static {
    for (int i = SPEEDUP; i < MAX_OFFSETS.length; i++) {
      MAX_OFFSETS[i] = (int) (MAX_OFFSETS[i] * 2.5);
    }
    int last = SPLIT_MANY + 1;
    for (int i = 0; i < MAX_OFFSETS.length; i++) {
      SIZE_OFFSETS[i] = last;
      last += MAX_OFFSETS[i];
    }
    SIZE_OFFSETS[SIZE_OFFSETS.length - 1] = last;
  }

  private static byte[] compress(final byte[] d) {
    final Deflater deflater = new Deflater();
    deflater.setStrategy(Deflater.HUFFMAN_ONLY);
    deflater.setInput(d);
    deflater.finish();
    final ByteArrayOutputStream out2 = new ByteArrayOutputStream(d.length);
    final byte[] buffer = new byte[1024];
    while (!deflater.finished()) {
      final int count = deflater.deflate(buffer);
      out2.write(buffer, 0, count);
    }
    deflater.end();
    return out2.toByteArray();
  }

  private static byte[] expand(final byte[] d) {
    final Inflater inflater = new Inflater();
    inflater.setInput(d);
    final ByteArrayOutputStream out = new ByteArrayOutputStream(d.length);
    final byte[] buffer = new byte[1024];
    try {
      while (!inflater.finished()) {
        final int count = inflater.inflate(buffer);
        out.write(buffer, 0, count);
      }
      inflater.end();
    }
    catch (final Exception e) {
      throw new IllegalArgumentException(e);
    }
    return out.toByteArray();
  }

  private static <K> void generate(final ArrayList<K> list, final UniversalHash<K> hash,
    int level, final int seed, final ByteArrayOutputStream out) {
    final int size = list.size();
    if (size <= 1) {
      out.write(size);
      return;
    }
    if (level > 32) {
      throw new IllegalStateException("Too many recursions; " +
        " incorrect universal hash function?");
    }
    if (size <= MAX_SIZE) {
      int maxOffset = MAX_OFFSETS[size];
      final int[] hashes = new int[size];
      for (int i = 0; i < size; i++) {
        hashes[i] = hash.hashCode(list.get(i), level, seed);
      }
      int testSize = size;
      if (size >= SPEEDUP) {
        testSize++;
        maxOffset /= testSize;
      }
      nextOffset: for (int offset = 0; offset < maxOffset; offset++) {
        int bits = 0;
        for (int i = 0; i < size; i++) {
          final int x = hashes[i];
          final int h = hash(x, level, offset, testSize);
          if ((bits & (1 << h)) != 0) {
            continue nextOffset;
          }
          bits |= 1 << h;
        }
        if (size >= SPEEDUP) {
          final int pos = Integer.numberOfTrailingZeros(~bits);
          writeSizeOffset(out, size, offset * (size + 1) + pos);
        }
        else {
          writeSizeOffset(out, size, offset);
        }
        return;
      }
    }
    int split;
    if (size > 57 * DIVIDE) {
      split = size / (36 * DIVIDE);
    }
    else {
      split = (size - 47) / DIVIDE;
    }
    split = Math.max(2, split);
    final boolean isRoot = level == 0;
    ArrayList<ArrayList<K>> lists;
    do {
      lists = new ArrayList<>(split);
      for (int i = 0; i < split; i++) {
        lists.add(new ArrayList<K>(size / split));
      }
      for (int i = 0; i < size; i++) {
        final K x = list.get(i);
        final ArrayList<K> l = lists.get(hash(x, hash, level, seed, 0, split));
        l.add(x);
        if (isRoot && split >= SPLIT_MANY &&
          l.size() > 36 * DIVIDE * 10) {
          level++;
          lists = null;
          break;
        }
      }
    } while (lists == null);
    if (split >= SPLIT_MANY) {
      out.write(SPLIT_MANY);
    }
    writeVarInt(out, split);
    final boolean multiThreaded = isRoot && list.size() > 1000;
    list.clear();
    list.trimToSize();
    if (multiThreaded) {
      generateMultiThreaded(lists, hash, level, seed, out);
    }
    else {
      for (final ArrayList<K> s2 : lists) {
        generate(s2, hash, level + 1, seed, out);
      }
    }
    if (isRoot && split >= SPLIT_MANY) {
      out.write(level);
    }
  }

  public static <K> byte[] generate(final Set<K> set, final UniversalHash<K> hash) {
    final ArrayList<K> list = new ArrayList<>(set);
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    final int seed = RANDOM.nextInt();
    out.write(seed >>> 24);
    out.write(seed >>> 16);
    out.write(seed >>> 8);
    out.write(seed);
    generate(list, hash, 0, seed, out);
    return compress(out.toByteArray());
  }

  private static <K> void generateMultiThreaded(
    final ArrayList<ArrayList<K>> lists,
    final UniversalHash<K> hash,
    final int level,
    final int seed,
    final ByteArrayOutputStream out) {
    final ArrayList<ByteArrayOutputStream> outList = new ArrayList<>();
    final int processors = Runtime.getRuntime().availableProcessors();
    final Thread[] threads = new Thread[processors];
    final AtomicInteger success = new AtomicInteger();
    final AtomicReference<Exception> failure = new AtomicReference<>();
    for (int i = 0; i < processors; i++) {
      threads[i] = new Thread() {

        @Override
        public void run() {
          try {
            while (true) {
              ArrayList<K> list;
              final ByteArrayOutputStream temp = new ByteArrayOutputStream();
              synchronized (lists) {
                if (lists.isEmpty()) {
                  break;
                }
                list = lists.remove(0);
                outList.add(temp);
              }
              generate(list, hash, level + 1, seed, temp);
            }
          }
          catch (final Exception e) {
            failure.set(e);
            return;
          }
          success.incrementAndGet();
        }

      };
    }
    for (final Thread t : threads) {
      t.start();
    }
    try {
      for (final Thread t : threads) {
        t.join();
      }
      if (success.get() != threads.length) {
        final Exception e = failure.get();
        if (e != null) {
          throw new RuntimeException(e);
        }
        throw new RuntimeException("Unknown failure in one thread");
      }
      for (final ByteArrayOutputStream temp : outList) {
        out.write(temp.toByteArray());
      }
    }
    catch (final InterruptedException e) {
      throw new RuntimeException(e);
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static int getOffset(final int n, final int size) {
    return n - SIZE_OFFSETS[size];
  }

  private static int getSize(final int n) {
    for (int i = 0; i < SIZE_OFFSETS.length; i++) {
      if (n < SIZE_OFFSETS[i]) {
        return i - 1;
      }
    }
    return 0;
  }

  private static int getVarIntLength(final byte[] d, int pos) {
    final int x = d[pos++];
    if (x >= 0) {
      return 1;
    }
    int len = 2;
    for (int s = 7; s < 64; s += 7) {
      final int b = d[pos++];
      if (b >= 0) {
        break;
      }
      len++;
    }
    return len;
  }

  private static int hash(int x, final int level, final int offset, final int size) {
    x += level + offset * 32;
    x = ((x >>> 16) ^ x) * 0x45d9f3b;
    x = ((x >>> 16) ^ x) * 0x45d9f3b;
    x = (x >>> 16) ^ x;
    return (x & (-1 >>> 1)) % size;
  }

  private static <K> int hash(final K o, final UniversalHash<K> hash, final int level,
    final int seed, final int offset, final int size) {
    int x = hash.hashCode(o, level, seed);
    x += level + offset * 32;
    x = ((x >>> 16) ^ x) * 0x45d9f3b;
    x = ((x >>> 16) ^ x) * 0x45d9f3b;
    x = (x >>> 16) ^ x;
    return (x & (-1 >>> 1)) % size;
  }

  private static int readVarInt(final byte[] d, int pos) {
    int x = d[pos++];
    if (x >= 0) {
      return x;
    }
    x &= 0x7f;
    for (int s = 7; s < 64; s += 7) {
      final int b = d[pos++];
      x |= (b & 0x7f) << s;
      if (b >= 0) {
        break;
      }
    }
    return x;
  }

  private static void writeSizeOffset(final ByteArrayOutputStream out, final int size,
    final int offset) {
    writeVarInt(out, SIZE_OFFSETS[size] + offset);
  }

  private static int writeVarInt(final ByteArrayOutputStream out, int x) {
    int len = 0;
    while ((x & ~0x7f) != 0) {
      out.write((byte) (0x80 | (x & 0x7f)));
      x >>>= 7;
      len++;
    }
    out.write((byte) x);
    return ++len;
  }

  private final UniversalHash<K> hash;

  private final byte[] data;

  private final int seed;

  private final int[] rootSize;

  private final int[] rootPos;

  private final int rootLevel;

  public PerfectHash(final byte[] desc, final UniversalHash<K> hash) {
    this.hash = hash;
    final byte[] b = data = expand(desc);
    seed = ((b[0] & 255) << 24) |
      ((b[1] & 255) << 16) |
      ((b[2] & 255) << 8) |
      (b[3] & 255);
    if (b[4] == SPLIT_MANY) {
      rootLevel = b[b.length - 1] & 255;
      final int split = readVarInt(b, 5);
      rootSize = new int[split];
      rootPos = new int[split];
      int pos = 5 + getVarIntLength(b, 5);
      int sizeSum = 0;
      for (int i = 0; i < split; i++) {
        rootSize[i] = sizeSum;
        rootPos[i] = pos;
        final int start = pos;
        pos = getNextPos(pos);
        sizeSum += getSizeSum(start, pos);
      }
    }
    else {
      rootLevel = 0;
      rootSize = null;
      rootPos = null;
    }
  }

  private int get(int pos, final K x, final boolean isRoot, final int level) {
    final int n = readVarInt(data, pos);
    if (n < 2) {
      return 0;
    }
    else if (n > SPLIT_MANY) {
      final int size = getSize(n);
      int offset = getOffset(n, size);
      if (size >= SPEEDUP) {
        final int p = offset % (size + 1);
        offset = offset / (size + 1);
        int result = hash(x, hash, level, seed, offset, size + 1);
        if (result >= p) {
          result--;
        }
        return result;
      }
      return hash(x, hash, level, seed, offset, size);
    }
    pos++;
    int split;
    if (n == SPLIT_MANY) {
      split = readVarInt(data, pos);
      pos += getVarIntLength(data, pos);
    }
    else {
      split = n;
    }
    final int h = hash(x, hash, level, seed, 0, split);
    int s;
    if (isRoot && rootPos != null) {
      s = rootSize[h];
      pos = rootPos[h];
    }
    else {
      final int start = pos;
      for (int i = 0; i < h; i++) {
        pos = getNextPos(pos);
      }
      s = getSizeSum(start, pos);
    }
    return s + get(pos, x, false, level + 1);
  }

  public int get(final K x) {
    return get(4, x, true, rootLevel);
  }

  private int getNextPos(int pos) {
    final int n = readVarInt(data, pos);
    pos += getVarIntLength(data, pos);
    if (n < 2 || n > SPLIT_MANY) {
      return pos;
    }
    int split;
    if (n == SPLIT_MANY) {
      split = readVarInt(data, pos);
      pos += getVarIntLength(data, pos);
    }
    else {
      split = n;
    }
    for (int i = 0; i < split; i++) {
      pos = getNextPos(pos);
    }
    return pos;
  }

  private int getSizeSum(final int start, final int end) {
    int s = 0;
    for (int pos = start; pos < end;) {
      final int n = readVarInt(data, pos);
      pos += getVarIntLength(data, pos);
      if (n < 2) {
        s += n;
      }
      else if (n > SPLIT_MANY) {
        s += getSize(n);
      }
      else if (n == SPLIT_MANY) {
        pos += getVarIntLength(data, pos);
      }
    }
    return s;
  }

}