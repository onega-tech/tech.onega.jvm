package org.slf4j;

import java.io.Serializable;
import java.util.Iterator;

@Deprecated
public interface Marker extends Serializable {

  public final String ANY_MARKER = "*";

  public final String ANY_NON_NULL_MARKER = "+";

  public void add(Marker reference);

  public boolean contains(Marker other);

  public boolean contains(String name);

  @Override
  public boolean equals(Object o);

  public String getName();

  @Deprecated
  public boolean hasChildren();

  @Override
  public int hashCode();

  public boolean hasReferences();

  public Iterator<Marker> iterator();

  public boolean remove(Marker reference);

}