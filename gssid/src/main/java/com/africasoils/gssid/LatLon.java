package com.africasoils.gssid;

public class LatLon {
  public LatLon(double lat, double lon) {
    this.lat = lat;
    this.lon = lon;
  }

  public double lat() {
    return lat;
  }

  public double lon() {
    return lon;
  }

  private final double lat;
  private final double lon;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof LatLon)) return false;

    LatLon latLon = (LatLon) o;

    if (Double.compare(latLon.lat, lat) != 0) return false;
    if (Double.compare(latLon.lon, lon) != 0) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    temp = Double.doubleToLongBits(lat);
    result = (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(lon);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    return result;
  }
}