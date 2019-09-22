package com.matt.nocom.server.model.shared.data;

import java.util.EnumSet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum Dimension {
  NETHER(1, -1, "Nether"),
  OVERWORLD(2, 0, "Overworld"),
  END(3, 1, "End")
  ;

  private int id;
  private int ordinal;
  private String name;

  public static Dimension from(int ordinal) {
    for (Dimension d : values()) {
      if (d.getOrdinal() == ordinal)
        return d;
    }
    return null;
  }

  private static final EnumSet<Dimension> ALL = EnumSet.allOf(Dimension.class);

  public static EnumSet<Dimension> all() {
    return ALL;
  }
}
