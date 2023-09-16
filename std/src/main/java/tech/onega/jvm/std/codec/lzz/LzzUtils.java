package tech.onega.jvm.std.codec.lzz;

final class LzzUtils {

  static void checkRange(final int intBufLength, final int off) {
    if (off < 0 || off >= intBufLength) {
      throw new RuntimeException("index out of bounds exception " + off);
    }
  }

  static void checkRange2(final int intBufLength, final int off, final int len) {
    if (len > 0) {
      checkRange(intBufLength, off);
      checkRange(intBufLength, off + len - 1);
    }
    else if (len < 0) {
      throw new RuntimeException("Wrong length");
    }
  }

  static int commonBytes(final byte[] b, int o1, int o2, final int limit) {
    int count = 0;
    while (o2 < limit && b[o1++] == b[o2++]) {
      ++count;
    }
    return count;
  }

  static int commonBytesBackward(final byte[] b, int o1, int o2, final int l1, final int l2) {
    int count = 0;
    while (o1 > l1 && o2 > l2 && b[--o1] == b[--o2]) {
      ++count;
    }
    return count;
  }

  private static void copy8Bytes(final byte[] src, final int sOff, final byte[] dest, final int dOff) {
    for (int i = 0; i < 8; ++i) {
      dest[dOff + i] = src[sOff + i];
    }
  }

  static int encodeSequence(final byte[] src, final int anchor, final int matchOff, final int matchRef, int matchLen,
    final byte[] dest, int dOff, final int destEnd) {
    final int runLen = matchOff - anchor;
    final int tokenOff = dOff++;
    if (dOff + runLen + (2 + 1 + LzzCodec.LAST_LITERALS) + (runLen >>> 8) > destEnd) {
      throw new RuntimeException("maxDestLen is too small");
    }
    int token;
    if (runLen >= LzzCodec.RUN_MASK) {
      token = (byte) (LzzCodec.RUN_MASK << LzzCodec.ML_BITS);
      dOff = LzzUtils.writeLen(runLen - LzzCodec.RUN_MASK, dest, dOff);
    }
    else {
      token = runLen << LzzCodec.ML_BITS;
    }
    LzzUtils.wildArraycopy(src, anchor, dest, dOff, runLen);
    dOff += runLen;
    final int matchDec = matchOff - matchRef;
    dest[dOff++] = (byte) matchDec;
    dest[dOff++] = (byte) (matchDec >>> 8);
    matchLen -= 4;
    if (dOff + (1 + LzzCodec.LAST_LITERALS) + (matchLen >>> 8) > destEnd) {
      throw new RuntimeException("maxDestLen is too small");
    }
    if (matchLen >= LzzCodec.ML_MASK) {
      token |= LzzCodec.ML_MASK;
      dOff = LzzUtils.writeLen(matchLen - LzzCodec.RUN_MASK, dest, dOff);
    }
    else {
      token |= matchLen;
    }
    dest[tokenOff] = (byte) token;
    return dOff;
  }

  static int hashHC(final int i) {
    return (i * -1640531535) >>> ((LzzCodec.MIN_MATCH * 8) - LzzCodec.HASH_LOG_HC);
  }

  static int lastLiterals(final byte[] src, final int sOff, final int srcLen, final byte[] dest, int dOff,
    final int destEnd) {
    final int runLen = srcLen;
    if (dOff + runLen + 1 + (runLen + 255 - LzzCodec.RUN_MASK) / 255 > destEnd) {
      throw new RuntimeException("Bad literals");
    }
    if (runLen >= LzzCodec.RUN_MASK) {
      dest[dOff++] = (byte) (LzzCodec.RUN_MASK << LzzCodec.ML_BITS);
      dOff = writeLen(runLen - LzzCodec.RUN_MASK, dest, dOff);
    }
    else {
      dest[dOff++] = (byte) (runLen << LzzCodec.ML_BITS);
    }
    System.arraycopy(src, sOff, dest, dOff, runLen);
    dOff += runLen;
    return dOff;
  }

  static int numberOfLeadingZeros(int i) {
    if (i == 0) {
      return 32;
    }
    int n = 1;
    if (i >>> 16 == 0) {
      n += 16;
      i <<= 16;
    }
    if (i >>> 24 == 0) {
      n += 8;
      i <<= 8;
    }
    if (i >>> 28 == 0) {
      n += 4;
      i <<= 4;
    }
    if (i >>> 30 == 0) {
      n += 2;
      i <<= 2;
    }
    n -= i >>> 31;
    return n;
  }

  static boolean readIntEquals(final byte[] buf, final int i, final int j) {
    return buf[i] == buf[j] && buf[i + 1] == buf[j + 1] && buf[i + 2] == buf[j + 2] && buf[i + 3] == buf[j + 3];
  }

  static int readIntLE(final byte[] buf, final int i) {
    return (buf[i] & 0xFF) | ((buf[i + 1] & 0xFF) << 8) | ((buf[i + 2] & 0xFF) << 16) | ((buf[i + 3] & 0xFF) << 24);
  }

  static int readShortLE(final byte[] buf, final int i) {
    return (buf[i] & 0xFF) | ((buf[i + 1] & 0xFF) << 8);
  }

  static int rotateLeft(final int i, final int distance) {
    return (i << distance) | (i >>> -distance);
  }

  static void safeIncrementalCopy(final byte[] dest, final int matchOff, final int dOff, final int matchLen) {
    for (int i = 0; i < matchLen; ++i) {
      dest[dOff + i] = dest[matchOff + i];
    }
  }

  static void wildArraycopy(final byte[] src, final int sOff, final byte[] dest, final int dOff, final int len) {
    for (int i = 0; i < len; i += 8) {
      copy8Bytes(src, sOff + i, dest, dOff + i);
    }
  }

  static void wildIncrementalCopy(final byte[] dest, int matchOff, int dOff, final int matchCopyEnd) {
    do {
      copy8Bytes(dest, matchOff, dest, dOff);
      matchOff += 8;
      dOff += 8;
    } while (dOff < matchCopyEnd);
  }

  static void writeIntLE(final int i, final byte[] buf, int off) {
    buf[off++] = (byte) i;
    buf[off++] = (byte) (i >>> 8);
    buf[off++] = (byte) (i >>> 16);
    buf[off++] = (byte) (i >>> 24);
  }

  private static int writeLen(int len, final byte[] dest, int dOff) {
    while (len >= 0xFF) {
      dest[dOff++] = (byte) 0xFF;
      len -= 0xFF;
    }
    dest[dOff++] = (byte) len;
    return dOff;
  }

}
