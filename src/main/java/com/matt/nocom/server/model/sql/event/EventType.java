package com.matt.nocom.server.model.sql.event;

import com.google.common.hash.Hashing;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EventType {
  private int id;
  private String type;
  private byte[] hash;

  public EventType(String type) {
    this(-1, type, Hashing.sha256()
        .hashBytes(type.toLowerCase().getBytes())
        .asBytes());
  }

  // the underscore indicates how much i dont want you to use this method
  public void __set_id(int id) {
    this.id = id;
  }
}
