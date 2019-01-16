package com.matt.nocom.server.model.game;


import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegionFileFilter implements Serializable {

  private String server;

  private String type; // "DOWNLOADED" or "GENERATED"

  private int dimension;

  private int x;

  private int z;
}
