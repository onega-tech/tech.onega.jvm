package tech.onega.jvm.std.crypto;

import tech.onega.jvm.std.io.reader.IOReader;
import tech.onega.jvm.std.io.writer.IOWriter;
import tech.onega.jvm.std.io.writer.IOWriterBytes;
import tech.onega.jvm.std.lang.RandUtils;
import tech.onega.jvm.std.struct.bytes.IBytes;
import tech.onega.jvm.std.validate.Check;

final public class ThreeFish {

  public static class Decoder {

    public interface Writer {

      void write(byte[] dest, int limit, int offset);

    }

    private final int bits;

    private final long[] key;

    private final long[] tweak;

    private final byte[] buffer;

    private int bufferOffset;

    private final long[] block;

    private boolean firstBlock;

    private int padding;

    private final Writer writer;

    public Decoder(final int bits, final IBytes keyBytes, final Writer writer) {
      this.bits = bits;
      this.key = keyFromBytes(keyBytes.toArray(), bits);
      this.tweak = tweakFromBytes(null);
      this.buffer = new byte[bits / 8];
      this.bufferOffset = 0;
      this.block = new long[this.buffer.length / 8];
      this.firstBlock = true;
      this.padding = -1;
      this.writer = writer;
    }

    public void decode(final byte[] data, final int limit, final int offset) {
      Check.isTrue(limit > 0);
      var dataOffset = offset;
      final var dataLength = offset + limit;
      Check.isTrue(dataLength <= data.length);
      while (dataOffset < dataLength) {
        if (this.bufferOffset == this.buffer.length) {
          this.decryptBuffer(false);
        }
        final var copyLength = Math.min(this.buffer.length - this.bufferOffset, dataLength - dataOffset);
        Check.isTrue(copyLength > 0);
        System.arraycopy(data, dataOffset, this.buffer, this.bufferOffset, copyLength);
        dataOffset += copyLength;
        this.bufferOffset += copyLength;
      }
    }

    private void decryptBuffer(final boolean eof) {
      blockFromBytes(this.buffer, this.block);
      decryptBlock(this.bits, this.key, this.tweak, this.block);
      blockToBytes(this.block, this.buffer);
      final int offset = this.firstBlock ? 1 : 0;
      if (this.firstBlock) {
        this.padding = this.buffer[0] & 0xff << 0;
        this.firstBlock = false;
      }
      final int limit = eof ? this.buffer.length - offset - this.padding : this.buffer.length - offset;
      if (limit > 0) {
        this.writer.write(this.buffer, limit, offset);
      }
      this.bufferOffset = 0;
    }

    public void end() {
      Check.equals(this.bufferOffset, this.buffer.length);
      this.decryptBuffer(true);
    }

    public void setPadding(final int padding) {
      Check.isTrue(this.firstBlock);
      this.padding = padding;
      this.firstBlock = false;
    }

  }

  private static final int[][] ROTATION_256 = {
    { 14, 16 },
    { 52, 57 },
    { 23, 40 },
    { 5, 37 },
    { 25, 33 },
    { 46, 12 },
    { 58, 22 },
    { 32, 32 }
  };

  private static final int[][] ROTATION_512 = {
    { 46, 36, 19, 37 },
    { 33, 27, 14, 42 },
    { 17, 49, 36, 39 },
    { 44, 9, 54, 56 },
    { 39, 30, 34, 24 },
    { 13, 50, 10, 17 },
    { 25, 29, 39, 43 },
    { 8, 35, 56, 22 }
  };

  private static final int[][] ROTATION_1024 = {
    { 24, 13, 8, 47, 8, 17, 22, 37 },
    { 38, 19, 10, 55, 49, 18, 23, 52 },
    { 33, 4, 51, 13, 34, 41, 59, 17 },
    { 5, 20, 48, 41, 47, 28, 16, 25 },
    { 41, 9, 37, 31, 12, 47, 44, 30 },
    { 16, 34, 56, 51, 4, 53, 42, 41 },
    { 31, 44, 47, 46, 19, 42, 44, 25 },
    { 9, 48, 35, 52, 23, 31, 37, 20 }
  };

  public static final int BITS_256 = 256;

  public static final int BITS_512 = 512;

  public static final int BITS_1024 = 1024;

  public static final int TWEAK_BYTES = 16;

  private static final long C_240 = 0x1BD11BDAA9FC1A22L;

  private static final int ROUNDS_256 = 72;

  private static final int ROUNDS_512 = 72;

  private static final int ROUNDS_1024 = 80;

  public static int[] MOD17 = initMod(17, ROUNDS_1024);

  private static int[] MOD9 = initMod(9, ROUNDS_1024);

  private static int[] MOD5 = initMod(5, ROUNDS_1024);

  private static int[] MOD3 = initMod(3, ROUNDS_1024);

  private static void blockFromBytes(final byte[] data, final long[] block) {
    int i = 0;
    while (i < data.length) {
      block[i >> 3] = longFromBytes(data, i);
      i += 8;
    }
  }

  private static void blockToBytes(final long[] block, final byte[] out) {
    int i = 0;
    while (i < out.length) {
      longToBytes(block[i >> 3], out, i);
      i += 8;
    }
  }

  public static IBytes decrypt(final int bits, final IBytes key, final IBytes data) {
    final IOWriterBytes writer = new IOWriterBytes(data.length());
    ThreeFish.decrypt(bits, key, data.asReader(), writer);
    return writer.toIBytes();
  }

