package com.matt.nocom.server.model.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClusterNode {
  @EqualsAndHashCode.Include
  private Integer id;
  private Integer count;
  private int x;
  private int z;
  private Dimension dimension;
  private String server;
  private Boolean core;
  private Integer clusterParent;
  private Integer disjointRank;
  private Integer disjointSize;
  private List<Leaf> leafs;

  @Data
  @AllArgsConstructor
  public static class Leaf {
    @EqualsAndHashCode.Include
    private int id;
    private int x;
    private int z;
  }
}
