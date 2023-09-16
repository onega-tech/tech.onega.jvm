package tech.onega.jvm.std.codec.base;

import java.util.function.BiConsumer;
import org.testng.annotations.Test;
import tech.onega.jvm.std.lang.RandUtils;
import tech.onega.jvm.std.validate.Check;

public class BaseNCodecTest {

  @Test
  public void testEncodeDecode() {
    final BiConsumer<BaseNCodec.Alphabet, String> check = (alphabet, word) -> {
      Check.equals(BaseNCodec.encode(BaseNCodec.decode(word, alphabet), alphabet), word);
    };
    check.accept(BaseNCodec.ALPHABET_BASE_64, "VGhlIHF1aWNrIGJyb3duIGZveCBqdW1wcyBvdmVyIHRoZSBsYXp5IGRvZy4");
    check.accept(BaseNCodec.ALPHABET_BASE_32_HEX, BaseNCodec.ALPHABET_BASE_32_HEX.letters());
    check.accept(BaseNCodec.ALPHABET_BASE_32, BaseNCodec.ALPHABET_BASE_32.letters());
    check.accept(BaseNCodec.ALPHABET_BASE_36, BaseNCodec.ALPHABET_BASE_36.letters());
    check.accept(BaseNCodec.ALPHABET_BASE_52, BaseNCodec.ALPHABET_BASE_52.letters());
    check.accept(BaseNCodec.ALPHABET_BASE_62, BaseNCodec.ALPHABET_BASE_62.letters());
    check.accept(BaseNCodec.ALPHABET_BASE_64, BaseNCodec.ALPHABET_BASE_64.letters());
    check.accept(BaseNCodec.ALPHABET_BASE_85, BaseNCodec.ALPHABET_BASE_85.letters());
    check.accept(BaseNCodec.ALPHABET_BASE_91, BaseNCodec.ALPHABET_BASE_91.letters());
    check.accept(BaseNCodec.ALPHABET_BASE_94, BaseNCodec.ALPHABET_BASE_94.letters());
    check.accept(BaseNCodec.ALPHABET_BASE_98, BaseNCodec.ALPHABET_BASE_98.letters());
    check.accept(BaseNCodec.ALPHABET_Z_85, BaseNCodec.ALPHABET_Z_85.letters());
    final var alph = BaseNCodec.ALPHABET_BASE_64.letters().getBytes();
    final var encoded = BaseNCodec.decode(BaseNCodec.ALPHABET_BASE_64.letters(), BaseNCodec.ALPHABET_BASE_64).toArray();
    Check.equals(encoded.length, 48);
    Check.equals(alph.length, 64);
  }

  @Test
  public void testEncodeDecodeRand() {
    final var dataLength = 32;
    final var alphabets = new BaseNCodec.Alphabet[] {
      BaseNCodec.ALPHABET_BASE_32,
      BaseNCodec.ALPHABET_BASE_32_HEX,
      BaseNCodec.ALPHABET_BASE_36,
      BaseNCodec.ALPHABET_BASE_52,
      BaseNCodec.ALPHABET_BASE_62,
      BaseNCodec.ALPHABET_BASE_64,
      BaseNCodec.ALPHABET_BASE_85,
      BaseNCodec.ALPHABET_BASE_91,
      BaseNCodec.ALPHABET_BASE_94,
      BaseNCodec.ALPHABET_BASE_98,
      BaseNCodec.ALPHABET_Z_85
    };
    final var iterations = 10_000;
    for (var i = 0; i < iterations; i++) {
      final var alphabet = alphabets[RandUtils.randInt(0, alphabets.length - 1)];
      final var data = RandUtils.randIBytes(dataLength);
      final var encoded = BaseNCodec.encode(data, alphabet);
      final var decoded = BaseNCodec.decode(encoded, alphabet);
      Check.equals(data, decoded);
    }
  }

}