  public static void decrypt(
    final int bits,
    final IBytes keyBytes,
    final IOReader reader,
    final IOWriter writer) {
    if (reader.available() < bits / 8) {
      throw new RuntimeException("Input lower then block size");
    }
    final long[] key = keyFromBytes(keyBytes.toArray(), bits);
    final long[] tweak = tweakFromBytes(null);
    final byte[] blockBytes = new byte[bits / 8];
    final long[] block = new long[blockBytes.length / 8];
    boolean firstBlock = true;
    int padding = -1;
    while (!reader.eof()) {
      reader.read(blockBytes, blockBytes.length, 0);
      blockFromBytes(blockBytes, block);
      decryptBlock(bits, key, tweak, block);
      blockToBytes(block, blockBytes);
      final int offset = firstBlock ? 1 : 0;
      if (firstBlock) {
        padding = blockBytes[0] & 0xff << 0;
      }
      final int limit = reader.eof() ? blockBytes.length - offset - padding : blockBytes.length - offset;
      if (limit > 0) {
        writer.write(blockBytes, limit, offset);
      }
      firstBlock = false;
    }
  }

  private static void decryptBlock(final int bits, final long[] key, final long[] tweak, final long[] block) {
    switch (bits) {
      case BITS_256:
        decryptBlock256(key, tweak, block);
        break;
      case BITS_512:
        decryptBlock512(key, tweak, block);
        break;
      case BITS_1024:
        decryptBlock1024(key, tweak, block);
        break;
      default:
        throw new RuntimeException("Bits " + bits + " not supported");
    }
  }

