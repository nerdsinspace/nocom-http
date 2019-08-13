package com.matt.nocom.server.util;

import com.google.common.collect.Lists;
import com.matt.nocom.server.model.sql.event.EventLevel;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class EventLevelRegistry {
  private static final List<EventLevel> REGISTRY = Lists.newArrayList();

  private static void register(EventLevel type) {
    REGISTRY.add(type);
  }

  private static EventLevel create(int id, String name) {
    EventLevel level = new EventLevel(id, name);
    register(level);
    return level;
  }

  public static Collection<EventLevel> all() {
    return Collections.unmodifiableCollection(REGISTRY);
  }

  public static Optional<EventLevel> getById(int id) {
    for(EventLevel level : REGISTRY) {
      if(level.getId() == id)
        return Optional.of(level);
    }
    return Optional.empty();
  }

  //
  // all data here should accurately correspond with the entries outlined in V03__events_table.sql
  //

  public static final EventLevel FATAL  = create(0, "Fatal");
  public static final EventLevel ERROR  = create(1, "Error");
  public static final EventLevel WARN   = create(2, "Warn");
  public static final EventLevel INFO   = create(3, "Info");
  public static final EventLevel DEBUG  = create(4, "Debug");
  public static final EventLevel TRACE  = create(5, "Trace");
}
