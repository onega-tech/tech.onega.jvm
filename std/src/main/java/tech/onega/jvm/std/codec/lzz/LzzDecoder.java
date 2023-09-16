package tech.onega.jvm.std.codec.lzz;

final class LzzDecoder {

  static int decode(final byte[] src, final int srcLength, final int srcOff, final byte[] dest,
    final int destLength, final int destOff, final int limit) {
    LzzUtils.checkRange(srcLength, srcOff);
    LzzUtils.checkRange2(destLength, destOff, limit);
    if (limit == 0) {
      if (src[srcOff] != 0) {
        throw new RuntimeException("Malformed input at " + srcOff);
      }
      return 1;
    }
    final int destEnd = destOff + limit;
    int sOff = srcOff;
    int dOff = destOff;
    while (true) {
      final int token = src[sOff] & 0xFF;
      ++sOff;
      int literalLen = token >>> LzzCodec.ML_BITS;
      if (literalLen == LzzCodec.RUN_MASK) {
        byte len = (byte) 0xFF;
        while ((len = src[sOff++]) == (byte) 0xFF) {
          literalLen += 0xFF;
        }
        literalLen += len & 0xFF;
      }
      final int literalCopyEnd = dOff + literalLen;
      if (literalCopyEnd > destEnd - LzzCodec.COPY_LENGTH) {
        if (literalCopyEnd != destEnd) {
          throw new RuntimeException("Malformed input at " + sOff);
        }
        else {
          System.arraycopy(src, sOff, dest, dOff, literalLen);
          sOff += literalLen;
          dOff = literalCopyEnd;
          break;
        }
      }
      LzzUtils.wildArraycopy(src, sOff, dest, dOff, literalLen);
      sOff += literalLen;
      dOff = literalCopyEnd;
      final int matchDec = LzzUtils.readShortLE(src, sOff);
      sOff += 2;
      final int matchOff = dOff - matchDec;
      if (matchOff < destOff) {
        throw new RuntimeException("Malformed input at " + sOff);
      }
      int matchLen = token & LzzCodec.ML_MASK;
      if (matchLen == LzzCodec.ML_MASK) {
        byte len = (byte) 0xFF;
        while ((len = src[sOff++]) == (byte) 0xFF) {
          matchLen += 0xFF;
        }
        matchLen += len & 0xFF;
      }
      matchLen += LzzCodec.MIN_MATCH;
      final int matchCopyEnd = dOff + matchLen;
      if (matchCopyEnd > destEnd - LzzCodec.COPY_LENGTH) {
        if (matchCopyEnd > destEnd) {
          throw new RuntimeException("Malformed input at " + sOff);
        }
        LzzUtils.safeIncrementalCopy(dest, matchOff, dOff, matchLen);
      }
      else {
        LzzUtils.wildIncrementalCopy(dest, matchOff, dOff, matchCopyEnd);
      }
      dOff = matchCopyEnd;
    }
    return sOff - srcOff;
  }

}
