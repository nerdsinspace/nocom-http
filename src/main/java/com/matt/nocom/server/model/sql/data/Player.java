package com.matt.nocom.server.model.sql.data;

import java.io.Serializable;
import java.util.UUID;
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
public class Player implements Serializable {
  private UUID uuid;

  @Default
  @Nullable
  private String name = null;

  @Override
  public boolean equals(Object obj) {
    return this == obj || (obj instanceof Player && getUuid().equals(((Player) obj).getUuid()));
  }

  @Override
  public int hashCode() {
    return getUuid().hashCode();
  }

  @Override
  public String toString() {
    return (name == null ? "" : (getName() + ": ")) + getUuid().toString();
  }
}
