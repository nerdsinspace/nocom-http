package com.matt.nocom.server.model.sql.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.matt.nocom.server.util.VectorXZ;
import com.matt.nocom.server.util.WorldEntry;
import java.io.Serializable;
import java.util.Objects;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location implements Serializable, VectorXZ, WorldEntry {
  /**
   * The time the location was found in unix time
   */
  private long time;

  /**
   * (Optional) The time this location was uploaded to the database
   */
  @Default
  @Nullable
  private Long uploadTime = null;

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
  public Position toPosition() {
    return Position.builder()
        .x(getX())
        .z(getZ())
        .time(getTime())
        .uploadTime(getUploadTime())
        .build();
  }

  @Override
  public boolean equals(Object obj) {
    return this == obj || (obj instanceof Location
        && getX() == ((Location) obj).getX()
        && getZ() == ((Location) obj).getZ()
        && getServer().equals(((Location) obj).getServer())
        && getDimension() == ((Location) obj).getDimension());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getX(), getZ(), getServer(), getDimension());
  }

  @Override
  public String toString() {
    return String.format("[pos=(%d, %d),server=%s,dim=%d", x, z, server, dimension);
  }
}