  private static void decryptBlock1024(final long[] key, final long[] tweak, final long[] block) {
    if (key.length != 33) {
      throw new IllegalArgumentException();
    }
    if (tweak.length != 5) {
      throw new IllegalArgumentException();
    }
    int d = (ROUNDS_1024 / 4) - 1;
    while (d >= 1) {
      final int dm17 = MOD17[d];
      final int dm3 = MOD3[d];
      block[0] -= key[dm17 + 1];
      block[1] -= key[dm17 + 2];
      block[2] -= key[dm17 + 3];
      block[3] -= key[dm17 + 4];
      block[4] -= key[dm17 + 5];
      block[5] -= key[dm17 + 6];
      block[6] -= key[dm17 + 7];
      block[7] -= key[dm17 + 8];
      block[8] -= key[dm17 + 9];
      block[9] -= key[dm17 + 10];
      block[10] -= key[dm17 + 11];
      block[11] -= key[dm17 + 12];
      block[12] -= key[dm17 + 13];
      block[13] -= key[dm17 + 14];
      block[13] -= tweak[dm3 + 1];
      block[14] -= key[dm17 + 15];
      block[14] -= tweak[dm3 + 2];
      block[15] -= key[dm17 + 16];
      block[15] -= d;
      block[15] -= 1;
      block[15] = xorRotr(block[15], ROTATION_1024[7][0], block[0]);
      block[0] -= block[15];
      block[11] = xorRotr(block[11], ROTATION_1024[7][1], block[2]);
      block[2] -= block[11];
      block[13] = xorRotr(block[13], ROTATION_1024[7][2], block[6]);
      block[6] -= block[13];
      block[9] = xorRotr(block[9], ROTATION_1024[7][3], block[4]);
      block[4] -= block[9];
      block[1] = xorRotr(block[1], ROTATION_1024[7][4], block[14]);
      block[14] -= block[1];
      block[5] = xorRotr(block[5], ROTATION_1024[7][5], block[8]);
      block[8] -= block[5];
      block[3] = xorRotr(block[3], ROTATION_1024[7][6], block[10]);
      block[10] -= block[3];
      block[7] = xorRotr(block[7], ROTATION_1024[7][7], block[12]);
      block[12] -= block[7];
      block[7] = xorRotr(block[7], ROTATION_1024[6][0], block[0]);
      block[0] -= block[7];
      block[5] = xorRotr(block[5], ROTATION_1024[6][1], block[2]);
      block[2] -= block[5];
      block[3] = xorRotr(block[3], ROTATION_1024[6][2], block[4]);
      block[4] -= block[3];
      block[1] = xorRotr(block[1], ROTATION_1024[6][3], block[6]);
      block[6] -= block[1];
      block[15] = xorRotr(block[15], ROTATION_1024[6][4], block[12]);
      block[12] -= block[15];
      block[13] = xorRotr(block[13], ROTATION_1024[6][5], block[14]);
      block[14] -= block[13];
      block[11] = xorRotr(block[11], ROTATION_1024[6][6], block[8]);
      block[8] -= block[11];
      block[9] = xorRotr(block[9], ROTATION_1024[6][7], block[10]);
      block[10] -= block[9];
      block[9] = xorRotr(block[9], ROTATION_1024[5][0], block[0]);
      block[0] -= block[9];
      block[13] = xorRotr(block[13], ROTATION_1024[5][1], block[2]);
      block[2] -= block[13];
      block[11] = xorRotr(block[11], ROTATION_1024[5][2], block[6]);
      block[6] -= block[11];
      block[15] = xorRotr(block[15], ROTATION_1024[5][3], block[4]);
      block[4] -= block[15];
      block[7] = xorRotr(block[7], ROTATION_1024[5][4], block[10]);
      block[10] -= block[7];
      block[3] = xorRotr(block[3], ROTATION_1024[5][5], block[12]);
      block[12] -= block[3];
      block[5] = xorRotr(block[5], ROTATION_1024[5][6], block[14]);
      block[14] -= block[5];
      block[1] = xorRotr(block[1], ROTATION_1024[5][7], block[8]);
      block[8] -= block[1];
      block[1] = xorRotr(block[1], ROTATION_1024[4][0], block[0]);
      block[0] -= block[1];
      block[3] = xorRotr(block[3], ROTATION_1024[4][1], block[2]);
      block[2] -= block[3];
      block[5] = xorRotr(block[5], ROTATION_1024[4][2], block[4]);
      block[4] -= block[5];
      block[7] = xorRotr(block[7], ROTATION_1024[4][3], block[6]);
      block[6] -= block[7];
      block[9] = xorRotr(block[9], ROTATION_1024[4][4], block[8]);
      block[8] -= block[9];
      block[11] = xorRotr(block[11], ROTATION_1024[4][5], block[10]);
      block[10] -= block[11];
      block[13] = xorRotr(block[13], ROTATION_1024[4][6], block[12]);
      block[12] -= block[13];
      block[15] = xorRotr(block[15], ROTATION_1024[4][7], block[14]);
      block[14] -= block[15];
      block[0] -= key[dm17];
      block[1] -= key[dm17 + 1];
      block[2] -= key[dm17 + 2];
      block[3] -= key[dm17 + 3];
      block[4] -= key[dm17 + 4];
      block[5] -= key[dm17 + 5];
      block[6] -= key[dm17 + 6];
      block[7] -= key[dm17 + 7];
      block[8] -= key[dm17 + 8];
      block[9] -= key[dm17 + 9];
      block[10] -= key[dm17 + 10];
      block[11] -= key[dm17 + 11];
      block[12] -= key[dm17 + 12];
      block[13] -= key[dm17 + 13];
      block[13] -= tweak[dm3];
      block[14] -= key[dm17 + 14];
      block[14] -= tweak[dm3 + 1];
      block[15] -= key[dm17 + 15];
      block[15] -= d;
      block[15] = xorRotr(block[15], ROTATION_1024[3][0], block[0]);
      block[0] -= block[15];
      block[11] = xorRotr(block[11], ROTATION_1024[3][1], block[2]);
      block[2] -= block[11];
      block[13] = xorRotr(block[13], ROTATION_1024[3][2], block[6]);
      block[6] -= block[13];
      block[9] = xorRotr(block[9], ROTATION_1024[3][3], block[4]);
      block[4] -= block[9];
      block[1] = xorRotr(block[1], ROTATION_1024[3][4], block[14]);
      block[14] -= block[1];
      block[5] = xorRotr(block[5], ROTATION_1024[3][5], block[8]);
      block[8] -= block[5];
      block[3] = xorRotr(block[3], ROTATION_1024[3][6], block[10]);
      block[10] -= block[3];
      block[7] = xorRotr(block[7], ROTATION_1024[3][7], block[12]);
      block[12] -= block[7];
      block[7] = xorRotr(block[7], ROTATION_1024[2][0], block[0]);
      block[0] -= block[7];
      block[5] = xorRotr(block[5], ROTATION_1024[2][1], block[2]);
      block[2] -= block[5];
      block[3] = xorRotr(block[3], ROTATION_1024[2][2], block[4]);
      block[4] -= block[3];
      block[1] = xorRotr(block[1], ROTATION_1024[2][3], block[6]);
      block[6] -= block[1];
      block[15] = xorRotr(block[15], ROTATION_1024[2][4], block[12]);
      block[12] -= block[15];
      block[13] = xorRotr(block[13], ROTATION_1024[2][5], block[14]);
      block[14] -= block[13];
      block[11] = xorRotr(block[11], ROTATION_1024[2][6], block[8]);
      block[8] -= block[11];
      block[9] = xorRotr(block[9], ROTATION_1024[2][7], block[10]);
      block[10] -= block[9];
      block[9] = xorRotr(block[9], ROTATION_1024[1][0], block[0]);
      block[0] -= block[9];
      block[13] = xorRotr(block[13], ROTATION_1024[1][1], block[2]);
      block[2] -= block[13];
      block[11] = xorRotr(block[11], ROTATION_1024[1][2], block[6]);
      block[6] -= block[11];
      block[15] = xorRotr(block[15], ROTATION_1024[1][3], block[4]);
      block[4] -= block[15];
      block[7] = xorRotr(block[7], ROTATION_1024[1][4], block[10]);
      block[10] -= block[7];
      block[3] = xorRotr(block[3], ROTATION_1024[1][5], block[12]);
      block[12] -= block[3];
      block[5] = xorRotr(block[5], ROTATION_1024[1][6], block[14]);
      block[14] -= block[5];
      block[1] = xorRotr(block[1], ROTATION_1024[1][7], block[8]);
      block[8] -= block[1];
      block[1] = xorRotr(block[1], ROTATION_1024[0][0], block[0]);
      block[0] -= block[1];
      block[3] = xorRotr(block[3], ROTATION_1024[0][1], block[2]);
      block[2] -= block[3];
      block[5] = xorRotr(block[5], ROTATION_1024[0][2], block[4]);
      block[4] -= block[5];
      block[7] = xorRotr(block[7], ROTATION_1024[0][3], block[6]);
      block[6] -= block[7];
      block[9] = xorRotr(block[9], ROTATION_1024[0][4], block[8]);
      block[8] -= block[9];
      block[11] = xorRotr(block[11], ROTATION_1024[0][5], block[10]);
      block[10] -= block[11];
      block[13] = xorRotr(block[13], ROTATION_1024[0][6], block[12]);
      block[12] -= block[13];
      block[15] = xorRotr(block[15], ROTATION_1024[0][7], block[14]);
      block[14] -= block[15];
      d -= 2;
    }
    block[0] -= key[0];
    block[1] -= key[1];
    block[2] -= key[2];
    block[3] -= key[3];
    block[4] -= key[4];
    block[5] -= key[5];
    block[6] -= key[6];
    block[7] -= key[7];
    block[8] -= key[8];
    block[9] -= key[9];
    block[10] -= key[10];
    block[11] -= key[11];
    block[12] -= key[12];
    block[13] -= key[13];
    block[13] -= tweak[0];
    block[14] -= key[14];
    block[14] -= tweak[1];
    block[15] -= key[15];
  }

