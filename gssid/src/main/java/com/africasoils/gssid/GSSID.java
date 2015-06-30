package com.africasoils.gssid;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

/**
 * A class representing a global soil sample ID. The class is immutable.
 */
public class GSSID {

  public static int GEOHASH_BITS = 48;
  public static int TIMESTAMP_BITS = 48;
  public static int SAMPLE_DEPTH_BITS = 8;
  public static int SAMPLE_EXTENSION_BITS = 8;
  public static int RANDOM_BITS = 16;

  public GSSID(double lat, double lon, long timestamp, int sampleDepth, int sampleExtension) {
    this(lat, lon, timestamp, sampleDepth, sampleExtension, new Random().nextInt(1 << RANDOM_BITS));
  }

  public GSSID(double lat, double lon, long timestamp, int sampleDepth, int sampleExtension,
               long random) {
    this.geohash = Geohash.fromLatLon(new LatLon(lat, lon), GEOHASH_BITS);
    this.timestamp = timestamp;
    this.sampleDepth = new SampleDepth(sampleDepth, sampleExtension);
    this.random = random;
    this.bytes = makeBytes(this.geohash, this.timestamp, this.sampleDepth, this.random);
  }

  public GSSID(byte[] bytes) {
    if (bytes.length != 16) {
      throw new IllegalArgumentException(
          String.format("expected 16 bytes but got %d bytes", bytes.length));
    }
    this.bytes = Arrays.copyOf(bytes, bytes.length);
    // Unpack the geohash.
    long geo = bytes[0] & 0xff;
    geo = (geo << 8) | (bytes[1] & 0xff);
    geo = (geo << 8) | (bytes[2] & 0xff);
    geo = (geo << 8) | (bytes[3] & 0xff);
    geo = (geo << 8) | (bytes[4] & 0xff);
    geo = (geo << 8) | (bytes[5] & 0xff);
    geohash = new Geohash(geo, GEOHASH_BITS);
    // Unpack the timestamp.
    long ts = bytes[6] & 0xff;
    ts = (ts << 8) | (bytes[7] & 0xff);
    ts = (ts << 8) | (bytes[8] & 0xff);
    ts = (ts << 8) | (bytes[9] & 0xff);
    ts = (ts << 8) | (bytes[10] & 0xff);
    ts = (ts << 8) | (bytes[11] & 0xff);
    timestamp = ts;
    // Unpack the sample depth.
    long sd = bytes[12] & 0xff;
    // Unpack the sample extension.
    long se = bytes[13] & 0xff;
    sampleDepth = new SampleDepth((int)sd, (int)se);
    // Unpack the random value.
    long rnd = bytes[14] & 0xff;
    rnd = (rnd << 8) | (bytes[15] & 0xff);
    random = rnd;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof GSSID)) return false;

    GSSID gssid = (GSSID) o;

    if (random != gssid.random) return false;
    if (timestamp != gssid.timestamp) return false;
    if (!geohash.equals(gssid.geohash)) return false;
    if (!sampleDepth.equals(gssid.sampleDepth)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = geohash.hashCode();
    result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
    result = 31 * result + sampleDepth.hashCode();
    result = 31 * result + (int) (random ^ (random >>> 32));
    return result;
  }

  public long timestamp() {
    return timestamp;
  }

  public Geohash geohash() {
    return geohash;
  }

  public SampleDepth sampleDepth() {
    return sampleDepth;
  }

  public long random() {
    return random;
  }

  public byte[] bytes() {
    return Arrays.copyOf(bytes, bytes.length);
  }

  private static final BigInteger RANDOM_MASK = BigInteger.valueOf((long)(1 << RANDOM_BITS) - 1);

  private final Geohash geohash;
  private final long timestamp;
  private final SampleDepth sampleDepth;
  private final long random;
  private final byte[] bytes;

  private static byte[] makeBytes(Geohash geohash, long timestamp, SampleDepth sampleDepth,
                                  long random) {
    byte[] bytes = new byte[16];
    // Pack the geohash.
    bytes[0] = (byte)(geohash.toLong() >>> (48 - 8));
    bytes[1] = (byte)(geohash.toLong() >>> (48 - 16));
    bytes[2] = (byte)(geohash.toLong() >>> (48 - 24));
    bytes[3] = (byte)(geohash.toLong() >>> (48 - 32));
    bytes[4] = (byte)(geohash.toLong() >>> (48 - 40));
    bytes[5] = (byte)(geohash.toLong() >>> (48 - 48));
    // Pack the timestamp.
    bytes[6] = (byte)(timestamp >>> (48 - 8));
    bytes[7] = (byte)(timestamp >>> (48 - 16));
    bytes[8] = (byte)(timestamp >>> (48 - 24));
    bytes[9] = (byte)(timestamp >>> (48 - 32));
    bytes[10] = (byte)(timestamp >>> (48 - 40));
    bytes[11] = (byte)(timestamp >>> (48 - 48));
    // Pack the sample depth.
    bytes[12] = (byte)sampleDepth.depth();
    // Pack the sample extension, multiplexing it with the sample depth.
    bytes[13] = (byte)sampleDepth.extension();
    // Pack the random bits.
    bytes[14] = (byte)(random >>> (16 - 8));
    bytes[15] = (byte)random;
    return bytes;
  }

}
