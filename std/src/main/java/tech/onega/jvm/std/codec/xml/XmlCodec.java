package tech.onega.jvm.std.codec.xml;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import tech.onega.jvm.std.annotation.Nullable;
import tech.onega.jvm.std.io.reader.IOReader;
import tech.onega.jvm.std.io.writer.IOWriterBytes;
import tech.onega.jvm.std.struct.bytes.IBytes;

final public class XmlCodec {

  public static DocumentBuilder createDocumentBuilder() {
    try {
      return DocumentBuilderFactory.newInstance().newDocumentBuilder();
    }
    catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static Transformer createTransformer(final boolean pretty) throws RuntimeException {
    return createTransformer(pretty, null);
  }

  public static Transformer createTransformer(final boolean pretty, @Nullable final Source xslSource) throws RuntimeException {
    try {
      final var factory = TransformerFactory.newDefaultInstance();
      if (pretty) {
        factory.setAttribute("indent-number", Integer.valueOf(2));
      }
      final var transformer = xslSource == null
        ? factory.newTransformer()
        : factory.newTransformer(xslSource);
      if (pretty) {
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      }
      else {
        transformer.setOutputProperty(OutputKeys.INDENT, "no");
      }
      return transformer;
    }
    catch (final RuntimeException e) {
      throw e;
    }
    catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static Document parse(final IBytes data) throws RuntimeException {
    return parse(data.asReader());
  }

  public static Document parse(final IOReader reader) throws RuntimeException {
    try {
      //https://xerces.apache.org/xerces2-j/features.html
      final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setIgnoringElementContentWhitespace(true);
      factory.setIgnoringComments(true);
      factory.setExpandEntityReferences(false);
      factory.setValidating(false);
      factory.setNamespaceAware(true);
      factory.setFeature("http://apache.org/xml/features/validation/schema", false);
      factory.setFeature("http://apache.org/xml/features/validation/schema-full-checking", false);
      factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
      factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
      factory.setFeature("http://apache.org/xml/features/dom/include-ignorable-whitespace", true);
      final DocumentBuilder docBuilder = factory.newDocumentBuilder();
      final Document document = docBuilder.parse(reader.asInputStream());
      return document;
    }
    catch (final RuntimeException e) {
      throw e;
    }
    catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static IBytes toBytes(final Node node, final boolean pretty) throws RuntimeException {
    return toBytes(node, pretty, 32 * 1024);
  }

  public static IBytes toBytes(final Node node, final boolean pretty, final int initialSize) throws RuntimeException {
    final Source source = new DOMSource(node);
    final IOWriterBytes writer = new IOWriterBytes(initialSize);
    try {
      final Result result = new StreamResult(writer.asOutputStream());
      final Transformer transformer = createTransformer(pretty);
      transformer.transform(source, result);
      return writer.toIBytes();
    }
    catch (final RuntimeException e) {
      throw e;
    }
    catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static IBytes toBytesXslt(final Transformer transformer, final Document xml, final int initialSize)
    throws RuntimeException {
    final IOWriterBytes writer = new IOWriterBytes(initialSize);
    try {
      final StreamResult result = new StreamResult(writer.asOutputStream());
      final Source source = new DOMSource(xml);
      transformer.transform(source, result);
      return writer.toIBytes();
    }
    catch (final RuntimeException e) {
      throw e;
    }
    catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static String toString(final Node node, final boolean pretty) throws RuntimeException {
    return toBytes(node, pretty).toString(StandardCharsets.UTF_8);
  }

  public static String toString(final Node node, final boolean pretty, final Charset charset) throws RuntimeException {
    return toBytes(node, pretty).toString(charset);
  }

  public static String toString(final Node node, final boolean pretty, final Charset charset, final int initialSize)
    throws RuntimeException {
    return toBytes(node, pretty, initialSize).toString(charset);
  }

  public static Document transformXslt(final Document xsl, final Document xml) throws RuntimeException {
    final Source xslSource = new DOMSource(xsl);
    final Transformer transformer = createTransformer(false, xslSource);
    return transformXslt(transformer, xml);
  }

  public static Document transformXslt(final Transformer transformer, final Document xml) throws RuntimeException {
    try {
      final Source xmlSource = new DOMSource(xml);
      final DOMResult domResult = new DOMResult();
      transformer.transform(xmlSource, domResult);
      return (Document) domResult.getNode();
    }
    catch (final TransformerException e) {
      throw new RuntimeException(e);
    }
  }

}