  private static void decryptBlock256(final long[] key, final long[] tweak, final long[] block) {
    if (key.length != 9) {
      throw new IllegalArgumentException();
    }
    if (tweak.length != 5) {
      throw new IllegalArgumentException();
    }
    int d = (ROUNDS_256 / 4) - 1;
    while (d >= 1) {
      final int dm5 = MOD5[d];
      final int dm3 = MOD3[d];
      block[0] -= key[dm5 + 1];
      block[1] -= key[dm5 + 2];
      block[1] -= tweak[dm3 + 1];
      block[2] -= key[dm5 + 3];
      block[2] -= tweak[dm3 + 2];
      block[3] -= key[dm5 + 4];
      block[3] -= d;
      block[3] -= 1;
      block[3] = xorRotr(block[3], ROTATION_256[7][0], block[0]);
      block[0] -= block[3];
      block[1] = xorRotr(block[1], ROTATION_256[7][1], block[2]);
      block[2] -= block[1];
      block[1] = xorRotr(block[1], ROTATION_256[6][0], block[0]);
      block[0] -= block[1];
      block[3] = xorRotr(block[3], ROTATION_256[6][1], block[2]);
      block[2] -= block[3];
      block[3] = xorRotr(block[3], ROTATION_256[5][0], block[0]);
      block[0] -= block[3];
      block[1] = xorRotr(block[1], ROTATION_256[5][1], block[2]);
      block[2] -= block[1];
      block[1] = xorRotr(block[1], ROTATION_256[4][0], block[0]);
      block[0] -= block[1];
      block[3] = xorRotr(block[3], ROTATION_256[4][1], block[2]);
      block[2] -= block[3];
      block[0] -= key[dm5];
      block[1] -= key[dm5 + 1];
      block[1] -= tweak[dm3];
      block[2] -= key[dm5 + 2];
      block[2] -= tweak[dm3 + 1];
      block[3] -= key[dm5 + 3];
      block[3] -= d;
      block[3] = xorRotr(block[3], ROTATION_256[3][0], block[0]);
      block[0] -= block[3];
      block[1] = xorRotr(block[1], ROTATION_256[3][1], block[2]);
      block[2] -= block[1];
      block[1] = xorRotr(block[1], ROTATION_256[2][0], block[0]);
      block[0] -= block[1];
      block[3] = xorRotr(block[3], ROTATION_256[2][1], block[2]);
      block[2] -= block[3];
      block[3] = xorRotr(block[3], ROTATION_256[1][0], block[0]);
      block[0] -= block[3];
      block[1] = xorRotr(block[1], ROTATION_256[1][1], block[2]);
      block[2] -= block[1];
      block[1] = xorRotr(block[1], ROTATION_256[0][0], block[0]);
      block[0] -= block[1];
      block[3] = xorRotr(block[3], ROTATION_256[0][1], block[2]);
      block[2] -= block[3];
      d = d - 2;
    }
    block[0] -= key[0];
    block[1] -= key[1];
    block[1] -= tweak[0];
    block[2] -= key[2];
    block[2] -= tweak[1];
    block[3] -= key[3];
  }

  private static void decryptBlock512(final long[] key, final long[] tweak, final long[] block) {
    if (key.length != 17) {
      throw new IllegalArgumentException();
    }
    if (tweak.length != 5) {
      throw new IllegalArgumentException();
    }
    int d = (ROUNDS_512 / 4) - 1;
    while (d >= 1) {
      final int dm9 = MOD9[d];
      final int dm3 = MOD3[d];
      block[0] -= key[dm9 + 1];
      block[1] -= key[dm9 + 2];
      block[2] -= key[dm9 + 3];
      block[3] -= key[dm9 + 4];
      block[4] -= key[dm9 + 5];
      block[5] -= key[dm9 + 6];
      block[5] -= tweak[dm3 + 1];
      block[6] -= key[dm9 + 7];
      block[6] -= tweak[dm3 + 2];
      block[7] -= key[dm9 + 8];
      block[7] -= d;
      block[7] -= 1;
      block[1] = xorRotr(block[1], ROTATION_512[7][0], block[6]);
      block[6] -= block[1];
      block[7] = xorRotr(block[7], ROTATION_512[7][1], block[0]);
      block[0] -= block[7];
      block[5] = xorRotr(block[5], ROTATION_512[7][2], block[2]);
      block[2] -= block[5];
      block[3] = xorRotr(block[3], ROTATION_512[7][3], block[4]);
      block[4] -= block[3];
      block[1] = xorRotr(block[1], ROTATION_512[6][0], block[4]);
      block[4] -= block[1];
      block[3] = xorRotr(block[3], ROTATION_512[6][1], block[6]);
      block[6] -= block[3];
      block[5] = xorRotr(block[5], ROTATION_512[6][2], block[0]);
      block[0] -= block[5];
      block[7] = xorRotr(block[7], ROTATION_512[6][3], block[2]);
      block[2] -= block[7];
      block[1] = xorRotr(block[1], ROTATION_512[5][0], block[2]);
      block[2] -= block[1];
      block[7] = xorRotr(block[7], ROTATION_512[5][1], block[4]);
      block[4] -= block[7];
      block[5] = xorRotr(block[5], ROTATION_512[5][2], block[6]);
      block[6] -= block[5];
      block[3] = xorRotr(block[3], ROTATION_512[5][3], block[0]);
      block[0] -= block[3];
      block[1] = xorRotr(block[1], ROTATION_512[4][0], block[0]);
      block[0] -= block[1];
      block[3] = xorRotr(block[3], ROTATION_512[4][1], block[2]);
      block[2] -= block[3];
      block[5] = xorRotr(block[5], ROTATION_512[4][2], block[4]);
      block[4] -= block[5];
      block[7] = xorRotr(block[7], ROTATION_512[4][3], block[6]);
      block[6] -= block[7];
      block[0] -= key[dm9];
      block[1] -= key[dm9 + 1];
      block[2] -= key[dm9 + 2];
      block[3] -= key[dm9 + 3];
      block[4] -= key[dm9 + 4];
      block[5] -= key[dm9 + 5];
      block[5] -= tweak[dm3];
      block[6] -= key[dm9 + 6];
      block[6] -= tweak[dm3 + 1];
      block[7] -= key[dm9 + 7];
      block[7] -= d;
      block[1] = xorRotr(block[1], ROTATION_512[3][0], block[6]);
      block[6] -= block[1];
      block[7] = xorRotr(block[7], ROTATION_512[3][1], block[0]);
      block[0] -= block[7];
      block[5] = xorRotr(block[5], ROTATION_512[3][2], block[2]);
      block[2] -= block[5];
      block[3] = xorRotr(block[3], ROTATION_512[3][3], block[4]);
      block[4] -= block[3];
      block[1] = xorRotr(block[1], ROTATION_512[2][0], block[4]);
      block[4] -= block[1];
      block[3] = xorRotr(block[3], ROTATION_512[2][1], block[6]);
      block[6] -= block[3];
      block[5] = xorRotr(block[5], ROTATION_512[2][2], block[0]);
      block[0] -= block[5];
      block[7] = xorRotr(block[7], ROTATION_512[2][3], block[2]);
      block[2] -= block[7];
      block[1] = xorRotr(block[1], ROTATION_512[1][0], block[2]);
      block[2] -= block[1];
      block[7] = xorRotr(block[7], ROTATION_512[1][1], block[4]);
      block[4] -= block[7];
      block[5] = xorRotr(block[5], ROTATION_512[1][2], block[6]);
      block[6] -= block[5];
      block[3] = xorRotr(block[3], ROTATION_512[1][3], block[0]);
      block[0] -= block[3];
      block[1] = xorRotr(block[1], ROTATION_512[0][0], block[0]);
      block[0] -= block[1];
      block[3] = xorRotr(block[3], ROTATION_512[0][1], block[2]);
      block[2] -= block[3];
      block[5] = xorRotr(block[5], ROTATION_512[0][2], block[4]);
      block[4] -= block[5];
      block[7] = xorRotr(block[7], ROTATION_512[0][3], block[6]);
      block[6] -= block[7];
      d -= 2;
    }
    block[0] -= key[0];
    block[1] -= key[1];
    block[2] -= key[2];
    block[3] -= key[3];
    block[4] -= key[4];
    block[5] -= key[5];
    block[5] -= tweak[0];
    block[6] -= key[6];
    block[6] -= tweak[1];
    block[7] -= key[7];
  }

