package tech.onega.jvm.std.codec.lzz;

final class LzzHash {

  private static final int PRIME1 = -1640531535;

  private static final int PRIME2 = -2048144777;

  private static final int PRIME3 = -1028477379;

  private static final int PRIME4 = 668265263;

  private static final int PRIME5 = 374761393;

  public static LzzHash create(final int seed) {
    return new LzzHash(seed);
  }

  public static int getValue(final LzzHash that) {
    int h32;
    if (that.totalLen >= 16) {
      h32 = LzzUtils.rotateLeft(that.v1, 1) + LzzUtils.rotateLeft(that.v2, 7) + LzzUtils.rotateLeft(that.v3, 12)
        + LzzUtils.rotateLeft(that.v4, 18);
    }
    else {
      h32 = that.seed + PRIME5;
    }
    h32 += that.totalLen;
    int off = 0;
    while (off <= that.memSize - 4) {
      h32 += LzzUtils.readIntLE(that.memory, off) * PRIME3;
      h32 = LzzUtils.rotateLeft(h32, 17) * PRIME4;
      off += 4;
    }
    while (off < that.memSize) {
      h32 += (that.memory[off] & 0xFF) * PRIME5;
      h32 = LzzUtils.rotateLeft(h32, 11) * PRIME1;
      ++off;
    }
    h32 ^= h32 >>> 15;
    h32 *= PRIME2;
    h32 ^= h32 >>> 13;
    h32 *= PRIME3;
    h32 ^= h32 >>> 16;
    return h32;
  }

  public static void reset(final LzzHash that) {
    that.v1 = that.seed + PRIME1 + PRIME2;
    that.v2 = that.seed + PRIME2;
    that.v3 = that.seed + 0;
    that.v4 = that.seed - PRIME1;
    that.totalLen = 0;
    that.memSize = 0;
  }

  public static void update(final LzzHash that, final byte[] buf, final int bufLength, int off, final int len) {
    LzzUtils.checkRange2(bufLength, off, len);
    that.totalLen += len;
    if (that.memSize + len < 16) {
      System.arraycopy(buf, off, that.memory, that.memSize, len);
      that.memSize += len;
      return;
    }
    final int end = off + len;
    if (that.memSize > 0) {
      System.arraycopy(buf, off, that.memory, that.memSize, 16 - that.memSize);
      that.v1 += LzzUtils.readIntLE(that.memory, 0) * PRIME2;
      that.v1 = LzzUtils.rotateLeft(that.v1, 13);
      that.v1 *= PRIME1;
      that.v2 += LzzUtils.readIntLE(that.memory, 4) * PRIME2;
      that.v2 = LzzUtils.rotateLeft(that.v2, 13);
      that.v2 *= PRIME1;
      that.v3 += LzzUtils.readIntLE(that.memory, 8) * PRIME2;
      that.v3 = LzzUtils.rotateLeft(that.v3, 13);
      that.v3 *= PRIME1;
      that.v4 += LzzUtils.readIntLE(that.memory, 12) * PRIME2;
      that.v4 = LzzUtils.rotateLeft(that.v4, 13);
      that.v4 *= PRIME1;
      off += 16 - that.memSize;
      that.memSize = 0;
    }
    final int limit = end - 16;
    while (off <= limit) {
      that.v1 += LzzUtils.readIntLE(buf, off) * PRIME2;
      that.v1 = LzzUtils.rotateLeft(that.v1, 13);
      that.v1 *= PRIME1;
      off += 4;
      that.v2 += LzzUtils.readIntLE(buf, off) * PRIME2;
      that.v2 = LzzUtils.rotateLeft(that.v2, 13);
      that.v2 *= PRIME1;
      off += 4;
      that.v3 += LzzUtils.readIntLE(buf, off) * PRIME2;
      that.v3 = LzzUtils.rotateLeft(that.v3, 13);
      that.v3 *= PRIME1;
      off += 4;
      that.v4 += LzzUtils.readIntLE(buf, off) * PRIME2;
      that.v4 = LzzUtils.rotateLeft(that.v4, 13);
      that.v4 *= PRIME1;
      off += 4;
    }
    if (off < end) {
      System.arraycopy(buf, off, that.memory, 0, end - off);
      that.memSize = end - off;
    }
  }

  private final int seed;

  private final byte[] memory;

  private int v1;

  private int v2;

  private int v3;

  private int v4;

  private int memSize;

  private long totalLen;

  private LzzHash(final int seed) {
    this.seed = seed;
    memory = new byte[16];
    LzzHash.reset(this);
  }

}
