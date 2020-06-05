package com.matt.nocom.server.model.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SessionGroup {
  private String username;
  private UUID uuid;

  @Builder.Default
  private List<PlayerSession> sessions = Lists.newArrayList();

  public void addSession(PlayerSession session) {
    this.getSessions().add(session);
  }
}