  public static IBytes encrypt(final int bits, final IBytes key, final IBytes data) {
    final int encryptLength = ThreeFish.encryptLength(bits, data.length());
    final IOWriterBytes writerEncrypted = new IOWriterBytes(encryptLength);
    ThreeFish.encrypt(bits, key, data.asReader(), writerEncrypted);
    return writerEncrypted.toIBytes();
  }

  public static void encrypt(
    final int bits,
    final IBytes keyBytes,
    final IOReader reader,
    final IOWriter writer) {
    final long[] key = keyFromBytes(keyBytes.toArray(), bits);
    final long[] tweak = tweakFromBytes(null);
    final byte[] blockBytes = new byte[bits / 8];
    final long[] block = new long[blockBytes.length / 8];
    final int length = reader.available();
    final int padding = encryptLength(bits, length) - (length + 1);
    int offset = 0;
    int blockNumber = 0;
    while (!reader.eof()) {
      if (blockNumber == 0) {
        blockBytes[offset++] = (byte) (((padding) >>> 0) & 0xFF);
      }
      final int copySize = Math.min(reader.available(), blockBytes.length - offset);
      reader.read(blockBytes, copySize, offset);
      offset += copySize;
      if (offset != blockBytes.length) {
        RandUtils.randBytesTo(blockBytes, blockBytes.length - offset, offset);
      }
      blockFromBytes(blockBytes, block);
      encryptBlock(bits, key, tweak, block);
      blockToBytes(block, blockBytes);
      writer.write(blockBytes, blockBytes.length, 0);
      offset = 0;
      blockNumber++;
    }
  }

  private static void encryptBlock(final int bits, final long[] key, final long[] tweak, final long[] block) {
    switch (bits) {
      case BITS_256:
        encryptBlock256(key, tweak, block);
        break;
      case BITS_512:
        encryptBlock512(key, tweak, block);
        break;
      case BITS_1024:
        encryptBlock1024(key, tweak, block);
        break;
      default:
        throw new RuntimeException("Bits " + bits + " not supported");
    }
  }

