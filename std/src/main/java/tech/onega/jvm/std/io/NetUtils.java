package tech.onega.jvm.std.io;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.URL;
import tech.onega.jvm.std.struct.bytes.IBytes;

final public class NetUtils {

  public static NetworkInterface findInterface(final String name) {
    try {
      final NetworkInterface result = NetworkInterface.getByName(name);
      if (result != null) {
        return result;
      }
      else if ("lo".equals(name)) {
        return NetworkInterface.getByInetAddress(InetAddress.getLoopbackAddress());
      }
      else if ("localhost".equals(name)) {
        return NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
      }
      return null;
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static int ip4ToInt(final String ip4) {
    final String[] ip4s = ip4.split("\\.");
    return (byte) Integer.parseInt(ip4s[0]) << 24
      | ((byte) Integer.parseInt(ip4s[1]) & 0xFF) << 16
      | ((byte) Integer.parseInt(ip4s[2]) & 0xFF) << 8
      | ((byte) Integer.parseInt(ip4s[3]) & 0xFF);
  }

  public static String ip4ToString(final int ip4) {
    return new StringBuilder(5 * 4)
      .append((ip4 >> 24) & 0xFF)
      .append('.')
      .append((ip4 >> 16) & 0xFF)
      .append('.')
      .append((ip4 >> 8) & 0xFF)
      .append('.')
      .append(ip4 & 0xFF)
      .toString();
  }

  public static IBytes loadURL(final URL url, final int bufferSize) throws IOException {
    try (InputStream in = new BufferedInputStream(url.openStream(), bufferSize)) {
      return IBytes.read(in);
    }
  }

  public static InetSocketAddress parseSocketAddress(final String address) {
    final String host = address.substring(0, address.indexOf(':'));
    final Integer port = Integer.parseInt(address.substring(address.indexOf(':') + 1));
    return InetSocketAddress.createUnresolved(host, port);
  }

}
