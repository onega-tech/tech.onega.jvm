package tech.onega.jvm.std.reflection;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import tech.onega.jvm.std.annotation.Nullable;
import tech.onega.jvm.std.struct.list.IList;
import tech.onega.jvm.std.struct.list.MList;
import tech.onega.jvm.std.struct.stream.StreamUtils;

final public class ReflectionUtils {

  private static class PackageScanner {

    private final static String PROTOCOL_FILE = "file";

    private final static String PROTOCOL_JAR = "jar";

    private final static String CLASS_EXT = ".class";

    private final static char SEP_CHAR = '/';

    private final static String SEP = "/";

    private static void addIfSuitable(final String path, final String rootPackage, final Set<String> result) {
      String className = path;
      if (!className.endsWith(CLASS_EXT)) {
        return;
      }
      className = className.replace(CLASS_EXT, "").replace(SEP_CHAR, '.');
      if (!className.startsWith(rootPackage)) {
        return;
      }
      result.add(className);
    }

    private static Set<URL> findResources(final String location, final ClassLoader classLoader) throws Exception {
      final String path = location.startsWith(SEP) ? location.substring(1) : location;
      return new LinkedHashSet<>(Collections.list(classLoader.getResources(path)));
    }

    private static String getCanonicalPath(final File file, final File root, final String rootLocation)
      throws IOException {
      final String name = file
        .getCanonicalPath()
        .replace(root.getCanonicalPath(), "")
        .replace('\\', SEP_CHAR);
      return rootLocation + name;
    }

    /**
     * Returns all classes for package.
     *
     * @param packageName
     *            Package name.
     * @param classLoader
     *            Class loader.
     * @return Collection of classes.
     * @throws JinException
     *             if any errors.
     */
    public static Set<Class<?>> getClasses(final String packageName, final ClassLoader classLoader)
      throws RuntimeException {
      try {
        final String rootLocation = packageName.replace('.', SEP_CHAR);
        // find names
        final Set<String> classNames = new HashSet<>();
        for (final URL url : findResources(rootLocation, classLoader)) {
          if (url.getProtocol().equals(PROTOCOL_FILE)) {
            final File root = new File(url.getFile());
            scanDir(root, root, packageName, rootLocation, classNames);
          }
          else if (url.getProtocol().equals(PROTOCOL_JAR)) {
            scanJar(url, packageName, classNames);
          }
        }
        // find classes
        final Set<Class<?>> result = new HashSet<>(classNames.size());
        for (final String item : classNames) {
          try {
            final Class<?> clazz = Class.forName(item, false, classLoader);
            result.add(clazz);
          }
          catch (final Throwable e) {
            // IGNORE class is bad continue
          }
        }
        return result;
      }
      catch (final RuntimeException e) {
        throw e;
      }
      catch (final Exception e) {
        throw new RuntimeException(e.getMessage(), e);
      }
    }

    private static void scanDir(final File file, final File root, final String rootPackage, final String rootLocation,
      final Set<String> result) throws IOException {
      if (!file.isDirectory()) {
        final String classPath = getCanonicalPath(file, root, rootLocation);
        addIfSuitable(classPath, rootPackage, result);
        return;
      }
      for (final File subFile : file.listFiles()) {
        if (subFile.isDirectory()) {
          scanDir(subFile, root, rootPackage, rootLocation, result);
        }
        else {
          final String classPath = getCanonicalPath(subFile, root, rootLocation);
          addIfSuitable(classPath, rootPackage, result);
        }
      }
    }

    private static void scanJar(final URL location, final String rootPackage, final Set<String> result)
      throws IOException {
      final URLConnection con = location.openConnection();
      if (!(con instanceof JarURLConnection)) {
        return;
      }
      final JarURLConnection jarCon = (JarURLConnection) con;
      jarCon.setUseCaches(false);
      final JarFile jarFile = jarCon.getJarFile();
      for (final Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements();) {
        final JarEntry entry = entries.nextElement();
        final String entryPath = entry.getName();
        addIfSuitable(entryPath, rootPackage, result);
      }
    }

  }

  public static IList<Class<?>> classChain(final Class<?> type) {
    final MList<Class<?>> out = MList.create(8);
    Class<?> current = type;
    while (true) {
      if (current.isInterface()) {
        break;
      }
      out.add(current);
      current = current.getSuperclass();
      if (current == null || current == Object.class) {
        break;
      }
    }
    return out.reverse().toIList();
  }