  private static void encryptBlock1024(final long[] key, final long[] tweak, final long[] block) {
    if (key.length != 33) {
      throw new IllegalArgumentException();
    }
    if (tweak.length != 5) {
      throw new IllegalArgumentException();
    }
    block[0] += key[0];
    block[1] += key[1];
    block[2] += key[2];
    block[3] += key[3];
    block[4] += key[4];
    block[5] += key[5];
    block[6] += key[6];
    block[7] += key[7];
    block[8] += key[8];
    block[9] += key[9];
    block[10] += key[10];
    block[11] += key[11];
    block[12] += key[12];
    block[13] += key[13] + tweak[0];
    block[14] += key[14] + tweak[1];
    block[15] += key[15];
    int d = 1;
    while (d < ROUNDS_1024 / 4) {
      final int dm17 = MOD17[d];
      final int dm3 = MOD3[d];
      block[1] = rotlXor(block[1], ROTATION_1024[0][0], block[0] += block[1]);
      block[3] = rotlXor(block[3], ROTATION_1024[0][1], block[2] += block[3]);
      block[5] = rotlXor(block[5], ROTATION_1024[0][2], block[4] += block[5]);
      block[7] = rotlXor(block[7], ROTATION_1024[0][3], block[6] += block[7]);
      block[9] = rotlXor(block[9], ROTATION_1024[0][4], block[8] += block[9]);
      block[11] = rotlXor(block[11], ROTATION_1024[0][5], block[10] += block[11]);
      block[13] = rotlXor(block[13], ROTATION_1024[0][6], block[12] += block[13]);
      block[15] = rotlXor(block[15], ROTATION_1024[0][7], block[14] += block[15]);
      block[9] = rotlXor(block[9], ROTATION_1024[1][0], block[0] += block[9]);
      block[13] = rotlXor(block[13], ROTATION_1024[1][1], block[2] += block[13]);
      block[11] = rotlXor(block[11], ROTATION_1024[1][2], block[6] += block[11]);
      block[15] = rotlXor(block[15], ROTATION_1024[1][3], block[4] += block[15]);
      block[7] = rotlXor(block[7], ROTATION_1024[1][4], block[10] += block[7]);
      block[3] = rotlXor(block[3], ROTATION_1024[1][5], block[12] += block[3]);
      block[5] = rotlXor(block[5], ROTATION_1024[1][6], block[14] += block[5]);
      block[1] = rotlXor(block[1], ROTATION_1024[1][7], block[8] += block[1]);
      block[7] = rotlXor(block[7], ROTATION_1024[2][0], block[0] += block[7]);
      block[5] = rotlXor(block[5], ROTATION_1024[2][1], block[2] += block[5]);
      block[3] = rotlXor(block[3], ROTATION_1024[2][2], block[4] += block[3]);
      block[1] = rotlXor(block[1], ROTATION_1024[2][3], block[6] += block[1]);
      block[15] = rotlXor(block[15], ROTATION_1024[2][4], block[12] += block[15]);
      block[13] = rotlXor(block[13], ROTATION_1024[2][5], block[14] += block[13]);
      block[11] = rotlXor(block[11], ROTATION_1024[2][6], block[8] += block[11]);
      block[9] = rotlXor(block[9], ROTATION_1024[2][7], block[10] += block[9]);
      block[15] = rotlXor(block[15], ROTATION_1024[3][0], block[0] += block[15]);
      block[11] = rotlXor(block[11], ROTATION_1024[3][1], block[2] += block[11]);
      block[13] = rotlXor(block[13], ROTATION_1024[3][2], block[6] += block[13]);
      block[9] = rotlXor(block[9], ROTATION_1024[3][3], block[4] += block[9]);
      block[1] = rotlXor(block[1], ROTATION_1024[3][4], block[14] += block[1]);
      block[5] = rotlXor(block[5], ROTATION_1024[3][5], block[8] += block[5]);
      block[3] = rotlXor(block[3], ROTATION_1024[3][6], block[10] += block[3]);
      block[7] = rotlXor(block[7], ROTATION_1024[3][7], block[12] += block[7]);
      block[0] += key[dm17];
      block[1] += key[dm17 + 1];
      block[2] += key[dm17 + 2];
      block[3] += key[dm17 + 3];
      block[4] += key[dm17 + 4];
      block[5] += key[dm17 + 5];
      block[6] += key[dm17 + 6];
      block[7] += key[dm17 + 7];
      block[8] += key[dm17 + 8];
      block[9] += key[dm17 + 9];
      block[10] += key[dm17 + 10];
      block[11] += key[dm17 + 11];
      block[12] += key[dm17 + 12];
      block[13] += key[dm17 + 13] + tweak[dm3];
      block[14] += key[dm17 + 14] + tweak[dm3 + 1];
      block[15] += key[dm17 + 15] + d;
      block[1] = rotlXor(block[1], ROTATION_1024[4][0], block[0] += block[1]);
      block[3] = rotlXor(block[3], ROTATION_1024[4][1], block[2] += block[3]);
      block[5] = rotlXor(block[5], ROTATION_1024[4][2], block[4] += block[5]);
      block[7] = rotlXor(block[7], ROTATION_1024[4][3], block[6] += block[7]);
      block[9] = rotlXor(block[9], ROTATION_1024[4][4], block[8] += block[9]);
      block[11] = rotlXor(block[11], ROTATION_1024[4][5], block[10] += block[11]);
      block[13] = rotlXor(block[13], ROTATION_1024[4][6], block[12] += block[13]);
      block[15] = rotlXor(block[15], ROTATION_1024[4][7], block[14] += block[15]);
      block[9] = rotlXor(block[9], ROTATION_1024[5][0], block[0] += block[9]);
      block[13] = rotlXor(block[13], ROTATION_1024[5][1], block[2] += block[13]);
      block[11] = rotlXor(block[11], ROTATION_1024[5][2], block[6] += block[11]);
      block[15] = rotlXor(block[15], ROTATION_1024[5][3], block[4] += block[15]);
      block[7] = rotlXor(block[7], ROTATION_1024[5][4], block[10] += block[7]);
      block[3] = rotlXor(block[3], ROTATION_1024[5][5], block[12] += block[3]);
      block[5] = rotlXor(block[5], ROTATION_1024[5][6], block[14] += block[5]);
      block[1] = rotlXor(block[1], ROTATION_1024[5][7], block[8] += block[1]);
      block[7] = rotlXor(block[7], ROTATION_1024[6][0], block[0] += block[7]);
      block[5] = rotlXor(block[5], ROTATION_1024[6][1], block[2] += block[5]);
      block[3] = rotlXor(block[3], ROTATION_1024[6][2], block[4] += block[3]);
      block[1] = rotlXor(block[1], ROTATION_1024[6][3], block[6] += block[1]);
      block[15] = rotlXor(block[15], ROTATION_1024[6][4], block[12] += block[15]);
      block[13] = rotlXor(block[13], ROTATION_1024[6][5], block[14] += block[13]);
      block[11] = rotlXor(block[11], ROTATION_1024[6][6], block[8] += block[11]);
      block[9] = rotlXor(block[9], ROTATION_1024[6][7], block[10] += block[9]);
      block[15] = rotlXor(block[15], ROTATION_1024[7][0], block[0] += block[15]);
      block[11] = rotlXor(block[11], ROTATION_1024[7][1], block[2] += block[11]);
      block[13] = rotlXor(block[13], ROTATION_1024[7][2], block[6] += block[13]);
      block[9] = rotlXor(block[9], ROTATION_1024[7][3], block[4] += block[9]);
      block[1] = rotlXor(block[1], ROTATION_1024[7][4], block[14] += block[1]);
      block[5] = rotlXor(block[5], ROTATION_1024[7][5], block[8] += block[5]);
      block[3] = rotlXor(block[3], ROTATION_1024[7][6], block[10] += block[3]);
      block[7] = rotlXor(block[7], ROTATION_1024[7][7], block[12] += block[7]);
      block[0] += key[dm17 + 1];
      block[1] += key[dm17 + 2];
      block[2] += key[dm17 + 3];
      block[3] += key[dm17 + 4];
      block[4] += key[dm17 + 5];
      block[5] += key[dm17 + 6];
      block[6] += key[dm17 + 7];
      block[7] += key[dm17 + 8];
      block[8] += key[dm17 + 9];
      block[9] += key[dm17 + 10];
      block[10] += key[dm17 + 11];
      block[11] += key[dm17 + 12];
      block[12] += key[dm17 + 13];
      block[13] += key[dm17 + 14] + tweak[dm3 + 1];
      block[14] += key[dm17 + 15] + tweak[dm3 + 2];
      block[15] += key[dm17 + 16] + d + 1;
      d += 2;
    }
  }

