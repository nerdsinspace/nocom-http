package com.matt.nocom.server.service;

import static com.matt.nocom.server.sqlite.Tables.EVENTS;
import static com.matt.nocom.server.sqlite.Tables.EVENT_LEVELS;
import static com.matt.nocom.server.sqlite.Tables.EVENT_TYPES;

import com.matt.nocom.server.model.sql.auth.User;
import com.matt.nocom.server.model.sql.event.Event;
import com.matt.nocom.server.model.sql.event.EventLevel;
import com.matt.nocom.server.model.sql.event.EventType;
import com.matt.nocom.server.service.auth.LoginService;
import com.matt.nocom.server.util.EventLevelRegistry;
import com.matt.nocom.server.util.EventTypeRegistry;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class EventService {
  private static final int SYSTEM_ID = -1;
  private static final User SYSTEM_USER = User.builder()
      .id(SYSTEM_ID)
      .username("SYSTEM")
      .build();

  public static User system() {
    return SYSTEM_USER;
  }

  private final DSLContext dsl;
  private final LoginService login;

  @Autowired
  public EventService(DSLContext dsl, LoginService login) {
    this.dsl = dsl;
    this.login = login;
  }

  private int publish(EventLevel level, EventType type, User user, String message) {
    return dsl.insertInto(EVENTS,
        EVENTS.EVENT_TIME,
        EVENTS.EVENT_LEVEL,
        EVENTS.EVENT_TYPE,
        EVENTS.USER_ID,
        EVENTS.USER_NAME,
        EVENTS.MESSAGE)
        .values(
            DSL.val(System.currentTimeMillis()),
            DSL.val(level.getId()),
            DSL.field(dsl.select(EVENT_TYPES.ID)
                .from(EVENT_TYPES)
                .where(EVENT_TYPES.HASH.eq(type.getHash()))
                .limit(1)),
            DSL.val(user.getId()),
            DSL.val(user.getUsername()),
            DSL.val(message)
        ).execute();
  }

  /**
   * Push event with given parameters
   * @param level event log level
   * @param type category of this event
   * @param user user that caused this event to fire (SYSTEM if none)
   * @param format formatted string
   * @param args format arguments
   * @return 1 if the event was successfully published to the database
   */
  public int publish(EventLevel level, EventType type, User user, String format, Object... args) {
    return publish(level, type, user, String.format(format, args));
  }
  public int publish(EventLevel level, EventType type, Authentication auth, String format, Object... args) {
    return publish(level, type,
        Optional.ofNullable(auth)
            .map(Authentication::getName)
            .flatMap(login::getUser)
            .orElseThrow(() -> new Error("No matching user found")),
        format, args);
  }

  public int publishSystem(EventLevel level, EventType type, String format, Object... args) {
    return publish(level, type, system(), format, args);
  }

  public int publishInfo(User user, EventType type, String format, Object... args) {
    return publish(EventLevelRegistry.INFO, type, user, format, args);
  }
  public int publishInfo(Authentication auth, EventType type, String format, Object... args) {
    return publish(EventLevelRegistry.INFO, type, auth, format, args);
  }
  public int publishInfo(EventType type, String format, Object... args) {
    return publishInfo(SecurityContextHolder.getContext().getAuthentication(), type, format, args);
  }

  public int publishSystemInfo(EventType type, String format, Object... args) {
    return publishSystem(EventLevelRegistry.INFO, type, format, args);
  }

  private List<Event> getEvents(Condition conditions, int limit) {
    return dsl.select(EVENTS.ID,
        EVENTS.EVENT_TIME,
        EVENTS.EVENT_LEVEL,
        EVENTS.EVENT_TYPE,
        EVENTS.USER_ID,
        EVENTS.USER_NAME,
        EVENTS.MESSAGE,
        EVENT_LEVELS.ID,
        EVENT_LEVELS.NAME,
        EVENT_TYPES.ID,
        EVENT_TYPES.NAME,
        EVENT_TYPES.HASH)
        .from(EVENTS)
        .innerJoin(EVENT_LEVELS)
        .on(EVENTS.EVENT_LEVEL.eq(EVENT_LEVELS.ID))
        .innerJoin(EVENT_TYPES)
        .on(EVENTS.EVENT_TYPE.eq(EVENT_TYPES.ID))
        .where(conditions)
        .orderBy(EVENTS.EVENT_TIME.desc())
        .limit(limit)
        .fetch(record -> Event.builder()
            .id(record.getValue(EVENTS.ID))
            .time(record.getValue(EVENTS.EVENT_TIME))
            .level(EventLevelRegistry.getById(record.getValue(EVENT_LEVELS.ID))
                .orElse(EventLevelRegistry.INFO))
            .type(getOrCreateEventType(
                record.getValue(EVENT_TYPES.ID),
                record.getValue(EVENT_TYPES.NAME),
                record.getValue(EVENT_TYPES.HASH)))
            .userId(record.getValue(EVENTS.USER_ID))
            .username(record.getValue(EVENTS.USER_NAME))
            .message(record.getValue(EVENTS.MESSAGE))
            .build());
  }

  private EventType getOrCreateEventType(final int id, final String name, final byte[] hash) {
    return EventTypeRegistry.getByHash(hash)
        .orElseGet(() -> EventTypeRegistry.create(id, name, hash));
  }
  
  public List<Event> getEvents(int view, int page, int level, int type,
      long beginDate, long endDate) {
    view = Math.max(1, view);
    page = Math.max(1, page);

    Condition filter = DSL.noCondition();

    if(beginDate > 0 || endDate > 0)
      filter = filter.and(EVENTS.EVENT_TIME.between(Math.max(beginDate, 0), endDate < 0 ? Long.MAX_VALUE : endDate));

    if(level > 0)
      filter = filter.and(EVENTS.EVENT_LEVEL.eq(level));

    if(type > 0)
      filter = filter.and(EVENTS.EVENT_TYPE.eq(type));

    List<Event> events = getEvents(filter, view * page);

    int start = (page - 1) * view;
    int end = Math.min(events.size(), view * page);

    if(events.size() < start)
      return Collections.emptyList(); // no events on this page
    else
      return events.subList(start, end);
  }

  public Collection<EventType> getEventTypes() {
    return dsl.selectFrom(EVENT_TYPES)
        .fetch(record -> getOrCreateEventType(
            record.getValue(EVENT_TYPES.ID),
            record.getValue(EVENT_TYPES.NAME),
            record.getValue(EVENT_TYPES.HASH)));
  }

  public Collection<EventLevel> getEventLevels() {
    return EventLevelRegistry.all();
  }
  
  public int getEventCount() {
    return dsl.fetchCount(EVENTS);
  }
}
