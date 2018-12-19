package com.matt.nocom.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.matt.nocom.server.util.SQLConsumer;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

  @Default Integer groupingRange = null;

  @JsonIgnore
  private int getIndex(String name) {
    switch (name) {
      case "server":
        return server == null ? 0 : 1;
      case "dimension":
        return dimension == null ? 0 : getIndex("server") + 1;
      case "startTime":
      case "endTime":
        return (startTime == null && endTime == null) ? 0
            : (getIndex("dimension") + ("endTime".equals(name) ? 2 : 1));
      default:
          return 0;
    }
  }

  @JsonIgnore
  private void ifPresent(String name, SQLConsumer<Integer> consumer) throws SQLException {
    int index = getIndex(name);
    if(index > 0) consumer.accept(index);
  }

  @JsonIgnore
  public void applyInserts(PreparedStatement statement) throws SQLException {
    ifPresent("server", index -> statement.setString(index, getServer()));
    ifPresent("dimension", index -> statement.setInt(index, getDimension()));
    ifPresent("startTime", index -> {
      statement.setLong(index, MoreObjects.firstNonNull(getStartTime(), 0L));
      statement.setLong(index + 1, MoreObjects.firstNonNull(getEndTime(), Long.MAX_VALUE));
    });
  }

  @JsonIgnore
  public String getQueryStatement() {
    if(Stream.of(server, dimension, startTime, endTime)
        .filter(Objects::nonNull)
        .count() < 1)
      return "";

    StringBuilder builder = new StringBuilder("WHERE");
    List<String> list = Lists.newArrayList();

    if(getServer() != null)
      list.add("Servers.hostname = ?");
    if(getDimension() != null)
      list.add("Dimensions.ordinal = ?");
    if(getStartTime() != null || getEndTime() != null)
      list.add("Locations.found_time BETWEEN ? AND ?");

    Iterator<String> it = list.iterator();
    while(it.hasNext()) {
      builder.append(' ');
      builder.append(it.next());
      if(it.hasNext()) builder.append("AND");
    }
    return builder.toString();
  }
}
