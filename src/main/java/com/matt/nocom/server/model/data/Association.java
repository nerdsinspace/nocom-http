package com.matt.nocom.server.model.data;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class Association {
  private int clusterId;
  private BigDecimal strength;
  private long lastSeen;
}
