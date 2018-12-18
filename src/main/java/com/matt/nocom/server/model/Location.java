package com.matt.nocom.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location implements Serializable {
  public static final UUID UNKNOWN_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

  /**
   * The time the location was found in unix time
   */
  private long time;

  /**
   * The time this location was uploaded to the database
   */
  @Builder.Default
  private long uploadTime = -1;

  /**
   * The X coordinate of the location
   */
  private int x;

  /**
   * The Z coordinate of the location
   */
  private int z;

  /**
   * The server the location was found on
   */
  private String server;

  /**
   * The dimension the coordinate was found in
   */
  private int dimension;

  @JsonIgnore
  public Position getPosition() {
    return Position.builder()
        .x(x)
        .z(z)
        .build();
  }

  @Override
  public boolean equals(Object obj) {
    return this == obj || (obj instanceof Location
        && getTime() == ((Location) obj).getTime()
        && getUploadTime() == ((Location) obj).getUploadTime()
        && getX() == ((Location) obj).getX()
        && getZ() == ((Location) obj).getZ()
        && getServer().equals(((Location) obj).getServer())
        && getDimension() == ((Location) obj).getDimension());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getTime(), getUploadTime(), getX(), getZ(), getServer(), getDimension());
  }

  @Override
  public String toString() {
    return String.format("[time=%d,uploadTime=%d,pos=(%d, %d),server=%s,dim=%d", time, uploadTime, x, z, server, dimension);
  }
}
