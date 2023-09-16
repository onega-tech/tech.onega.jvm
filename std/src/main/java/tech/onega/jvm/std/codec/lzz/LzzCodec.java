package tech.onega.jvm.std.codec.lzz;

import tech.onega.jvm.std.io.reader.IOReader;
import tech.onega.jvm.std.io.reader.IOReaderBytes;
import tech.onega.jvm.std.io.writer.IOWriter;
import tech.onega.jvm.std.io.writer.IOWriterBytes;
import tech.onega.jvm.std.struct.bytes.IBytes;
import tech.onega.jvm.std.validate.Check;

final public class LzzCodec {

  static final int COMPRESSION_METHOD_LZZ = 0x20;

  static final byte[] MAGIC = new byte[] { 'L', 'Z', '4', 'B', 'l', 'o', 'c', 'k' };

  // static final byte[] MAGIC = new byte[] {};
  static final int MAGIC_LENGTH = MAGIC.length;

  public static final int MIN_BLOCK_SIZE = 64;

  static final int COMPRESSION_LEVEL_BASE = 10;

  public static final int MAX_BLOCK_SIZE = 1 << (COMPRESSION_LEVEL_BASE + 0x0F);

  public static final int COMPRESSION_LEVEL_DEFAULT = 8 + 1;

  public static final int COMPRESSION_LEVEL_MAX = 16 + 1;

  static final int MAX_INPUT_SIZE = 0x7E000000;

  static final int HEADER_LENGTH = MAGIC_LENGTH + 1 + 4 + 4 + 4;

  static final int COMPRESSION_METHOD_RAW = 0x10;

  static final int DEFAULT_SEED = 0x9747b28c;

  static final int MEMORY_USAGE = 14;

  static final int NOT_COMPRESSIBLE_DETECTION_LEVEL = 6;

  static final int MIN_MATCH = 4;

  static final int HASH_LOG = MEMORY_USAGE - 2;

  static final int HASH_TABLE_SIZE = 1 << HASH_LOG;

  static final int SKIP_STRENGTH = Math.max(NOT_COMPRESSIBLE_DETECTION_LEVEL, 2);

  static final int COPY_LENGTH = 8;

  static final int LAST_LITERALS = 5;

  static final int MF_LIMIT = COPY_LENGTH + MIN_MATCH;

  static final int MIN_LENGTH = MF_LIMIT + 1;

  static final int MAX_DISTANCE = 1 << 16;

  static final int ML_BITS = 4;

  static final int ML_MASK = (1 << ML_BITS) - 1;

  static final int RUN_BITS = 8 - ML_BITS;

  static final int RUN_MASK = (1 << RUN_BITS) - 1;

  static final int LZZ_64K_LIMIT = (1 << 16) + (MF_LIMIT - 1);

  static final int HASH_LOG_64K = HASH_LOG + 1;

  static final int HASH_TABLE_SIZE_64K = 1 << HASH_LOG_64K;

  static final int HASH_LOG_HC = 15;

  static final int HASH_TABLE_SIZE_HC = 1 << HASH_LOG_HC;

  static final int OPTIMAL_ML = ML_MASK - 1 + MIN_MATCH;

  public static byte[] encode(final byte[] decompressed) {
    return encode(decompressed, MAX_BLOCK_SIZE, COMPRESSION_LEVEL_MAX);
  }

  public static byte[] encode(final byte[] decompressed, final int blockSize, final int compressorLevel) {
    final IOWriterBytes result = new IOWriterBytes(decompressed.length);
    encode(new IOReaderBytes(decompressed), result, blockSize, compressorLevel);
    return result.toBytes();
  }

  public static IBytes encode(final IBytes decompressed) {
    return encode(decompressed, MAX_BLOCK_SIZE, COMPRESSION_LEVEL_MAX);
  }

  public static IBytes encode(final IBytes decompressed, final int blockSize, final int compressorLevel) {
    final IOWriterBytes result = new IOWriterBytes(decompressed.length() / 4);
    encode(decompressed.asReader(), result, blockSize, compressorLevel);
    return result.toIBytes();
  }

  public static void encode(final IOReader data, final IOWriter result, final int blockSize, final int level) {
    final LzzEncoder compressor = LzzEncoder.create(level);
    final LzzHash checksum = LzzHash.create(DEFAULT_SEED);
    final int compressionLevel = compressionLevel(blockSize);
    final int bufferLength = blockSize;
    final byte[] buffer = new byte[bufferLength];
    final int compressedBlockSize = HEADER_LENGTH + LzzEncoder.maxCompressedLength(blockSize);
    final byte[] compressedBuffer = new byte[compressedBlockSize];
    System.arraycopy(MAGIC, 0, compressedBuffer, 0, MAGIC_LENGTH);
    while (!data.eof()) {
      final int readed = data.read(buffer, blockSize, 0);
      LzzHash.reset(checksum);
      LzzHash.update(checksum, buffer, bufferLength, 0, readed);
      final int check = LzzHash.getValue(checksum);
      int compressedLength = LzzEncoder.encode(compressor, buffer, bufferLength, 0, compressedBuffer,
        compressedBlockSize, HEADER_LENGTH, readed);
      final int compressMethod;
      if (compressedLength >= readed) {
        compressMethod = COMPRESSION_METHOD_RAW;
        compressedLength = readed;
        System.arraycopy(buffer, 0, compressedBuffer, HEADER_LENGTH, readed);
      }
      else {
        compressMethod = COMPRESSION_METHOD_LZZ;
      }
      compressedBuffer[MAGIC_LENGTH] = (byte) (compressMethod | compressionLevel);
      LzzUtils.writeIntLE(compressedLength, compressedBuffer, MAGIC_LENGTH + 1);
      LzzUtils.writeIntLE(readed, compressedBuffer, MAGIC_LENGTH + 5);
      LzzUtils.writeIntLE(check, compressedBuffer, MAGIC_LENGTH + 9);
      Check.equals(MAGIC_LENGTH + 13, HEADER_LENGTH);
      result.write(compressedBuffer, HEADER_LENGTH + compressedLength, 0);
    }
    compressedBuffer[MAGIC_LENGTH] = (byte) (COMPRESSION_METHOD_RAW | compressionLevel);
    LzzUtils.writeIntLE(0, compressedBuffer, MAGIC_LENGTH + 1);
    LzzUtils.writeIntLE(0, compressedBuffer, MAGIC_LENGTH + 5);
    LzzUtils.writeIntLE(0, compressedBuffer, MAGIC_LENGTH + 9);
    Check.equals(MAGIC_LENGTH + 13, HEADER_LENGTH);
    result.write(compressedBuffer, HEADER_LENGTH, 0);
  }

