package com.matt.nocom.server.model.data;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.EnumSet;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum Dimension {
  NETHER(-1, "Nether"),
  OVERWORLD(0, "Overworld"),
  END(1, "End")
  ;

  private final int ordinal;
  private final String name;

  public short getOrdinalAsShort() {
    return (short) getOrdinal();
  }

  private static final EnumSet<Dimension> ALL = EnumSet.allOf(Dimension.class);
  private static final Map<Integer, Dimension> ORDINAL_MAP = Maps.newHashMap();

  static {
    for(Dimension dim : values()) {
      ORDINAL_MAP.put(dim.getOrdinal(), dim);
    }
  }

  public static Dimension byOrdinal(int ordinal) {
    return ORDINAL_MAP.get(ordinal);
  }

  public static EnumSet<Dimension> all() {
    return ALL;
  }
}
