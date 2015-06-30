package com.africasoils.gssid;

import java.util.HashMap;
import java.util.Map;

public class Geohash {

  public static Geohash fromString(String s) {
    long geohash = 0;
    int bits = 0;
    for (int i = 0; i < s.length(); i++, bits += 5) {
      int bitChunk = charMap.get(s.charAt(i));
      geohash = (geohash << 5) | bitChunk;
    }
    return new Geohash(geohash, bits);
  }

  /**
   * Converts a given lat/lon pair to a geohash bit sequence with the specified bit width.
   *
   * @param latLon the latitude and longitude to encode.
   * @param bits number of bits of accuracy (use an odd number to keep accuracy equal for lat and
   *             lon).
   * @return
   */
  public static Geohash fromLatLon(LatLon latLon, int bits) {
    long geohash = 0;
    double lat = latLon.lat(), lon = latLon.lon();
    double latMin = -90, latMax =  90, lonMin = -180, lonMax = 180;
    for (int bit = 0; bit < bits; bit++) {
      geohash <<= 1;
      // Even bits are longitude, odd bits are latitude.
      if ((bit & 1) == 0) {
        double lonMid = (lonMin + lonMax) / 2;
        if (lon > lonMid) {
          geohash |= 1;
          lonMin = lonMid;
        } else {
          lonMax = lonMid;
        }
      } else {
        double latMid = (latMin + latMax) / 2;
        if (lat > latMid) {
          geohash |= 1;
          latMin = latMid;
        } else {
          latMax = latMid;
        }
      }
    }
    return new Geohash(geohash, bits);
  }

  /**
   * Constructs a new Geohash from a long representation.
   * @param geohash the long geohash.
   * @param bits number of bits used to encode the geohash.
   */
  public Geohash(long geohash, int bits) {
    this.geohash = geohash;
    this.bits = bits;
  }

  /**
   * @return the string representation of the Geohash according to the official spec. The long
   *    representation is padded to 5 bits before converting to string.
   */
  @Override
  public String toString() {
    long padding = (bits % 5) == 0 ? 0 : 5 - (bits % 5);
    long paddedGeohash = geohash << padding;
    StringBuilder result = new StringBuilder();
    for (int bit = 0; bit < bits; bit += 5) {
      result.append(chars[(int)(paddedGeohash >> bit) & 0x1f]);
    }
    return result.reverse().toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Geohash)) return false;

    Geohash geohash1 = (Geohash) o;

    if (bits != geohash1.bits) return false;
    if (geohash != geohash1.geohash) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = (int) (geohash ^ (geohash >>> 32));
    result = 31 * result + bits;
    return result;
  }

  public LatLon toLatLon() {
    double latMin = -90, latMax =  90, lonMin = -180, lonMax = 180;
    // `bit` is index from MSB in `geohash`, starting at 0.
    // Even bit indices encode longitude while odd bit indices encode latitude.
    for (int bit = 0; bit < bits; bit++) {
      // Even bit values in geohash mean coordinate is less than midpoint.
      boolean negative = (geohash & (1L << (bits - bit - 1))) == 0;
      if ((bit & 1) == 0) {
        double lonMid = (lonMin + lonMax) / 2;
        if (negative) {
          lonMax = lonMid;
        } else {
          lonMin = lonMid;
        }
      } else {
        double latMid = (latMin + latMax) / 2;
        if (negative) {
          latMax = latMid;
        } else {
          latMin = latMid;
        }
      }
    }
    return new LatLon((latMin + latMax) / 2, (lonMin + lonMax) / 2);
  }

  /**
   * @return the long representation of the Geohash.
   */
  public long toLong() {
    return geohash;
  }

  /**
   * @return the number of bits used to represent this Geohash.
   */
  public int bits() {
    return bits;
  }

  private static final char[] chars = "0123456789bcdefghjkmnpqrstuvwxyz".toCharArray();
  private static final Map<Character, Integer> charMap = new HashMap<>();
  static {
    for (int i = 0; i < 32; i++) {
      charMap.put(chars[i], i);
    }
  }

  private final long geohash;
  private final int bits;

}
