package tech.onega.jvm.std.codec.lzz;

final class LzzMatch {

  static void copyTo(final LzzMatch src, final LzzMatch dest) {
    dest.len = src.len;
    dest.start = src.start;
    dest.ref = src.ref;
  }

  static LzzMatch create() {
    return new LzzMatch();
  }

  static int end(final LzzMatch that) {
    return that.start + that.len;
  }

  static void fix(final LzzMatch that, final int correction) {
    that.start += correction;
    that.ref += correction;
    that.len -= correction;
  }

  static void reset(final LzzMatch that) {
    that.start = 0;
    that.ref = 0;
    that.len = 0;
  }

  int start;

  int ref;

  int len;

  private LzzMatch() {
  }

}