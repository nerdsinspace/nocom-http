package com.matt.nocom.server.service;

import static com.matt.nocom.server.h2.codegen.tables.Event.EVENT;

import com.matt.nocom.server.model.event.Event;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;

@Repository
@RequiredArgsConstructor
public class EventRepository {
  private final DSLContext dsl;

  @Transactional
  public int publish(String causedBy, String type, String message) {
    return dsl.insertInto(EVENT, EVENT.CAUSED_BY, EVENT.TYPE, EVENT.MESSAGE)
        .values(causedBy, type, message)
        .execute();
  }

  public int publishUser(Authentication causedBy, String type, String message, Object... args) {
    Objects.requireNonNull(causedBy, "causedBy");
    return publish(Objects.requireNonNull(causedBy.getName(), "username"),
        type, String.format(message, args));
  }

  public int publishUser(String type, String message, Object... args) {
    SecurityContext ctx = Objects.requireNonNull(SecurityContextHolder.getContext(), "no security context");
    return publishUser(ctx.getAuthentication(), type, message, args);
  }

  public int publishSystem(String type, String message, Object... args) {
    return publish("SYSTEM", type, String.format(message, args));
  }

  @Transactional(readOnly = true)
  protected List<Event> getEvents(Condition conditions, int limit) {
    return dsl.selectFrom(EVENT)
        .where(conditions)
        .orderBy(EVENT.CREATED_TIME.desc())
        .limit(limit)
        .fetch(record -> Event.builder()
            .id(record.getId())
            .createdTime(record.getCreatedTime().toInstant())
            .type(record.getType())
            .causedBy(record.getCausedBy())
            .message(record.getMessage())
            .build());
  }
  
  public List<Event> getEvents(int view, int page, @Nullable String type,
      @Nullable Instant beginDate, @Nullable Instant endDate) {
    view = Math.max(1, view);
    page = Math.max(1, page);

    Condition filter = DSL.noCondition();

    if(beginDate != null && endDate != null) {
      filter = filter.and(EVENT.CREATED_TIME.between(Timestamp.from(beginDate), Timestamp.from(endDate)));
    } else if(beginDate != null) {
      filter = filter.and(EVENT.CREATED_TIME.ge(Timestamp.from(beginDate)));
    } else if(endDate != null) {
      filter = filter.and(EVENT.CREATED_TIME.le(Timestamp.from(endDate)));
    }

    if(type != null) {
      filter = filter.and(EVENT.TYPE.eq(type));
    }

    List<Event> events = getEvents(filter, view * page);

    int start = (page - 1) * view;
    int end = Math.min(events.size(), view * page);

    if(events.size() < start)
      return Collections.emptyList(); // no events on this page
    else
      return events.subList(start, end);
  }

  @Transactional(readOnly = true)
  public Collection<String> getEventTypes() {
    return dsl.selectDistinct(EVENT.TYPE)
        .from(EVENT)
        .orderBy(EVENT.TYPE)
        .fetch(EVENT.TYPE);
  }

  @Transactional(readOnly = true)
  public int getEventCount() {
    return dsl.fetchCount(EVENT);
  }
}
