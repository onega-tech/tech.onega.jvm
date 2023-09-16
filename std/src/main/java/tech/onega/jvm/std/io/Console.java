package tech.onega.jvm.std.io;

import java.io.PrintStream;
import tech.onega.jvm.std.codec.json.JsonCodec;
import tech.onega.jvm.std.struct.array.ArrayUtils;

final public class Console {

  public static void dumpBytes(final byte[] bytes) {
    for (int i = 0; i < bytes.length; i++) {
      System.err.print(bytes[i]);
      if (i < bytes.length - 1) {
        System.err.print(',');
        System.err.print(' ');
      }
    }
    System.err.print('\n');
  }

  public static void dumpUint16(final char[] chars) {
    for (int i = 0; i < chars.length; i++) {
      System.err.print((int) chars[i]);
      if (i < chars.length - 1) {
        System.err.print(',');
        System.err.print(' ');
      }
    }
    System.err.print('\n');
  }

  public static void err(final Object... vals) {
    print(System.err, vals);
  }

  public static void errFormat(final String message, final Object... args) {
    printFormat(System.err, message, args);
  }

  public static void errJson(final Object... vals) {
    printJson(System.err, vals);
  }

  public static void log(final Object... vals) {
    print(System.out, vals);
  }

  public static void logFormat(final String message, final Object... args) {
    printFormat(System.out, message, args);
  }

  public static void logJson(final Object... vals) {
    printJson(System.out, vals);
  }

  public static void print(final PrintStream out, final Object... vals) {
    if (vals.length == 1) {
      out.println(String.valueOf(vals[0]));
    }
    else {
      out.println(ArrayUtils.toString(vals, vals.length, 0));
    }
  }

  public static void printFormat(final PrintStream out, final String message, final Object... args) {
    out.println(String.format(message, args));
  }

  public static void printJson(final PrintStream out, final Object... vals) {
    final var jsonVal = (vals != null && vals.length == 1 ? vals[0] : vals);
    out.println(JsonCodec.toString(jsonVal, true));
  }

}
