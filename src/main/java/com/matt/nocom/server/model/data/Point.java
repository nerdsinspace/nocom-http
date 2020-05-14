package com.matt.nocom.server.model.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Point {
  private int x;
  private int z;
}
