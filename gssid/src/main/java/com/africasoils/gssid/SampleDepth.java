package com.africasoils.gssid;

public class SampleDepth {
  /**
   * @return the depth of the sample.
   */
  public int depth() {
    return depth;
  }

  /**
   * @return the extension distance past the depth of the sample.
   */
  public int extension() {
    return extension;
  }

  private int depth;
  private int extension;

  SampleDepth(int depth, int extension) {
    if (depth < 0) {
      throw new IllegalArgumentException("depth is negative");
    }
    if (extension < 0) {
      throw new IllegalArgumentException("extension is negative");
    }
    this.depth = depth;
    this.extension = extension;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SampleDepth)) return false;

    SampleDepth that = (SampleDepth) o;

    if (depth != that.depth) return false;
    if (extension != that.extension) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = depth;
    result = 31 * result + extension;
    return result;
  }
}