  @SuppressWarnings("unchecked")
  public static <T> T createInstance(final Class<T> type, final Object... args) {
    try {
      final Constructor<?> constructor = type.getDeclaredConstructors()[0];
      constructor.setAccessible(true);
      return (T) constructor.newInstance(args);
    }
    catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static IList<Field> fields(final Class<?> type) {
    final MList<Field> out = MList.create(16);
    final IList<Class<?>> chain = classChain(type);
    for (final Class<?> cl : chain) {
      for (final Field field : cl.getDeclaredFields()) {
        if (Modifier.isPrivate(field.getModifiers())) {
          out.add(field);
        }
      }
    }
    for (final Class<?> cl : chain) {
      for (final Field field : cl.getDeclaredFields()) {
        if (!Modifier.isPrivate(field.getModifiers())) {
          out.add(field);
        }
      }
    }
    return out.toIList();
  }

  public static <T> IList<Constructor<T>> findConstructorsWithAnnotation(final Class<T> type,
    final Class<? extends Annotation> annotationClass) {
    final MList<Constructor<T>> result = MList.create();
    @SuppressWarnings("unchecked")
    final Constructor<T>[] constructors = (Constructor<T>[]) type.getDeclaredConstructors();
    for (final Constructor<T> cons : constructors) {
      if (cons.isAnnotationPresent(annotationClass)) {
        result.add(cons);
      }
    }
    return result.toIList();
  }

  public static IList<Method> findMethods(final Class<?> type) {
    final MList<Method> out = MList.create(16);
    final IList<Class<?>> chain = classChain(type);
    for (final Class<?> cl : chain) {
      for (final Method method : cl.getDeclaredMethods()) {
        if (Modifier.isPrivate(method.getModifiers())) {
          out.add(method);
        }
      }
    }
    for (final Class<?> cl : chain) {
      for (final Method method : cl.getDeclaredMethods()) {
        if (!Modifier.isPrivate(method.getModifiers())) {
          out.add(method);
        }
      }
    }
    return out.toIList();
  }

  @Nullable
  public static Method firstMethod(final Class<?> type, final String name) {
    return StreamUtils.createStream(type.getDeclaredMethods())
      .filter(m -> m.getName().equals(name))
      .findAny()
      .orElse(null);
  }

  public static Object invoke(@Nullable final Object instance, final Method method, final Object... args) {
    open(method);
    try {
      return method.invoke(instance, args);
    }
    catch (final InvocationTargetException e) {
      throw new RuntimeException(e.getCause());
    }
    catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static boolean isEnum(final Object t) {
    return t != null && t instanceof Class && ((Class<?>) t).isEnum();
  }

  public static boolean isExtendedFrom(final Type parentType, final Type childType) {
    if (parentType == null || childType == null) {
      return false;
    }
    else if (parentType == childType) {
      return true;
    }
    else if (childType instanceof Class<?> && parentType instanceof Class<?>) {
      var childClass = (Class<?>) childType;
      var parentClass = (Class<?>) parentType;
      return childClass.isAssignableFrom(parentClass);
    }
    return false;
  }

  public static boolean isFinal(final Field field) {
    return Modifier.isFinal(field.getModifiers());
  }

  public static boolean isGetter(final Method method) {
    return method.getParameterCount() == 0 && !isVoid(method);
  }

  public static boolean isOptional(final Type type) {
    if (!(type instanceof ParameterizedType)) {
      return false;
    }
    return ((ParameterizedType) type).getRawType() == Optional.class;
  }

  public static boolean isPrivate(final Field field) {
    return Modifier.isPrivate(field.getModifiers());
  }

  public static boolean isSetter(final Method method) {
    return method.getParameterCount() == 1;
  }

  public static boolean isStatic(final Field field) {
    return (field.getModifiers() & Modifier.STATIC) != 0;
  }

  public static boolean isStatic(final Method method) {
    return (method.getModifiers() & Modifier.STATIC) != 0;
  }

  public static boolean isTransistent(final Field field) {
    return Modifier.isTransient(field.getModifiers());
  }

  public static boolean isVoid(final Method method) {
    final Class<?> returnType = method.getReturnType();
    return returnType == Void.class || returnType == void.class || returnType == Void.TYPE;
  }

  public static void open(final Field field) {
    try {
      field.setAccessible(true);
    }
    catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static void open(final Method method) {
    try {
      method.setAccessible(true);
    }
    catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static Type optionalValueType(final Type optionalType) {
    final ParameterizedType pType = (ParameterizedType) optionalType;
    return pType.getActualTypeArguments()[0];
  }

  public static Object read(@Nullable final Object instance, final Field field) {
    open(field);
    try {
      return field.get(instance);
    }
    catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static Set<Class<?>> scanPackage(final String packageName, final ClassLoader classLoader)
    throws RuntimeException {
    return PackageScanner.getClasses(packageName, classLoader);
  }

  public static Class<?> toClass(final Type type) {
    if (type instanceof Class) {
      return (Class<?>) type;
    }
    else if (type instanceof ParameterizedType) {
      final var rawType = ((ParameterizedType) type).getRawType();
      return toClass(rawType);
    }
    return null;
  }

}