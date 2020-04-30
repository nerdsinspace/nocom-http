package com.matt.nocom.server.model.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.matt.nocom.server.model.data.Dimension;
import lombok.Builder;
import lombok.Data;

import javax.annotation.Nullable;
import java.time.Instant;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Hit {
  @Nullable
  private Integer trackId;
  private Dimension dimension;
  @Nullable
  private String server;
  private int x;
  private int z;
  private Instant createdAt;
}
