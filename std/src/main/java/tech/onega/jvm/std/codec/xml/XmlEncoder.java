package tech.onega.jvm.std.codec.xml;

import java.lang.reflect.Array;
import java.util.Map;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import tech.onega.jvm.std.annotation.ThreadSafe;
import tech.onega.jvm.std.reflection.ReflectionUtils;
import tech.onega.jvm.std.struct.map.IMap;
import tech.onega.jvm.std.struct.map.MMap;

@ThreadSafe
final public class XmlEncoder {

  public static final String ATTR_CLASS = "class";

  public static final String NODE_ITEM = "item";

  public static final String NODE_ROOT = "root";

  public static final String NODE_VALUE = "value";

  private static final Pattern KEY_PATTERN = Pattern.compile("[^a-zA-Z0-9_]");

  private static final DocumentBuilder DOCUMENT_BUILDER = XmlCodec.createDocumentBuilder();

  private static Element createElementForRoot(final Document document, final Element root, final Object key) {
    Element element;
    if (root == null) {
      element = document.createElement(NODE_ROOT);
      document.appendChild(element);
    }
    else {
      final String keyName;
      if (NODE_ITEM.equals(key)) {
        keyName = NODE_ITEM;
      }
      else if (key instanceof Number) {
        keyName = NODE_ITEM;
      }
      else {
        keyName = KEY_PATTERN.matcher(String.valueOf(key)).replaceAll("_").trim();
      }
      element = document.createElement(keyName);
      root.appendChild(element);
    }
    return element;
  }

  public static Document encode(final Object data) throws RuntimeException {
    final Document document = DOCUMENT_BUILDER.newDocument();
    document.setXmlStandalone(true);
    makeValue(document, null, null, data, 0, 100);
    return document;
  }

  @SuppressWarnings("unchecked")
  private static void makeValue(
    final Document document,
    final Element root,
    final Object key,
    Object value,
    int level,
    final int maxLevel) throws RuntimeException {
    //
    final var valueClassName = value == null ? null : value.getClass().getCanonicalName();
    if (value != null && value instanceof XmlSerializableValue) {
      value = ((XmlSerializableValue) value).serialize();
    }
    if (value == null) {
      return;
    }
    else if (level > maxLevel) {
      return;
    }
    final var valueClass = value.getClass();
    final var element = createElementForRoot(document, root, key);
    if (valueClass.isPrimitive() || value instanceof Number || value instanceof String) {
      element.setTextContent(String.valueOf(value));
    }
    else if (value instanceof Map) {
      final var map = (Map<Object, Object>) value;
      for (final var entry : map.entrySet()) {
        makeValue(document, element, entry.getKey(), entry.getValue(), level++, maxLevel);
      }
    }
    else if (value instanceof IMap) {
      final var map = (IMap<Object, Object>) value;
      for (final var entry : map) {
        makeValue(document, element, entry.key(), entry.value(), level++, maxLevel);
      }
    }
    else if (value instanceof MMap) {
      final var map = (MMap<Object, Object>) value;
      for (final var entry : map) {
        makeValue(document, element, entry.key(), entry.value(), level++, maxLevel);
      }
    }
    else if (value instanceof Iterable) {
      final var vals = (Iterable<Object>) value;
      for (final var v : vals) {
        makeValue(document, element, NODE_ITEM, v, level++, maxLevel);
      }
    }
    else if (valueClass.isArray()) {
      final var size = Array.getLength(value);
      for (var i = 0; i < size; i++) {
        makeValue(document, element, NODE_ITEM, Array.get(value, i), level++, maxLevel);
      }
    }
    else {
      final var packageName = value.getClass().getPackage().getName();
      if (packageName.startsWith("java") || packageName.startsWith("com.sun")) {
        element.setTextContent(String.valueOf(value));
      }
      else {
        element.setAttribute(ATTR_CLASS, valueClassName);
        for (final var field : valueClass.getDeclaredFields()) {
          if (ReflectionUtils.isTransistent(field) || ReflectionUtils.isStatic(field)) {
            continue;
          }
          makeValue(document, element, field.getName(), ReflectionUtils.read(value, field), level++, maxLevel);
        }
      }
    }
  }

}