  private static int compressionLevel(final int blockSize) {
    if (blockSize < MIN_BLOCK_SIZE) {
      throw new RuntimeException("blockSize must be >= " + MIN_BLOCK_SIZE + ", got " + blockSize);
    }
    else if (blockSize > MAX_BLOCK_SIZE) {
      throw new RuntimeException("blockSize must be <= " + MAX_BLOCK_SIZE + ", got " + blockSize);
    }
    int compressionLevel = 32 - LzzUtils.numberOfLeadingZeros(blockSize - 1);
    Check.isTrue((1 << compressionLevel) >= blockSize);
    Check.isTrue(blockSize * 2 > (1 << compressionLevel));
    compressionLevel = Math.max(0, compressionLevel - COMPRESSION_LEVEL_BASE);
    Check.isTrue(compressionLevel >= 0 && compressionLevel <= 0x0F);
    return compressionLevel;
  }

  public static byte[] decode(final byte[] compressed) {
    final IOWriterBytes result = new IOWriterBytes(compressed.length);
    decode(new IOReaderBytes(compressed), result);
    return result.toBytes();
  }

  public static IBytes decode(final IBytes compressed) {
    final IOWriterBytes result = new IOWriterBytes(compressed.length());
    decode(compressed.asReader(), result);
    return result.toIBytes();
  }

  public static void decode(final IOReader data, final IOWriter result) {
    final LzzHash checksum = LzzHash.create(DEFAULT_SEED);
    int bufferLength = 0;
    byte[] buffer = null;
    final int compressedBufferLength = HEADER_LENGTH;
    byte[] compressedBuffer = new byte[compressedBufferLength];
    int originalLen = 0;
    while (!data.eof()) {
      int readed = data.read(compressedBuffer, HEADER_LENGTH, 0);
      Check.isTrue(readed == HEADER_LENGTH);
      for (int i = 0; i < MAGIC_LENGTH; ++i) {
        if (compressedBuffer[i] != MAGIC[i]) {
          throw new RuntimeException("Stream is corrupted");
        }
      }
      final int token = compressedBuffer[MAGIC_LENGTH] & 0xFF;
      final int compressionMethod = token & 0xF0;
      final int compressionLevel = COMPRESSION_LEVEL_BASE + (token & 0x0F);
      if (compressionMethod != COMPRESSION_METHOD_RAW && compressionMethod != COMPRESSION_METHOD_LZZ) {
        throw new RuntimeException("Stream is corrupted");
      }
      final int compressedLen = LzzUtils.readIntLE(compressedBuffer, MAGIC_LENGTH + 1);
      originalLen = LzzUtils.readIntLE(compressedBuffer, MAGIC_LENGTH + 5);
      final int check = LzzUtils.readIntLE(compressedBuffer, MAGIC_LENGTH + 9);
      Check.equals(HEADER_LENGTH, MAGIC_LENGTH + 13);
      if (originalLen > 1 << compressionLevel
        || originalLen < 0
        || compressedLen < 0
        || (originalLen == 0 && compressedLen != 0)
        || (originalLen != 0 && compressedLen == 0)
        || (compressionMethod == COMPRESSION_METHOD_RAW && originalLen != compressedLen)) {
        throw new RuntimeException("Stream is corrupted");
      }
      if (originalLen == 0 && compressedLen == 0) {
        if (check != 0) {
          throw new RuntimeException("Stream is corrupted");
        }
        break;
      }
      if (bufferLength < originalLen) {
        bufferLength = Math.max(originalLen, bufferLength * 3 / 2);
        buffer = new byte[bufferLength];
      }
      if (compressionMethod == COMPRESSION_METHOD_RAW) {
        readed = data.read(buffer, originalLen, 0);
        Check.isTrue(readed == originalLen);
      }
      else if (compressionMethod == COMPRESSION_METHOD_LZZ) {
        if (compressedBufferLength < originalLen) {
          compressedBuffer = new byte[Math.max(compressedLen, compressedBufferLength * 3 / 2)];
        }
        readed = data.read(compressedBuffer, compressedLen, 0);
        Check.isTrue(readed == compressedLen);
        final int compressedLen2 = LzzDecoder.decode(compressedBuffer, HEADER_LENGTH, 0, buffer, bufferLength,
          0, originalLen);
        if (compressedLen != compressedLen2) {
          throw new RuntimeException("Stream is corrupted");
        }
      }
      else {
        throw new RuntimeException("Fail compressionMethod");
      }
      LzzHash.reset(checksum);
      LzzHash.update(checksum, buffer, bufferLength, 0, originalLen);
      if (LzzHash.getValue(checksum) != check) {
        throw new RuntimeException("Stream is corrupted");
      }
      result.write(buffer, originalLen, 0);
    }
  }

}
