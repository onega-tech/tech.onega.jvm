package tech.onega.jvm.std.codec.lzz;

import tech.onega.jvm.std.validate.Check;

final class LzzEncoder {

  static int encode(final LzzEncoder that, final byte[] src, final int srcLength, final int srcOff,
    final byte[] dest, final int destLength, final int destOff, final int limit) {
    that.maxDestLen = dest.length - destOff;
    LzzUtils.checkRange2(srcLength, srcOff, limit);
    LzzUtils.checkRange2(destLength, destOff, that.maxDestLen);
    that.srcEnd = srcOff + limit;
    that.destEnd = destOff + that.maxDestLen;
    that.mfLimit = that.srcEnd - LzzCodec.MF_LIMIT;
    that.matchLimit = that.srcEnd - LzzCodec.LAST_LITERALS;
    that.sOff = srcOff;
    that.dOff = destOff;
    that.anchor = that.sOff++;
    LzzHashTable.reset(that.hashTable, srcOff, that.maxAttempts);
    LzzMatch.reset(that.match0);
    LzzMatch.reset(that.match1);
    LzzMatch.reset(that.match2);
    LzzMatch.reset(that.match3);
    while (that.sOff < that.mfLimit) {
      if (!LzzHashTable.insertAndFindBestMatch(that.hashTable, src, that.sOff, that.matchLimit, that.match1)) {
        that.sOff++;
      }
      else {
        LzzMatch.copyTo(that.match1, that.match0);
        search2(that, src, dest);
      }
    }
    that.dOff = LzzUtils.lastLiterals(src, that.anchor, that.srcEnd - that.anchor, dest, that.dOff, that.destEnd);
    return that.dOff - destOff;
  }

  static LzzEncoder create(final int compressionLevel) {
    return new LzzEncoder(compressionLevel);
  }

  static int maxCompressedLength(final int length) {
    if (length < 0) {
      throw new RuntimeException("length must be >= 0, got " + length);
    }
    else if (length >= LzzCodec.MAX_INPUT_SIZE) {
      throw new RuntimeException("length must be < " + LzzCodec.MAX_INPUT_SIZE);
    }
    return length + length / 255 + 16;
  }

  private static void search2(final LzzEncoder that, final byte[] src, final byte[] dest) {
    while (true) {
      Check.isTrue(that.match1.start >= that.anchor);
      if (LzzMatch.end(that.match1) >= that.mfLimit
        || !LzzHashTable.insertAndFindWiderMatch(that.hashTable, src, LzzMatch.end(that.match1) - 2,
          that.match1.start + 1, that.matchLimit, that.match1.len, that.match2)) {
        that.dOff = LzzUtils.encodeSequence(src, that.anchor, that.match1.start, that.match1.ref, that.match1.len, dest,
          that.dOff, that.destEnd);
        that.anchor = that.sOff = LzzMatch.end(that.match1);
        return;
      }
      if (that.match0.start < that.match1.start && that.match2.start < that.match1.start + that.match0.len) {
        LzzMatch.copyTo(that.match0, that.match1);
      }
      Check.isTrue(that.match2.start > that.match1.start);
      if (that.match2.start - that.match1.start < 3) {
        LzzMatch.copyTo(that.match2, that.match1);
      }
      else if (!search3(that, src, dest)) {
        return;
      }
    }
  }

