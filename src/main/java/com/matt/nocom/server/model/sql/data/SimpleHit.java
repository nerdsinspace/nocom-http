package com.matt.nocom.server.model.sql.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.Instant;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SimpleHit {
  private int x;
  private int z;
  private Instant createdAt;
}
