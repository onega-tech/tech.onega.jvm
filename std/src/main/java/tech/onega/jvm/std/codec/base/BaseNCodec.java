package tech.onega.jvm.std.codec.base;

import java.math.BigInteger;
import tech.onega.jvm.std.annotation.Immutable;
import tech.onega.jvm.std.annotation.ThreadSafe;
import tech.onega.jvm.std.struct.bytes.IBytes;
import tech.onega.jvm.std.struct.map.IMap;
import tech.onega.jvm.std.struct.map.MMap;

@ThreadSafe
final public class BaseNCodec {

  @Immutable
  public static class Alphabet {

    public static Alphabet of(final String letters) {
      final MMap<Character, Integer> map = MMap.create(letters.length());
      for (int i = 0; i < letters.length(); i++) {
        map.add(letters.charAt(i), i);
      }
      final BigInteger base = BigInteger.valueOf(letters.length());
      return new Alphabet(letters, map.destroy(), base);
    }

    private final IMap<Character, Integer> map;

    private final BigInteger base;

    private final String letters;

    private Alphabet(final String letters, final IMap<Character, Integer> map, final BigInteger base) {
      this.map = map;
      this.base = base;
      this.letters = letters;
    }

    public int length() {
      return letters.length();
    }

    public char letter(final int index) {
      return letters.charAt(index);
    }

    public String letters() {
      return letters;
    }

    @Override
    public String toString() {
      return letters;
    }

  }

  public static final Alphabet ALPHABET_BASE_32_HEX = Alphabet.of("0123456789ABCDEFGHIJKLMNOPQRSTUV");

  public static final Alphabet ALPHABET_BASE_32 = Alphabet.of("ABCDEFGHIJKLMNOPQRSTUVWXYZ234567");

  public static final Alphabet ALPHABET_BASE_36 = Alphabet.of("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");

  public static final Alphabet ALPHABET_BASE_52 = Alphabet.of("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");

  public static final Alphabet ALPHABET_BASE_62 = Alphabet
    .of("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");

  public static final Alphabet ALPHABET_BASE_64 = Alphabet
    .of("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/");

  public static final Alphabet ALPHABET_BASE_85 = Alphabet
    .of("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!#$%&()*+-;<=>?@^_`{|}~");

  public static final Alphabet ALPHABET_Z_85 = Alphabet
    .of("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ.-:+=^!/*?&<>()[]{}@%$#");

  public static final Alphabet ALPHABET_BASE_91 = Alphabet
    .of("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!#$%&()*+,./:;<=>?@[]^_`{|}~\"");

  public static final Alphabet ALPHABET_BASE_94 = Alphabet
    .of("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!#$%&()*+,./:;<=>?@[]^_`{|}~\"-\\'");

  public static final Alphabet ALPHABET_BASE_98 = Alphabet
    .of("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!#$%&()*+,./:;<=>?@[]^_`{|}~\"-\\' \t\n\r");

  public static IBytes decode(final String word, final Alphabet alphabet) {
    final byte[] bytes = decodeToBigInteger(word, alphabet).toByteArray();
    final boolean stripSignByte = bytes.length > 1 && bytes[0] == 0 && bytes[1] < 0;
    int leadingZeros = 0;
    for (int i = 0; i < word.length() && word.charAt(i) == alphabet.letter(0); i++) {
      leadingZeros++;
    }
    if (leadingZeros == word.length()) {
      return IBytes.wrap(new byte[leadingZeros]);
    }
    final byte[] tmp = new byte[bytes.length - (stripSignByte ? 1 : 0) + leadingZeros];
    System.arraycopy(bytes, stripSignByte ? 1 : 0, tmp, leadingZeros, tmp.length - leadingZeros);
    return IBytes.wrap(tmp);
  }

  private static BigInteger decodeToBigInteger(final String word, final Alphabet alphabet) {
    BigInteger bi = BigInteger.ZERO;
    for (int i = word.length() - 1; i >= 0; i--) {
      final int alphaIndex = alphabet.map.get(word.charAt(i), -1);
      if (alphaIndex == -1) {
        throw new IllegalStateException("Illegal character " + word.charAt(i) + " at " + i);
      }
      bi = bi.add(BigInteger.valueOf(alphaIndex).multiply(alphabet.base.pow(word.length() - 1 - i)));
    }
    return bi;
  }

  public static String encode(final IBytes data, final Alphabet alphabet) {
    final byte[] input = data.toArray();
    BigInteger bi = new BigInteger(1, input);
    final boolean isZero = bi.equals(BigInteger.ZERO);
    final StringBuilder stringBuilder = new StringBuilder(input.length * 2);
    while (bi.compareTo(alphabet.base) >= 0) {
      final BigInteger mod = bi.mod(alphabet.base);
      stringBuilder.insert(0, alphabet.letter(mod.intValue()));
      bi = bi.subtract(mod).divide(alphabet.base);
    }
    if (!isZero) {
      stringBuilder.insert(0, alphabet.letter(bi.intValue()));
    }
    // Convert leading zeros too.
    for (final byte anInput : input) {
      if (anInput == 0) {
        stringBuilder.insert(0, alphabet.letter(0));
      }
      else {
        break;
      }
    }
    return stringBuilder.toString();
  }

}
