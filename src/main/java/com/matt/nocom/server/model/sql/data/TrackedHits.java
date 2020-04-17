package com.matt.nocom.server.model.sql.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.matt.nocom.server.model.shared.data.Dimension;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TrackedHits {
  private int trackId;
  private Dimension dimension;
  private String server;

  @EqualsAndHashCode.Exclude
  private List<SimpleHit> hits;

  public void addHit(Hit hit) {
    getHits().add(new SimpleHit(hit.getX(), hit.getZ(), hit.getCreatedAt()));
  }
}
