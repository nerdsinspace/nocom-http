package com.matt.nocom.server.model.sql.data;

import com.matt.nocom.server.model.shared.data.Dimension;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.Optional;

@Getter
@AllArgsConstructor
@Builder
public class Track {
  private final Instant createAt;
  private final int chunkX;
  private final int chunkZ;
  private final int overworldBlockX;
  private final int overworldBlockZ;
  private final Dimension dimension;
  private final int trackId;

  @Nullable
  private final Integer previousTrackId;
  @Nullable
  private final Dimension previousDimension;

  public Optional<Integer> getPreviousTrackId() {
    return Optional.ofNullable(previousTrackId);
  }

  public Optional<Dimension> getPreviousDimension() {
    return Optional.ofNullable(previousDimension);
  }
}
