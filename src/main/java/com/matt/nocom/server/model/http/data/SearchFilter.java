package com.matt.nocom.server.model.http.data;

import static com.matt.nocom.server.sqlite.Tables.DIMENSIONS;
import static com.matt.nocom.server.sqlite.Tables.LOCATIONS;
import static com.matt.nocom.server.sqlite.Tables.POSITIONS;
import static com.matt.nocom.server.sqlite.Tables.SERVERS;

import com.google.common.base.MoreObjects;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jooq.Condition;
import org.jooq.impl.DSL;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchFilter implements Serializable {
  @Default
  private String server = null;

  @Default
  private Integer dimension = null;

  @Default
  private Long startTime = null;

  @Default
  private Long endTime = null;

  @Default
  private Integer minHits = null;

  @Default
  private Integer groupingRange = null;

  @Default
  private Integer minDelta = null;

  public void forceGrouping() {
    if(getGroupingRange() == null) setGroupingRange(0);
  }

  public Condition getConditions() {
    Condition condition = DSL.noCondition();

    if(getServer() != null)
      condition = condition.and(SERVERS.HOSTNAME.equalIgnoreCase(getServer()));

    if(getDimension() != null)
      condition = condition.and(DIMENSIONS.ORDINAL.eq(getDimension()));

    if(getStartTime() != null || getEndTime() != null)
      condition = condition.and(LOCATIONS.FOUND_TIME.between(
          MoreObjects.firstNonNull(getStartTime(), 0L),
          MoreObjects.firstNonNull(getEndTime(), Long.MAX_VALUE)
      ));

    if(getMinDelta() != null)
      condition = condition
          .and(DSL.abs(POSITIONS.X).ge(getMinDelta()))
          .and(DSL.abs(POSITIONS.Z).ge(getMinDelta()))
          .and(DSL.abs(DSL.abs(POSITIONS.X).sub(DSL.abs(POSITIONS.Z))).ge(getMinDelta()));

    return condition;
  }
}
