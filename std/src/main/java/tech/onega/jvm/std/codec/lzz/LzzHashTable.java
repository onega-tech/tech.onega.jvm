package tech.onega.jvm.std.codec.lzz;

import java.util.Arrays;
import tech.onega.jvm.std.validate.Check;

final class LzzHashTable {

  private static final int MASK = LzzCodec.MAX_DISTANCE - 1;

  private static void addHash(final LzzHashTable that, final byte[] bytes, final int off) {
    final int v = LzzUtils.readIntLE(bytes, off);
    LzzHashTable.addHash2(that, v, off);
  }

  private static void addHash2(final LzzHashTable that, final int v, final int off) {
    final int h = LzzUtils.hashHC(v);
    int delta = off - that.hashTable[h];
    Check.isTrue(delta > 0);// : delta;
    if (delta >= LzzCodec.MAX_DISTANCE) {
      delta = LzzCodec.MAX_DISTANCE - 1;
    }
    that.chainTable[off & MASK] = (short) delta;
    that.hashTable[h] = off;
  }

  public static LzzHashTable create(final int base, final int maxAttempts) {
    return new LzzHashTable(base, maxAttempts);
  }

  public static void destroy(final LzzHashTable that) {
  }

  private static int hashPointer(final LzzHashTable that, final byte[] bytes, final int off) {
    final int v = LzzUtils.readIntLE(bytes, off);
    return LzzHashTable.hashPointer2(that, v);
  }

  private static int hashPointer2(final LzzHashTable that, final int v) {
    final int h = LzzUtils.hashHC(v);
    return that.hashTable[h];
  }

  private static void insert(final LzzHashTable that, final int off, final byte[] bytes) {
    for (; that.nextToUpdate < off; ++that.nextToUpdate) {
      addHash(that, bytes, that.nextToUpdate);
    }
  }

  static boolean insertAndFindBestMatch(final LzzHashTable that, final byte[] buf, final int off, final int matchLimit,
    final LzzMatch match) {
    match.start = off;
    match.len = 0;
    int delta = 0;
    int repl = 0;
    insert(that, off, buf);
    int ref = hashPointer(that, buf, off);
    if (ref >= off - 4 && ref <= off && ref >= that.base) {
      if (LzzUtils.readIntEquals(buf, ref, off)) {
        delta = off - ref;
        repl = match.len = LzzCodec.MIN_MATCH
          + LzzUtils.commonBytes(buf, ref + LzzCodec.MIN_MATCH, off + LzzCodec.MIN_MATCH, matchLimit);
        match.ref = ref;
      }
      ref = next(that, ref);
    }
    for (int i = 0; i < that.maxAttempts; ++i) {
      if (ref < Math.max(that.base, off - LzzCodec.MAX_DISTANCE + 1) || ref > off) {
        break;
      }
      if (LzzUtils.readIntEquals(buf, ref, off)) {
        final int matchLen = LzzCodec.MIN_MATCH
          + LzzUtils.commonBytes(buf, ref + LzzCodec.MIN_MATCH, off + LzzCodec.MIN_MATCH, matchLimit);
        if (matchLen > match.len) {
          match.ref = ref;
          match.len = matchLen;
        }
      }
      ref = next(that, ref);
    }
    if (repl != 0) {
      int ptr = off;
      final int end = off + repl - (LzzCodec.MIN_MATCH - 1);
      while (ptr < end - delta) {
        that.chainTable[ptr & MASK] = (short) delta; // pre load
        ++ptr;
      }
      do {
        that.chainTable[ptr & MASK] = (short) delta;
        that.hashTable[LzzUtils.hashHC(LzzUtils.readIntLE(buf, ptr))] = ptr;
        ++ptr;
      } while (ptr < end);
      that.nextToUpdate = end;
    }
    return match.len != 0;
  }

  static boolean insertAndFindWiderMatch(final LzzHashTable that, final byte[] buf, final int off, final int startLimit,
    final int matchLimit, final int minLen, final LzzMatch match) {
    match.len = minLen;
    insert(that, off, buf);
    int ref = hashPointer(that, buf, off);
    for (int i = 0; i < that.maxAttempts; ++i) {
      if (ref < Math.max(that.base, off - LzzCodec.MAX_DISTANCE + 1) || ref > off) {
        break;
      }
      if (LzzUtils.readIntEquals(buf, ref, off)) {
        final int matchLenForward = LzzCodec.MIN_MATCH
          + LzzUtils.commonBytes(buf, ref + LzzCodec.MIN_MATCH, off + LzzCodec.MIN_MATCH, matchLimit);
        final int matchLenBackward = LzzUtils.commonBytesBackward(buf, ref, off, that.base, startLimit);
        final int matchLen = matchLenBackward + matchLenForward;
        if (matchLen > match.len) {
          match.len = matchLen;
          match.ref = ref - matchLenBackward;
          match.start = off - matchLenBackward;
        }
      }
      ref = next(that, ref);
    }
    return match.len > minLen;
  }

  private static int next(final LzzHashTable that, final int off) {
    return off - (that.chainTable[off & MASK] & 0xFFFF);
  }

  public static void reset(final LzzHashTable that, final int base, final int maxAttempts) {
    that.base = base;
    that.maxAttempts = maxAttempts;
    that.nextToUpdate = base;
    Arrays.fill(that.hashTable, -1);
    Arrays.fill(that.chainTable, (short) 0);
  }

  private int nextToUpdate;

  private int base;

  private final int[] hashTable;

  private final short[] chainTable;

  private int maxAttempts;

  private LzzHashTable(final int base, final int maxAttempts) {
    this.hashTable = new int[LzzCodec.HASH_TABLE_SIZE_HC];
    this.chainTable = new short[LzzCodec.MAX_DISTANCE];
    reset(this, base, maxAttempts);
  }

}