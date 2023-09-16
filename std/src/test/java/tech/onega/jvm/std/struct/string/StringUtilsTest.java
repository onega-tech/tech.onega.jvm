package tech.onega.jvm.std.struct.string;

import java.util.function.Consumer;
import org.testng.annotations.Test;
import tech.onega.jvm.std.struct.hash.KV;
import tech.onega.jvm.std.struct.list.IList;
import tech.onega.jvm.std.struct.map.IMap;
import tech.onega.jvm.std.validate.Check;

public class StringUtilsTest {

  private static String execStrBuilder(final Consumer<StringBuilder> consumer) {
    final var builder = new StringBuilder(64);
    consumer.accept(builder);
    return builder.toString();
  }

  @Test
  public void testAppendDigit() {
    Check.equals(execStrBuilder(b -> Strings.appendDigit(b, 300, 0, true)), "");
    Check.equals(execStrBuilder(b -> Strings.appendDigit(b, 300, 0, false)), "");
    Check.equals(execStrBuilder(b -> Strings.appendDigit(b, 300, 1, true)), "0");
    Check.equals(execStrBuilder(b -> Strings.appendDigit(b, 300, 1, false)), "0");
    Check.equals(execStrBuilder(b -> Strings.appendDigit(b, 300, 2, true)), "00");
    Check.equals(execStrBuilder(b -> Strings.appendDigit(b, 300, 2, false)), "00");
    Check.equals(execStrBuilder(b -> Strings.appendDigit(b, 3, 1, true)), "3");
    Check.equals(execStrBuilder(b -> Strings.appendDigit(b, 3, 2, true)), "30");
    Check.equals(execStrBuilder(b -> Strings.appendDigit(b, 3, 3, true)), "300");
    Check.equals(execStrBuilder(b -> Strings.appendDigit(b, 3, 1, false)), "3");
    Check.equals(execStrBuilder(b -> Strings.appendDigit(b, 3, 2, false)), "03");
    Check.equals(execStrBuilder(b -> Strings.appendDigit(b, 3, 3, false)), "003");
  }

  @Test
  public void testCompare() {
    Check.isTrue("a".compareTo("a") == 0);
    Check.isTrue("b".compareTo("a") > 0);
    Check.isTrue("A".compareTo("a") < 0);
  }

  @Test
  public void testJoin() {
    Check.equals(Strings.join(new String[] { "a", "b", "c" }, "."), "a.b.c");
    Check.equals(Strings.join(new String[] { "a", "b", "c" }, ".", 1, 1), "b");
    Check.equals(Strings.join(new String[] { "a", "b", "c" }, ".", 2, 1), "b.c");
  }

  @Test
  public void testReplaceAll() {
    Check.equals(Strings.replaceAll("assert {a}!={b}", "{a}", 1, "{b}", 2), "assert 1!=2");
  }

  @Test
  public void testReplaceVector() {
    Check.equals(Strings.replaceVector("assert {a}!={b}", IList.of(KV.of("{a}", 1), KV.of("{b}", 2))),
      "assert 1!=2");
    Check.equals(Strings.replaceVector("assert {a}!={b}", IMap.of(KV.of("{a}", 1), KV.of("{b}", 2))),
      "assert 1!=2");
  }

  @Test
  public void testSplitStringByLength() {
    Check.equals(Strings.splitStringByLength("", 2), new String[] {});
    Check.equals(Strings.splitStringByLength("a", 2), new String[] { "a" });
    Check.equals(Strings.splitStringByLength("ab", 2), new String[] { "ab" });
    Check.equals(Strings.splitStringByLength("abc", 2), new String[] { "ab", "c" });
    Check.equals(Strings.splitStringByLength("abcd", 2), new String[] { "ab", "cd" });
    Check.equals(Strings.splitStringByLength("abcde", 1), new String[] { "a", "b", "c", "d", "e" });
    Check.equals(Strings.splitStringByLength("abcde", 3), new String[] { "abc", "de" });
  }

}
