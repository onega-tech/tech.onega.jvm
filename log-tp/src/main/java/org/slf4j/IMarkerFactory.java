package org.slf4j;

@Deprecated
public interface IMarkerFactory {

  boolean detachMarker(String name);

  boolean exists(String name);

  Marker getDetachedMarker(String name);

  Marker getMarker(String name);

}
