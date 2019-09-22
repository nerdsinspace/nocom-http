package com.matt.nocom.server.model.http.data;

import java.io.Serializable;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MojangPlayer implements Serializable {
  private String name;

  @Nullable
  private Long changedToAt = null;

  @Override
  public boolean equals(Object obj) {
    return this == obj || (obj instanceof MojangPlayer && getName().equalsIgnoreCase(((MojangPlayer) obj).getName()));
  }

  @Override
  public int hashCode() {
    return getName().toLowerCase().hashCode();
  }
}
