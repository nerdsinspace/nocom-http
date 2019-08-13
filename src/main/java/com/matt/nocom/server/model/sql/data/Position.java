package com.matt.nocom.server.model.sql.data;

import com.matt.nocom.server.util.VectorXZ;
import java.io.Serializable;
import java.util.Objects;
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
public class Position implements Serializable, VectorXZ {
  private int x;
  private int z;

  @Default
  private Long time = null;

  @Default
  private Long uploadTime = null;

  /**
   * The biome the location is probably in
   */
  @Default
  private String biome = null;

  @Override
  public boolean equals(Object obj) {
    return this == obj || (obj instanceof Position
        && getX() == ((Position) obj).getX()
        && getZ() == ((Position) obj).getZ());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getX(), getZ());
  }

  @Override
  public String toString() {
    return "{" + x + ", " + z + "}";
  }
}
