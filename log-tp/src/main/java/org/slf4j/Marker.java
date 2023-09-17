package org.slf4j;

import java.io.Serializable;
import java.util.Iterator;

public interface Marker extends Serializable {

  String ANY_MARKER = "*";

  String ANY_NON_NULL_MARKER = "+";

  void add(Marker reference);

  boolean contains(Marker other);

  boolean contains(String name);

  @Override
  boolean equals(Object o);

  String getName();

  @Deprecated
  boolean hasChildren();

  @Override
  int hashCode();

  boolean hasReferences();

  Iterator<Marker> iterator();

  boolean remove(Marker reference);

}