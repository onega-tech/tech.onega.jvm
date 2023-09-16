package tech.onega.jvm.std.reflection;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import tech.onega.jvm.std.io.FileUtils;
import tech.onega.jvm.std.struct.set.ISet;
import tech.onega.jvm.std.struct.set.MSet;

final public class ClassLoaderUtils {

  public static ISet<URI> listResourceDir(final ClassLoader classLoader, final String dirName) throws Exception {
    final var result = MSet.<URI>create(64);
    listResourceDir(result, classLoader, dirName);
    return result.destroy();
  }

  public static void listResourceDir(final MSet<URI> result, final ClassLoader classLoader, final String dirName)
    throws Exception {
    final var dirUrls = Collections.list(classLoader.getResources(dirName));
    for (final var dirUrl : dirUrls) {
      final var protocol = dirUrl.getProtocol().toLowerCase();
      if (protocol.equals("file")) {
        for (final var file : FileUtils.listDir(new File(dirUrl.getFile()), true)) {
          result.add(file.toURI());
        }
      }
      else if (protocol.equals("jar")) {
        final var jarFilePrefix = dirUrl.toExternalForm().split("!")[0];
        final var jarConnection = (JarURLConnection) dirUrl.openConnection();
        for (final var jarEntry : Collections.list(jarConnection.getJarFile().entries())) {
          if (jarEntry.getName().startsWith(dirName) && !jarEntry.getName().endsWith("/") && !jarEntry.isDirectory()) {
            final var jarEntryUrl = new URL(jarFilePrefix + "!/" + jarEntry.getName());
            result.add(jarEntryUrl.toURI());
          }
        }
      }
    }
  }

}
