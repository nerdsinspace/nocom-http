package com.matt.nocom.server.service;

import com.matt.nocom.server.model.event.Event;
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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.matt.nocom.server.h2.codegen.tables.Event.EVENT;

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
            .createdTime(record.getCreatedTime().toInstant(ZoneOffset.UTC))
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

    final var begin = beginDate == null ? null : LocalDateTime.ofInstant(beginDate, ZoneId.systemDefault());
    final var end = endDate == null ? null : LocalDateTime.ofInstant(endDate, ZoneId.systemDefault());

    if (beginDate != null && endDate != null) {
      filter = filter.and(EVENT.CREATED_TIME.between(begin, end));
    } else if (beginDate != null) {
      filter = filter.and(EVENT.CREATED_TIME.ge(begin));
    } else if (endDate != null) {
      filter = filter.and(EVENT.CREATED_TIME.le(end));
    }

    if (type != null) {
      filter = filter.and(EVENT.TYPE.eq(type));
    }

    List<Event> events = getEvents(filter, view * page);

    int start = (page - 1) * view;
    int last = Math.min(events.size(), view * page);

    if (events.size() < start)
      return Collections.emptyList(); // no events on this page
    else
      return events.subList(start, last);
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
