package com.matt.nocom.server.model.sql.data;

import java.util.EnumSet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum Dimension {
  NETHER("Nether", -1),
  OVERWORLD("Overworld", 0),
  END("End", 1)
  ;

  private String name;
  private int ordinal;

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