  private static boolean search3(final LzzEncoder that, final byte[] src, final byte[] dest) {
    while (true) {
      if (that.match2.start - that.match1.start < LzzCodec.OPTIMAL_ML) {
        int newMatchLen = that.match1.len;
        if (newMatchLen > LzzCodec.OPTIMAL_ML) {
          newMatchLen = LzzCodec.OPTIMAL_ML;
        }
        if (that.match1.start + newMatchLen > LzzMatch.end(that.match2) - LzzCodec.MIN_MATCH) {
          newMatchLen = that.match2.start - that.match1.start + that.match2.len - LzzCodec.MIN_MATCH;
        }
        final int correction = newMatchLen - (that.match2.start - that.match1.start);
        if (correction > 0) {
          LzzMatch.fix(that.match2, correction);
        }
      }
      if (that.match2.start + that.match2.len >= that.mfLimit
        || !LzzHashTable.insertAndFindWiderMatch(that.hashTable, src, LzzMatch.end(that.match2) - 3, that.match2.start,
          that.matchLimit, that.match2.len, that.match3)) {
        if (that.match2.start < LzzMatch.end(that.match1)) {
          that.match1.len = that.match2.start - that.match1.start;
        }
        that.dOff = LzzUtils.encodeSequence(src, that.anchor, that.match1.start, that.match1.ref, that.match1.len, dest,
          that.dOff, that.destEnd);
        that.anchor = that.sOff = LzzMatch.end(that.match1);
        that.dOff = LzzUtils.encodeSequence(src, that.anchor, that.match2.start, that.match2.ref, that.match2.len, dest,
          that.dOff, that.destEnd);
        that.anchor = that.sOff = LzzMatch.end(that.match2);
        return false;
      }
      if (that.match3.start < LzzMatch.end(that.match1) + 3) {
        if (that.match3.start >= LzzMatch.end(that.match1)) {
          if (that.match2.start < LzzMatch.end(that.match1)) {
            final int correction = LzzMatch.end(that.match1) - that.match2.start;
            LzzMatch.fix(that.match2, correction);
            if (that.match2.len < LzzCodec.MIN_MATCH) {
              LzzMatch.copyTo(that.match3, that.match2);
            }
          }
          that.dOff = LzzUtils.encodeSequence(src, that.anchor, that.match1.start, that.match1.ref, that.match1.len,
            dest, that.dOff, that.destEnd);
          that.anchor = that.sOff = LzzMatch.end(that.match1);
          LzzMatch.copyTo(that.match3, that.match1);
          LzzMatch.copyTo(that.match2, that.match0);
          return true;
        }
        LzzMatch.copyTo(that.match3, that.match2);
        continue;
      }
      if (that.match2.start < LzzMatch.end(that.match1)) {
        if (that.match2.start - that.match1.start < LzzCodec.ML_MASK) {
          if (that.match1.len > LzzCodec.OPTIMAL_ML) {
            that.match1.len = LzzCodec.OPTIMAL_ML;
          }
          if (LzzMatch.end(that.match1) > LzzMatch.end(that.match2) - LzzCodec.MIN_MATCH) {
            that.match1.len = LzzMatch.end(that.match2) - that.match1.start - LzzCodec.MIN_MATCH;
          }
          final int correction = LzzMatch.end(that.match1) - that.match2.start;
          LzzMatch.fix(that.match2, correction);
        }
        else {
          that.match1.len = that.match2.start - that.match1.start;
        }
      }
      that.dOff = LzzUtils.encodeSequence(src, that.anchor, that.match1.start, that.match1.ref, that.match1.len, dest,
        that.dOff, that.destEnd);
      that.anchor = that.sOff = LzzMatch.end(that.match1);
      LzzMatch.copyTo(that.match2, that.match1);
      LzzMatch.copyTo(that.match3, that.match2);
    }
  }

  private final int maxAttempts;

  private int srcEnd;

  private int destEnd;

  private int mfLimit;

  private int matchLimit;

  private int maxDestLen;

  private int sOff;

  private int dOff;

  private int anchor;

  private final LzzHashTable hashTable;

  private final LzzMatch match0;

  private final LzzMatch match1;

  private final LzzMatch match2;

  private final LzzMatch match3;

  private LzzEncoder(final int compressionLevel) {
    this.maxAttempts = 1 << (compressionLevel - 1);
    this.match0 = LzzMatch.create();
    this.match1 = LzzMatch.create();
    this.match2 = LzzMatch.create();
    this.match3 = LzzMatch.create();
    this.hashTable = LzzHashTable.create(0, this.maxAttempts);
  }

}
