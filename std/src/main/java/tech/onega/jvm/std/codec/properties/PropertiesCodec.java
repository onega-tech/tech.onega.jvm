package tech.onega.jvm.std.codec.properties;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URI;
import java.util.Properties;
import tech.onega.jvm.std.struct.hash.KV;
import tech.onega.jvm.std.struct.map.IMap;

final public class PropertiesCodec {

  public static IMap<String, String> encode(final byte[] content) throws RuntimeException {
    try {
      final Properties properties = new Properties();
      properties.load(new ByteArrayInputStream(content));
      return properties.entrySet()
        .stream()
        .map(entry -> KV.of(String.valueOf(entry.getKey()), String.valueOf(entry.getValue())))
        .collect(IMap.collector(properties.size()));
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static IMap<String, String> encode(final File file) throws RuntimeException {
    try (FileInputStream fis = new FileInputStream(file)) {
      return encode(fis);
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static IMap<String, String> encode(final InputStream inputStream) throws RuntimeException {
    final Properties properties = new Properties();
    try (final InputStream bis = new BufferedInputStream(inputStream, 64 * 1024)) {
      properties.load(bis);
      return properties.entrySet()
        .stream()
        .map(entry -> KV.of(String.valueOf(entry.getKey()), String.valueOf(entry.getValue())))
        .collect(IMap.collector(properties.size()));
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static IMap<String, String> encode(final String content) throws RuntimeException {
    try {
      final Properties properties = new Properties();
      properties.load(new StringReader(content));
      return properties.entrySet()
        .stream()
        .map(entry -> KV.of(String.valueOf(entry.getKey()), String.valueOf(entry.getValue())))
        .collect(IMap.collector(properties.size()));
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static IMap<String, String> encode(final URI uri) throws RuntimeException {
    try (InputStream fis = uri.toURL().openStream()) {
      return encode(fis);
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

}