  private static void encryptBlock256(final long[] key, final long[] tweak, final long[] block) {
    if (key.length != 9) {
      throw new IllegalArgumentException();
    }
    if (tweak.length != 5) {
      throw new IllegalArgumentException();
    }
    block[0] += key[0];
    block[1] += key[1] + tweak[0];
    block[2] += key[2] + tweak[1];
    block[3] += key[3];
    int d = 1;
    while (d < ROUNDS_256 / 4) {
      final int dm5 = MOD5[d];
      final int dm3 = MOD3[d];
      block[1] = rotlXor(block[1], ROTATION_256[0][0], block[0] += block[1]);
      block[3] = rotlXor(block[3], ROTATION_256[0][1], block[2] += block[3]);
      block[3] = rotlXor(block[3], ROTATION_256[1][0], block[0] += block[3]);
      block[1] = rotlXor(block[1], ROTATION_256[1][1], block[2] += block[1]);
      block[1] = rotlXor(block[1], ROTATION_256[2][0], block[0] += block[1]);
      block[3] = rotlXor(block[3], ROTATION_256[2][1], block[2] += block[3]);
      block[3] = rotlXor(block[3], ROTATION_256[3][0], block[0] += block[3]);
      block[1] = rotlXor(block[1], ROTATION_256[3][1], block[2] += block[1]);
      block[0] += key[dm5];
      block[1] += key[dm5 + 1] + tweak[dm3];
      block[2] += key[dm5 + 2] + tweak[dm3 + 1];
      block[3] += key[dm5 + 3] + d;
      block[1] = rotlXor(block[1], ROTATION_256[4][0], block[0] += block[1]);
      block[3] = rotlXor(block[3], ROTATION_256[4][1], block[2] += block[3]);
      block[3] = rotlXor(block[3], ROTATION_256[5][0], block[0] += block[3]);
      block[1] = rotlXor(block[1], ROTATION_256[5][1], block[2] += block[1]);
      block[1] = rotlXor(block[1], ROTATION_256[6][0], block[0] += block[1]);
      block[3] = rotlXor(block[3], ROTATION_256[6][1], block[2] += block[3]);
      block[3] = rotlXor(block[3], ROTATION_256[7][0], block[0] += block[3]);
      block[1] = rotlXor(block[1], ROTATION_256[7][1], block[2] += block[1]);
      block[0] += key[dm5 + 1];
      block[1] += key[dm5 + 2] + tweak[dm3 + 1];
      block[2] += key[dm5 + 3] + tweak[dm3 + 2];
      block[3] += key[dm5 + 4] + d + 1;
      d += 2;
    }
  }

