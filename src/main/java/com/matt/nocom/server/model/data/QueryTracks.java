package com.matt.nocom.server.model.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QueryTracks {
  @NonNull
  private String server;
  private long duration = 10_000;
}
