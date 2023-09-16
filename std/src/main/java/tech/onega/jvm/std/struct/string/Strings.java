package tech.onega.jvm.std.struct.string;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import tech.onega.jvm.std.struct.hash.KV;
import tech.onega.jvm.std.struct.vector.Vector;
import tech.onega.jvm.std.validate.Check;

final public class Strings {

  public static void appendDigit(
    StringBuilder builder,
    Object digit,
    int width,
    boolean zeroFromRight) {
    //
    var val = String.valueOf(digit).toCharArray();
    if (val.length == width) {
      builder.append(val);
      return;
    }
    var zeroStart = zeroFromRight ? Math.min(val.length, width) : 0;
    var zeroEnd = zeroFromRight ? width - 1 : Math.max(0, width - val.length - 1);
    var valIndex = Math.max(0, val.length - width);
    for (var i = 0; i < width; i++) {
      if (i >= zeroStart && i <= zeroEnd) {
        builder.append('0');
      }
      else {
        builder.append(val[valIndex++]);
      }
    }
  }

  public static String firstNotBlank(String... values) {
    for (var value : values) {
      if (!isBlank(value)) {
        return value;
      }
    }
    return null;
  }

  public static boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }

  public static boolean isNotBlank(String value) {
    return !isBlank(value);
  }

  public static String join(Iterable<String> values, String joiner) {
    var builder = new StringBuilder();
    joinTo(values, joiner, builder);
    return builder.toString();
  }

  public static String join(String[] items, String joiner) {
    return (items == null || items.length == 0) ? "" : join(items, joiner, items.length, 0);
  }

  public static String join(String[] items, String joiner, int limit, int offset) {
    if (items == null
      || items.length == 0
      || limit == 0
      || offset > items.length) {
      return "";
    }
    var endPos = offset + limit;
    int resultSize;
    {
      var tmp = joiner.length() * (limit - 1);
      for (var i = offset; i < endPos; i++) {
        tmp += items[i].length();
      }
      resultSize = tmp;
    }
    var builder = new StringBuilder(resultSize);
    for (var i = offset; i < endPos; i++) {
      builder.append(items[i]);
      if (i < endPos - 1) {
        builder.append(joiner);
      }
    }
    var result = builder.toString();
    Check.equals(resultSize, result.length(), "Wrong logic resultSize: %s not equals result.length() %s", resultSize, result.length());
    return builder.toString();
  }

  public static void joinTo(Iterable<String> values, String joiner, StringBuilder builder) {
    var iter = values.iterator();
    while (iter.hasNext()) {
      builder.append(iter.next());
      if (iter.hasNext()) {
        builder.append(joiner);
      }
    }
  }

  public static String replace(String text, String repl, String with, int max) {
    if (text == null || repl == null || with == null || repl.length() == 0) {
      return text;
    }
    var buf = new StringBuilder(text.length());
    int start = 0, end;
    while ((end = text.indexOf(repl, start)) != -1) {
      buf.append(text, start, end).append(with);
      start = end + repl.length();
      if (--max == 0) {
        break;
      }
    }
    buf.append(text, start, text.length());
    return buf.toString();
  }

  public static String replaceAll(String source, Object... fromTo) {
    if (fromTo.length == 0) {
      return source;
    }
    var size = fromTo.length / 2;
    var searchList = new String[size];
    var replacementList = new String[size];
    var k = 0;
    for (var i = 0; i < fromTo.length; i = i + 2) {
      searchList[k] = String.valueOf(fromTo[i]);
      replacementList[k] = String.valueOf(fromTo[i + 1]);
      k++;
    }
    return replaceEach(source, searchList, replacementList, false, 0);
  }

  /**
   * @param text
   *   text to search and replace in, no-op if null
   * @param searchList
   *   the Strings to search for, no-op if null
   * @param replacementList
   *   the Strings to replace them with, no-op if null
   * @param repeat
   *   if true, then replace repeatedly until there are no more possible replacements or timeToLive < 0
   * @param timeToLive
   *   if less than 0 then there is a circular reference and endless loop
   */
  public static String replaceEach(
    String text,
    String[] searchList,
    String[] replacementList,
    boolean repeat,
    int timeToLive) {
    //
    if (text == null || text.isEmpty() || searchList == null ||
      searchList.length == 0 || replacementList == null || replacementList.length == 0) {
      return text;
    }
    if (timeToLive < 0) {
      throw new IllegalStateException(
        "Aborting to protect against StackOverflowError - output of one loop is the input of another");
    }
    var searchLength = searchList.length;
    var replacementLength = replacementList.length;
    if (searchLength != replacementLength) {
      throw new IllegalArgumentException(
        "Search and Replace array lengths don't match: " + searchLength + " vs " + replacementLength);
    }
    var noMoreMatchesForReplIndex = new boolean[searchLength];
    var textIndex = -1;
    var replaceIndex = -1;
    var tempIndex = -1;
    for (var i = 0; i < searchLength; i++) {
      if (noMoreMatchesForReplIndex[i] || searchList[i] == null ||
        searchList[i].isEmpty() || replacementList[i] == null) {
        continue;
      }
      tempIndex = text.indexOf(searchList[i]);
      if (tempIndex == -1) {
        noMoreMatchesForReplIndex[i] = true;
      }
      else {
        if (textIndex == -1 || tempIndex < textIndex) {
          textIndex = tempIndex;
          replaceIndex = i;
        }
      }
    }
    if (textIndex == -1) {
      return text;
    }
    var start = 0;
    var increase = 0;
    for (var i = 0; i < searchList.length; i++) {
      if (searchList[i] == null || replacementList[i] == null) {
        continue;
      }
      var greater = replacementList[i].length() - searchList[i].length();
      if (greater > 0) {
        increase += 3 * greater; // assume 3 matches
      }
    }
    increase = Math.min(increase, text.length() / 5);
    StringBuilder buf = new StringBuilder(text.length() + increase);
    while (textIndex != -1) {
      for (var i = start; i < textIndex; i++) {
        buf.append(text.charAt(i));
      }
      buf.append(replacementList[replaceIndex]);
      start = textIndex + searchList[replaceIndex].length();
      textIndex = -1;
      replaceIndex = -1;
      tempIndex = -1;
      for (var i = 0; i < searchLength; i++) {
        if (noMoreMatchesForReplIndex[i] || searchList[i] == null ||
          searchList[i].isEmpty() || replacementList[i] == null) {
          continue;
        }
        tempIndex = text.indexOf(searchList[i], start);
        if (tempIndex == -1) {
          noMoreMatchesForReplIndex[i] = true;
        }
        else {
          if (textIndex == -1 || tempIndex < textIndex) {
            textIndex = tempIndex;
            replaceIndex = i;
          }
        }
      }
    }
    var textLength = text.length();
    for (var i = start; i < textLength; i++) {
      buf.append(text.charAt(i));
    }
    var result = buf.toString();
    if (!repeat) {
      return result;
    }
    return replaceEach(result, searchList, replacementList, repeat, timeToLive - 1);
  }

  public static String replaceVector(String source, Vector<? extends KV<?, ?>> replaces) {
    if (replaces.isEmpty()) {
      return source;
    }
    var size = replaces.size();
    var searchList = new String[size];
    var replacementList = new String[size];
    var i = 0;
    for (var kv : replaces) {
      searchList[i] = String.valueOf(kv.key());
      replacementList[i] = String.valueOf(kv.value());
      i++;
    }
    return replaceEach(source, searchList, replacementList, false, 0);
  }

  public static void setSystemCharset(String charsetName) {
    try {
      System.setProperty("file.encoding", charsetName);
      System.setProperty("client.encoding.override", charsetName);
      Field charset = Charset.class.getDeclaredField("defaultCharset");
      charset.setAccessible(true);
      charset.set(null, null);
    }
    catch (Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  public static String[] splitStringByLength(String src, int length) {
    var resutlSize = (int) Math.ceil((double) src.length() / (double) length);
    var result = new String[resutlSize];
    for (var i = 0; i < result.length; i++) {
      result[i] = src.substring(i * length, Math.min(src.length(), (i + 1) * length));
    }
    return result;
  }

}