  private static void encryptBlock512(final long[] key, final long[] tweak, final long[] block) {
    if (key.length != 17) {
      throw new IllegalArgumentException();
    }
    if (tweak.length != 5) {
      throw new IllegalArgumentException();
    }
    block[0] += key[0];
    block[1] += key[1];
    block[2] += key[2];
    block[3] += key[3];
    block[4] += key[4];
    block[5] += key[5] + tweak[0];
    block[6] += key[6] + tweak[1];
    block[7] += key[7];
    int d = 1;
    while (d < ROUNDS_512 / 4) {
      final int dm9 = MOD9[d];
      final int dm3 = MOD3[d];
      block[1] = rotlXor(block[1], ROTATION_512[0][0], block[0] += block[1]);
      block[3] = rotlXor(block[3], ROTATION_512[0][1], block[2] += block[3]);
      block[5] = rotlXor(block[5], ROTATION_512[0][2], block[4] += block[5]);
      block[7] = rotlXor(block[7], ROTATION_512[0][3], block[6] += block[7]);
      block[1] = rotlXor(block[1], ROTATION_512[1][0], block[2] += block[1]);
      block[7] = rotlXor(block[7], ROTATION_512[1][1], block[4] += block[7]);
      block[5] = rotlXor(block[5], ROTATION_512[1][2], block[6] += block[5]);
      block[3] = rotlXor(block[3], ROTATION_512[1][3], block[0] += block[3]);
      block[1] = rotlXor(block[1], ROTATION_512[2][0], block[4] += block[1]);
      block[3] = rotlXor(block[3], ROTATION_512[2][1], block[6] += block[3]);
      block[5] = rotlXor(block[5], ROTATION_512[2][2], block[0] += block[5]);
      block[7] = rotlXor(block[7], ROTATION_512[2][3], block[2] += block[7]);
      block[1] = rotlXor(block[1], ROTATION_512[3][0], block[6] += block[1]);
      block[7] = rotlXor(block[7], ROTATION_512[3][1], block[0] += block[7]);
      block[5] = rotlXor(block[5], ROTATION_512[3][2], block[2] += block[5]);
      block[3] = rotlXor(block[3], ROTATION_512[3][3], block[4] += block[3]);
      block[0] += key[dm9];
      block[1] += key[dm9 + 1];
      block[2] += key[dm9 + 2];
      block[3] += key[dm9 + 3];
      block[4] += key[dm9 + 4];
      block[5] += key[dm9 + 5] + tweak[dm3];
      block[6] += key[dm9 + 6] + tweak[dm3 + 1];
      block[7] += key[dm9 + 7] + d;
      block[1] = rotlXor(block[1], ROTATION_512[4][0], block[0] += block[1]);
      block[3] = rotlXor(block[3], ROTATION_512[4][1], block[2] += block[3]);
      block[5] = rotlXor(block[5], ROTATION_512[4][2], block[4] += block[5]);
      block[7] = rotlXor(block[7], ROTATION_512[4][3], block[6] += block[7]);
      block[1] = rotlXor(block[1], ROTATION_512[5][0], block[2] += block[1]);
      block[7] = rotlXor(block[7], ROTATION_512[5][1], block[4] += block[7]);
      block[5] = rotlXor(block[5], ROTATION_512[5][2], block[6] += block[5]);
      block[3] = rotlXor(block[3], ROTATION_512[5][3], block[0] += block[3]);
      block[1] = rotlXor(block[1], ROTATION_512[6][0], block[4] += block[1]);
      block[3] = rotlXor(block[3], ROTATION_512[6][1], block[6] += block[3]);
      block[5] = rotlXor(block[5], ROTATION_512[6][2], block[0] += block[5]);
      block[7] = rotlXor(block[7], ROTATION_512[6][3], block[2] += block[7]);
      block[1] = rotlXor(block[1], ROTATION_512[7][0], block[6] += block[1]);
      block[7] = rotlXor(block[7], ROTATION_512[7][1], block[0] += block[7]);
      block[5] = rotlXor(block[5], ROTATION_512[7][2], block[2] += block[5]);
      block[3] = rotlXor(block[3], ROTATION_512[7][3], block[4] += block[3]);
      block[0] += key[dm9 + 1];
      block[1] += key[dm9 + 2];
      block[2] += key[dm9 + 3];
      block[3] += key[dm9 + 4];
      block[4] += key[dm9 + 5];
      block[5] += key[dm9 + 6] + tweak[dm3 + 1];
      block[6] += key[dm9 + 7] + tweak[dm3 + 2];
      block[7] += key[dm9 + 8] + d + 1;
      d += 2;
    }
  }

  public static int encryptLength(final int bits, final int length) {
    final double blockSize = bits / 8;
    final double lengthWithPadding = length + 1;
    return (int) (Math.ceil(lengthWithPadding / blockSize) * blockSize);
  }

  private static int[] initMod(final int v, final int length) {
    final int[] result = new int[length];
    for (int i = 0; i < length; i++) {
      result[i] = i % v;
    }
    return result;
  }

  private static long[] keyFromBytes(final byte[] keyBytes, final int bits) {
    final int blockBytes = bits / 8;
    final int blockWords = blockBytes / 8;
    if (keyBytes.length != blockBytes) {
      throw new IllegalArgumentException("Threefish key must be same size as block (" + blockBytes + " bytes)");
    }
    final long[] key = new long[2 * blockWords + 1];
    long keyMarker = C_240;
    int i = 0;
    while (i < blockWords) {
      key[i] = longFromBytes(keyBytes, i * 8);
      keyMarker = keyMarker ^ key[i];
      i++;
    }
    key[blockWords] = keyMarker;
    i++;
    while (i < key.length) {
      key[i] = key[i - blockWords - 1];
      i++;
    }
    return key;
  }

  private static long longFromBytes(final byte[] bytes, final int offset) {
    if ((offset + 8) > bytes.length) {
      throw new IllegalArgumentException();
    }
    return (bytes[offset + 0] & 0xffL)
      | ((bytes[offset + 1] & 0xffL) << 8)
      | ((bytes[offset + 2] & 0xffL) << 16)
      | ((bytes[offset + 3] & 0xffL) << 24)
      | ((bytes[offset + 4] & 0xffL) << 32)
      | ((bytes[offset + 5] & 0xffL) << 40)
      | ((bytes[offset + 6] & 0xffL) << 48)
      | ((bytes[offset + 7] & 0xffL) << 56);
  }

  private static void longToBytes(final long word, final byte[] bytes, final int offset) {
    if ((offset + 8) > bytes.length) {
      throw new IllegalArgumentException();
    }
    bytes[offset + 0] = (byte) (word);
    bytes[offset + 1] = (byte) ((word >> 8));
    bytes[offset + 2] = (byte) ((word >> 16));
    bytes[offset + 3] = (byte) ((word >> 24));
    bytes[offset + 4] = (byte) ((word >> 32));
    bytes[offset + 5] = (byte) ((word >> 40));
    bytes[offset + 6] = (byte) ((word >> 48));
    bytes[offset + 7] = (byte) ((word >> 56));
  }

  private static long rotlXor(final long x, final int n, final long xor) {
    return ((x << n) | (x >>> -n)) ^ xor;
  }

  private static long[] tweakFromBytes(final byte[] tweakBytes) {
    final long[] t = new long[5];
    if (tweakBytes != null) {
      if (tweakBytes.length != TWEAK_BYTES) {
        throw new IllegalArgumentException("Threefish tweak must be " + TWEAK_BYTES + " bytes");
      }
      t[0] = longFromBytes(tweakBytes, 0);
      t[1] = longFromBytes(tweakBytes, 8);
      t[2] = t[0] ^ t[1];
      t[3] = t[0];
      t[4] = t[1];
    }
    return t;
  }

  private static long xorRotr(final long x, final int n, final long xor) {
    final long xored = x ^ xor;
    return (xored >>> n) | (xored << -n);
  }

}
