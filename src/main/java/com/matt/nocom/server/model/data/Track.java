package com.matt.nocom.server.model.data;

import com.matt.nocom.server.model.data.Dimension;
import lombok.*;

import javax.annotation.Nullable;
import java.time.Instant;

@Data
@Builder
public class Track {
  @Nullable
  @EqualsAndHashCode.Include
  private Integer trackId;
  private Dimension dimension;
  @Nullable
  private String server;

  private int x;
  private int z;
  private Instant createAt;

  @Nullable
  private Integer previousTrackId;
  @Nullable
  private Dimension previousDimension;
}
